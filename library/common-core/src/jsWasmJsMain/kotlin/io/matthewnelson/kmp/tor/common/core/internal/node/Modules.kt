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
@file:OptIn(ExperimentalWasmJsInterop::class)

package io.matthewnelson.kmp.tor.common.core.internal.node

import io.matthewnelson.kmp.file.SysFsInfo
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@get:Throws(UnsupportedOperationException::class)
internal val node_child_process: ModuleChildProcess by lazy {
    requireNodeJs { "child_process" }
    nodeModuleChildProcess()
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

private fun nodeModuleChildProcess(): ModuleChildProcess = js("eval('require')('child_process')")
private fun nodeModuleOs(): ModuleOs = js("eval('require')('os')")
private fun nodeModuleZlib(): ModuleZlib = js("eval('require')('zlib')")

@Suppress("NOTHING_TO_INLINE")
@Throws(UnsupportedOperationException::class)
private inline fun requireNodeJs(module: () -> String) {
    if (SysFsInfo.name == "FsJsNode") return
    val m = module()
    throw UnsupportedOperationException("Failed to load module[$m] >> FileSystem is not FsJsNode")
}
