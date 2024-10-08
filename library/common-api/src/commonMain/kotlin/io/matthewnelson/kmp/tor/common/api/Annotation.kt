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

/**
 * Denotes an api as experimental and subject to change at any time.
 *
 * Should probably not use it for anything other than playing around
 * with.
 *
 * Any usage of a declaration annotated with [ExperimentalKmpTorApi]
 * must be accepted by annotating that usage with the [OptIn]
 * annotation, e.g. @OptIn(ExperimentalKmpTorApi::class), or by using
 * the following compiler argument:
 *
 *   -Xopt-in=io.matthewnelson.kmp.tor.common.api.ExperimentalKmpTorApi
 * */
@RequiresOptIn
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS,
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalKmpTorApi

/**
 * Denotes an api as internal and subject to change at any time.
 *
 * Any usage of a declaration annotated with [InternalKmpTorApi]
 * must be accepted by annotating that usage with the [OptIn]
 * annotation, e.g. @OptIn(InternalKmpTorApi::class), or by using
 * the following compiler argument:
 *
 *   -Xopt-in=io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
 * */
@RequiresOptIn
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS,
)
@Retention(AnnotationRetention.BINARY)
public annotation class InternalKmpTorApi

/**
 * A marker annotations for DSLs.
 */
@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
)
public annotation class KmpTorDsl
