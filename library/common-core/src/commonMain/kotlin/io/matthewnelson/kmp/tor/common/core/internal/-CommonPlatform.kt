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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.Resource

@Suppress("NOTHING_TO_INLINE")
internal inline fun StringBuilder.appendIndent(
    value: Any,
    indent: String = "    "
): StringBuilder = append(indent).append(value)

@Suppress("NOTHING_TO_INLINE")
internal inline fun String.toFinalFileName(): String = substringAfterLast('/')
    .substringBeforeLast(".gz")

@Throws(Throwable::class)
@OptIn(InternalKmpTorApi::class)
internal expect fun Resource.extractTo(destinationDir: File, onlyIfDoesNotExist: Boolean): File
