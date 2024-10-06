/*
 * Copyright (c) 2024 Matthew Nelson
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
package io.matthewnelson.kmp.tor.common.api

import io.matthewnelson.kmp.tor.common.api.internal.SynchronizedObject
import io.matthewnelson.kmp.tor.common.api.internal.synchronized
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmName

public abstract class TorApi
@InternalKmpTorApi
protected constructor() {

    @get:JvmName("isRunning")
    public val isRunning: Boolean get() = _isRunning

    @Volatile
    private var _isRunning: Boolean = false
    private val lock = SynchronizedObject()

    /**
     * Executes tor's tor_run_main function.
     *
     * **NOTE:** `argv[0]` is **always** set to `"tor"` when converting [configurationArgs]
     * to an array before passing to [torRunMainProtected].
     *
     * **NOTE:** This is a blocking API and should be called from a background thread.
     *
     * e.g.
     *
     *     torRunMain(listOf("--version"))
     *     torRunMain(listOf("--SocksPort", "0", "--verify-config"))
     *
     * @param [configurationArgs] Arguments to populate tor's tor_main_configuration_t
     *   struct with.
     * @throws [IllegalArgumentException] if [configurationArgs] is empty, or if
     *   [torRunMainProtected] returns non-0 and `--verify-config` is present in
     *   [configurationArgs]
     * @throws [IllegalStateException] if [isRunning], or if [torRunMainProtected]
     *   returns non-0 and `--verify-config` is **not** present in [configurationArgs]
     * */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    public fun torRunMain(configurationArgs: List<String>) {
        check(!_isRunning) { "tor is running" }

        val args = synchronized(lock) {
            check(!_isRunning) { "tor is running" }
            require(configurationArgs.isNotEmpty()) { "args cannot be empty" }

            _isRunning = true
            Array(configurationArgs.size + 1) { i -> if (i == 0) "tor" else configurationArgs[i - 1] }
        }

        val result = try {
            torRunMainProtected(args)
        } finally {
            _isRunning = false
        }

        if (result == 0) return

        val msg = "tor_run_main completed exceptionally."
        throw if (args.contains("--verify-config")) {
            IllegalArgumentException("$msg ARGS: ${configurationArgs}.")
        } else {
            IllegalStateException(msg)
        }
    }

    protected abstract fun torRunMainProtected(args: Array<String>): Int

    // TODO: Logger
}
