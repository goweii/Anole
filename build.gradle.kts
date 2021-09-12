// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    @Suppress("JcenterRepositoryObsolete")
    repositories {
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha11")
        classpath(kotlin("gradle-plugin"))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.0-alpha08")
    }
}

allprojects {
    @Suppress("JcenterRepositoryObsolete")
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://gitee.com/goweii/maven-repository/raw/master/releases/") }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}