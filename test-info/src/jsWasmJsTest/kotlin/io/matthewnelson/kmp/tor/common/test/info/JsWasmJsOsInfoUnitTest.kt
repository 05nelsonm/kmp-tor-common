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

import io.matthewnelson.kmp.file.SysFsInfo
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.OSArch
import io.matthewnelson.kmp.tor.common.core.OSHost
import io.matthewnelson.kmp.tor.common.core.OSInfo
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertIsNot

/**
 * This is primarily to ensure that things do not crash if using Js/Browser...
 * */
@OptIn(InternalKmpTorApi::class)
class JsWasmJsOsInfoUnitTest {

    @Test
    fun givenOsInfo_whenIsJsBrowser_thenHostAndArchAreUnknown() {
        if (SysFsInfo.name != "FsJsBrowser") {
            println("Skipping...")
            return
        }

        assertIs<OSHost.Unknown>(OSInfo.INSTANCE.osHost)
        assertIs<OSArch.Unsupported>(OSInfo.INSTANCE.osArch)
    }

    @Test
    fun givenOsInfo_whenIsJsNode_thenHostAndArchAreNOTUnknown() {
        if (SysFsInfo.name != "FsJsNode") {
            println("Skipping...")
            return
        }

        assertIsNot<OSHost.Unknown>(OSInfo.INSTANCE.osHost)
        assertIsNot<OSArch.Unsupported>(OSInfo.INSTANCE.osArch)
    }
}
