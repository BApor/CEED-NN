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

    override fun onBindViewHolder(holder: GroupDetailsViewHolder, position: Int) {
        val currentGroup = seedGroupList[position]
        holder.bind(currentGroup)
    }

    override fun getItemCount(): Int = seedGroupList.size


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
            val seedGroupName = itemView.findViewById<TextView>(R.id.seedGroupName)
            val seedGroupNumber = itemView.findViewById<TextView>(R.id.seedGroupNumber)
            val seedGroupNumberPercent = itemView.findViewById<TextView>(R.id.seedGroupNumberPercent)
            val seedGroupArea = itemView.findViewById<TextView>(R.id.seedGroupArea)
            val seedGroupAreaPercent = itemView.findViewById<TextView>(R.id.seedGroupAreaPercent)
            val seedGroupMass = itemView.findViewById<TextView>(R.id.seedGroupMass)
            val seedGroupMassPercent = itemView.findViewById<TextView>(R.id.seedGroupMassPercent)

            seedGroupName.text = seedGroup.name
            seedGroupNumber.text = "Number of Seeds: ${seedGroup.seeds.size}"
            seedGroupNumberPercent.text = "Seed Number Ratio: ${seedGroup.percentageSeedNumber} %"
            seedGroupArea.text = "Group Area: ${seedGroup.groupArea} mm^2"
            seedGroupAreaPercent.text = "Group Area Ratio: ${seedGroup.percentageArea} %"
            seedGroupMass.text = "Group Mass: ${seedGroup.groupMass} g"
            seedGroupMassPercent.text = "Group Mass Ratio: ${seedGroup.percentageMass} %"
        }
    }
}