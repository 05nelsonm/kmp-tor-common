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

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlinx.atomicfu.locks.withLock as _withLock

@InternalKmpTorApi
public actual typealias SynchronizedObject = kotlinx.atomicfu.locks.SynchronizedObject

@InternalKmpTorApi
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public actual inline fun synchronizedObject(): SynchronizedObject = SynchronizedObject()

@PublishedApi
@OptIn(InternalKmpTorApi::class)
internal actual inline fun <T: Any?> synchronizedImpl(
    lock: SynchronizedObject,
    block: () -> T
): T = lock._withLock(block)
