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

import io.matthewnelson.kmp.file.Buffer
import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.OpenExcl
import io.matthewnelson.kmp.file.delete2
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.read
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.file.toIOException
import io.matthewnelson.kmp.file.write
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.Resource

@OptIn(InternalKmpTorApi::class)
internal actual fun Resource.extractTo(destinationDir: File, onlyIfDoesNotExist: Boolean): File {
    val destination = destinationDir.resolve(platform.fsFileName)

    if (onlyIfDoesNotExist && destination.exists2()) return destination

    val moduleResource = resolveResource(platform.moduleName + platform.resourcePath).toFile()

    destination.delete2(ignoreReadOnly = true, mustExist = false)

    var buffer = moduleResource.read()

    if (platform.isGzipped) {
        val gzBuffer = zlib_gunzipSync(buffer.unwrap())
        buffer = Buffer.wrap(gzBuffer)
    }

    val excl = OpenExcl.MustCreate.of(mode = if (isExecutable) "500" else "400")
    try {
        destination.write(excl, buffer)
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

@Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")
private inline fun resolveResource(path: String): String = try {
    js("require.resolve(path)") as String
} catch (t: Throwable) {
    throw t.toIOException()
}
