package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.adapter.SeedDetailsAdapter
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.databinding.FragmentSeedDetailsBinding
import com.example.ceed_nn.view.AppViewModel

class SeedDetailsFragment : Fragment() {

    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appViewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_seed_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSeedDetails)
        val adapter = SeedDetailsAdapter(
            onItemClick = {seedDetection -> navigateToSeedSizeDetails(seedDetection)}
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val seedGroupId = arguments?.getInt("seedGroupId", -1)

        if (seedGroupId == -1) {
            Log.e("SeedDetailsFragment", "Invalid seed group ID")
            return
        }

        adapter.submitSeedDetailsList(appViewModel.seedGroups.find { it.id == seedGroupId}!!.seeds)

    }

    private fun navigateToSeedSizeDetails(seedDetection: SeedDetectionDTO) {
        findNavController().navigate(
            R.id.to_seedSizeDetailsFragment,
            bundleOf(
                "seedDetectionId" to seedDetection.id,
                "seedDetectionClassId" to seedDetection.classId
            )
        )
    }
}