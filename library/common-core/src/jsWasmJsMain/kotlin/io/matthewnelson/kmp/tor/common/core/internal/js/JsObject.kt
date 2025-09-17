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
@file:Suppress("NOTHING_TO_INLINE")

package io.matthewnelson.kmp.tor.common.core.internal.js

import kotlin.js.JsName

@JsName("Object")
internal external class JsObject

internal expect fun jsObject(): JsObject

internal inline fun JsObject.getIntOrNull(key: String): Int? = jsObjectGetIntOrNull(this, key)
internal expect fun jsObjectGetIntOrNull(obj: JsObject, key: String): Int?
internal inline fun JsObject.getStringOrNull(key: String): String? = jsObjectGetStringOrNull(this, key)
internal expect fun jsObjectGetStringOrNull(obj: JsObject, key: String): String?

internal inline operator fun JsObject.set(key: String, value: JsArray) { jsObjectSet(this, key, value) }
internal expect fun jsObjectSet(obj: JsObject, key: String, value: JsArray)
internal inline operator fun JsObject.set(key: String, value: Boolean) { jsObjectSet(this, key, value) }
internal expect fun jsObjectSet(obj: JsObject, key: String, value: Boolean)
internal inline operator fun JsObject.set(key: String, value: Int) { jsObjectSet(this, key, value) }
internal expect fun jsObjectSet(obj: JsObject, key: String, value: Int)
internal inline operator fun JsObject.set(key: String, value: String) { jsObjectSet(this, key, value) }
internal expect fun jsObjectSet(obj: JsObject, key: String, value: String)
