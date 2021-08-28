package per.goweii.android.anole.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.RvItemChoiceDefSearchBinding
import per.goweii.android.anole.utils.DefSearch

class ChoiceDefSearchAdapter(
    private val list: List<DefSearch.CustomSearch>
) : RecyclerView.Adapter<ChoiceDefSearchAdapter.Holder>() {
    var onChoice: ((DefSearch.CustomSearch) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            RvItemChoiceDefSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).also { holder ->
            holder.binding.ivLogo.setOnClickListener {
                onChoice?.invoke(list[holder.adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val customSearch = list[position]
        when {
            customSearch.logoRes != null -> holder.binding.ivLogo.load(customSearch.logoRes!!)
            customSearch.logoUrl != null -> holder.binding.ivLogo.load(customSearch.logoUrl!!)
            else -> holder.binding.ivLogo.setImageResource(R.drawable.ic_browser)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(
        val binding: RvItemChoiceDefSearchBinding
    ) : RecyclerView.ViewHolder(binding.root)
}