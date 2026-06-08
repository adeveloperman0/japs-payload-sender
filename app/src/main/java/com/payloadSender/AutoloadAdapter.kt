package com.payloadSender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AutoloadAdapter(
    private val items: MutableList<AutoloadItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PRESET = 0
        private const val TYPE_DELAY = 1
    }

    inner class PresetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAutoloadPresetName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvAutoloadPresetDetails)
        val btnDelete: Button = itemView.findViewById(R.id.btnAutoloadDelete)

        fun bind(item: AutoloadItem.PresetItem) {
            tvName.text = item.preset.name
            tvDetails.text = "${item.preset.ipAddress}:${item.preset.port}"
            btnDelete.setOnClickListener { onDeleteClick(adapterPosition) }
        }
    }

    inner class DelayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDelay: TextView = itemView.findViewById(R.id.tvDelayText)

        fun bind(item: AutoloadItem.DelayItem) {
            tvDelay.text = "⏱ Espera ${item.delaySeconds}s"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is AutoloadItem.PresetItem -> TYPE_PRESET
            is AutoloadItem.DelayItem -> TYPE_DELAY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PRESET -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_autoload_preset, parent, false)
                PresetViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_autoload_delay, parent, false)
                DelayViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is AutoloadItem.PresetItem -> (holder as PresetViewHolder).bind(item)
            is AutoloadItem.DelayItem -> (holder as DelayViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<AutoloadItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
