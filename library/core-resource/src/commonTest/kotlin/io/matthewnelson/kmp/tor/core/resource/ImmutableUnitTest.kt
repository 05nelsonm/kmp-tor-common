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
package io.matthewnelson.kmp.tor.core.resource

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import io.matthewnelson.kmp.tor.core.resource.ImmutableMap.Companion.toImmutableMap
import io.matthewnelson.kmp.tor.core.resource.ImmutableSet.Companion.toImmutableSet
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalKmpTorApi::class)
class ImmutableUnitTest {

    @Test
    fun givenContents_whenTheSame_thenImmutableMapEqualsOther() {
        val map1 = mutableMapOf("this" to "that", "here" to "there")
        val map2 = map1.toMutableMap()
        assertEquals(map1, map2)

        val iMap1 = map1.toImmutableMap()
        val iMap2 = map2.toImmutableMap()
        assertEquals(iMap1, iMap2)
        assertEquals(iMap1.toString(), iMap2.toString())
        assertEquals(iMap1.hashCode(), iMap2.hashCode())

        assertEquals(map1, iMap1)
        assertEquals(map1.toString(), iMap1.toString())
        assertEquals(map1.hashCode(), iMap1.hashCode())
    }

    @Test
    fun givenContents_whenTheSame_thenImmutableSetEqualsOther() {
        val set1 = mutableSetOf("this", "that", "here", "there")
        val set2 = set1.toMutableSet()
        assertEquals(set1, set2)

        val iSet1 = set1.toImmutableSet()
        val iSet2 = set2.toImmutableSet()
        assertEquals(iSet1, iSet2)
        assertEquals(iSet1.toString(), iSet2.toString())
        assertEquals(iSet1.hashCode(), iSet2.hashCode())

        assertEquals(set1, iSet1)
        assertEquals(set1.toString(), iSet1.toString())
        assertEquals(set1.hashCode(), iSet1.hashCode())
    }
}
