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
import io.matthewnelson.kmp.tor.common.api.internal.*
import io.matthewnelson.kmp.tor.common.api.internal.Singleton
import io.matthewnelson.kmp.tor.common.api.internal.newLock
import kotlin.concurrent.Volatile
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

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
         * Extract tor's geoip & geoip6 files from application resources to [resourceDir].
         * */
        @Throws(IllegalStateException::class, IOException::class)
        public open fun extract(): GeoipFiles = error("overridden")

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
        public abstract class Exec private constructor(directory: File?): Tor(directory.checkNotNull { "Exec" }) {

            /**
             * Lambda for building and/or spawning a process.
             *
             * e.g. (Using the `kmp-process` library)
             *
             *     val builder = loaderExec.process(myBinder) { tor, configureEnv ->
             *         Process.Builder(command = tor.path)
             *             .args(myTorArgs)
             *             .environment(configureEnv)
             *             .stdin(Stdio.Null)
             *     }
             *
             * @see [RuntimeBinder]
             * @throws [IllegalStateException] if [binder] is inappropriate, or there
             *   was a failure to extract the tor executable from application resources.
             * @throws [IOException] if there was a failure to extract the tor executable
             *   from application resources.
             * */
            @Throws(IllegalStateException::class, IOException::class)
            public open fun <T: Any?> process(
                binder: RuntimeBinder,
                block: (tor: File, configureEnv: MutableMap<String, String>.() -> Unit) -> T,
            ): T = error("overridden")

            protected companion object {

                /**
                 * For implementors of [Exec].
                 *
                 * **NOTE:** `extract` and `extractTor` are always invoked
                 * while holding a lock and are synchronized.
                 * */
                @JvmStatic
                protected fun getOrCreate(
                    resourceDir: File,
                    extract: (resourceDir: File) -> GeoipFiles,
                    extractTor: (resourceDir: File) -> File,
                    configureEnv: MutableMap<String, String>.(resourceDir: File) -> Unit,
                    toString: (resourceDir: File) -> String,
                ): Tor = Tor.Companion.getOrCreate(create = {
                    object : Exec(resourceDir) {

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun extract(): GeoipFiles = withBinderLock(null) { extract(this.resourceDir) }

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun <T: Any?> process(
                            binder: RuntimeBinder,
                            block: (tor: File, configureEnv: MutableMap<String, String>.() -> Unit) -> T,
                        ): T {
                            val dir = this.resourceDir
                            val tor = withBinderLock(binder) { extractTor(dir) }
                            return block(tor) { configureEnv(dir) }
                        }

                        override fun toString(): String = toString(this.resourceDir)
                    }
                })
            }

            /** Can extend to gain access to protected static functions, but never instantiate. */
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
        public abstract class NoExec private constructor(resourceDir: File?): Tor(resourceDir.checkNotNull { "NoExec" }) {

            /**
             * Lambda for running tor via [TorApi].
             *
             * e.g.
             *
             *     loaderNoExec.withApi(myBinder) {
             *         torRunMain(myTorArgs)
             *         assertEquals(TorApi.State.STARTED, state())
             *     }
             *
             * @see [RuntimeBinder]
             * @throws [IllegalStateException] if [binder] is inappropriate, there
             *   was a failure to extract libtor from application resources, or
             *   libtor failed to load.
             * @throws [IOException] if there was a failure to extract libtor from
             *   application resources.
             * */
            @Throws(IllegalStateException::class, IOException::class)
            public open fun <T: Any?> withApi(
                binder: RuntimeBinder,
                block: TorApi.() -> T,
            ): T = error("overridden")

            protected companion object {

                /**
                 * For implementors of [NoExec].
                 *
                 * **NOTE:** `extract` and `create` are always invoked
                 * while holding a lock and are synchronized. The resulting
                 * [TorApi] reference from `create` is cached so a single
                 * instance is only every created, and subsequently reused.
                 * */
                @JvmStatic
                protected fun getOrCreate(
                    resourceDir: File,
                    extract: (resourceDir: File) -> GeoipFiles,
                    create: (resourceDir: File) -> TorApi,
                    toString: (resourceDir: File) -> String,
                ): Tor = Tor.Companion.getOrCreate(create = {
                    object : NoExec(resourceDir) {

                        @Volatile
                        private var _api: TorApi? = null

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun extract(): GeoipFiles = withBinderLock(null) { extract(this.resourceDir) }

                        @Throws(IllegalStateException::class, IOException::class)
                        override fun <T: Any?> withApi(
                            binder: RuntimeBinder,
                            block: TorApi.() -> T,
                        ): T {
                            val api = withBinderLock(binder) { _api ?: create(this.resourceDir).also { _api = it } }
                            return block(api)
                        }

                        override fun toString(): String = toString(this.resourceDir)
                    }
                })
            }

            /** Can extend to gain access to protected static functions, but never instantiate. */
            @Throws(IllegalStateException::class)
            protected constructor(): this(resourceDir = null)
        }

        // Ensures that only 1 instance of ResourceLoader.Tor is created.
        private companion object: Singleton<Tor>() {

            @Suppress("WRONG_INVOCATION_KIND")
            @OptIn(ExperimentalContracts::class)
            @Throws(IllegalStateException::class)
            private inline fun File?.checkNotNull(subclassName: () -> String): File {
                contract {
                    callsInPlace(subclassName, InvocationKind.AT_MOST_ONCE)
                }

                check(this != null) {
                    "ResourceLoader.Tor.${subclassName()} cannot be instantiated. " +
                    "Use a private constructor and static factory function 'getOrCreate'."
                }

                return this
            }
        }
    }

    /**
     * Interface for runtime implementations to use for binding [ResourceLoader]
     * instances, inhibit any external invocations.
     *
     * For example, [Tor.Exec.process] and [Tor.NoExec.withApi] will be bound to
     * a [RuntimeBinder] upon first invocation. If a different [RuntimeBinder] is
     * utilized later (from someone who has access to the loader, but not the binder),
     * an exception is raised. This is in order to export runtime functionality to
     * a single implementation and constrain access.
     * */
    public interface RuntimeBinder

    @Volatile
    private var _binder: KClass<out RuntimeBinder>? = null
    private val lock = newLock()

    /** @suppress */
    protected companion object {

        @OptIn(ExperimentalContracts::class)
        @Throws(IllegalArgumentException::class, IllegalStateException::class)
        internal fun <T: Any?> ResourceLoader.withBinderLock(binder: RuntimeBinder?, block: () -> T): T {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }

            var e: Exception? = null

            val result = lock.withLock lock@ {
                if (binder == null) return@lock block()
                val clazz = binder::class
                if (_binder == null) {
                    if (clazz.simpleName == null) {
                        e = IllegalArgumentException(
                            "binder instance must have a fully qualified name. " +
                            "It cannot be an anonymous instance."
                        )
                        return@lock null
                    } else {
                        _binder = clazz
                    }
                } else {
                    if (clazz != _binder) {
                        e = IllegalStateException("ResourceLoader is already bound to $_binder")
                        return@lock null
                    }
                }

                block()
            }

            e?.let { throw it }

            @Suppress("UNCHECKED_CAST")
            return result as T
        }
    }

    /** @suppress */
    public final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other::class != this::class) return false
        return other.hashCode() == hashCode()
    }

    /** @suppress */
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
