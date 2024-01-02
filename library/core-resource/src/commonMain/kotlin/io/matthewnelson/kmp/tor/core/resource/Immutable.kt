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
import io.matthewnelson.kmp.tor.core.resource.ImmutableSet.Companion.wrapImmutableSet
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

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

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = delegate.subList(fromIndex, toIndex).wrapImmutableList()
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
        @JvmName("get")
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

        @JvmSynthetic
        internal fun <T> Collection<T>.wrapImmutableList(): List<T> {
            if (isEmpty()) return emptyList()
            if (this is ImmutableList<T>) return this
            if (this is List<T>) return ImmutableList(this)
            return ImmutableList(toList())
        }
    }
}

@InternalKmpTorApi
public class ImmutableMap<K, V> private constructor(
    private val delegate: Map<K, V>,
): Map<K, V> {

    private class Entry<K, V>(
        private val delegate: Map.Entry<K, V>,
    ): Map.Entry<K, V> {
        override val key: K get() = delegate.key
        override val value: V get() = delegate.value

        override fun equals(other: Any?): Boolean = delegate == other
        override fun hashCode(): Int = delegate.hashCode()
        override fun toString(): String = delegate.toString()
    }

    private class Values<V>(
        private val delegate: Collection<V>,
    ): Collection<V> {
        override val size: Int get() = delegate.size
        override fun isEmpty(): Boolean = delegate.isEmpty()
        override fun iterator(): Iterator<V> = delegate.iterator()
        override fun containsAll(elements: Collection<V>): Boolean = delegate.containsAll(elements)
        override fun contains(element: V): Boolean = delegate.contains(element)

        override fun equals(other: Any?): Boolean = delegate == other
        override fun hashCode(): Int = delegate.hashCode()
        override fun toString(): String = delegate.toString()
    }

    override val entries: Set<Map.Entry<K, V>> get() {
        val entries = delegate.entries
        val set = HashSet<Entry<K, V>>(entries.size, 1.0F)
        entries.mapTo(set) { Entry(it) }
        return set.wrapImmutableSet()
    }
    override val keys: Set<K> get() = delegate.keys.wrapImmutableSet()
    override val size: Int get() = delegate.size
    override val values: Collection<V> get() = Values(delegate.values)
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
        @JvmName("get")
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

        @JvmSynthetic
        internal fun <K, V> Map<K, V>.wrapImmutableMap(): Map<K, V> {
            if (isEmpty()) return emptyMap()
            if (this is ImmutableMap<K, V>) return this
            return ImmutableMap(this)
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
        @JvmName("get")
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

        @JvmSynthetic
        internal fun <T> Collection<T>.wrapImmutableSet(): Set<T> {
            if (isEmpty()) return emptySet()
            if (this is ImmutableSet<T>) return this
            if (this is Set<T>) return ImmutableSet(this)
            return ImmutableSet(toSet())
        }
    }
}
