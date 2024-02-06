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
package io.matthewnelson.kmp.tor.core.resource

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@InternalKmpTorApi
public fun interface ProcessRunner {

    @Throws(IOException::class, InterruptedException::class)
    public fun runAndWait(commands: List<String>, timeout: Duration): String

    public object Default: ProcessRunner {

        @Throws(IOException::class, InterruptedException::class)
        override fun runAndWait(commands: List<String>, timeout: Duration): String {
            val p = Runtime.getRuntime().exec(commands.toTypedArray())

            return try {
                p.waitFor(timeout, destroyOnTimeout = false)
                p.inputStream.reader().readText()
            } finally {
                p.forciblyDestroy()
            }
        }
    }

    @InternalKmpTorApi
    public companion object {

        @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        @Throws(IOException::class, InterruptedException::class)
        public inline fun ProcessRunner.runAndWait(
            commands: List<String>
        ): String = runAndWait(commands, 250.milliseconds)
    }
}
