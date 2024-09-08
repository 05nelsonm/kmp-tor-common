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

import io.matthewnelson.kmp.file.SysTempDir
import io.matthewnelson.kmp.file.readUtf8
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.tor.common.api.annotation.InternalKmpTorApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalKmpTorApi::class)
class JvmResourceUnitTest {

    @Test
    fun givenJsResource_whenGzipped_thenExtractsSuccessfully() {
        val alias = "hello_gzip"
        val config = Resource.Config.create {
            resource(alias) {
                isExecutable = false
                platform {
                    resourceClass = JvmResourceUnitTest::class.java
                    resourcePath = "/io/matthewnelson/kmp/tor/common/core/hello_world.gz"
                }
            }
        }

        val dir = SysTempDir.resolve(randomName())
        val dir2 = dir.resolve(randomName())
        val file = config.extractTo(dir2, onlyIfDoesNotExist = false)[alias]!!

        try {
            assertEquals("hello_world", file.name)
            assertEquals("Hello World!", file.readUtf8().trim())
        } finally {
            file.delete()
            dir2.delete()
            dir.delete()
        }
    }

    @Test
    fun givenJsResource_whenNotGzipped_thenExtractsSuccessfully() {
        val alias = "hello"
        val config = Resource.Config.create {
            resource(alias) {
                isExecutable = false
                platform {
                    resourceClass = JvmResourceUnitTest::class.java
                    resourcePath = "/io/matthewnelson/kmp/tor/common/core/hello_world"
                }
            }
        }

        val dir = SysTempDir.resolve(randomName())
        val dir2 = dir.resolve(randomName())
        val file = config.extractTo(dir2, onlyIfDoesNotExist = false)[alias]!!

        try {
            assertEquals("hello_world", file.name)
            assertEquals("Hello World!", file.readUtf8().trim())
        } finally {
            file.delete()
            dir2.delete()
            dir.delete()
        }
    }

}
