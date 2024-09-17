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
@file:Suppress("RedundantCompanionReference")

package io.matthewnelson.kmp.tor.common.api

import io.matthewnelson.kmp.file.*
import io.matthewnelson.kmp.tor.common.api.internal.Singleton
import io.matthewnelson.kmp.tor.common.api.internal.SynchronizedObject
import io.matthewnelson.kmp.tor.common.api.internal.synchronized
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Abstraction for loadable resources.
 *
 * See [kmp-tor-resource](https://github.com/05nelsonm/kmp-tor-resource)
 * */
public abstract class ResourceLoader private constructor() {

    /**
     * Model for loading `tor` resources.
     *
     * API design is such that only 1 [Tor] loader can be instantiated. Any
     * attempts to instantiate a second will result in the return of the
     * first one that was created. This is to inhibit any attempts to switch
     * between [Exec] and [NoExec] during runtime, as well as ensure resources
     * are only extracted to a single location.
     *
     * @see [Exec]
     * @see [NoExec]
     * */
    public sealed class Tor private constructor(resourceDir: File): ResourceLoader() {

        /**
         * The absolute path to the directory for which resources will be extracted.
         * */
        @JvmField
        public val resourceDir: File = resourceDir.absoluteFile.normalize()

        /**
         * Model for running `tor` as an executable within a child process.
         *
         * Platform availability (for running executables):
         *  - Android
         *  - Jvm
         *  - Native Android
         *  - Native Linux
         *  - Native macOS (if not using app-sandbox)
         *  - Native Windows
         *  - Node.js
         *
         * @see [NoExec]
         * */
        public abstract class Exec private constructor(directory: File?): Tor(directory.checkNotNull("Exec")) {

            protected companion object {

                @JvmStatic
                @InternalKmpTorApi
                protected fun getOrCreate(
                    resourceDir: File,
                    extract: (resourceDir: File) -> GeoipFiles,
                    extractTor: (resourceDir: File) -> File,
                    configureEnv: MutableMap<String, String>.(resourceDir: File) -> Unit,
                    toString: (resourceDir: File) -> String,
                ): Tor = Tor.Companion.getOrCreate(create = {
                    @OptIn(InternalKmpTorApi::class)
                    object : Exec(resourceDir) {

                        private val lock = SynchronizedObject()

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun extract(): GeoipFiles = synchronized(lock) { extract(this.resourceDir) }

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun <T: Any?> execute(
                            block: (tor: File, configureEnv: MutableMap<String, String>.() -> Unit) -> T
                        ): T {
                            val dir = this.resourceDir
                            val tor = synchronized(lock) { extractTor(dir) }
                            return block(tor) { configureEnv(dir) }
                        }

                        override fun toString(): String = toString(this.resourceDir)
                    }
                })
            }

            /**
             * Lambda for building and/or spawning a process.
             *
             * e.g. (Using `kmp-process`)
             *
             *     val builder = loaderExec.execute { tor, configureEnv ->
             *         Process.Builder(tor.path)
             *             .args(torArgs)
             *             .environment { configureEnv() }
             *             .stdin(Stdio.Null)
             *     }
             * */
            @InternalKmpTorApi
            @Throws(IllegalStateException::class, IOException::class)
            public open fun <T: Any?> execute(
                block: (tor: File, configureEnv: MutableMap<String, String>.() -> Unit) -> T
            ): T = error("overridden")

            /** Can extend to gain access to protected static functions, but never instantiate. */
            @InternalKmpTorApi
            @Throws(IllegalStateException::class)
            protected constructor(): this(directory = null)
        }

        /**
         * Model for running `tor` within your application's memory space.
         *
         * Platform availability (for static compilation and/or dynamic loading):
         *  - Android
         *  - Jvm
         *  - Native Android
         *  - Native Linux
         *  - Native macOS
         *  - Native iOS
         *  - Native tvOS
         *  - Native watchOS
         *  - Native Windows
         *
         * @see [Exec]
         * */
        public abstract class NoExec private constructor(resourceDir: File?): Tor(resourceDir.checkNotNull("NoExec")) {

            protected companion object {

                @JvmStatic
                @InternalKmpTorApi
                protected fun getOrCreate(
                    resourceDir: File,
                    extract: (resourceDir: File) -> GeoipFiles,
                    load: () -> TorApi,
                    toString: (resourceDir: File) -> String,
                ): Tor = Tor.Companion.getOrCreate(create = {
                    @OptIn(InternalKmpTorApi::class)
                    object : NoExec(resourceDir) {

                        private val lock = SynchronizedObject()
                        private val api = Singleton<TorApi>()

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun extract(): GeoipFiles = synchronized(lock) { extract(this.resourceDir) }

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun <T: Any?> withApi(block: TorApi.() -> T): T {
                            val api = api.getOrCreate(load)
                            return block(api)
                        }

                        override fun toString(): String = toString(this.resourceDir)
                    }
                })
            }

            @InternalKmpTorApi
            @Throws(IllegalStateException::class, IOException::class)
            public open fun <T: Any?> withApi(block: TorApi.() -> T): T = error("overridden")

            /** Can extend to gain access to protected static functions, but never instantiate. */
            @InternalKmpTorApi
            @Throws(IllegalStateException::class)
            protected constructor(): this(resourceDir = null)
        }

        @InternalKmpTorApi
        @Throws(IllegalStateException::class, IOException::class)
        public open fun extract(): GeoipFiles = error("overridden")

        // Ensures that only 1 instance of ResourceLoader.Tor is created.
        private companion object: Singleton<Tor>() {

            private fun File?.checkNotNull(subclassName: String): File {
                check(this != null) {
                    "ResourceLoader.Tor.$subclassName cannot be instantiated. " +
                    "Use a private constructor and static factory function 'getOrCreate'."
                }

                return this
            }
        }
    }

    public final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other::class != this::class) return false
        return other.hashCode() == hashCode()
    }

    public final override fun hashCode(): Int {
        var result = 17

        when (this) {
            is Tor -> resourceDir.hashCode()
            else -> 0
        }.let { result = result * 31 + it }

        result = result * 31 + this::class.hashCode()

        return result
    }
}
