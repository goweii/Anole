package per.goweii.android.anole

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import per.goweii.android.anole.databinding.*
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.view.KernelView

class MainActivity : AppCompatActivity() {
    companion object {
        private const val KEY_PAGE_URLS = "page_urls"
    }

    private lateinit var binding: ActivityMainBinding
    private var showHome = true
    private var kernelView: KernelView? = null

    private val bookmarkAdapter = BookmarkAdapter()

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
        binding.ivBack.setOnClickListener {
            kernelView?.goBackOrForward(-1)
        }
        binding.ivForward.setOnClickListener {
            kernelView?.goBackOrForward(1)
        }
        binding.ivMenu.setOnClickListener {
            showMenuDialog()
        }
        binding.ivHome.setOnClickListener {
            showHome = true
            refreshTopWeb()
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
                    loadUrl(getSearchUrl(binding.etTitle.text.toString()))
                } else {
                    loadUrl(inputUrl)
                }
                hideIme()
            }
        }
        binding.etTitle.setOnFocusChangeListener { _, b ->
            binding.ivEnterOrSearch.setImageResource(R.drawable.ic_enter)
            if (b) {
                binding.etTitle.setText(kernelView?.url)
            } else {
                binding.etTitle.setText(kernelView?.title)
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
        binding.rvBookmark.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = bookmarkAdapter
            bookmarkAdapter.onClickItem = {
                createWeb(it.url)
            }
            bookmarkAdapter.onLongClickItem = {
                removeBookmark(it)
            }
        }
        loadBookmarks()
        if (savedInstanceState != null) {
            val pageUrls = savedInstanceState.getStringArrayList(KEY_PAGE_URLS)
            if (!pageUrls.isNullOrEmpty()) {
                pageUrls.forEach {
                    createWeb(it)
                }
            }
        }
        refreshTopWeb()
        intent?.let { parseIntentToOpenUrl(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { parseIntentToOpenUrl(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val pageUrls = arrayListOf<String>()
        binding.container.children.map {
            it as KernelView
            it.url
        }.filter {
            !it.isNullOrBlank()
        }.forEach {
            pageUrls.add(it!!)
        }
        outState.putStringArrayList(KEY_PAGE_URLS, pageUrls)
    }

    private fun parseIntentToOpenUrl(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                if ("http" == it.scheme || "https" == it.scheme) {
                    createWeb(it.toString())
                }
            }
        }
    }

    private fun loadBookmarks() {
        val bookmarks = BookmarkHelper.getInstance(this).get()
        bookmarkAdapter.setData(bookmarks)
    }

    private fun addOrUpdateBookmark(url: String, title: String, logo: Bitmap?) {
        var bookmark = BookmarkHelper.getInstance(this).update(url, title, logo)
        if (bookmark != null) {
            bookmarkAdapter.updateData(bookmark)
        } else {
            bookmark = BookmarkHelper.getInstance(this).add(url, title, logo)
            bookmarkAdapter.addData(bookmark)
        }
    }

    private fun removeBookmark(bookmark: Bookmark) {
        if (BookmarkHelper.getInstance(this).remove(bookmark.url) != null) {
            bookmarkAdapter.removeData(bookmark)
        }
    }

    private fun closeAllWeb() {
        binding.container.children.map {
            it as KernelView
        }.also {
            binding.container.removeAllViews()
            it.forEach { kernelView ->
                closeWeb(kernelView)
            }
        }
        refreshTopWeb()
    }

    private fun closeCurrWeb() {
        binding.container.children.lastOrNull()?.let {
            closeWeb(it as KernelView)
        }
    }

    private fun closeWeb(kernelView: KernelView) {
        val parent = kernelView.parent
        if (parent != null) {
            parent as ViewGroup
            parent.removeView(kernelView)
        }
        kernelView.destroy()
        refreshTopWeb()
    }

    private fun showWeb(kernelView: KernelView) {
        kernelView.bringToFront()
        showHome = false
        refreshTopWeb()
    }

    private fun createWeb(url: String = getHomeUrl()): KernelView {
        showHome = false
        val kernelView = WebFactory.with(this)
            .applyDefaultConfig()
            .attachTo(binding.container)
            .bindToLifecycle(this)
            .get()
        kernelView.loadUrl(url)
        refreshTopWeb()
        return kernelView
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
        kernelView = null
        setCurrTitle(null)
        setCurrLogo(null)
        for (i in 0 until binding.container.childCount) {
            val kernelView = binding.container.getChildAt(i) as KernelView
            kernelView.visibility = View.GONE
            kernelView.webClient.removeAbility(backForwardIconAbility)
            kernelView.webClient.removeAbility(pageInfoAbility)
            if (i == binding.container.childCount - 1) {
                if (!showHome) {
                    this.kernelView = kernelView
                    kernelView.visibility = View.VISIBLE
                    if (!kernelView.webClient.containsAbility(backForwardIconAbility)) {
                        kernelView.webClient.addAbility(backForwardIconAbility)
                    }
                    if (!kernelView.webClient.containsAbility(pageInfoAbility)) {
                        kernelView.webClient.addAbility(pageInfoAbility)
                    }
                }
            }
        }
    }

    private fun loadUrl(url: String) {
        kernelView?.loadUrl(url) ?: createWeb(url)
    }

    private fun getHomeUrl(): String {
        return "https://www.baidu.com"
    }

    private fun getSearchUrl(str: String): String {
        return "https://www.baidu.com/s?wd=$str"
    }

    override fun onBackPressed() {
        if (kernelView?.canGoBack == true) {
            kernelView?.goBack()
            return
        }
        if (binding.container.childCount > 0) {
            closeCurrWeb()
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
        } catch (e: Exception) {
        }
        return null
    }

    private fun showMenuDialog() {
        BottomSheetDialog(this).apply {
            val list = arrayListOf<MenuAction>()
            list.add(MenuAction("收藏", R.drawable.ic_bookmark) {
                dismiss()
                val kernelView = kernelView ?: return@MenuAction
                val title = kernelView.title ?: return@MenuAction
                val url = kernelView.url ?: return@MenuAction
                val logo = kernelView.favicon
                addOrUpdateBookmark(url, title, logo)
            })
            list.add(MenuAction("刷新", R.drawable.ic_reload) {
                dismiss()
                kernelView?.reload()
            })
            list.add(MenuAction("扫码", R.drawable.ic_scan) {
                dismiss()
                startActivity(Intent(this@MainActivity, ScanActivity::class.java))
            })
            list.add(MenuAction("退出", R.drawable.ic_finish) {
                dismiss()
                finish()
            })
            val binding = DialogMenuBinding.inflate(layoutInflater)
            binding.rvMenu.apply {
                layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                adapter = MenuAdapter(list)
            }
            binding.ivClose.setOnClickListener {
                dismiss()
            }
            setContentView(
                binding.root, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }.show()
    }

    private fun showWindowChoiceDialog() {
        BottomSheetDialog(this).apply {
            val list = binding.container.children.map {
                it as KernelView
            }.toList().asReversed().toMutableList()
            val binding = DialogWindowChoiceBinding.inflate(layoutInflater)
            val adapter = WindowChoiceAdapter(list).apply {
                onChoice = { p ->
                    val kernelView = list[p]
                    list.add(list.removeAt(p))
                    notifyItemMoved(p, list.lastIndex)
                    binding.rvWindows.smoothScrollToPosition(0)
                    showWeb(kernelView)
                    dismiss()
                }
                onDelete = { p ->
                    val kernelView = list.removeAt(p)
                    notifyItemRemoved(p)
                    binding.tvWindowsCount.text = "${list.size}"
                    closeWeb(kernelView)
                }
            }
            binding.rvWindows.layoutManager = LinearLayoutManager(context)
            binding.rvWindows.adapter = adapter
            binding.tvWindowsCount.text = "${list.size}"
            binding.ivAdd.setOnClickListener {
                val kernelView = createWeb()
                list.add(0, kernelView)
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
        }.show()
    }
}