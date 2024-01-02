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
package io.matthewnelson.kmp.tor.core.resource

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import io.matthewnelson.kmp.tor.core.resource.ImmutableList.Companion.toImmutableList
import io.matthewnelson.kmp.tor.core.resource.ImmutableSet.Companion.toImmutableSet
import kotlin.jvm.JvmStatic

@InternalKmpTorApi
public class ImmutableList<T> private constructor(
    private val delegate: List<T>,
): List<T> {

    override val size: Int get() = delegate.size
    override operator fun get(index: Int): T = delegate[index]
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): Iterator<T> = delegate.iterator()
    override fun listIterator(): ListIterator<T> = delegate.listIterator()
    override fun listIterator(index: Int): ListIterator<T> = delegate.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = ImmutableList(delegate.subList(fromIndex, toIndex))
    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)
    override fun indexOf(element: T): Int = delegate.indexOf(element)
    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)
    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun equals(other: Any?): Boolean = delegate == other
    override fun hashCode(): Int = delegate.hashCode()
    override fun toString(): String = delegate.toString()

    @InternalKmpTorApi
    public companion object {

        @JvmStatic
        public fun <T> Collection<T>.toImmutableList(): List<T> {
            if (isEmpty()) return emptyList()
            if (this is ImmutableList<T>) return this
            return ImmutableList(toList())
        }

        @JvmStatic
        public fun <T> of(vararg elements: T): List<T> {
            if (elements.isEmpty()) return emptyList()
            return ImmutableList(elements.toList())
        }
    }
}

@InternalKmpTorApi
public class ImmutableMap<K, V> private constructor(
    private val delegate: Map<K, V>,
): Map<K, V> {

    internal class Entry<K, V>(
        private val delegate: Map.Entry<K, V>,
    ): Map.Entry<K, V> {
        override val key: K get() = delegate.key
        override val value: V get() = delegate.value

        override fun equals(other: Any?): Boolean = delegate == other
        override fun hashCode(): Int = delegate.hashCode()
        override fun toString(): String = delegate.toString()
    }

    override val entries: Set<Map.Entry<K, V>> get() = delegate.entries.map { Entry(it) }.toImmutableSet()
    override val keys: Set<K> get() = delegate.keys.toImmutableSet()
    override val size: Int get() = delegate.size
    override val values: Collection<V> get() = delegate.values.toImmutableList()
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override operator fun get(key: K): V? = delegate[key]
    override fun containsValue(value: V): Boolean = delegate.containsValue(value)
    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun equals(other: Any?): Boolean = delegate == other
    override fun hashCode(): Int = delegate.hashCode()
    override fun toString(): String = delegate.toString()

    @InternalKmpTorApi
    public companion object {

        @JvmStatic
        public fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> {
            if (isEmpty()) return emptyMap()
            if (this is ImmutableMap<K, V>) return this
            return ImmutableMap(toMap())
        }

        @JvmStatic
        public fun <K, V> of(vararg pairs: Pair<K, V>): Map<K, V> {
            if (pairs.isEmpty()) return emptyMap()
            return ImmutableMap(pairs.toMap())
        }
    }
}

@InternalKmpTorApi
public class ImmutableSet<T> private constructor(
    private val delegate: Set<T>,
): Set<T> {

    override val size: Int get() = delegate.size
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): Iterator<T> = delegate.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)
    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun equals(other: Any?): Boolean = delegate == other
    override fun hashCode(): Int = delegate.hashCode()
    override fun toString(): String = delegate.toString()

    @InternalKmpTorApi
    public companion object {

        @JvmStatic
        public fun <T> Collection<T>.toImmutableSet(): Set<T> {
            if (isEmpty()) return emptySet()
            if (this is ImmutableSet<T>) return this
            return ImmutableSet(toSet())
        }

        @JvmStatic
        public fun <T> of(vararg elements: T): Set<T> {
            if (elements.isEmpty()) return emptySet()
            return ImmutableSet(elements.toSet())
        }
    }
}
