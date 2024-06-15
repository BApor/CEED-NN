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
import android.widget.Button
import androidx.camera.core.CameraControl
import com.example.ceed_nn.ai.NonMaxSuppression
import com.example.ceed_nn.ai.PropertiesProcessor
import com.example.ceed_nn.ai.PytorchMobile


class CameraFragment : Fragment() {
    private lateinit var cameraControl: CameraControl
    private var referenceScale = 0f

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService
    private var isFlashOn = false


    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkModelType()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val flashToggleSwitch: Button = binding.flashSwitch
        flashToggleSwitch.setOnClickListener {
            toggleFlash()
        }

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

    private fun checkModelType() {
        PytorchMobile.loadModel(requireContext(), "yolov5_xbs.pth")
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        var cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

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
                .setTargetResolution(Size(2500, 2500))
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
        var cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processFrame(imageProxy: ImageProxy) {
        val bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val rectPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        val textPaint = Paint().apply {
            color = Color.GREEN
            textSize = 60f
            textAlign = Paint.Align.LEFT
        }

        val detections = PytorchMobile.detect(imageProxy)

        for(detection: NonMaxSuppression.DetectResult in detections) {
            canvas.drawRect(detection.boundingBox, rectPaint)
            val text = detection.classId.toString()
            val textX = detection.boundingBox.left.toFloat()
            val textY = detection.boundingBox.top.toFloat() - 10
            canvas.drawText(text, textX, textY, textPaint)
        }

        binding.imageView.post {
            binding.imageView.setImageBitmap(bitmap)
        }

        if (detections.isNotEmpty())
            calculatePropreties(detections, imageProxy)

        imageProxy.close()
    }

    fun calculatePropreties(detections: List<NonMaxSuppression.DetectResult>, imageProxy: ImageProxy) {
        val newReferenceScale = 100f / PropertiesProcessor.calculateReferencePixels(detections)

        if (referenceScale == 0f)
            referenceScale = 100f / PropertiesProcessor.calculateReferencePixels(detections)
        else if ((newReferenceScale != 0f)
            && (((newReferenceScale / referenceScale * 100f) in 0f .. 5f)
            || ((newReferenceScale / referenceScale * 100f) in 100f .. 105f)))
            referenceScale = 100f / PropertiesProcessor.calculateReferencePixels(detections) // 10 milliméter, mert egy centimeter a referencianégyzet

        val seedAreas = PropertiesProcessor.calculateSeedPixels(detections, imageProxy, referenceScale)


        for (i in 0 until detections.size) {
            if (detections[i].classId != 0)
                binding.textView.text = "${detections[i].classId}: ${seedAreas[i]}"
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

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        cameraControl.enableTorch(isFlashOn)
    }


    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun onResume() {
        super.onResume()
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