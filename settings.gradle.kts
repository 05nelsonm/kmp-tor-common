rootProject.name = "kmp-tor-core"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings

if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    listOf(
        "core-api",
        "core-lib-locator",
        "core-resource",
        "core-resource-initializer",
        "core-test",
    ).forEach { module ->
        include(":library:$module")
    }
}
