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
package io.matthewnelson.kmp.tor.common.test.info

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.OSInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

@OptIn(InternalKmpTorApi::class)
class MainUnitTest {

    @Test
    fun givenMain_whenNot2Arguments_thenThrowsException() {
        assertFailsWith<IllegalStateException> { main(emptyArray()) }
    }

    @Test
    fun givenMain_whenHostNotExpected_thenThrowsException() {
        try {
            main(arrayOf("invalid", OSInfo.INSTANCE.osArch.toString()))
            fail("main did not throw exception...")
        } catch (e: IllegalArgumentException) {
            assertEquals(true, e.message?.contains("expectedHost[invalid]"))
        }
    }

    @Test
    fun givenMain_whenArchNotExpected_thenThrowsException() {
        try {
            main(arrayOf(OSInfo.INSTANCE.osHost.toString(), "invalid"))
            fail("main did not throw exception...")
        } catch (e: IllegalArgumentException) {
            assertEquals(true, e.message?.contains("expectedArch[invalid]"))
        }
    }
}
