/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.file.Closeable
import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.OpenExcl
import io.matthewnelson.kmp.file.delete2
import io.matthewnelson.kmp.file.errnoToIOException
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.openWrite
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.file.use
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.Resource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.EINTR
import platform.posix.errno
import platform.zlib.gzFile
import platform.zlib.gzclose_r
import platform.zlib.gzread
import platform.zlib.Z_OK
import kotlin.concurrent.Volatile
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Throws(Throwable::class)
@OptIn(ExperimentalForeignApi::class, InternalKmpTorApi::class)
internal actual fun Resource.extractTo(destinationDir: File, onlyIfDoesNotExist: Boolean): File {
    val name = platform.nativeResource.name
    val destFinal = if (platform.isGzipped) {
        destinationDir.resolve(platform.fsFileName)
    } else {
        null
    }

    // Could be gzipped, could not. Need to extract it to FS first
    val dest = destinationDir.resolve(name)

    if (onlyIfDoesNotExist) {
        val file = (destFinal ?: dest)
        if (file.exists2()) return file
    }

    dest.delete2(ignoreReadOnly = true)
    destFinal?.delete2(ignoreReadOnly = true)

    val excl = OpenExcl.MustCreate.of(mode = if (isExecutable) "500" else "400")
    try {
        dest.openWrite(excl = if (destFinal == null) excl else null).use { s ->
            platform.nativeResource.read { buf, len ->
                s.write(buf, 0, len)
            }
        }
    } catch (e: Exception) {
        try {
            dest.delete2(ignoreReadOnly = true)
        } catch (ee: IOException) {
            e.addSuppressed(ee)
        }
        throw e
    }

    // Was not gzipped. dest is the final destination.
    if (destFinal == null) return dest

    var threw: IOException? = null
    try {
        dest.gzOpenRead { gzFile ->
            destFinal.openWrite(excl = excl).use { s ->
                val buf = ByteArray(4096)

                while (true) {
                    val read = buf.usePinned { pinned ->
                        gzread(gzFile, pinned.addressOf(0), buf.size.toUInt())
                    }

                    if (read < 0) throw errnoToIOException(errno)
                    if (read == 0) break
                    s.write(buf, 0, read)
                }
            }
        }
    } catch (e: IOException) {
        try {
            destFinal.delete2(ignoreReadOnly = true)
        } catch (ee: IOException) {
            e.addSuppressed(ee)
        }
        threw = e
    } finally {
        // Always clean up and delete the gzipped file
        try {
            dest.delete2(ignoreReadOnly = true)
        } catch (e: IOException) {
            threw?.addSuppressed(e)
        }
    }

    threw?.let { throw it }

    return destFinal
}

@Throws(IOException::class)
@OptIn(ExperimentalContracts::class, ExperimentalForeignApi::class)
private inline fun <T: Any?> File.gzOpenRead(block: (file: gzFile) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    @Suppress("VariableInitializerIsRedundant")
    var ptr: gzFile? = null
    while (true) {
        ptr = gzopenRO()
        if (ptr != null) break
        if (errno == EINTR) continue
        throw errnoToIOException(errno, this)
    }

    val closeable = object : Closeable {

        @Volatile
        private var _ptr = ptr

        override fun close() {
            val ptr = _ptr ?: return
            _ptr = null
            while (true) {
                if (gzclose_r(ptr) == Z_OK) break
                if (errno == EINTR) continue
                throw errnoToIOException(errno)
            }
        }
    }

    return closeable.use { block(ptr) }
}

@ExperimentalForeignApi
@Suppress("NOTHING_TO_INLINE")
internal expect inline fun File.gzopenRO(): gzFile?
