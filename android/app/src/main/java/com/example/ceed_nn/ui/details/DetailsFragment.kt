package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.adapter.GroupDetailsAdapter
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.databinding.FragmentDetailsBinding
import com.example.ceed_nn.util.NumUtil
import com.example.ceed_nn.view.AppViewModel
import java.nio.file.attribute.GroupPrincipal

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

        view.findViewById<TextView>(R.id.textView).text= "${NumUtil.floatRoundTo(appViewModel.totalArea, 2)} mm^2"
        adapter.submitSeedGroupList(appViewModel.seedGroups)
    }

    private fun navigateToSeedDetails(seedGroup: SeedGroupDTO) {
        findNavController().navigate(
            R.id.to_seedDetailsFragment,
            bundleOf("seedGroupIndex" to seedGroup.index)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}