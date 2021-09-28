object Versions {
    const val kotlinStdlib = "1.5.30"
    const val androidxLifecycle = "2.4.0-alpha03"
    const val androidxCoreKtx = "1.7.0-alpha02"
    const val androidxAppcompat = "1.4.0-alpha03"
    const val androidxWebkit = "1.4.0"
    const val androidxNavigation = "2.4.0-alpha08"
    const val junit = "4.13.2"
    const val androidxTestJunit = "1.1.3"
    const val androidxTestEspresso = "3.4.0"
}

object Libs {
    const val junit = "junit:junit:${Versions.junit}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val androidxTestEspresso =
        "androidx.test.espresso:espresso-core:${Versions.androidxTestEspresso}"

    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCoreKtx}"
    const val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxAppcompat}"
    const val androidxNavigationFragmentKtx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.androidxNavigation}"
    const val androidxNavigationUiKtx =
        "androidx.navigation:navigation-ui-ktx:${Versions.androidxNavigation}"

    const val androidxWebkit = "androidx.webkit:webkit:${Versions.androidxWebkit}"

    const val androidxLifecycleViewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleLiveDataSavedState =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.androidxLifecycle}"
    const val androidxLifecycleRuntimeKtx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleCompiler =
        "androidx.lifecycle:lifecycle-compiler:${Versions.androidxLifecycle}"
    const val androidxLifecycleCommonJava8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.androidxLifecycle}"

    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.0"
    const val material = "com.google.android.material:material:1.4.0"
    const val flexbox = "com.google.android.flexbox:flexbox:3.0.0"

    const val coil = "io.coil-kt:coil:1.3.2"

    const val layerDesignMaterial = "per.goweii.layer:layer-design-material:1.0.0"
    const val layerDesignCupertino = "per.goweii.layer:layer-design-cupertino:1.0.0"

    const val codexCore = "per.goweii.codex:core:1.0.0"
    const val codexProcessorZxing = "per.goweii.codex:processor-zxing:1.0.0"
    const val codexDecoratorFinderWechat = "per.goweii.codex:decorator-finder-wechat:1.0.0"
    const val codexDecoratorFrozen = "per.goweii.codex:decorator-frozen:1.0.0"
    const val codexDecoratorGesture = "per.goweii.codex:decorator-gesture:1.0.0"
    const val codexDecoratorVibrate = "per.goweii.codex:decorator-vibrate:1.0.0"

    const val smartRefreshLayout = "com.scwang.smartrefresh:SmartRefreshLayout:1.1.3"
    const val smartRefreshHorizontal = "com.scwang.smartrefresh:SmartRefreshHorizontal:1.1.2-x"
}