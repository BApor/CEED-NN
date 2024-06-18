package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.R
import com.example.ceed_nn.adapter.GroupDetailsAdapter
import com.example.ceed_nn.adapter.SeedDetailsAdapter
import com.example.ceed_nn.databinding.FragmentDetailsBinding
import com.example.ceed_nn.databinding.FragmentSeeddetailsBinding
import com.example.ceed_nn.view.AppViewModel

class SeedDetailsFragment : Fragment() {

    private var _binding: FragmentSeeddetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeeddetailsBinding.inflate(inflater, container, false)
        appViewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_seeddetails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSeedDetails)
        val adapter = SeedDetailsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val seedGroupIndex = arguments?.getInt("seedGroupIndex", -1)

        if (seedGroupIndex == -1) {
            Log.e("RecipeDetailsData", "Invalid recipe ID")
            return
        }

        adapter.submitSeedDetailsList(appViewModel.seedGroups.find { it.index == seedGroupIndex}!!.seeds)

    }
}