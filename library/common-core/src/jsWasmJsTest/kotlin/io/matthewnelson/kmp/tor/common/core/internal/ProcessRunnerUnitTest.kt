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
package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.OSHost
import io.matthewnelson.kmp.tor.common.core.OSInfo
import io.matthewnelson.kmp.tor.common.core.internal.node.node_child_process
import io.matthewnelson.kmp.tor.common.core.internal.node.processRunner
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds

@OptIn(InternalKmpTorApi::class)
class ProcessRunnerUnitTest {

    @Test
    fun givenLinuxHost_whenCheckBitness_thenReturns32Or64() {
        if (OSInfo.INSTANCE.osHost !is OSHost.Linux) {
            println("Skipping...")
            return
        }

        val out = node_child_process
            .processRunner()
            .runAndWait(listOf("file", "/bin/bash"), 250.milliseconds)

        if (!out.contains("ELF")) {
            fail("command 'file /bin/bash' result does not contain ELF...\n\n$out")
        }
        if (!out.contains("-bit")) {
            fail("command 'file /bin/bash' result does not contain -bit...\n\n$out")
        }

        println(out)
    }
}
