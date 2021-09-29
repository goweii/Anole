package per.goweii.android.anole.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.ItemDefSearchBinding
import per.goweii.android.anole.utils.DefSearch

class SelectDefSearchAdapter(
    private val list: List<DefSearch.CustomSearch>
) : RecyclerView.Adapter<SelectDefSearchAdapter.Holder>() {

    var onItemClick: ((DefSearch.CustomSearch) -> Unit)? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemDefSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(private val binding: ItemDefSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivIcon.setOnClickListener {
                onItemClick?.invoke(list[adapterPosition])
            }
        }

        fun bind(customSearch: DefSearch.CustomSearch) {
            customSearch.logoRes?.let {
                binding.ivIcon.setImageResource(it)
            } ?: customSearch.logoUrl?.let {
                binding.ivIcon.load(customSearch.logoUrl)
            } ?: kotlin.run {
                binding.ivIcon.setImageResource(R.drawable.ic_browser)
            }
            binding.tvName.text = customSearch.name
        }
    }
}