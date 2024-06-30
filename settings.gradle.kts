pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "champion"
include(":app")
include(":core:model")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:data")
include(":core:design")
include(":feature:home")
include(":feature:champion")
include(":feature:item")
include(":feature:setting")
include(":core:domain")
include(":feature:spell")
include(":feature:game")
