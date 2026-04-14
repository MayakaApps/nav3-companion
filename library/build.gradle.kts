plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.mayakapps"
version = "0.1.0"

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    applyDefaultHierarchyTemplate()

    jvmToolchain(21)

    jvm()

    android {
        namespace = "com.mayakapps.nav3companion"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.androidx.navigation3.ui)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val jvmMain by getting
        val iosMain by getting
        create("nonAndroidMain") {
            dependsOn(commonMain)
            jvmMain.dependsOn(this)
            iosMain.dependsOn(this)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "nav3-companion", version.toString())

    pom {
        name = "Navigation3 Companion"
        description =
            "A KMP library that provides utilities to work with Navigation3 in a more convenient way."
        inceptionYear = "2026"
        url = "https://github.com/MayakaApps/nav3-companion"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "MayakaApps"
                name = "MayakaApps"
                url = "https://github.com/MayakaApps"
            }
        }
        scm {
            url = "https://github.com/MayakaApps/nav3-companion"
            connection = "scm:git:https://github.com/MayakaApps/nav3-companion.git"
            developerConnection = "scm:git:ssh://git@github.com/MayakaApps/nav3-companion.git"
        }
    }
}
