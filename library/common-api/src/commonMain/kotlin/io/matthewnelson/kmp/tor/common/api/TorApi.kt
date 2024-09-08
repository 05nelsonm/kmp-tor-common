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
import io.matthewnelson.kmp.tor.common.api.internal.SynchronizedObject
import io.matthewnelson.kmp.tor.common.api.internal.synchronized
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmName

public abstract class TorApi
@InternalKmpTorApi
public constructor() {

    @get:JvmName("isRunning")
    public val isRunning: Boolean get() = _isRunning

    @OptIn(InternalKmpTorApi::class)
    private val lock = SynchronizedObject()
    @Volatile
    private var _isRunning: Boolean = false

    @Throws(IllegalArgumentException::class, IllegalStateException::class, IOException::class)
    public fun startTor(args: List<String>) {
        check(!_isRunning) { "tor is running" }

        @OptIn(InternalKmpTorApi::class)
        val argsArray = synchronized(lock) {
            check(!_isRunning) { "tor is running" }

            val a = args.toTypedArray()
            require(a.isNotEmpty()) { "args cannot be empty" }

            _isRunning = true
            a
        }

        try {
            torMain(argsArray)
        } finally {
            _isRunning = false
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class, IOException::class)
    protected abstract fun torMain(args: Array<String>)

    // TODO: Logger
}
