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
package io.matthewnelson.kmp.tor.common.lib.locator.internal

import io.matthewnelson.kmp.tor.common.lib.locator.KmpTorLibLocator

internal const val ENV_KEY_NATIVE_LIBRARY_DIR: String = "io.matthewnelson.kmp.tor.common.NATIVE_LIBRARY_DIR"

@Suppress("NOTHING_TO_INLINE")
internal inline fun KmpTorLibLocator.Companion.commonErrorMsg(): String {
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
