package com.example.ceed_nn.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO

class SeedDetailsAdapter(
    private val onItemClick: (SeedDetectionDTO) -> Unit
) : RecyclerView.Adapter<SeedDetailsAdapter.SeedDetailsViewHolder>(){

    private var seedDetectionsList: List<SeedDetectionDTO> = emptyList()

    fun submitSeedDetailsList(seedDeta: List<SeedDetectionDTO>) {
        seedDetectionsList = seedDeta
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeedDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seed, parent, false)
        return SeedDetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int = seedDetectionsList.size

    override fun onBindViewHolder(holder: SeedDetailsViewHolder, position: Int) {
        val currentSeedDetails = seedDetectionsList[position]
        holder.bind(currentSeedDetails)
    }

    inner class SeedDetailsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentDetection = seedDetectionsList[position]
                    onItemClick.invoke(currentDetection)
                }
            }
        }

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
