plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.trusttheroute.app"
    compileSdk = 34

        defaultConfig {
            applicationId = "com.trusttheroute.app"
            minSdk = 26  // Android 8.0 (Oreo)
            targetSdk = 34
            versionCode = 2
            versionName = "1.0.1"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
            
            // Временно отключаем проверку совместимости с 16 KB для разработки
            // Yandex MapKit 4.5.1 не поддерживает 16 KB страницы
            ndk {
                abiFilters += listOf("x86_64", "armeabi-v7a", "arm64-v8a")
            }
        }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    // KAPT configuration for Java 17+ compatibility
    kapt {
        javacOptions {
            option("--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
            option("--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        // Временное решение для поддержки 16 KB страниц
        // Yandex MapKit 4.5.1 не поддерживает 16 KB страницы
        // Используем legacy packaging для обхода проблемы
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-common:1.2.0")

    // Yandex MapKit
    // Версия 4.5.1 не поддерживает 16 KB страницы
    // Временно используем эту версию для разработки
    // TODO: Обновить до версии с поддержкой 16 KB когда будет доступна
    implementation("com.yandex.android:maps.mobile:4.5.1-full")

    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coil (Image loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptTest("com.google.dagger:hilt-android-compiler:2.48")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
