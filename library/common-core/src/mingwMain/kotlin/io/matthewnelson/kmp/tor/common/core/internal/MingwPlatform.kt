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
package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.path
import kotlinx.cinterop.ExperimentalForeignApi
import platform.zlib.gzFile
import platform.zlib.gzopen

@ExperimentalForeignApi
@Suppress("NOTHING_TO_INLINE")
internal actual inline fun File.gzopenRO(): gzFile? = gzopen(path, "rb")
