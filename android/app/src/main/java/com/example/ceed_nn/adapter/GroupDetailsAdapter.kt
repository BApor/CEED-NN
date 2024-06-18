package com.example.ceed_nn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.data.stuctures.SeedGroupDTO

class GroupDetailsAdapter(
    private val onItemClick: (SeedGroupDTO) -> Unit
) : RecyclerView.Adapter<GroupDetailsAdapter.GroupDetailsViewHolder>(){

    private var seedGroupList: List<SeedGroupDTO> = emptyList()

    fun submitSeedGroupList(newGroupList: List<SeedGroupDTO>) {
        seedGroupList = newGroupList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupDetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int = seedGroupList.size

    override fun onBindViewHolder(holder: GroupDetailsViewHolder, position: Int) {
        val currentGroup = seedGroupList[position]
        holder.bind(currentGroup)
    }
    inner class GroupDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentGroup = seedGroupList[position]
                    onItemClick.invoke(currentGroup)
                }
            }
        }

        fun bind(seedGroup: SeedGroupDTO) {
            val seedTypeThumbnail = itemView.findViewById<ImageView>(R.id.seedTypeImage)
            val seedTypeName = itemView.findViewById<TextView>(R.id.seedTypeName)
            val seedTypeArea = itemView.findViewById<TextView>(R.id.seedTypeArea)
            val seedTypeMass = itemView.findViewById<TextView>(R.id.seedTypeMass)
            seedTypeThumbnail.setImageBitmap(seedGroup.photo)
            seedTypeName.text = seedGroup.name
            seedTypeArea.text = "Group Area: ${seedGroup.totalArea}"
            seedTypeMass.text = "Group Mass: ${seedGroup.totalMass}"
        }
    }
}