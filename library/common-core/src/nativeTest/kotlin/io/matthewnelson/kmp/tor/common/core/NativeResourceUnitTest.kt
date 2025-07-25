/*
 * Copyright (c) 2023 Matthew Nelson
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

import io.matthewnelson.kmp.file.*
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.test.*

@OptIn(InternalKmpTorApi::class)
class NativeResourceUnitTest {

    @Test
    fun givenNativeResource_whenRead_thenPassesValidation() {
        var invocations = 0L

        // read will throw if size and sha256 are invalid
        resource_lorem_ipsum.read { _, _ -> invocations++ }

        // Ensure that there was something going on
        assertEquals(resource_lorem_ipsum.chunks, invocations)
    }

    @Test
    fun givenNativeResource_whenSha256_thenIsSameAsFile() {
        // Do not run this test for Windows because the actual file
        // will be read differently b/c it is special (EOL character)
        if (isWindows) return

        val loremIpsumFile = TEST_SUPPORT_DIR.resolve(resource_lorem_ipsum.name)

        assertEquals(resource_lorem_ipsum.sha256, loremIpsumFile.readBytes().sha256())
    }

    @Test
    fun givenNativeResource_whenGzipped_thenExtractsSuccessfully() {
        val alias = "gzip_test"

        val config = Resource.Config.create {
            resource(alias) {
                mode("400")
                platform { nativeResource = resource_LoremIpsum_gz }
            }
        }

        assertEquals("LoremIpsum", config[alias].platform.fsFileName)
        assertTrue(config[alias].platform.isGzipped)

        val dir = SysTempDir.resolve(randomName())
        val file = config.extractTo(dir, onlyIfDoesNotExist = false)[alias]!!

        try {
            // Check that the gz file was cleaned up
            assertFalse("${file.path}.gz".toFile().exists2())

            val actual = file.readBytes()
            // There was decompression happening
            assertTrue(actual.size > resource_LoremIpsum_gz.size, "actual.size <= resource.size")
            assertEquals(resource_lorem_ipsum.sha256, actual.sha256(), "sha256 hashes do not match")
        } finally {
            file.delete2(ignoreReadOnly = true)
            dir.delete2(ignoreReadOnly = true)
        }
    }

    @Test
    fun givenNativeResource_whenNotGzipped_thenExtractsSuccessfully() {
        val alias = "test"

        val config = Resource.Config.create {
            resource(alias) {
                mode("400")
                platform { nativeResource = resource_lorem_ipsum }
            }
        }

        assertEquals("lorem_ipsum", config[alias].platform.fsFileName)
        assertFalse(config[alias].platform.isGzipped)

        val dir = SysTempDir.resolve(randomName())
        val dir2 = dir.resolve(randomName())
        val file = config.extractTo(dir2, onlyIfDoesNotExist = false)[alias]!!

        try {
            assertEquals(resource_lorem_ipsum.sha256, file.readBytes().sha256())
        } finally {
            file.delete2(ignoreReadOnly = true)
            dir2.delete2(ignoreReadOnly = true)
            dir.delete2(ignoreReadOnly = true)
        }
    }
}
