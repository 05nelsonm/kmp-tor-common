/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
plugins {
    id("configuration")
}

private val testConfig = TestConfigInject()

kmpConfiguration {
    configureShared(java9ModuleName = "io.matthewnelson.kmp.tor.common.core", publish = true) {
        common {
            sourceSetMain {
                dependencies {
                    api(project(":library:common-api"))
                    // TODO: Switch to implementation
                    api(libs.immutable.collections)
                }
            }

            sourceSetTest {
                kotlin.srcDir(testConfig.testConfigSrcDir)

                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.encoding.base16)
                    implementation(kotlincrypto.hash.sha2)
                }
            }
        }

        kotlin {
            sourceSets.findByName("jsWasmJsTest")?.dependencies {
                implementation(npm("kmp-tor-core-test-resources", "2.0.0"))
            }
        }

        kotlin {
            with(sourceSets) {
                val sets = arrayOf("jsWasmJs", "jvm").mapNotNull { name ->
                    val main = findByName(name + "Main") ?: return@mapNotNull null
                    main to getByName(name + "Test")
                }
                if (sets.isEmpty()) return@kotlin

                val main = maybeCreate("nonNativeMain").apply { dependsOn(getByName("commonMain")) }
                val test = maybeCreate("nonNativeTest").apply { dependsOn(getByName("commonTest")) }
                sets.forEach { (m, t) -> m.dependsOn(main); t.dependsOn(test) }
            }
        }

        kotlin {
            with(sourceSets) {
                findByName("nativeMain")?.apply {
                    dependencies {
                        implementation(libs.encoding.base16)
                        implementation(libs.encoding.base64)
                        implementation(libs.kotlinx.atomicfu)
                        implementation(kotlincrypto.hash.sha2)
                    }
                }
            }
        }
    }
}

private class TestConfigInject {
    // Inject project directory path for tests
    val testConfigSrcDir: File by lazy {
        val kotlinSrcDir = layout
            .buildDirectory
            .get()
            .asFile
            .resolve("generated")
            .resolve("sources")
            .resolve("testConfig")
            .resolve("commonTest")
            .resolve("kotlin")

        val core = kotlinSrcDir
            .resolve("io")
            .resolve("matthewnelson")
            .resolve("kmp")
            .resolve("tor")
            .resolve("common")
            .resolve("core")

        core.mkdirs()

        core.resolve("TestConfig.kt").writeText("""
            package io.matthewnelson.kmp.tor.common.core

            // e.g. linux-libc,x86_64
            internal const val ASSERT_HOST_OSINFO: String = "${properties["ASSERT_HOST_OSINFO"] ?: ""}"
            internal const val PROJECT_DIR_PATH: String = "${projectDir.canonicalPath.replace("\\", "\\\\")}"

        """.trimIndent())

        kotlinSrcDir
    }
}
