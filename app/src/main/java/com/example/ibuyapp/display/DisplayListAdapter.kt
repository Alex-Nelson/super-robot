package com.example.ibuyapp.display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ibuyapp.data.UserList
import com.example.ibuyapp.databinding.ListItemDisplayListBinding

/**
 * An Adapter for the RecyclerView in DisplayFragment.
 *
 * */
class DisplayListAdapter :
    ListAdapter<UserList, DisplayListAdapter.ListViewHolder>(ListDiffCallback()){

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder.from(parent)
    }

    class ListViewHolder private constructor(private val binding: ListItemDisplayListBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: UserList){
            binding.list = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDisplayListBinding.inflate(
                    layoutInflater, parent, false)
                return ListViewHolder(binding)
            }
        }
    }
}

class ListDiffCallback : DiffUtil.ItemCallback<UserList>(){
    override fun areItemsTheSame(oldItem: UserList, newItem: UserList): Boolean {
        return oldItem.listId == newItem.listId
    }

    override fun areContentsTheSame(oldItem: UserList, newItem: UserList): Boolean {
        return oldItem == newItem
    }

}