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

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.OpenExcl
import io.matthewnelson.kmp.file.delete2
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.openWrite
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.Resource
import java.util.zip.GZIPInputStream
import kotlin.Throws

@Throws(Throwable::class)
@OptIn(InternalKmpTorApi::class)
internal actual fun Resource.extractTo(destinationDir: File, onlyIfDoesNotExist: Boolean): File {
    val destination = destinationDir.resolve(platform.fsFileName)

    if (onlyIfDoesNotExist && destination.exists2()) return destination

    var resourceStream = platform.resourceClass.getResourceAsStream(platform.resourcePath)
        ?: throw IOException("Failed to get resource input stream for ${platform.resourcePath}")

    try {
        destination.delete2(ignoreReadOnly = true, mustExist = false)
    } catch (e: IOException) {
        try {
            resourceStream.close()
        } catch (ee: IOException) {
            e.addSuppressed(ee)
        }
        throw e
    }

    if (platform.isGzipped) {
        try {
            resourceStream = GZIPInputStream(resourceStream)
        } catch (e: IOException) {
            try {
                resourceStream.close()
            } catch (ee: IOException) {
                e.addSuppressed(ee)
            }
            throw e
        }
    }

    val excl = OpenExcl.MustCreate.of(mode = mode)

    try {
        resourceStream.use { iStream ->
            destination.openWrite(excl = excl).use { s ->
                val buf = ByteArray(4096)

                while (true) {
                    val read = iStream.read(buf)
                    if (read == -1) break
                    s.write(buf, 0, read)
                }
            }
        }
    } catch (e: IOException) {
        try {
            destination.delete2(ignoreReadOnly = true)
        } catch (ee: IOException) {
            e.addSuppressed(ee)
        }
        throw e
    }

    return destination
}
