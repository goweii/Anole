buildscript {
    @Suppress("JcenterRepositoryObsolete")
    repositories {
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin"))
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")
    }
}

allprojects {
    @Suppress("JcenterRepositoryObsolete")
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}