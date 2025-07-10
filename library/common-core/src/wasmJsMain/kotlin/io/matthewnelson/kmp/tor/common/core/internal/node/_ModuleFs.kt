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

package io.matthewnelson.kmp.tor.common.core.internal.node

import io.matthewnelson.kmp.file.DelicateFileApi
import io.matthewnelson.kmp.file.jsExternTryCatch
import io.matthewnelson.kmp.tor.common.core.internal.js.JsObject

/** [docs](https://nodejs.org/api/fs.html) */
internal actual external interface ModuleFs {
    fun readdirSync(path: String, options: JsObject): JsArray<JsAny?>?
}

// @Throws(Throwable::class)
internal actual fun ModuleFs.readDir(path: String, options: JsObject): List<String?>? {
    @OptIn(DelicateFileApi::class)
    val entries = jsExternTryCatch { readdirSync(path, options) } ?: return null
    val list = ArrayList<String?>(entries.length)
    for (i in 0 until entries.length) {
        val entry = entries[i]
        list.add(entry?.toString())
    }
    return list
}
