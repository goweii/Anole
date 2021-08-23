package per.goweii.anole.kernel

interface WebSettings {
    var supportZoom: Boolean
    var builtInZoomControls: Boolean
    var displayZoomControls: Boolean
    var allowFileAccess: Boolean
    var allowContentAccess: Boolean
    var loadWithOverviewMode: Boolean
    var enableSmoothTransition: Boolean
    var saveFormData: Boolean
    var savePassword: Boolean
    var textZoom: Int
    var textSize: TextSize?
    var defaultZoom: ZoomDensity?
    var lightTouchEnabled: Boolean
    var useWideViewPort: Boolean
    var supportMultipleWindows: Boolean
    var layoutAlgorithm: LayoutAlgorithm?
    var standardFontFamily: String?
    var fixedFontFamily: String?
    var sansSerifFontFamily: String?
    var serifFontFamily: String?
    var cursiveFontFamily: String?
    var fantasyFontFamily: String?
    var minimumFontSize: Int
    var minimumLogicalFontSize: Int
    var defaultFontSize: Int
    var defaultFixedFontSize: Int
    var loadsImagesAutomatically: Boolean
    var blockNetworkImage: Boolean
    var blockNetworkLoads: Boolean
    var geolocationDatabasePath: String?
    var geolocationEnabled: Boolean
    var appCacheEnabled: Boolean
    var appCachePath: String?
    var appCacheMaxSize: Long
    var domStorageEnabled: Boolean
    var databasePath: String?
    var databaseEnabled: Boolean
    var javaScriptEnabled: Boolean
    var allowUniversalAccessFromFileURLs: Boolean
    var allowFileAccessFromFileURLs: Boolean
    var pluginState: PluginState?
    var javaScriptCanOpenWindowsAutomatically: Boolean
    var defaultTextEncodingName: String?
    var userAgentString: String?
    var needInitialFocus: Boolean
    var renderPriority: RenderPriority?
    var cacheMode: CacheMode?
    var forceDark: ForceDark?
    var mixedContentMode: MixedContentMode?

    enum class MixedContentMode {
        MIXED_CONTENT_ALWAYS_ALLOW,
        MIXED_CONTENT_NEVER_ALLOW,
        MIXED_CONTENT_COMPATIBILITY_MODE
    }

    enum class LayoutAlgorithm {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    enum class TextSize(var value: Int) {
        SMALLEST(50),
        SMALLER(75),
        NORMAL(100),
        LARGER(150),
        LARGEST(200)
    }

    enum class ZoomDensity(var value: Int) {
        FAR(150),
        MEDIUM(100),
        CLOSE(75)
    }

    enum class CacheMode {
        LOAD_DEFAULT,
        LOAD_NORMAL,
        LOAD_CACHE_ELSE_NETWORK,
        LOAD_NO_CACHE,
        LOAD_CACHE_ONLY
    }

    enum class RenderPriority {
        NORMAL,
        HIGH,
        LOW
    }

    enum class PluginState {
        ON,
        ON_DEMAND,
        OFF
    }

    enum class ForceDark {
        FORCE_DARK_OFF,
        FORCE_DARK_AUTO,
        FORCE_DARK_ON
    }
}