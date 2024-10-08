/*
 * Copyright (c) 2023 Matthew Nelson
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

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.api.KmpTorDsl
import io.matthewnelson.kmp.tor.common.core.internal.toFinalFileName

@InternalKmpTorApi
public actual class PlatformResource private constructor(
    internal val moduleName: String,
    internal val resourcePath: String,
) {

    public actual val fsFileName: String = resourcePath.toFinalFileName()
    public actual val isGzipped: Boolean = resourcePath.endsWith(".gz")

    @KmpTorDsl
    @InternalKmpTorApi
    public actual class Builder internal actual constructor() {

        public var moduleName: String? = null
        public var resourcePath: String? = null

        internal actual fun build(): PlatformResource? {
            val name = moduleName ?: return null
            var path = resourcePath ?: return null

            if (!path.startsWith('/')) {
                path = "/$path"
            }

            return PlatformResource(name, path)
        }
    }

    actual override fun equals(other: Any?): Boolean {
        return  other is PlatformResource
                && other.moduleName == moduleName
                && other.resourcePath == resourcePath
    }

    actual override fun hashCode(): Int {
        var result = 17
        result = result * 31 + moduleName.hashCode()
        result = result * 31 + resourcePath.hashCode()
        return result
    }

    actual override fun toString(): String = buildString {
        appendLine("PlatformResource: [")
        append("    moduleName: ")
        appendLine(moduleName)
        append("    resourcePath: ")
        appendLine(resourcePath)
        append(']')
    }
}
