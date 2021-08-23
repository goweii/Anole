package per.goweii.android.anole

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import per.goweii.android.anole.databinding.ItemBookmarkBinding

class BookmarkAdapter : RecyclerView.Adapter<BookmarkAdapter.Holder>() {
    private val list = arrayListOf<Bookmark>()
    var onClickItem: ((Bookmark) -> Unit)? = null
    var onLongClickItem: ((Bookmark) -> Unit)? = null

    fun setData(bookmarks: List<Bookmark>) {
        list.clear()
        list.addAll(bookmarks)
        notifyDataSetChanged()
    }

    fun addData(bookmark: Bookmark) {
        list.add(bookmark)
        notifyItemInserted(list.lastIndex)
    }

    fun removeData(bookmark: Bookmark) {
        val index = list.indexOf(bookmark)
        if (index >= 0) {
            list.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateData(bookmark: Bookmark) {
        val index = list.indexOf(bookmark)
        if (index >= 0) {
            notifyItemChanged(index)
        }
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
            binding.root.setOnClickListener {
                onClickItem?.invoke(list[adapterPosition])
            }
            binding.root.setOnLongClickListener {
                onLongClickItem?.invoke(list[adapterPosition])
                return@setOnLongClickListener true
            }
        }

        fun bind(bookmark: Bookmark) {
            if (bookmark.logo != null) {
                binding.ivLogo.setImageBitmap(bookmark.logo)
            } else {
                binding.ivLogo.setImageResource(R.drawable.ic_browser)
            }
            binding.tvTitle.text = bookmark.title
        }
    }
}