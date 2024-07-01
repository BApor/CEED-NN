package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.adapter.GroupDetailsAdapter
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.view.AppViewModel

class DetailsFragment : Fragment() {

    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appViewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        appViewModel.fetchSeedClassesFromAssets()

        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSeedGroups)
        val adapter = GroupDetailsAdapter(
            onItemClick = {group -> navigateToSeedDetails(group)}
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appViewModel.fetchDetectionDetails()
        appViewModel.fetchTotalProperties()

        view.findViewById<TextView>(R.id.totalSeedNumber).text = "Total number of seeds: ${appViewModel.totalNumber}"
        view.findViewById<TextView>(R.id.totalArea).text = "Total area: ${appViewModel.totalArea} mm^2"
        view.findViewById<TextView>(R.id.totalMass).text = "Total mass: ${appViewModel.totalMass} g"

        adapter.submitSeedGroupList(appViewModel.seedGroups)
    }

    private fun navigateToSeedDetails(seedGroup: SeedGroupDTO) {
        findNavController().navigate(
            R.id.to_seedDetailsFragment,
            bundleOf("seedGroupId" to seedGroup.id)
        )
    }
}