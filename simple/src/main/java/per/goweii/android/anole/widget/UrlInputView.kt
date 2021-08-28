package per.goweii.android.anole.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import coil.load
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.LayoutUrlInputBinding
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url

class UrlInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: LayoutUrlInputBinding =
        LayoutUrlInputBinding.inflate(LayoutInflater.from(context), this, true)

    private val inputMethodManager by lazy {
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val ivIcon: ImageView get() = binding.ivIcon
    private val etText: EditText get() = binding.etText
    private val ivAction: ImageView get() = binding.ivAction

    private val currText: String? get() = etText.text?.toString()

    private var icon: Bitmap? = null
    private var url: String? = null
    private var title: String? = null

    var onEnter: ((url: String) -> Unit)? = null
    var onSearch: ((key: String) -> Unit)? = null
    var onCollect: ((url: String) -> Unit)? = null
    var onDefSearch: ((ImageView) -> Unit)? = null

    init {
        etText.setSelectAllOnFocus(true)
        etText.imeOptions = EditorInfo.IME_ACTION_GO
        etText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    val url = Url.parse(currText)
                    if (url.maybeUrl) {
                        onEnter?.invoke(url.toUrl()!!)
                    } else {
                        if (!currText.isNullOrBlank()) {
                            onSearch?.invoke(currText!!)
                        }
                    }
                }
            }
            etText.clearFocus()
            return@setOnEditorActionListener true
        }
        etText.setOnFocusChangeListener { _, hasFocus ->
            setText()
            setIcon()
            setAction()
            if (hasFocus) {
                inputMethodManager.showSoftInput(etText, 0)
            } else {
                inputMethodManager.hideSoftInputFromWindow(etText.windowToken, 0)
            }
        }
        etText.addTextChangedListener {
            setIcon()
            setAction()
        }
        ivAction.setOnClickListener {
            if (etText.isFocused) {
                etText.text = null
            } else {
                if (!url.isNullOrBlank()) {
                    onCollect?.invoke(url!!)
                }
            }
        }
    }

    fun setIcon(icon: Bitmap?) {
        this.icon = icon
        setIcon()
    }

    fun setTitle(title: String?) {
        this.title = title
        setText()
    }

    fun setUrl(url: String?) {
        this.url = url
        setText()
    }

    private fun setText() {
        if (etText.isFocused) {
            etText.setText(url)
        } else {
            if (title.isNullOrBlank()) {
                val url = Url.parse(url)
                etText.setText(url.host)
            } else {
                val url = Url.parse(title)
                if (url.isUrl) {
                    etText.setText(url.host)
                } else {
                    etText.setText(title)
                }
            }
        }
    }

    private fun setIcon() {
        if (etText.isFocused) {
            val url = Url.parse(currText)
            if (url.maybeUrl) {
                ivIcon.setImageResource(R.drawable.ic_browser)
                ivIcon.setOnClickListener(null)
            } else {
                DefSearch.getInstance(context).getDefSearch().apply {
                    when {
                        logoRes != null -> ivIcon.load(logoRes!!)
                        logoUrl != null -> ivIcon.load(logoUrl!!)
                        else -> ivIcon.setImageResource(R.drawable.ic_browser)
                    }
                }
                ivIcon.setOnClickListener {
                    onDefSearch?.invoke(ivIcon)
                }
            }
        } else {
            ivIcon.setOnClickListener(null)
            if (icon != null) {
                ivIcon.setImageBitmap(icon)
            } else {
                val url = Url.parse(url)
                if (url.maybeUrl) {
                    if (url.isHttps) {
                        ivIcon.setImageResource(R.drawable.ic_safe)
                    } else {
                        ivIcon.setImageResource(R.drawable.ic_harm)
                    }
                } else {
                    ivIcon.setImageResource(R.drawable.ic_browser)
                }
            }
        }
    }

    private fun setAction() {
        if (etText.isFocused) {
            ivAction.setImageResource(R.drawable.ic_clear)
        } else {
            val url = Url.parse(url)
            if (url.maybeUrl) {
                ivAction.setImageResource(R.drawable.ic_collect)
            } else {
                ivAction.setImageDrawable(null)
            }
        }
    }

}