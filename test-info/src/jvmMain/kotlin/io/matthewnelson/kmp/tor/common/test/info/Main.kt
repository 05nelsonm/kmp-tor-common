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
import io.matthewnelson.kmp.tor.common.core.OSArch
import io.matthewnelson.kmp.tor.common.core.OSHost
import io.matthewnelson.kmp.tor.common.core.OSInfo

internal fun main(array: Array<out String>) {
    check(array.size == 2) { "Invalid arguments. 2 expected." }
    assertInfo(array[0], array[1])
    println("PASS")
}

@OptIn(InternalKmpTorApi::class)
@Throws(IllegalArgumentException::class)
private fun assertInfo(expectedHost: String, expectedArch: String) {
    val host = OSInfo.INSTANCE.osHost
    val arch = OSInfo.INSTANCE.osArch

    check(host !is OSHost.Unknown) { "Unknown OSHost[$host]" }
    check(arch !is OSArch.Unsupported) { "Unsupported OSArch[$arch]" }

    require(expectedHost == host.toString()) { "expectedHost[$expectedHost] != actual[$host]" }
    require(expectedArch == arch.toString()) { "expectedArch[$expectedArch] != actual[$arch]" }
}
