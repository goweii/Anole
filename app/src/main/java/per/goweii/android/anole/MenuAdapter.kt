package per.goweii.android.anole

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import per.goweii.android.anole.databinding.ItemMenuBinding

class MenuAdapter(
    private val list: List<MenuAction>
) : RecyclerView.Adapter<MenuAdapter.Holder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                list[adapterPosition].action.invoke()
            }
        }

        fun bind(menuAction: MenuAction) {
            binding.ivIcon.setImageResource(menuAction.icon)
            binding.tvName.text = menuAction.name
        }
    }
}