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

import io.matthewnelson.kmp.file.IOException

/**
 * Abstraction for [ResourceLoader.Tor.NoExec] implementations to provide.
 *
 * Implementations of [TorApi] **must** support restart capabilities. The only
 * reliable way to do this with the `tor` C library is dynamic loading/unloading
 * before/after each invocation of `tor_run_main`. This is due to static variables
 * in `tor` that are never uninitialized, requiring that it be unloaded from memory
 * via `dlclose` (or `FreeLibrary` for `Windows`) after `tor_run_main` completes
 * and all necessary cleanup has been performed. If `tor` is not unloaded after
 * completion of `tor_run_main`, successive calls to [torRunMain] run the highly
 * probable risk of encountering a segmentation fault. The API design of [TorApi]
 * was conceived with this necessity in mind.
 * */
public abstract class TorApi protected constructor() {

    public enum class State {

        /**
         * Nothing happening. Free to call [torRunMain].
         * */
        OFF,

        /**
         * [torRunMain] is awaiting return.
         * */
        STARTING,

        /**
         * Tor's `tor_run_main` function is running in its thread.
         * */
        STARTED,

        /**
         * Tor's `tor_run_main` function has returned and a call to
         * [terminateAndAwaitResult] is needed to clean up resources.
         * */
        STOPPED,
    }

    /**
     * Executes tor's `tor_run_main` function. If it does not throw, tor has been
     * started. A following call to [terminateAndAwaitResult] MUST be had before
     * calling [torRunMain] again.
     *
     * Callers are expected to poll [state] for a value of [State.STOPPED], then
     * call [terminateAndAwaitResult] to release resources.
     *
     * **NOTE:** `argv[0]` is **always** set to `"tor"` when converting [configuration]
     * to an array to pass to the implementation.
     *
     * **NOTE:** This is a blocking API and should be called from a background thread.
     *
     * e.g.
     *
     *     torRunMain(listOf("--SocksPort", "0", "--verify-config"))
     *
     * @param [configuration] Arguments to populate tor's `tor_main_configuration_t`
     *   struct with.
     * @throws [IllegalStateException] If [State] is not [State.OFF], or there was
     *   an issue with startup.
     * @throws [IOException] If there was an issue extracting tor to the filesystem.
     * */
    @Throws(IllegalStateException::class, IOException::class)
    public fun torRunMain(configuration: List<String>) {
        val args = Array(configuration.size + 1) { i ->
            if (i == 0) "tor" else configuration[i - 1]
        }

        torRunMain(args)
    }

    /**
     * Retrieve the current [State] of the [TorApi] implementation.
     * */
    public abstract fun state(): State

    /**
     * Terminates tor (if it is running) and releases resources. This
     * MUST be called before another invocation of [torRunMain] can be had.
     *
     * **NOTE:** This is a blocking API and should be called from a background thread.
     *
     * @return `-1` if [state] == [State.OFF] (nothing to terminate). Otherwise,
     *   the exit value of `tor_run_main`.
     * */
    public abstract fun terminateAndAwaitResult(): Int

    /**
     * Design is such that implementations **must** start `tor` in its own thread
     * and return upon successful startup. Any errors must be thrown as the annotated
     * exceptions and [state] must be reset to [State.OFF].
     * */
    @Throws(IllegalStateException::class, IOException::class)
    protected abstract fun torRunMain(args: Array<String>)

    public final override fun toString(): String = "TorApi[state=${state()}]"
}
