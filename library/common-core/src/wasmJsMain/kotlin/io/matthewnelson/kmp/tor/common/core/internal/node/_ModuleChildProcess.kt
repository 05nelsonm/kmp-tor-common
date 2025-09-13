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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "NOTHING_TO_INLINE")

package io.matthewnelson.kmp.tor.common.core.internal.node

import io.matthewnelson.kmp.file.DelicateFileApi
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.jsExternTryCatch
import io.matthewnelson.kmp.file.toIOException
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner
import io.matthewnelson.kmp.tor.common.core.internal.js.JsObject

internal actual external interface ModuleChildProcess {
    fun spawnSync(command: String, args: JsAny, options: JsObject): JsObject
}

internal actual fun ModuleChildProcess.processRunner(): ProcessRunner = ProcessRunner { commands, duration ->
    val command = commands.firstOrNull() ?: throw IOException("No commands specified")

    val args = JsStringArray.of(commands.size - 1)
    for (i in 1 until commands.size) {
        args.set(index = i - 1, commands[i])
    }

    val options = processOptions(
        stdio = JsStringArray.of(3).apply {
            set(0, "ignore")
            set(1, "pipe")
            set(2, "ignore")
        },
        timeout = duration.inWholeMilliseconds.coerceIn(250L, 1_000L).toInt(),
        encoding = "utf8",
        windowsHide = true,
    )

    val ret = try {
        @OptIn(DelicateFileApi::class)
        jsExternTryCatch { spawnSync(command, args, options) }
    } catch (t: Throwable) {
        throw t.toIOException()
    }

    val status = processStatus(ret) ?: throw IOException("status == null")
    if (status != 0) throw IOException("status[$status] != 0")
    processStdout(ret)?.trim() ?: ""
}

@JsName("Array")
@Suppress("UNUSED")
private external class JsStringArray: JsAny {
    companion object {
        fun of(size: Int): JsStringArray
    }
}

private inline operator fun JsStringArray.set(index: Int, value: String) = jsArraySet(this, index, value)

@Suppress("UNUSED")
private fun jsArraySet(array: JsStringArray, index: Int, value: String) {
    js("array[index] = value")
}

@Suppress("UNUSED")
private fun processOptions(
    stdio: JsStringArray,
    timeout: Int,
    encoding: String,
    windowsHide: Boolean,
): JsObject = js("({ stdio: stdio, timeout: timeout, encoding: encoding, windowsHide: windowsHide })")

@Suppress("RedundantNullableReturnType")
private fun processStatus(ret: JsObject): Int? = js("ret['status']")

@Suppress("RedundantNullableReturnType")
private fun processStdout(ret: JsObject): String? = js("ret['stdout']")
