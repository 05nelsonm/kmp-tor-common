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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.common.lib.locator

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.AppInitializer
import java.io.File

/**
 * Utility for obtaining an absolute path to native libraries within an
 * application's [ApplicationInfo.nativeLibraryDir].
 *
 * Is initialized on devices prior to [android.app.Application.onCreate]
 * via its [androidx.startup.Initializer].
 *
 * @see [Companion.isInitialized]
 * @see [Companion.find]
 * @see [Companion.require]
 * */
@Deprecated("No longer relevant. Use BaseDexClassLoader.findLibrary instead")
public class KmpTorLibLocator private constructor() {

    private var nativeLibraryDir: File? = null

    public companion object {

        @Suppress("DEPRECATION")
        private val INSTANCE = KmpTorLibLocator()

        /**
         * If the instance of [KmpTorLibLocator] has been initialized yet.
         * */
        @JvmStatic
        public fun isInitialized(): Boolean = INSTANCE.nativeLibraryDir != null

        /**
         * Find a native lib.
         *
         * e.g.
         *
         *     KmpTorLibLocator.find("libtor.so")
         *
         * @param [libName] the name of the lib
         * @return [File] or null if not found
         * */
        @JvmStatic
        public fun find(libName: String): File? {
            val lib = INSTANCE.nativeLibraryDir
                ?.resolve(libName)
                ?: return null

            if (!lib.exists()) return null
            if (!lib.isFile) return null

            return lib
        }

        /**
         * Find a native lib.
         *
         * e.g.
         *
         *     KmpTorLibLocator.require("libtor.so")
         *
         * @param [libName] the name of the lib
         * @return [File]
         * @throws [IllegalStateException] if [libName] was not found
         * */
        @JvmStatic
        @Throws(IllegalStateException::class)
        public fun require(libName: String): File = find(libName)
            ?: throw IllegalStateException("Failed to find lib[$libName]")

        /**
         * For `kmp-tor-resource` usage when Android Runtime is detected and
         * [isInitialized] is `false`.
         * */
        @JvmStatic
        public fun errorMsg(): String {
            @Suppress("DEPRECATION")
            val classPath = KmpTorLibLocator::class.qualifiedName + '$' + "Initializer"

            return """
                KmpTorLibLocator.Initializer cannot be initialized lazily.
                Please ensure that you have:
                <meta-data
                    android:name='$classPath'
                    android:value='androidx.startup' />
                under InitializationProvider in your AndroidManifest.xml
            """.trimIndent()
        }
    }

    @Suppress("UNUSED", "DEPRECATION")
    internal class Initializer internal constructor(): androidx.startup.Initializer<KmpTorLibLocator> {

        override fun create(context: Context): KmpTorLibLocator {
            val appInitializer = AppInitializer.getInstance(context)
            check(appInitializer.isEagerlyInitialized(javaClass)) { errorMsg() }
            INSTANCE.nativeLibraryDir = File(context.applicationInfo.nativeLibraryDir).absoluteFile
            return INSTANCE
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }
}
