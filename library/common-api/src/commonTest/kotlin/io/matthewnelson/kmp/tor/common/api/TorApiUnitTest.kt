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

import kotlin.test.*

class TorApiUnitTest {

    private class TestApi(
        private val runMain: (args: Array<String>, log: Logger) -> Int = { _, _ -> 0 },
    ): TorApi() {
        override fun torRunMainProtected(args: Array<String>, log: Logger): Int {
            return runMain(args, log)
        }

        fun err(log: Logger, msg: String) { log.notifyErr(msg) }
    }

    @Test
    fun givenTorRunMain_whenRunning_thenReturns0() {
        var invocationRunMain = 0

        var api: TestApi? = null
        api = TestApi { _, _ ->
            invocationRunMain++
            assertEquals(-1, api!!.torRunMain(emptyList()))
            assertTrue(api!!.isRunning)
            0
        }

        assertEquals(0, api.torRunMain(emptyList()))
        assertEquals(1, invocationRunMain)
        assertFalse(api.isRunning)
    }

    @Test
    fun givenTorRunMain_whenArgsCreated_thenArgv0IsTor() {
        var args: Array<String>? = null
        val api = TestApi { a, _ -> args = a; 0 }

        api.torRunMain(emptyList())
        assertContentEquals(arrayOf("tor"), args)

        api.torRunMain(listOf("something"))
        assertContentEquals(arrayOf("tor", "something"), args)
    }

    @Test
    fun givenLogger_whenNotifyErr_thenIsFormattedProperly() {
        val api = TestApi()
        var output: String? = null
        val log = TorApi.Logger { output = it; println(it) }

        // blanks are ignored
        api.err(log, msg = "     ")
        assertNull(output)

        api.err(log, msg = "something")
        assertEquals("[err] something\n", output)

        // To ensure that whitespace is trimmed
        val blankLine = "    "
        val trailingSpaces = "multi-line     "

        api.err(log, msg = """
            some
            $blankLine
            $trailingSpaces
            output  
        """.trimIndent())
        assertEquals(
            expected = """
                [err] some
                [err] ${trailingSpaces.trim()}
                [err] output

            """.trimIndent(),
            actual = output
        )
    }
}
