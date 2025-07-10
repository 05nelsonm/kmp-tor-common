/*
 * Copyright (c) 2025 Matthew Nelson
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
@file:Suppress("NOTHING_TO_INLINE")

package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.file.Buffer
import io.matthewnelson.kmp.file.DelicateFileApi
import io.matthewnelson.kmp.file.jsExternTryCatch
import io.matthewnelson.kmp.tor.common.core.internal.node.ModuleZlib

// @Throws(Throwable::class)
internal actual inline fun Buffer.gunzip(zlib: ModuleZlib): Buffer {
    val unwrapped = unwrap()
    @OptIn(DelicateFileApi::class)
    val b = jsExternTryCatch { zlib.gunzipSync(unwrapped) }
    return Buffer.wrap(b)
}

// @Throws(Throwable::class)
internal actual fun resolveResource(path: String): String = js("require.resolve(path)")
