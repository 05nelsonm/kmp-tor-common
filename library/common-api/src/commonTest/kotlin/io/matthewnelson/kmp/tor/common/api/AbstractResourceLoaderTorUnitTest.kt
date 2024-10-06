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
package io.matthewnelson.kmp.tor.common.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Because of singleton creation, tests are run on
 * jvmMain and nativeMain.
 * */
abstract class AbstractResourceLoaderTorUnitTest {

    protected abstract fun loader(): ResourceLoader.Tor

    protected object BinderExpected: ResourceLoader.RuntimeBinder
    private object BinderIllegal: ResourceLoader.RuntimeBinder

    @Test
    fun givenAlreadyBound_whenDifferentBinder_thenThrowsException() {
        when (val l = loader()) {
            is ResourceLoader.Tor.Exec -> {
                assertFailsWith<IllegalArgumentException> {
                    l.process(object : ResourceLoader.RuntimeBinder {}) { _, _ -> }
                }

                l.process(BinderExpected) { _, _ -> }

                assertFailsWith<IllegalStateException> {
                    l.process(BinderIllegal) { _, _ -> }
                }
            }
            is ResourceLoader.Tor.NoExec -> {
                assertFailsWith<IllegalArgumentException> {
                    l.withApi(object : ResourceLoader.RuntimeBinder {}) { }
                }

                val expected = l.withApi(BinderExpected) { hashCode() }

                assertFailsWith<IllegalStateException> {
                    l.withApi(BinderIllegal) { }
                }

                val actual = l.withApi(BinderExpected) { hashCode() }

                assertEquals(expected, actual, "TorApi was instantiated multiple times (static reference was not set)")
            }
        }
    }
}
