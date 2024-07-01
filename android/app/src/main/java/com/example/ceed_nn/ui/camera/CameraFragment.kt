package com.example.ceed_nn.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ceed_nn.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Size
import android.view.Surface
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.camera.core.CameraControl
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ceed_nn.R
import com.example.ceed_nn.view.AppViewModel
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraControl: CameraControl
    private lateinit var cameraExecutor: ExecutorService

    private var isFlashOn = false
    private var isBBoxesEnabled = false

    private lateinit var appViewModel: AppViewModel

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        appViewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        appViewModel.initializeRepositories(requireContext())

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val flashToggleSwitch: Button = binding.flashSwitch
        flashToggleSwitch.setOnClickListener {
            toggleFlash()
        }

        val bboxesToggleSwitch: Button = binding.bboxSwitch
        bboxesToggleSwitch.setOnClickListener {
            toggleBBoxes()
        }

        configureSpinner()
        appViewModel.setDetEngineModel(binding.modelSpinner.selectedItem.toString())

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        return root
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        cameraControl.enableTorch(isFlashOn)
    }

    private fun toggleBBoxes() {
        isBBoxesEnabled = !isBBoxesEnabled
    }

    private fun configureSpinner() {
        val spinner = binding.modelSpinner

        val modelList = arrayOf<String?>("YOLOv8", "YOLOv6", "YOLOv5", "YOLOv3")

        val mArrayAdapter = ArrayAdapter<Any?>(requireContext(), R.layout.item_spinner, modelList)
        mArrayAdapter.setDropDownViewResource(R.layout.item_spinner)

        spinner.adapter = mArrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    appViewModel.setDetEngineModel(selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetResolution(Size(1080, 1088))
                .build()

            preview.setSurfaceProvider { request ->
                val surface = Surface(binding.textureView.surfaceTexture)
                request.provideSurface(surface, cameraExecutor) {}
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1088))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processFrame(imageProxy)
            }

            try {
                val camera = cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,
                    imageAnalysis, preview)
                cameraControl = camera.cameraControl
            } catch (excep: Exception) {
                Log.e("CameraFragment", "Camera binding failed", excep)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun processFrame(imageProxy: ImageProxy) {
        appViewModel.setCurrentFrame(imageProxy)
        appViewModel.fetchDetections()

        if (isAdded && view != null)
            requireActivity().runOnUiThread {
                if (isBBoxesEnabled)
                    drawBoundingBoxes(appViewModel.detections)
                else
                    binding.imageView.setImageResource(0)

                val modelTime = appViewModel.time
                binding.msTextView.text = "${modelTime.toFloat()} ms"
            }

        imageProxy.close()
    }

    private fun drawBoundingBoxes(detections: List<SeedDetectionDTO>) {
        val bitmap = Bitmap.createBitmap(1080, 1088, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val rectPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        val textPaint = Paint().apply {
            color = Color.GREEN
            textSize = 50f
            textAlign = Paint.Align.LEFT
        }

        val referenceWarningPaint = Paint().apply {
            color = Color.RED
            textSize = 80f
            textAlign = Paint.Align.LEFT
        }

        if (detections.isEmpty()){
            canvas.drawText("Reference not calculated yet!", 30f, 100f, referenceWarningPaint)
        } else
            for(detection in detections) {
                canvas.drawRect(detection.boundingBox, rectPaint)
                val classText = detection.classId.toString()
                val classTextX = detection.boundingBox.left.toFloat()
                val classTextY = detection.boundingBox.top.toFloat() - 10
                canvas.drawText(classText, classTextX, classTextY, textPaint)
            }

        binding.imageView.post {
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (allPermissionsGranted())
                startCamera()
            else
                Log.e("CameraFragment", "Camera permission not granted")
        }
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun onResume() {
        super.onResume()
        binding.flashSwitch.isChecked = false
        isFlashOn = false
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}