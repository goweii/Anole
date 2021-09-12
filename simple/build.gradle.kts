plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = Androids.compileSdkVersion
    buildToolsVersion = Androids.buildToolsVersion
    defaultConfig {
        applicationId = Androids.applicationId
        versionCode = Androids.versionCode
        versionName = Androids.versionName
        minSdk = 21
        targetSdk = Androids.targetSdkVersion
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "consumer-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "consumer-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = Androids.sourceCompatibility
        targetCompatibility = Androids.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = Androids.jvmTarget
    }
    applicationVariants.forEach { variant ->
        variant.outputs.forEach { output ->
            if (output is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                output.outputFileName =
                    "anole-${variant.buildType}-${defaultConfig.versionName}-${defaultConfig.versionCode}.apk"
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(kotlin("stdlib", Versions.kotlinStdlib))

    implementation(Libs.androidxCoreKtx)
    implementation(Libs.androidxAppcompat)
    implementation(Libs.androidxNavigationFragmentKtx)
    implementation(Libs.androidxNavigationUiKtx)

    implementation(project(":anole-kernel"))
    implementation(project(":anole-kernel-system"))

    implementation(Libs.constraintlayout)
    implementation(Libs.material)

    implementation(Libs.coil)

    implementation(Libs.layerDesignMaterial)
    implementation(Libs.layerDesignCupertino)

    implementation(Libs.codexCore)
    implementation(Libs.codexProcessorZxing)
    implementation(Libs.codexDecoratorFinderWechat)
    implementation(Libs.codexDecoratorFrozen)
    implementation(Libs.codexDecoratorGesture)
    implementation(Libs.codexDecoratorVibrate)

    implementation(Libs.smartRefreshLayout)
    implementation(Libs.smartRefreshHorizontal)
}
