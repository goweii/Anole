package per.goweii.anole.kernel.system

import android.os.Build
import per.goweii.anole.kernel.WebSettings

@Suppress("DEPRECATION")
class SystemWetSettings(
    private val webView: android.webkit.WebSettings
) : WebSettings {
    override var supportZoom: Boolean
        get() = webView.supportZoom()
        set(value) {
            webView.setSupportZoom(value)
        }

    override var builtInZoomControls: Boolean
        get() = webView.builtInZoomControls
        set(value) {
            webView.builtInZoomControls = value
        }

    override var displayZoomControls: Boolean
        get() = webView.displayZoomControls
        set(value) {
            webView.displayZoomControls = value
        }

    override var allowFileAccess: Boolean
        get() = webView.allowFileAccess
        set(value) {
            webView.allowFileAccess = value
        }

    override var allowContentAccess: Boolean
        get() = webView.allowContentAccess
        set(value) {
            webView.allowContentAccess = value
        }

    override var loadWithOverviewMode: Boolean
        get() = webView.loadWithOverviewMode
        set(value) {
            webView.loadWithOverviewMode = value
        }

    override var enableSmoothTransition: Boolean
        get() = webView.enableSmoothTransition()
        set(value) {
            webView.setEnableSmoothTransition(value)
        }

    override var saveFormData: Boolean
        get() = webView.saveFormData
        set(value) {
            webView.saveFormData = value
        }

    override var savePassword: Boolean
        get() = webView.savePassword
        set(value) {
            webView.savePassword = value
        }

    override var textZoom: Int
        get() = webView.textZoom
        set(value) {
            webView.textZoom = value
        }

    override var textSize: WebSettings.TextSize?
        get() = when (webView.textSize) {
            android.webkit.WebSettings.TextSize.SMALLEST -> WebSettings.TextSize.SMALLEST
            android.webkit.WebSettings.TextSize.SMALLER -> WebSettings.TextSize.SMALLER
            android.webkit.WebSettings.TextSize.NORMAL -> WebSettings.TextSize.NORMAL
            android.webkit.WebSettings.TextSize.LARGER -> WebSettings.TextSize.LARGER
            android.webkit.WebSettings.TextSize.LARGEST -> WebSettings.TextSize.LARGEST
            else -> null
        }
        set(value) {
            webView.textSize = when (value) {
                WebSettings.TextSize.SMALLEST -> android.webkit.WebSettings.TextSize.SMALLEST
                WebSettings.TextSize.SMALLER -> android.webkit.WebSettings.TextSize.SMALLER
                WebSettings.TextSize.NORMAL -> android.webkit.WebSettings.TextSize.NORMAL
                WebSettings.TextSize.LARGER -> android.webkit.WebSettings.TextSize.LARGER
                WebSettings.TextSize.LARGEST -> android.webkit.WebSettings.TextSize.LARGEST
                else -> null
            }
        }

    override var defaultZoom: WebSettings.ZoomDensity?
        get() = when (webView.defaultZoom) {
            android.webkit.WebSettings.ZoomDensity.FAR -> WebSettings.ZoomDensity.FAR
            android.webkit.WebSettings.ZoomDensity.MEDIUM -> WebSettings.ZoomDensity.MEDIUM
            android.webkit.WebSettings.ZoomDensity.CLOSE -> WebSettings.ZoomDensity.CLOSE
            else -> null
        }
        set(value) {
            webView.defaultZoom = when (value) {
                WebSettings.ZoomDensity.FAR -> android.webkit.WebSettings.ZoomDensity.FAR
                WebSettings.ZoomDensity.MEDIUM -> android.webkit.WebSettings.ZoomDensity.MEDIUM
                WebSettings.ZoomDensity.CLOSE -> android.webkit.WebSettings.ZoomDensity.CLOSE
                else -> null
            }
        }

    override var lightTouchEnabled: Boolean
        get() = webView.lightTouchEnabled
        set(value) {
            webView.lightTouchEnabled = value
        }

    override var useWideViewPort: Boolean
        get() = webView.useWideViewPort
        set(value) {
            webView.useWideViewPort = value
        }

    override var supportMultipleWindows: Boolean
        get() = webView.supportMultipleWindows()
        set(value) {
            webView.setSupportMultipleWindows(value)
        }

    override var layoutAlgorithm: WebSettings.LayoutAlgorithm?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            when (webView.layoutAlgorithm) {
                android.webkit.WebSettings.LayoutAlgorithm.NORMAL -> WebSettings.LayoutAlgorithm.NORMAL
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN -> WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS -> WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                android.webkit.WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING -> WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                else -> null
            }
        } else {
            when (webView.layoutAlgorithm) {
                android.webkit.WebSettings.LayoutAlgorithm.NORMAL -> WebSettings.LayoutAlgorithm.NORMAL
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN -> WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS -> WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                else -> null
            }
        }
        set(value) {
            webView.layoutAlgorithm = when (value) {
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
        get() = webView.standardFontFamily
        set(value) {
            webView.standardFontFamily = value
        }

    override var fixedFontFamily: String?
        get() = webView.fixedFontFamily
        set(value) {
            webView.fixedFontFamily = value
        }

    override var sansSerifFontFamily: String?
        get() = webView.sansSerifFontFamily
        set(value) {
            webView.sansSerifFontFamily = value
        }

    override var serifFontFamily: String?
        get() = webView.serifFontFamily
        set(value) {
            webView.serifFontFamily = value
        }

    override var cursiveFontFamily: String?
        get() = webView.cursiveFontFamily
        set(value) {
            webView.cursiveFontFamily = value
        }

    override var fantasyFontFamily: String?
        get() = webView.fantasyFontFamily
        set(value) {
            webView.fantasyFontFamily = value
        }

    override var minimumFontSize: Int
        get() = webView.minimumFontSize
        set(value) {
            webView.minimumFontSize = value
        }

    override var minimumLogicalFontSize: Int
        get() = webView.minimumLogicalFontSize
        set(value) {
            webView.minimumLogicalFontSize = value
        }

    override var defaultFontSize: Int
        get() = webView.defaultFontSize
        set(value) {
            webView.defaultFontSize = value
        }

    override var defaultFixedFontSize: Int
        get() = webView.defaultFixedFontSize
        set(value) {
            webView.defaultFixedFontSize = value
        }

    override var loadsImagesAutomatically: Boolean
        get() = webView.loadsImagesAutomatically
        set(value) {
            webView.loadsImagesAutomatically = value
        }

    override var blockNetworkImage: Boolean
        get() = webView.blockNetworkImage
        set(value) {
            webView.blockNetworkImage = value
        }

    override var blockNetworkLoads: Boolean
        get() = webView.blockNetworkLoads
        set(value) {
            webView.blockNetworkLoads = value
        }

    override var javaScriptEnabled: Boolean
        get() = webView.javaScriptEnabled
        set(value) {
            webView.javaScriptEnabled = value
        }

    override var allowUniversalAccessFromFileURLs: Boolean
        get() = webView.allowUniversalAccessFromFileURLs
        set(value) {
            webView.allowUniversalAccessFromFileURLs = value
        }

    override var allowFileAccessFromFileURLs: Boolean
        get() = webView.allowFileAccessFromFileURLs
        set(value) {
            webView.allowFileAccessFromFileURLs = value
        }


    override var pluginState: WebSettings.PluginState?
        get() = when (webView.pluginState) {
            android.webkit.WebSettings.PluginState.ON -> WebSettings.PluginState.ON
            android.webkit.WebSettings.PluginState.ON_DEMAND -> WebSettings.PluginState.ON_DEMAND
            android.webkit.WebSettings.PluginState.OFF -> WebSettings.PluginState.OFF
            else -> null
        }
        set(value) {
            webView.pluginState = when (value) {
                WebSettings.PluginState.ON -> android.webkit.WebSettings.PluginState.ON
                WebSettings.PluginState.ON_DEMAND -> android.webkit.WebSettings.PluginState.ON_DEMAND
                WebSettings.PluginState.OFF -> android.webkit.WebSettings.PluginState.OFF
                else -> null
            }
        }

    override var databasePath: String?
        get() = webView.databasePath
        set(value) {
            webView.databasePath = value
        }

    override var geolocationDatabasePath: String?
        get() = null
        set(value) {
            webView.setGeolocationDatabasePath(value)
        }

    override var setAppCacheEnabled: Boolean
        get() = false
        set(value) {
            webView.setAppCacheEnabled(value)
        }

    override var appCachePath: String?
        get() = null
        set(value) {
            webView.setAppCachePath(value)
        }

    override var appCacheMaxSize: Long
        get() = 0
        set(value) {
            webView.setAppCacheMaxSize(value)
        }

    override var databaseEnabled: Boolean
        get() = webView.databaseEnabled
        set(value) {
            webView.databaseEnabled = value
        }

    override var domStorageEnabled: Boolean
        get() = webView.domStorageEnabled
        set(value) {
            webView.domStorageEnabled = value
        }

    override var geolocationEnabled: Boolean
        get() = false
        set(value) {
            webView.setGeolocationEnabled(value)
        }

    override var javaScriptCanOpenWindowsAutomatically: Boolean
        get() = webView.javaScriptCanOpenWindowsAutomatically
        set(value) {
            webView.javaScriptCanOpenWindowsAutomatically = value
        }

    override var defaultTextEncodingName: String?
        get() = webView.defaultTextEncodingName
        set(value) {
            webView.defaultTextEncodingName = value
        }

    override var userAgentString: String?
        get() = webView.userAgentString
        set(value) {
            webView.userAgentString = value
        }

    override var needInitialFocus: Boolean
        get() = false
        set(value) {
            webView.setNeedInitialFocus(value)
        }

    override var renderPriority: WebSettings.RenderPriority?
        get() = null
        set(value) {
            webView.setRenderPriority(
                when (value) {
                    WebSettings.RenderPriority.NORMAL -> android.webkit.WebSettings.RenderPriority.NORMAL
                    WebSettings.RenderPriority.HIGH -> android.webkit.WebSettings.RenderPriority.HIGH
                    WebSettings.RenderPriority.LOW -> android.webkit.WebSettings.RenderPriority.LOW
                    else -> null
                }
            )
        }

    override var cacheMode: WebSettings.CacheMode?
        get() = when (webView.cacheMode) {
            android.webkit.WebSettings.LOAD_DEFAULT -> WebSettings.CacheMode.LOAD_DEFAULT
            android.webkit.WebSettings.LOAD_NORMAL -> WebSettings.CacheMode.LOAD_NORMAL
            android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK -> WebSettings.CacheMode.LOAD_CACHE_ELSE_NETWORK
            android.webkit.WebSettings.LOAD_NO_CACHE -> WebSettings.CacheMode.LOAD_NO_CACHE
            android.webkit.WebSettings.LOAD_CACHE_ONLY -> WebSettings.CacheMode.LOAD_CACHE_ONLY
            else -> null
        }
        set(value) {
            webView.cacheMode = when (value) {
                WebSettings.CacheMode.LOAD_DEFAULT -> android.webkit.WebSettings.LOAD_DEFAULT
                WebSettings.CacheMode.LOAD_NORMAL -> android.webkit.WebSettings.LOAD_NORMAL
                WebSettings.CacheMode.LOAD_CACHE_ELSE_NETWORK -> android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
                WebSettings.CacheMode.LOAD_NO_CACHE -> android.webkit.WebSettings.LOAD_NO_CACHE
                WebSettings.CacheMode.LOAD_CACHE_ONLY -> android.webkit.WebSettings.LOAD_CACHE_ONLY
                else -> android.webkit.WebSettings.LOAD_DEFAULT
            }
        }
    override var forceDark: WebSettings.ForceDark?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (webView.forceDark) {
                android.webkit.WebSettings.FORCE_DARK_AUTO -> WebSettings.ForceDark.FORCE_DARK_AUTO
                android.webkit.WebSettings.FORCE_DARK_OFF -> WebSettings.ForceDark.FORCE_DARK_OFF
                android.webkit.WebSettings.FORCE_DARK_ON -> WebSettings.ForceDark.FORCE_DARK_ON
                else -> null
            }
        } else {
            null
        }
        set(value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (value) {
                    WebSettings.ForceDark.FORCE_DARK_AUTO -> android.webkit.WebSettings.FORCE_DARK_AUTO
                    WebSettings.ForceDark.FORCE_DARK_OFF -> android.webkit.WebSettings.FORCE_DARK_OFF
                    WebSettings.ForceDark.FORCE_DARK_ON -> android.webkit.WebSettings.FORCE_DARK_ON
                    else -> android.webkit.WebSettings.FORCE_DARK_AUTO
                }
            }
        }
}