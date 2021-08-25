package per.goweii.anole.kernel.system

import android.os.Build
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import per.goweii.anole.kernel.WebSettings

@Suppress("DEPRECATION")
class SystemWetSettings(
    private val settings: android.webkit.WebSettings
) : WebSettings {
    override var supportZoom: Boolean
        get() = settings.supportZoom()
        set(value) {
            settings.setSupportZoom(value)
        }

    override var builtInZoomControls: Boolean
        get() = settings.builtInZoomControls
        set(value) {
            settings.builtInZoomControls = value
        }

    override var displayZoomControls: Boolean
        get() = settings.displayZoomControls
        set(value) {
            settings.displayZoomControls = value
        }

    override var allowFileAccess: Boolean
        get() = settings.allowFileAccess
        set(value) {
            settings.allowFileAccess = value
        }

    override var allowContentAccess: Boolean
        get() = settings.allowContentAccess
        set(value) {
            settings.allowContentAccess = value
        }

    override var loadWithOverviewMode: Boolean
        get() = settings.loadWithOverviewMode
        set(value) {
            settings.loadWithOverviewMode = value
        }

    override var enableSmoothTransition: Boolean
        get() = settings.enableSmoothTransition()
        set(value) {
            settings.setEnableSmoothTransition(value)
        }

    override var saveFormData: Boolean
        get() = settings.saveFormData
        set(value) {
            settings.saveFormData = value
        }

    override var savePassword: Boolean
        get() = settings.savePassword
        set(value) {
            settings.savePassword = value
        }

    override var textZoom: Int
        get() = settings.textZoom
        set(value) {
            settings.textZoom = value
        }

    override var textSize: WebSettings.TextSize?
        get() = when (settings.textSize) {
            android.webkit.WebSettings.TextSize.SMALLEST -> WebSettings.TextSize.SMALLEST
            android.webkit.WebSettings.TextSize.SMALLER -> WebSettings.TextSize.SMALLER
            android.webkit.WebSettings.TextSize.NORMAL -> WebSettings.TextSize.NORMAL
            android.webkit.WebSettings.TextSize.LARGER -> WebSettings.TextSize.LARGER
            android.webkit.WebSettings.TextSize.LARGEST -> WebSettings.TextSize.LARGEST
            else -> null
        }
        set(value) {
            settings.textSize = when (value) {
                WebSettings.TextSize.SMALLEST -> android.webkit.WebSettings.TextSize.SMALLEST
                WebSettings.TextSize.SMALLER -> android.webkit.WebSettings.TextSize.SMALLER
                WebSettings.TextSize.NORMAL -> android.webkit.WebSettings.TextSize.NORMAL
                WebSettings.TextSize.LARGER -> android.webkit.WebSettings.TextSize.LARGER
                WebSettings.TextSize.LARGEST -> android.webkit.WebSettings.TextSize.LARGEST
                else -> null
            }
        }

    override var defaultZoom: WebSettings.ZoomDensity?
        get() = when (settings.defaultZoom) {
            android.webkit.WebSettings.ZoomDensity.FAR -> WebSettings.ZoomDensity.FAR
            android.webkit.WebSettings.ZoomDensity.MEDIUM -> WebSettings.ZoomDensity.MEDIUM
            android.webkit.WebSettings.ZoomDensity.CLOSE -> WebSettings.ZoomDensity.CLOSE
            else -> null
        }
        set(value) {
            settings.defaultZoom = when (value) {
                WebSettings.ZoomDensity.FAR -> android.webkit.WebSettings.ZoomDensity.FAR
                WebSettings.ZoomDensity.MEDIUM -> android.webkit.WebSettings.ZoomDensity.MEDIUM
                WebSettings.ZoomDensity.CLOSE -> android.webkit.WebSettings.ZoomDensity.CLOSE
                else -> null
            }
        }

    override var lightTouchEnabled: Boolean
        get() = settings.lightTouchEnabled
        set(value) {
            settings.lightTouchEnabled = value
        }

    override var useWideViewPort: Boolean
        get() = settings.useWideViewPort
        set(value) {
            settings.useWideViewPort = value
        }

    override var supportMultipleWindows: Boolean
        get() = settings.supportMultipleWindows()
        set(value) {
            settings.setSupportMultipleWindows(value)
        }

    override var layoutAlgorithm: WebSettings.LayoutAlgorithm?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            when (settings.layoutAlgorithm) {
                android.webkit.WebSettings.LayoutAlgorithm.NORMAL -> WebSettings.LayoutAlgorithm.NORMAL
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN -> WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS -> WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                android.webkit.WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING -> WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                else -> null
            }
        } else {
            when (settings.layoutAlgorithm) {
                android.webkit.WebSettings.LayoutAlgorithm.NORMAL -> WebSettings.LayoutAlgorithm.NORMAL
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN -> WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS -> WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                else -> null
            }
        }
        set(value) {
            settings.layoutAlgorithm = when (value) {
                WebSettings.LayoutAlgorithm.NORMAL -> android.webkit.WebSettings.LayoutAlgorithm.NORMAL
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN -> android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS -> android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    android.webkit.WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                } else {
                    null
                }
                else -> null
            }
        }

    override var standardFontFamily: String?
        get() = settings.standardFontFamily
        set(value) {
            settings.standardFontFamily = value
        }

    override var fixedFontFamily: String?
        get() = settings.fixedFontFamily
        set(value) {
            settings.fixedFontFamily = value
        }

    override var sansSerifFontFamily: String?
        get() = settings.sansSerifFontFamily
        set(value) {
            settings.sansSerifFontFamily = value
        }

    override var serifFontFamily: String?
        get() = settings.serifFontFamily
        set(value) {
            settings.serifFontFamily = value
        }

    override var cursiveFontFamily: String?
        get() = settings.cursiveFontFamily
        set(value) {
            settings.cursiveFontFamily = value
        }

    override var fantasyFontFamily: String?
        get() = settings.fantasyFontFamily
        set(value) {
            settings.fantasyFontFamily = value
        }

    override var minimumFontSize: Int
        get() = settings.minimumFontSize
        set(value) {
            settings.minimumFontSize = value
        }

    override var minimumLogicalFontSize: Int
        get() = settings.minimumLogicalFontSize
        set(value) {
            settings.minimumLogicalFontSize = value
        }

    override var defaultFontSize: Int
        get() = settings.defaultFontSize
        set(value) {
            settings.defaultFontSize = value
        }

    override var defaultFixedFontSize: Int
        get() = settings.defaultFixedFontSize
        set(value) {
            settings.defaultFixedFontSize = value
        }

    override var loadsImagesAutomatically: Boolean
        get() = settings.loadsImagesAutomatically
        set(value) {
            settings.loadsImagesAutomatically = value
        }

    override var blockNetworkImage: Boolean
        get() = settings.blockNetworkImage
        set(value) {
            settings.blockNetworkImage = value
        }

    override var blockNetworkLoads: Boolean
        get() = settings.blockNetworkLoads
        set(value) {
            settings.blockNetworkLoads = value
        }

    override var javaScriptEnabled: Boolean
        get() = settings.javaScriptEnabled
        set(value) {
            settings.javaScriptEnabled = value
        }

    override var allowUniversalAccessFromFileURLs: Boolean
        get() = settings.allowUniversalAccessFromFileURLs
        set(value) {
            settings.allowUniversalAccessFromFileURLs = value
        }

    override var allowFileAccessFromFileURLs: Boolean
        get() = settings.allowFileAccessFromFileURLs
        set(value) {
            settings.allowFileAccessFromFileURLs = value
        }


    override var pluginState: WebSettings.PluginState?
        get() = when (settings.pluginState) {
            android.webkit.WebSettings.PluginState.ON -> WebSettings.PluginState.ON
            android.webkit.WebSettings.PluginState.ON_DEMAND -> WebSettings.PluginState.ON_DEMAND
            android.webkit.WebSettings.PluginState.OFF -> WebSettings.PluginState.OFF
            else -> null
        }
        set(value) {
            settings.pluginState = when (value) {
                WebSettings.PluginState.ON -> android.webkit.WebSettings.PluginState.ON
                WebSettings.PluginState.ON_DEMAND -> android.webkit.WebSettings.PluginState.ON_DEMAND
                WebSettings.PluginState.OFF -> android.webkit.WebSettings.PluginState.OFF
                else -> null
            }
        }

    override var databasePath: String?
        get() = settings.databasePath
        set(value) {
            settings.databasePath = value
        }

    override var geolocationDatabasePath: String?
        get() = null
        set(value) {
            settings.setGeolocationDatabasePath(value)
        }

    override var appCacheEnabled: Boolean
        get() = false
        set(value) {
            settings.setAppCacheEnabled(value)
        }

    override var appCachePath: String?
        get() = null
        set(value) {
            settings.setAppCachePath(value)
        }

    override var appCacheMaxSize: Long
        get() = 0
        set(value) {
            settings.setAppCacheMaxSize(value)
        }

    override var databaseEnabled: Boolean
        get() = settings.databaseEnabled
        set(value) {
            settings.databaseEnabled = value
        }

    override var domStorageEnabled: Boolean
        get() = settings.domStorageEnabled
        set(value) {
            settings.domStorageEnabled = value
        }

    override var geolocationEnabled: Boolean
        get() = false
        set(value) {
            settings.setGeolocationEnabled(value)
        }

    override var javaScriptCanOpenWindowsAutomatically: Boolean
        get() = settings.javaScriptCanOpenWindowsAutomatically
        set(value) {
            settings.javaScriptCanOpenWindowsAutomatically = value
        }

    override var defaultTextEncodingName: String?
        get() = settings.defaultTextEncodingName
        set(value) {
            settings.defaultTextEncodingName = value
        }

    override var userAgentString: String?
        get() = settings.userAgentString
        set(value) {
            settings.userAgentString = value
        }

    override var needInitialFocus: Boolean
        get() = false
        set(value) {
            settings.setNeedInitialFocus(value)
        }

    override var renderPriority: WebSettings.RenderPriority?
        get() = null
        set(value) {
            settings.setRenderPriority(
                when (value) {
                    WebSettings.RenderPriority.NORMAL -> android.webkit.WebSettings.RenderPriority.NORMAL
                    WebSettings.RenderPriority.HIGH -> android.webkit.WebSettings.RenderPriority.HIGH
                    WebSettings.RenderPriority.LOW -> android.webkit.WebSettings.RenderPriority.LOW
                    else -> null
                }
            )
        }

    override var cacheMode: WebSettings.CacheMode?
        get() = when (settings.cacheMode) {
            android.webkit.WebSettings.LOAD_DEFAULT -> WebSettings.CacheMode.LOAD_DEFAULT
            android.webkit.WebSettings.LOAD_NORMAL -> WebSettings.CacheMode.LOAD_NORMAL
            android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK -> WebSettings.CacheMode.LOAD_CACHE_ELSE_NETWORK
            android.webkit.WebSettings.LOAD_NO_CACHE -> WebSettings.CacheMode.LOAD_NO_CACHE
            android.webkit.WebSettings.LOAD_CACHE_ONLY -> WebSettings.CacheMode.LOAD_CACHE_ONLY
            else -> null
        }
        set(value) {
            settings.cacheMode = when (value) {
                WebSettings.CacheMode.LOAD_DEFAULT -> android.webkit.WebSettings.LOAD_DEFAULT
                WebSettings.CacheMode.LOAD_NORMAL -> android.webkit.WebSettings.LOAD_NORMAL
                WebSettings.CacheMode.LOAD_CACHE_ELSE_NETWORK -> android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
                WebSettings.CacheMode.LOAD_NO_CACHE -> android.webkit.WebSettings.LOAD_NO_CACHE
                WebSettings.CacheMode.LOAD_CACHE_ONLY -> android.webkit.WebSettings.LOAD_CACHE_ONLY
                else -> android.webkit.WebSettings.LOAD_DEFAULT
            }
        }
    override var forceDark: WebSettings.ForceDark?
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                when (settings.forceDark) {
                    android.webkit.WebSettings.FORCE_DARK_AUTO -> WebSettings.ForceDark.FORCE_DARK_AUTO
                    android.webkit.WebSettings.FORCE_DARK_OFF -> WebSettings.ForceDark.FORCE_DARK_OFF
                    android.webkit.WebSettings.FORCE_DARK_ON -> WebSettings.ForceDark.FORCE_DARK_ON
                    else -> null
                }
            }
            WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) -> {
                when (WebSettingsCompat.getForceDark(settings)) {
                    WebSettingsCompat.FORCE_DARK_AUTO -> WebSettings.ForceDark.FORCE_DARK_AUTO
                    WebSettingsCompat.FORCE_DARK_OFF -> WebSettings.ForceDark.FORCE_DARK_OFF
                    WebSettingsCompat.FORCE_DARK_ON -> WebSettings.ForceDark.FORCE_DARK_ON
                    else -> null
                }
            }
            else -> null
        }
        set(value) {
            if (value == null) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                settings.forceDark = when (value) {
                    WebSettings.ForceDark.FORCE_DARK_AUTO -> android.webkit.WebSettings.FORCE_DARK_AUTO
                    WebSettings.ForceDark.FORCE_DARK_OFF -> android.webkit.WebSettings.FORCE_DARK_OFF
                    WebSettings.ForceDark.FORCE_DARK_ON -> android.webkit.WebSettings.FORCE_DARK_ON
                }
            } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(
                    settings, when (value) {
                        WebSettings.ForceDark.FORCE_DARK_AUTO -> WebSettingsCompat.FORCE_DARK_AUTO
                        WebSettings.ForceDark.FORCE_DARK_OFF -> WebSettingsCompat.FORCE_DARK_OFF
                        WebSettings.ForceDark.FORCE_DARK_ON -> WebSettingsCompat.FORCE_DARK_ON
                    }
                )
            }
        }
    override var mixedContentMode: WebSettings.MixedContentMode?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when (settings.mixedContentMode) {
                android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW -> WebSettings.MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW
                android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW -> WebSettings.MixedContentMode.MIXED_CONTENT_NEVER_ALLOW
                android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE -> WebSettings.MixedContentMode.MIXED_CONTENT_COMPATIBILITY_MODE
                else -> null
            }
        } else {
            null
        }
        set(value) {
            if (value == null) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                when (value) {
                    WebSettings.MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW -> android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    WebSettings.MixedContentMode.MIXED_CONTENT_NEVER_ALLOW -> android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    WebSettings.MixedContentMode.MIXED_CONTENT_COMPATIBILITY_MODE -> android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                }
            }
        }
}