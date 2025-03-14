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
@file:JvmName("SynchronizedCommon")
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@InternalKmpTorApi
public expect open class SynchronizedObject
@Deprecated("Use synchronizedObject()", ReplaceWith("synchronizedObject()"))
public constructor()

@InternalKmpTorApi
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public expect inline fun synchronizedObject(): SynchronizedObject

@PublishedApi
@OptIn(InternalKmpTorApi::class)
internal expect inline fun <T: Any?> synchronizedImpl(
    lock: SynchronizedObject,
    block: () -> T
): T

@InternalKmpTorApi
@OptIn(ExperimentalContracts::class)
@Suppress("WRONG_INVOCATION_KIND", "LEAKED_IN_PLACE_LAMBDA")
public inline fun <T: Any?> synchronized(
    lock: SynchronizedObject,
    block: () -> T
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return synchronizedImpl(lock, block)
}
