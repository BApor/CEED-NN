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
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.adapter.GroupDetailsAdapter
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.databinding.FragmentDetailsBinding
import com.example.ceed_nn.view.AppViewModel
import java.nio.file.attribute.GroupPrincipal

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        appViewModel.fetchSeedClassesFromAssets()

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewSeedGroups
        val adapter = GroupDetailsAdapter(
            onItemClick = {group -> navigateToSeedDetails(group)}
        )
        recyclerView.adapter = adapter

        appViewModel.fetchDetectionDetails()
        appViewModel.fetchTotalProperties()

        adapter.submitSeedGroupList(appViewModel.seedGroups)

        return root
    }

    private fun navigateToSeedDetails(seedGroup: SeedGroupDTO) {
        findNavController().navigate(
            R.id.to_seedDetailsFragment,
            bundleOf("seedGroupIndex" to seedGroup.index)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}