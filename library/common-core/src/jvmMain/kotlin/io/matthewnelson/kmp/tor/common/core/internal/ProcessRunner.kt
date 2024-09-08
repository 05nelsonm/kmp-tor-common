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
package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.file.ANDROID
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal fun interface ProcessRunner {

    @Throws(IOException::class, InterruptedException::class)
    fun runAndWait(commands: List<String>, timeout: Duration): String

    object Default: ProcessRunner {

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

    companion object {

        @Suppress("NOTHING_TO_INLINE")
        @Throws(IOException::class, InterruptedException::class)
        internal inline fun ProcessRunner.runAndWait(
            commands: List<String>
        ): String = runAndWait(commands, 250.milliseconds)

        @Throws(InterruptedException::class)
        internal fun Process.waitFor(timeout: Duration, destroyOnTimeout: Boolean = true): Boolean {
            val startTime = System.nanoTime()
            var remaining = timeout.inWholeNanoseconds

            do {
                try {
                    exitValue()
                    return true
                } catch (_: IllegalThreadStateException) {
                    if (remaining > 0) {
                        Thread.sleep(
                            min(
                                (TimeUnit.NANOSECONDS.toMillis(remaining) + 1).toDouble(),
                                100.0
                            ).toLong()
                        )
                    }
                }

                remaining = timeout.inWholeNanoseconds - (System.nanoTime() - startTime)
            } while (remaining > 0)

            if (destroyOnTimeout) {
                forciblyDestroy()
            }

            return false
        }

        @Suppress("NOTHING_TO_INLINE", "NewApi")
        internal inline fun Process.forciblyDestroy(): Process {
            return ANDROID.SDK_INT?.let { sdkInt ->
                if (sdkInt >= 26) {
                    destroyForcibly()
                } else {
                    destroy()
                    this
                }
            } ?: destroyForcibly()
        }
    }
}
