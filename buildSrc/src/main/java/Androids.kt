import org.gradle.api.JavaVersion

object Androids {
    const val compileSdkVersion = 31
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 14
    const val targetSdkVersion = 31
    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8
    const val jvmTarget = "1.8"
    const val versionCode = 1
    const val versionName = "1.0.0-SNAPSHOT"
    const val applicationId = "per.goweii.android.anole"
}