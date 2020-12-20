package per.goweii.android.anole

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.util.forEach
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import per.goweii.android.anole.databinding.ActivityMainBinding
import per.goweii.android.anole.databinding.DialogWindowChoiceBinding
import per.goweii.android.anole.databinding.ItemWindowChoiceBinding
import per.goweii.anole.AnoleBuilder
import per.goweii.anole.ability.*
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.view.AnoleView
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var anoleView: AnoleView? = null

    private lateinit var backForwardIconAbility: BackForwardIconAbility
    private lateinit var pageInfoAbility: PageInfoAbility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backForwardIconAbility = BackForwardIconAbility(
            binding.ivBack,
            binding.ivForward
        )
        pageInfoAbility = PageInfoAbility(
            onReceivedPageTitle = { setCurrTitle(it) },
            onReceivedPageIcon = { setCurrLogo(it) }
        )
        binding.container.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                refreshTopWeb()
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                refreshTopWeb()
            }
        })
        createWeb()
        binding.ivBack.setOnClickListener {
            anoleView?.goBackOrForward(-1)
        }
        binding.ivForward.setOnClickListener {
            anoleView?.goBackOrForward(1)
        }
        binding.ivWindows.setOnClickListener {
            if (binding.container.childCount == 0) {
                createWeb()
            } else {
                showWindowChoiceDialog()
            }
        }
        binding.ivWindows.setOnLongClickListener {
            createWeb()
            return@setOnLongClickListener true
        }
        binding.ivEnterOrSearch.setOnClickListener {
            if (binding.etTitle.isFocused) {
                val inputUrl = binding.etTitle.getUrl()
                if (inputUrl.isNullOrEmpty()) {
                    anoleView?.loadUrl(getSearchUrl(binding.etTitle.text.toString()))
                } else {
                    anoleView?.loadUrl(inputUrl)
                }
                hideIme()
            }
        }
        binding.etTitle.setOnFocusChangeListener { _, b ->
            binding.ivEnterOrSearch.setImageResource(R.drawable.ic_enter)
            if (b) {
                binding.etTitle.setText(anoleView?.url)
            } else {
                binding.etTitle.setText(anoleView?.title)
            }
        }
        binding.etTitle.doAfterTextChanged {
            if (binding.etTitle.isFocused) {
                val inputUrl = binding.etTitle.getUrl()
                if (inputUrl.isNullOrEmpty()) {
                    binding.etTitle.imeOptions = EditorInfo.IME_ACTION_SEARCH
                    binding.ivEnterOrSearch.setImageResource(R.drawable.ic_search)
                } else {
                    binding.etTitle.imeOptions = EditorInfo.IME_ACTION_GO
                    binding.ivEnterOrSearch.setImageResource(R.drawable.ic_enter)
                }
            }
        }
        binding.etTitle.setSelectAllOnFocus(true)
        binding.etTitle.imeOptions = EditorInfo.IME_ACTION_GO
        binding.etTitle.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    binding.ivEnterOrSearch.performClick()
                    return@setOnEditorActionListener true
                }
                EditorInfo.IME_ACTION_SEARCH -> {
                    binding.ivEnterOrSearch.performClick()
                    return@setOnEditorActionListener true
                }
                else -> {
                    return@setOnEditorActionListener false
                }
            }
        }
    }

    private fun closeAllWeb() {
        binding.container.children.map {
            it as AnoleView
        }.also {
            binding.container.removeAllViews()
            it.forEach { anoleView ->
                closeWeb(anoleView)
            }
        }
        refreshTopWeb()
    }

    private fun closeWeb(anoleView: AnoleView) {
        val parent = anoleView.parent
        if (parent != null) {
            parent as ViewGroup
            parent.removeView(anoleView)
        }
        anoleView.destroy()
        refreshTopWeb()
    }

    private fun createWeb(): AnoleView {
        val anoleView = AnoleBuilder.with(this)
            .applyDefaultConfig()
            .attachTo(binding.container)
            .get(this)
        anoleView.loadUrl(getHomeUrl())
        refreshTopWeb()
        return anoleView
    }

    private fun setCurrTitle(title: String?) {
        if (!binding.etTitle.isFocused) {
            binding.etTitle.setText(title)
        }
    }

    private fun setCurrLogo(logo: Bitmap?) {
        if (logo == null) {
            binding.ivLogo.setImageResource(R.drawable.ic_browser)
        } else {
            binding.ivLogo.setImageBitmap(logo)
        }
    }

    private fun refreshTopWeb() {
        if (binding.container.childCount == 0) {
            binding.tvWindowsCount.visibility = View.GONE
            binding.tvWindowsCount.text = ""
            binding.ivWindows.setImageResource(R.drawable.ic_add)
        } else {
            binding.tvWindowsCount.visibility = View.VISIBLE
            binding.tvWindowsCount.text = "${binding.container.childCount}"
            binding.ivWindows.setImageResource(R.drawable.ic_windows)
        }
        anoleView = null
        setCurrTitle(null)
        setCurrLogo(null)
        for (i in 0 until binding.container.childCount) {
            val anoleView = binding.container.getChildAt(i)
            anoleView as AnoleView
            if (i == binding.container.childCount - 1) {
                this.anoleView = anoleView
                anoleView.visibility = View.VISIBLE
                anoleView.client.addAbility(backForwardIconAbility)
                anoleView.client.addAbility(pageInfoAbility)
            } else {
                anoleView.visibility = View.GONE
                anoleView.client.removeAbility(backForwardIconAbility)
                anoleView.client.removeAbility(pageInfoAbility)
            }
        }
    }

    private fun getHomeUrl(): String {
        return "https://cn.bing.com"
    }

    private fun getSearchUrl(str: String): String {
        return "https://cn.bing.com/search?q=$str"
    }

    override fun onBackPressed() {
        if (anoleView?.handleBackPressed() == true) {
            return
        }
        super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val etLocation = IntArray(2)
            binding.etTitle.getLocationInWindow(etLocation)
            if (ev.rawX.toInt() !in (etLocation[0])..(etLocation[0] + binding.etTitle.width) ||
                ev.rawY.toInt() !in (etLocation[1])..(etLocation[1] + binding.etTitle.height)
            ) {
                binding.etTitle.clearFocus()
                hideIme()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideIme() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etTitle.windowToken, 0)
    }

    private fun EditText.getUrl(): String? {
        val input = text.toString()
        try {
            val uri = Uri.parse(input)
            if ("http" == uri.scheme || "https" == uri.scheme) {
                return input
            }
            if (!uri.host.isNullOrBlank()) {
                return "https://$input"
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun showWindowChoiceDialog() {
        BottomSheetDialog(this).apply {
            val list = binding.container.children.map {
                it as AnoleView
            }.toList().asReversed().toMutableList()
            val binding = DialogWindowChoiceBinding.inflate(layoutInflater)
            val adapter = WindowChoiceAdapter(list).apply {
                onChoice = { p ->
                    val anoleView = list[p]
                    list.add(list.removeAt(p))
                    notifyItemMoved(p, list.lastIndex)
                    binding.rvWindows.smoothScrollToPosition(0)
                    anoleView.bringToFront()
                    refreshTopWeb()
                    dismiss()
                }
                onDelete = { p ->
                    val anoleView = list.removeAt(p)
                    notifyItemRemoved(p)
                    binding.tvWindowsCount.text = "${list.size}"
                    closeWeb(anoleView)
                }
            }
            binding.rvWindows.layoutManager = LinearLayoutManager(context)
            binding.rvWindows.adapter = adapter
            binding.tvWindowsCount.text = "${list.size}"
            binding.ivAdd.setOnClickListener {
                val anoleView = createWeb()
                list.add(0, anoleView)
                adapter.notifyItemInserted(0)
                binding.tvWindowsCount.text = "${list.size}"
                binding.rvWindows.smoothScrollToPosition(0)
            }
            binding.ivDelete.setOnClickListener {
                val count = list.size
                list.clear()
                adapter.notifyItemRangeRemoved(0, count)
                binding.tvWindowsCount.text = "${list.size}"
                closeAllWeb()
                dismiss()
            }
            setContentView(
                binding.root, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            setOnDismissListener {
                adapter.removeAllPageInfoAbilities()
            }
            show()
        }
    }
}

private class WindowChoiceAdapter(
    private val list: MutableList<AnoleView>
) : RecyclerView.Adapter<WindowChoiceAdapter.Holder>() {
    var onChoice: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null

    private val pageInfoAbilities = HashMap<AnoleView, PageInfoAbility>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemWindowChoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val anoleView = list[position]
        holder.bind(anoleView)
        var pageInfoAbility = pageInfoAbilities[anoleView]
        if (pageInfoAbility == null) {
            pageInfoAbility = PageInfoAbility(onReceivedPageTitle = {
                notifyItemByPageInfoAbility(this)
            }, onReceivedPageIcon = {
                notifyItemByPageInfoAbility(this)
            })
            pageInfoAbilities[anoleView] = pageInfoAbility
        }
        if (!anoleView.client.containsAbility(pageInfoAbility)) {
            anoleView.client.addAbility(pageInfoAbility)
        }
    }

    private fun notifyItemByPageInfoAbility(pageInfoAbility: PageInfoAbility) {
        pageInfoAbilities.entries.forEach {
            if (it.value == pageInfoAbility) {
                val index = list.indexOf(it.key)
                if (index != -1) {
                    try {
                        notifyItemChanged(index)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        }
    }

    fun removeAllPageInfoAbilities() {
        pageInfoAbilities.entries.forEach {
            it.key.client.removeAbility(it.value)
        }
    }

    inner class Holder(private val binding: ItemWindowChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onChoice?.invoke(adapterPosition)
            }
            binding.ivDelete.setOnClickListener {
                onDelete?.invoke(adapterPosition)
            }
        }

        fun bind(anoleView: AnoleView) {
            anoleView.favicon?.let {
                binding.ivLogo.setImageBitmap(it)
            } ?: binding.ivLogo.setImageResource(R.drawable.ic_browser)
            binding.tvTitle.text = anoleView.title
        }
    }
}


inline fun log(tag: String = "Anole", context: () -> String?) {
    Log.d(tag, context.invoke() ?: "null")
}