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

/**
 * Utility for obtaining absolute path to native libraries within an
 * application's `nativeLibraryDir`.
 *
 * @see [Companion.isInitialized]
 * @see [Companion.find]
 * @see [Companion.require]
 * */
public expect class KmpTorLibLocator private constructor() {

    public companion object {

        /**
         * If the instance of [KmpTorLibLocator] has been initialized yet.
         * */
        public fun isInitialized(): Boolean

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
        public fun find(libName: String): File?

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
        public fun require(libName: String): File

        /**
         * For `kmp-tor-resource` usage when Android Runtime is detected and
         * [isInitialized] is `false`.
         * */
        public fun errorMsg(): String
    }
}
