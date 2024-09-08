rootProject.name = "kmp-tor-common"

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
        "common-api",
        "common-lib-locator",
        "common-core",
        "common-test",
    ).forEach { module ->
        include(":library:$module")
    }
}
