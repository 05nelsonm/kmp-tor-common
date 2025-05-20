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

package io.matthewnelson.kmp.tor.common.lib.locator

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.path
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.tor.common.lib.locator.internal.ENV_KEY_NATIVE_LIBRARY_DIR
import io.matthewnelson.kmp.tor.common.lib.locator.internal.commonErrorMsg
import kotlinx.cinterop.*
import platform.posix.S_IFMT
import platform.posix.S_IFREG
import platform.posix.getenv
import platform.posix.stat

/**
 * Utility for obtaining absolute path to native libraries within an
 * application's `nativeLibraryDir`.
 *
 * Is initialized by the Android [KmpTorLibLocator] implementation, which
 * is a required dependency for the application utilizing this Android
 * Native implementation.
 *
 * @see [Companion.isInitialized]
 * @see [Companion.find]
 * @see [Companion.require]
 * */
public actual class KmpTorLibLocator private actual constructor() {

    public actual companion object {

        /**
         * If the instance of [KmpTorLibLocator] has been initialized yet.
         * */
        public actual fun isInitialized(): Boolean = NATIVE_LIBRARY_PATH != null

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
        public actual fun find(libName: String): File? {
            val lib = NATIVE_LIBRARY_PATH
                ?.toFile()
                ?.resolve(libName)
                ?: return null

            if (!lib.exists()) return null

            @OptIn(ExperimentalForeignApi::class)
            memScoped {
                val stat = alloc<stat>()
                if (stat(lib.path, stat.ptr) != 0) return null
                val mode = stat.st_mode.toInt()
                if ((mode and S_IFMT) != S_IFREG) return null
            }

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
        public actual fun require(libName: String): File = find(libName)
            ?: throw IllegalStateException("Failed to find lib[$libName]")

        /**
         * For `kmp-tor-resource` usage when Android Runtime is detected and
         * [isInitialized] is `false`.
         * */
        public actual fun errorMsg(): String = commonErrorMsg()
    }
}

private inline val NATIVE_LIBRARY_PATH: String? get() {
    @OptIn(ExperimentalForeignApi::class)
    return getenv(ENV_KEY_NATIVE_LIBRARY_DIR)?.toKString()?.ifBlank { null }
}
