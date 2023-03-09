plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("maven-publish")

}

group = "dk.biscon.redux"
version = "1.0.0"

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 26
        targetSdk = 32
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "REDUX_VERSION",
            "\"1.0.1\""
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    namespace = "dk.biscon.redux"
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        //withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    android {
        publishLibraryVariants("debug", "release")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
                implementation("junit:junit:4.12")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                //implementation(compose.runtime)
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting

        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.8.0")
                val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
                implementation(composeBom)
                implementation("androidx.compose.runtime:runtime")
                implementation("androidx.arch.core:core-common:2.2.0")
                implementation("androidx.arch.core:core-runtime:2.2.0")
                implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.5.1")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
                implementation(composeBom)
                implementation("androidx.compose.ui:ui-test-manifest")
                implementation("androidx.compose.ui:ui-test-junit4")
            }
        }
    }
}
