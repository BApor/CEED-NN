package com.example.ceed_nn.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
import com.example.ceed_nn.ai.DetectResult
import com.example.ceed_nn.ai.NonMaxSuppression
import com.example.ceed_nn.ai.PropertiesProcessor
import com.example.ceed_nn.ai.PytorchMobile


class CameraFragment : Fragment() {
    private lateinit var cameraControl: CameraControl

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService
    private var isFlashOn = false

    private var referenceScale = 0f
    private var referenceCounter = 0
    private val referenceNumber = 10
    private var newReferenceScale = 0f
    private var detections: List<DetectResult> = listOf( DetectResult(Rect(0,0,0,0), 0, 0f, 0f))


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
        var cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processFrame(imageProxy: ImageProxy) {
        val bitmap = Bitmap.createBitmap(1080, 1088, Bitmap.Config.ARGB_8888)
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

        detections = PytorchMobile.detect(imageProxy)

        for(i in 0 until detections.size) {
            canvas.drawRect(detections[i].boundingBox, rectPaint)
            val classText = detections[i].classId.toString()
            val classTextX = detections[i].boundingBox.left.toFloat()
            val classTextY = detections[i].boundingBox.top.toFloat() - 1
            canvas.drawText(classText, classTextX, classTextY, textPaint)

            val indexText = i.toString()
            val indexTextX = detections[i].boundingBox.right.toFloat()
            val indexTextY = detections[i].boundingBox.top.toFloat() - 10
            canvas.drawText(indexText, indexTextX, indexTextY, textPaint)
        }

        binding.imageView.post {
            binding.imageView.setImageBitmap(bitmap)
        }

        if (detections.isNotEmpty())
            calculatePropreties(imageProxy)

        imageProxy.close()
    }

    fun calculatePropreties( imageProxy: ImageProxy) {
        if (referenceCounter < referenceNumber){
            val newPartialReferenceScale = 100f / PropertiesProcessor.calculateReferencePixels(detections)
           if (newPartialReferenceScale != Float.POSITIVE_INFINITY
               && newPartialReferenceScale  != 0f){
               newReferenceScale += newPartialReferenceScale
                referenceCounter++
            }
        } else {
            newReferenceScale /= referenceNumber
        }

        if ((newReferenceScale != 0f && newReferenceScale != Float.POSITIVE_INFINITY) &&
            (referenceCounter == referenceNumber)) {
            referenceScale = newReferenceScale / 10
            newReferenceScale = 0f
        }

        if (referenceCounter == referenceNumber) {
            detections =
                PropertiesProcessor.calculateSeedPixels(detections, imageProxy, referenceScale)


            for (i in 0 until detections.size) {
                if (detections[i].classId != 0)
                    binding.textView.text = "${detections[i].classId}: ${detections[i].seedArea} mm^2"
                else
                    binding.textView2.text = "${detections[i].classId}: ${detections[i].seedArea} mm^2"
            }

            referenceCounter = 0
            referenceScale = 0f
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