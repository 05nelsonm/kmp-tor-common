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
import kotlin.test.*

@OptIn(InternalKmpTorApi::class)
class ImmutableUnitTest {

    @Test
    fun givenContents_whenTheSame_thenImmutableListEqualsOther() {
        val list1 = mutableListOf("this", "that", "here", "there")
        val list2 = list1.toMutableList()
        assertEquals(list1, list2)

        val iList1 = list1.toImmutableList()
        val iList2 = list2.toImmutableList()
        assertEquals("ImmutableList", iList1::class.simpleName)
        assertNotEquals("ImmutableList", emptyList<String>().toImmutableList()::class.simpleName)
        assertNotEquals("ImmutableList", immutableListOf<String>()::class.simpleName)
        assertEquals("ImmutableIterator", iList1.iterator()::class.simpleName)
        assertEquals("ImmutableListIterator", iList1.listIterator()::class.simpleName)
        assertEquals("ImmutableListIterator", iList1.listIterator(1)::class.simpleName)

        assertEquals(iList1, iList2)
        assertEquals(iList1.toString(), iList2.toString())
        assertEquals(iList1.hashCode(), iList2.hashCode())

        assertEquals(list1, iList1)
        assertEquals(list1.toString(), iList1.toString())
        assertEquals(list1.hashCode(), iList1.hashCode())
    }

    @Test
    fun givenContents_whenTheSame_thenImmutableMapEqualsOther() {
        val map1 = mutableMapOf("this" to "that", "here" to "there")
        val map2 = map1.toMutableMap()
        assertEquals(map1, map2)
        assertEquals(map1.entries, map2.entries)
        assertEquals(map1.keys, map2.keys)
        assertNotEquals(map1.values, map2.values)

        val iMap1 = map1.toImmutableMap()
        val iMap2 = map2.toImmutableMap()
        assertEquals("ImmutableMap", iMap1::class.simpleName)
        assertNotEquals("ImmutableMap", emptyMap<String, String>().toImmutableMap()::class.simpleName)
        assertNotEquals("ImmutableMap", immutableMapOf<String, String>()::class.simpleName)
        assertEquals("ImmutableSet", iMap1.keys::class.simpleName)
        assertEquals("ImmutableSet", iMap1.entries::class.simpleName)
        assertEquals("ImmutableIterator", iMap1.values.iterator()::class.simpleName)

        assertEquals(iMap1, iMap2)
        assertEquals(iMap1.entries, iMap2.entries)
        assertEquals(iMap1.keys, iMap2.keys)
        assertNotEquals(iMap1.values, iMap2.values)
        assertContentEquals(iMap1.values, iMap2.values)
        assertEquals(iMap1.toString(), iMap2.toString())
        assertEquals(iMap1.hashCode(), iMap2.hashCode())

        assertEquals(map1, iMap1)
        assertEquals(map1.entries, iMap1.entries)
        assertEquals(map1.keys, iMap1.keys)
        assertNotEquals(map1.values, iMap1.values)
        assertContentEquals(map1.values, iMap1.values)
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
        assertEquals("ImmutableSet", iSet1::class.simpleName)
        assertNotEquals("ImmutableSet", emptySet<String>().toImmutableSet()::class.simpleName)
        assertNotEquals("ImmutableSet", immutableSetOf<String>()::class.simpleName)
        assertEquals("ImmutableIterator", iSet1.iterator()::class.simpleName)

        assertEquals(iSet1, iSet2)
        assertEquals(iSet1.toString(), iSet2.toString())
        assertEquals(iSet1.hashCode(), iSet2.hashCode())

        assertEquals(set1, iSet1)
        assertEquals(set1.toString(), iSet1.toString())
        assertEquals(set1.hashCode(), iSet1.hashCode())
    }
}
