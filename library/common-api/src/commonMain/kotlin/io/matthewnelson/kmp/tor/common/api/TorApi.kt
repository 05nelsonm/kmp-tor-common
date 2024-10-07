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
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Abstraction for [ResourceLoader.Tor.NoExec] implementations to provide.
 * */
public abstract class TorApi protected constructor() {

    @get:JvmName("isRunning")
    public val isRunning: Boolean get() = _isRunning

    @Volatile
    private var _isRunning: Boolean = false
    private val lock = SynchronizedObject()

    /**
     * Executes tor's `tor_run_main` function.
     *
     * **NOTE:** `argv[0]` is **always** set to `"tor"` when converting [configuration]
     * to an array before passing them to [torRunMainProtected].
     *
     * **NOTE:** This is a blocking API and should be called from a background thread.
     *
     * e.g.
     *
     *     torRunMain(listOf("--SocksPort", "0", "--verify-config"))
     *
     * @param [configuration] Arguments to populate tor's `tor_main_configuration_t`
     *   struct with.
     * @return 0 on successful completion, non-0 on exceptional completion.
     * */
    public fun torRunMain(configuration: List<String>): Int {
        return torRunMain(configuration, NO_OP_LOGGER)
    }

    /**
     * Executes tor's `tor_run_main` function.
     *
     * **NOTE:** `argv[0]` is **always** set to `"tor"` when converting [configuration]
     * to an array before passing them to [torRunMainProtected].
     *
     * **NOTE:** This is a blocking API and should be called from a background thread.
     *
     * e.g.
     *
     *     torRunMain(listOf("--version"), TorApi.Logger { println(it) })
     *     torRunMain(listOf("--SocksPort", mySocksPort, "--verify-config"), myLogger)
     *
     * @param [configuration] Arguments to populate tor's `tor_main_configuration_t`
     *   struct with.
     * @param [log] pass in a [Logger] to receive log messages.
     * @return 0 on successful completion, non-0 on exceptional completion.
     * */
    public fun torRunMain(configuration: List<String>, log: Logger): Int {
        if (_isRunning) {
            log.notifyErr("tor is running")
            return -1
        }

        val args = synchronized(lock) {
            if (_isRunning) return@synchronized null
            _isRunning = true
            Array(configuration.size + 1) { i ->
                if (i == 0) "tor" else configuration[i - 1]
            }
        }

        if (args == null) {
            log.notifyErr("tor is running")
            return -1
        }

        val result = try {
            torRunMainProtected(args, log)
        } finally {
            _isRunning = false
        }

        return result
    }

    /**
     * Logging to supplement what would normally be observed from process stdout.
     *
     * @see [Severity]
     * */
    public abstract class Logger {

        /**
         * The minimum severity for tor to use.
         *
         * [tor-man#Log][https://github.com/05nelsonm/kmp-tor-resource/blob/master/docs/tor-man.adoc#Log]
         * */
        @JvmField
        public val minSeverity: Severity

        public constructor(): this(Severity.NOTICE)
        public constructor(minSeverity: Severity) { this.minSeverity = minSeverity }

        public abstract fun notify(log: String)

        /**
         * [tor-man#Log][https://github.com/05nelsonm/kmp-tor-resource/blob/master/docs/tor-man.adoc#Log]
         * */
        public enum class Severity {
            DEBUG,
            INFO,
            NOTICE,
            WARN,
            ERR;
        }

        public companion object {

            @JvmStatic
            public operator fun invoke(
                notify: (log: String) -> Unit,
            ): Logger = invoke(Severity.NOTICE, notify)

            @JvmStatic
            public operator fun invoke(
                severity: Severity,
                notify: (log: String) -> Unit,
            ): Logger = object : Logger(severity) {
                override fun notify(log: String) { notify.invoke(log) }
            }
        }
    }

    protected abstract fun torRunMainProtected(args: Array<String>, log: Logger): Int

    /**
     * Helper for implementations to utilize which formats [msg] to the expected
     * error output.
     * */
    protected fun Logger.notifyErr(msg: String) {
        val formatted = StringBuilder(msg.length).apply {
            for (line in msg.lines()) {
                if (line.isBlank()) continue
                append("[err] ")
                appendLine(line.trim())
            }
        }.toString()

        if (formatted.isBlank()) return

        notify(formatted)
    }

    private companion object {
        private val NO_OP_LOGGER = Logger(Logger.Severity.ERR) {}
    }
}
