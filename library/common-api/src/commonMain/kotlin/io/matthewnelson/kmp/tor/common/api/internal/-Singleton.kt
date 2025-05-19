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
package io.matthewnelson.kmp.tor.common.api.internal

import kotlin.concurrent.Volatile
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Internal reminder to not do stupid things
 * */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn("Use Singleton.getOrCreate extension")
internal annotation class UseGetOrCreate

internal open class Singleton<T: Any> internal constructor() {

    @Volatile
    @UseGetOrCreate
    internal var instance: T? = null
    @UseGetOrCreate
    internal val lock = newLock()
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T: Any> Singleton<T>.getOrCreate(create: () -> T): T {
    contract { callsInPlace(create, InvocationKind.AT_MOST_ONCE) }

    @OptIn(UseGetOrCreate::class)
    return instance ?: lock.withLock {
        instance ?: create().also { instance = it }
    }
}
