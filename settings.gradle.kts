pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Configure build cache settings
buildCache {
    local {
        directory = File(rootDir, "build-cache")
    }
}

rootProject.name = "NotifBot"
include(":android")
