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
            val seedGroupThumbnail = itemView.findViewById<ImageView>(R.id.seedGroupImage)
            val seedGroupName = itemView.findViewById<TextView>(R.id.seedGroupName)
            val seedGroupArea = itemView.findViewById<TextView>(R.id.seedGroupArea)
            val seedGroupMass = itemView.findViewById<TextView>(R.id.seedGroupMass)

            seedGroupThumbnail.setImageBitmap(seedGroup.photo)
            seedGroupName.text = seedGroup.name
            seedGroupArea.text = "Group Area: ${seedGroup.totalArea}"
            seedGroupMass.text = "Group Mass: ${seedGroup.totalMass}"
        }
    }
}