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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.common.core.internal.node

import io.matthewnelson.kmp.file.DelicateFileApi
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.jsExternTryCatch
import io.matthewnelson.kmp.file.toIOException
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner
import io.matthewnelson.kmp.tor.common.core.internal.js.JsArray
import io.matthewnelson.kmp.tor.common.core.internal.js.JsObject
import io.matthewnelson.kmp.tor.common.core.internal.js.getIntOrNull
import io.matthewnelson.kmp.tor.common.core.internal.js.getStringOrNull
import io.matthewnelson.kmp.tor.common.core.internal.js.jsObject
import io.matthewnelson.kmp.tor.common.core.internal.js.set

internal external interface ModuleChildProcess {
    fun spawnSync(command: String, args: JsArray, options: JsObject): JsObject
}

internal fun ModuleChildProcess.processRunner(): ProcessRunner = ProcessRunner { commands, duration ->
    val command = commands.firstOrNull() ?: throw IOException("No commands specified")

    val args = JsArray.of(commands.size - 1)
    for (i in 1 until commands.size) {
        args[i - 1] = commands[i]
    }

    val options = jsObject()
    options["stdio"] = JsArray.of(3).apply {
        this[0] = "ignore"
        this[1] = "pipe"
        this[2] = "ignore"
    }
    options["timeout"] = duration.inWholeMilliseconds.coerceIn(250L, 1_000L).toInt()
    options["encoding"] = "utf8"
    options["windowHide"] = true

    val ret = try {
        @OptIn(DelicateFileApi::class)
        jsExternTryCatch { spawnSync(command, args, options) }
    } catch (t: Throwable) {
        throw t.toIOException()
    }

    val status = ret.getIntOrNull("status") ?: throw IOException("status == null")
    if (status != 0) throw IOException("status[$status] != 0")
    ret.getStringOrNull("stdout") ?: ""
}
