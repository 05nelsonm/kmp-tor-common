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
package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalKmpTorApi::class)
abstract class OSInfoBaseTest {

    @Test
    fun givenHost_whenCheckOSInfo_thenMatchesExpected() {
        if (ASSERT_HOST_OSINFO.isBlank()) {
            println("Skipping...")
            return
        }

        val splits = ASSERT_HOST_OSINFO.split(',')

        assertEquals(2, splits.size, "Invalid ASSERT_HOST_OSINFO. Try \"{host},{arch}\" (e.g. \"linux-libc,x86_64\"")

        assertEquals(OSInfo.INSTANCE.osHost.toString(), splits[0])
        assertEquals(OSInfo.INSTANCE.osArch.toString(), splits[1])
    }

    private companion object {
        init {
            println("OS_HOST: ${OSInfo.INSTANCE.osHost}")
            println("OS_ARCH: ${OSInfo.INSTANCE.osArch}")
        }
    }
}
