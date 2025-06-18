
import java.util.Properties
import java.io.FileInputStream

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "Mapbox"
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                // Read the token from local.properties
                password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                    ?: throw GradleException("MAPBOX_DOWNLOADS_TOKEN not found in local.properties file.")
            }
        }
    }
}

rootProject.name = "PRM392_GROUP2_Skincare_Mobile"
include(":app")