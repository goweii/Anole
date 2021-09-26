package per.goweii.android.anole.widget

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.doOnAttach
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.LayoutUrlInputBinding
import per.goweii.android.anole.main.ChoiceDefSearchAdapter
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url
import per.goweii.layer.core.Layer
import per.goweii.layer.core.anim.AnimatorHelper
import per.goweii.layer.popup.PopupLayer

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

    var onEnter: ((url: String) -> Unit)? = null

    init {
        etText.imeOptions = EditorInfo.IME_ACTION_GO
        etText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    ivAction.performClick()
                }
            }
            return@setOnEditorActionListener true
        }
        etText.setOnFocusChangeListener { _, hasFocus ->
            updateState()
            if (hasFocus) {
                inputMethodManager.showSoftInput(etText, 0)
            } else {
                inputMethodManager.hideSoftInputFromWindow(etText.windowToken, 0)
            }
        }
        etText.addTextChangedListener {
            updateState()
        }
        ivAction.setOnClickListener {
            val text = currText ?: return@setOnClickListener
            etText.clearFocus()
            val url = Url.parse(text).toUrl()
                ?: DefSearch.getInstance(context).getDefSearch().getSearchUrl(text)
            onEnter?.invoke(url)
        }
    }

    fun requestFocusAndShowIm() {
        doOnAttach {
            it.post {
                etText.requestFocus()
            }
        }
    }

    fun setText(text: String?) {
        etText.setText(text)
    }

    private fun updateState() {
        updateIcon()
        updateAction()
    }

    private fun updateIcon() {
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
                showChoiceDefSearchPopup()
            }
        }
    }

    private fun updateAction() {
        if (currText.isNullOrEmpty()) {
            ivAction.setImageDrawable(null)
        } else {
            val url = Url.parse(currText)
            if (url.maybeUrl) {
                ivAction.setImageResource(R.drawable.ic_enter)
            } else {
                ivAction.setImageResource(R.drawable.ic_search)
            }
        }
    }

    private fun showChoiceDefSearchPopup() {
        val offsetY = (context.resources.getDimensionPixelSize(R.dimen.dimenShadowRadius)
                + context.resources.getDimensionPixelSize(R.dimen.dimenShadowOffsetY)
                + context.resources.getDimensionPixelSize(R.dimen.dimenMarginThin) * 2)
        PopupLayer(ivIcon)
            .setHorizontal(PopupLayer.Align.Horizontal.CENTER)
            .setVertical(PopupLayer.Align.Vertical.ALIGN_TOP)
            .setDirection(PopupLayer.Align.Direction.VERTICAL)
            .setInside(false)
            .setOffsetYpx(-offsetY.toFloat())
            .setContentView(R.layout.popup_choice_def_search)
            .setContentAnimator(object : Layer.AnimatorCreator {
                override fun createInAnimator(view: View): Animator {
                    return AnimatorHelper.createZoomAlphaInAnim(view, view.width / 2, offsetY)
                }

                override fun createOutAnimator(view: View): Animator {
                    return AnimatorHelper.createZoomAlphaOutAnim(view, view.width / 2, offsetY)
                }
            })
            .addOnBindDataListener { layer ->
                val rv = layer.requireView<RecyclerView>(R.id.popup_choice_def_search_rv)
                rv.layoutManager = LinearLayoutManager(
                    rv.context, LinearLayoutManager.VERTICAL, false
                )
                val allSelectList = arrayListOf<DefSearch.CustomSearch>()
                allSelectList.addAll(DefSearch.getInstance(context).getAllSearch())
                val defSearch = DefSearch.getInstance(context).getDefSearch()
                allSelectList.remove(defSearch)
                allSelectList.add(0, defSearch)
                rv.adapter = ChoiceDefSearchAdapter(allSelectList).apply {
                    onChoice = {
                        DefSearch.getInstance(context).saveDefSearch(it)
                        updateState()
                        layer.dismiss()
                    }
                }
            }
            .show()
    }
}