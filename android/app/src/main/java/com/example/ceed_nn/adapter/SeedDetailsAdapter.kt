package com.example.ceed_nn.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
class SeedDetailsAdapter : RecyclerView.Adapter<SeedDetailsAdapter.SeedDetailsViewHolder>(){

    private var seedDetailsList: List<SeedDetectionDTO> = emptyList()

    fun submitSeedDetailsList(seedDeta: List<SeedDetectionDTO>) {
        seedDetailsList = seedDeta
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeedDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seed, parent, false)
        return SeedDetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int = seedDetailsList.size

    override fun onBindViewHolder(holder: SeedDetailsViewHolder, position: Int) {
        val currentSeedDetails = seedDetailsList[position]
        holder.bind(currentSeedDetails)
    }

    inner class SeedDetailsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        fun bind(seedDetails: SeedDetectionDTO) {
            val seedDetectionThumbnail = itemView.findViewById<ImageView>(R.id.seedDetectionImage)
            val seedDetectionArea = itemView.findViewById<TextView>(R.id.seedDetectionArea)
            val seedDetectionMass = itemView.findViewById<TextView>(R.id.seedDetectionMass)

            seedDetectionThumbnail.setImageBitmap(seedDetails.photo)
            seedDetectionArea.text = "${seedDetails.seedArea} mm^2"
            seedDetectionMass.text = "${seedDetails.seedMass} g"
        }
    }


}
