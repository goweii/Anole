plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = Androids.compileSdkVersion
    buildToolsVersion = Androids.buildToolsVersion

    defaultConfig {
        minSdk = Androids.minSdkVersion
        targetSdk = Androids.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "consumer-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = Androids.sourceCompatibility
        targetCompatibility = Androids.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = Androids.jvmTarget
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", Versions.kotlinStdlib))

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidxTestJunit)
    androidTestImplementation(Libs.androidxTestEspresso)

    implementation(Libs.androidxCoreKtx)
    implementation(Libs.androidxAppcompat)

    implementation(Libs.androidxLifecycleViewModelKtx)
    implementation(Libs.androidxLifecycleLiveDataKtx)
    implementation(Libs.androidxLifecycleRuntimeKtx)
    //kapt(Libs.androidx_lifecycle_compiler)
    implementation(Libs.androidxLifecycleCommonJava8)
}