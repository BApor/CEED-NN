package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ceed_nn.adapter.GroupDetailsAdapter
import com.example.ceed_nn.adapter.SeedDetailsAdapter
import com.example.ceed_nn.databinding.FragmentDetailsBinding
import com.example.ceed_nn.databinding.FragmentSeeddetailsBinding
import com.example.ceed_nn.view.AppViewModel

class SeedDetailsFragment : Fragment() {

    private var _binding: FragmentSeeddetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val appViewModel =
            ViewModelProvider(this).get(AppViewModel::class.java)

        _binding = FragmentSeeddetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewSeedDetails
        val adapter = SeedDetailsAdapter()
        recyclerView.adapter = adapter

        val seedGroupIndex = arguments?.getInt("seedGroupIndex", -1)

        if (seedGroupIndex == -1) {
            Log.e("RecipeDetailsData", "Invalid recipe ID")
            return root
        }

        adapter.submitSeedDetailsList(appViewModel.seedGroups.find { it.index == seedGroupIndex}!!.seeds)

        return root
    }
}