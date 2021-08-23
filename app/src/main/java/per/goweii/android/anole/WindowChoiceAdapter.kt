package per.goweii.android.anole

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import per.goweii.android.anole.databinding.ItemWindowChoiceBinding
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.view.KernelView

class WindowChoiceAdapter(
    private val list: MutableList<KernelView>
) : RecyclerView.Adapter<WindowChoiceAdapter.Holder>() {
    var onChoice: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null

    private val pageInfoAbilities = HashMap<KernelView, PageInfoAbility>()

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
        val kernelView = list[position]
        holder.bind(kernelView)
        var pageInfoAbility = pageInfoAbilities[kernelView]
        if (pageInfoAbility == null) {
            pageInfoAbility = PageInfoAbility(onReceivedPageTitle = {
                notifyItemByPageInfoAbility(this)
            }, onReceivedPageIcon = {
                notifyItemByPageInfoAbility(this)
            })
            pageInfoAbilities[kernelView] = pageInfoAbility
        }
        if (!kernelView.webClient.containsAbility(pageInfoAbility)) {
            kernelView.webClient.addAbility(pageInfoAbility)
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
            it.key.webClient.removeAbility(it.value)
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

        fun bind(kernelView: KernelView) {
            kernelView.favicon?.let {
                binding.ivLogo.setImageBitmap(it)
            } ?: binding.ivLogo.setImageResource(R.drawable.ic_browser)
            binding.tvTitle.text = kernelView.title
        }
    }
}