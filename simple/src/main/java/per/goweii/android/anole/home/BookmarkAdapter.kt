package per.goweii.android.anole.home

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.ItemBookmarkBinding
import java.util.*

class BookmarkAdapter : RecyclerView.Adapter<BookmarkAdapter.Holder>() {
    private val list = arrayListOf<Bookmark>()

    private val itemTouchHelper = ItemTouchHelper(ItemTouchCallback())
    private val itemTouchListener = ItemTouchListener()

    var onClickItem: ((Bookmark) -> Unit)? = null
    var onRemoveItem: ((Bookmark) -> Unit)? = null
    var onEditModeChanged: ((Boolean) -> Unit)? = null
    var onItemSwap: ((from: Bookmark, to: Bookmark) -> Unit)? = null

    var editMode = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
                onEditModeChanged?.invoke(value)
            }
        }

    fun setData(bookmarks: List<Bookmark>) {
        DiffUtil.calculateDiff(
            BookmarkDiffCallback(
                oldList = list,
                newList = bookmarks
            )
        ).let {
            list.clear()
            list.addAll(bookmarks)
            it.dispatchUpdatesTo(this)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnItemTouchListener(itemTouchListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnItemTouchListener(itemTouchListener)
        itemTouchHelper.attachToRecyclerView(null)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(private val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivLogo.setOnClickListener {
                list.getOrNull(adapterPosition)?.let {
                    if (!editMode) {
                        onClickItem?.invoke(it)
                    }
                }
            }
            binding.ivLogo.setOnLongClickListener {
                editMode = true
                return@setOnLongClickListener true
            }
            binding.ivRemove.setOnClickListener {
                list.getOrNull(adapterPosition)?.let {
                    onRemoveItem?.invoke(it)
                }
            }
        }

        fun bind(bookmark: Bookmark) {
            if (bookmark.logo != null) {
                binding.ivLogo.setImageBitmap(bookmark.logo)
            } else {
                binding.ivLogo.setImageResource(R.drawable.ic_browser)
            }
            binding.tvTitle.text = bookmark.title
            binding.ivRemove.isVisible = editMode
        }
    }

    private inner class ItemTouchListener : RecyclerView.OnItemTouchListener,
        GestureDetector.OnGestureListener {
        private val rect = Rect()
        private val gestureDetector = GestureDetector(this)

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (!editMode) return false
            rv.forEach {
                rect.set(it.left, it.top, it.right, it.bottom)
                if (rect.contains(e.x.toInt(), e.y.toInt())) {
                    return false
                }
            }
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            if (editMode) {
                gestureDetector.onTouchEvent(e)
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }

        override fun onDown(e: MotionEvent): Boolean {
            return editMode
        }

        override fun onShowPress(p0: MotionEvent) {
        }

        override fun onSingleTapUp(p0: MotionEvent): Boolean {
            editMode = false
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, dx: Float, dy: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, vx: Float, vy: Float): Boolean {
            return false
        }
    }

    private inner class ItemTouchCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = if (editMode) {
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            } else {
                0
            }
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(list, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            val fromBookmark = list[fromPosition]
            val toBookmark = list[toPosition]
            onItemSwap?.invoke(fromBookmark, toBookmark)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }
}