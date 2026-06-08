package com.payloadSender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PresetAdapter(
    private val presets: MutableList<Preset>,
    private val onPresetClick: (Preset) -> Unit,
    private val onDeleteClick: (Preset) -> Unit,
    private val onAutoloadClick: (Preset) -> Unit
) : RecyclerView.Adapter<PresetAdapter.PresetViewHolder>() {

    inner class PresetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPresetName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvPresetDetails)
        val btnLoad: Button = itemView.findViewById(R.id.btnLoadPreset)
        val btnAutoload: Button = itemView.findViewById(R.id.btnAddAutoload)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeletePreset)

        fun bind(preset: Preset) {
            tvName.text = preset.name
            tvDetails.text = "${preset.ipAddress}:${preset.port} • ${preset.payloadFileName}"

            btnLoad.setOnClickListener { onPresetClick(preset) }
            btnAutoload.setOnClickListener { onAutoloadClick(preset) }
            btnDelete.setOnClickListener { onDeleteClick(preset) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preset, parent, false)
        return PresetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(presets[position])
    }

    override fun getItemCount() = presets.size

    fun updateList(newPresets: List<Preset>) {
        presets.clear()
        presets.addAll(newPresets)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        presets.removeAt(position)
        notifyItemRemoved(position)
    }
}
