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
package io.matthewnelson.kmp.tor.common.core.internal.node

import io.matthewnelson.kmp.file.SysFsInfo

internal expect fun nodeModuleFs(): ModuleFs
internal expect fun nodeModuleOs(): ModuleOs
internal expect fun nodeModuleZlib(): ModuleZlib

@get:Throws(UnsupportedOperationException::class)
internal val node_fs: ModuleFs by lazy {
    requireNodeJs { "fs" }
    nodeModuleFs()
}

@get:Throws(UnsupportedOperationException::class)
internal val node_os: ModuleOs by lazy {
    requireNodeJs { "os" }
    nodeModuleOs()
}

@get:Throws(UnsupportedOperationException::class)
internal val node_zlib: ModuleZlib by lazy {
    requireNodeJs { "zlib" }
    nodeModuleZlib()
}

@Suppress("NOTHING_TO_INLINE")
@Throws(UnsupportedOperationException::class)
private inline fun requireNodeJs(module: () -> String) {
    if (SysFsInfo.name == "FsJsNode") return
    val m = module()
    throw UnsupportedOperationException("Failed to load module[$m] >> FileSystem is not FsJsNode")
}
