package com.example.ceed_nn.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ceed_nn.R
import com.example.ceed_nn.databinding.FragmentSeedSizeDetailsBinding
import com.example.ceed_nn.view.AppViewModel

class SeedSizeDetailsFragment : Fragment() {

    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appViewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_seed_size_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var seedDetectionId = 0
        var seedDetectionClassId = 0
        arguments?.let {
            seedDetectionId = it.getInt("seedDetectionId", -1)
            seedDetectionClassId = it.getInt("seedDetectionClassId", -1)
        }

        if (seedDetectionId == -1 || seedDetectionClassId == -1) {
            Log.e("SeedSizeDetailsFragment", "Invalid seed detection ID")
            return
        }

        appViewModel.fetchSeedSizeDetails(seedDetectionId, seedDetectionClassId)

        view.findViewById<ImageView>(R.id.imageView).setImageBitmap(appViewModel.seedSizeResults.photo)
        view.findViewById<TextView>(R.id.verDiagText).text = "Vertical diagonal: ${appViewModel.seedSizeResults.verDiagSize} mm"
        view.findViewById<TextView>(R.id.horDiagText).text = "Horizontal diagonal: ${appViewModel.seedSizeResults.horDiagSize} mm"
    }
}