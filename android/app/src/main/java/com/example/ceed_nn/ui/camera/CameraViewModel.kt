package com.example.ceed_nn.ui.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _cameraFacing = MutableLiveData(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraFacing: LiveData<CameraSelector> = _cameraFacing

    fun toggleCameraFacing() {
        if (_cameraFacing.value == CameraSelector.DEFAULT_BACK_CAMERA)
            _cameraFacing.value = CameraSelector.DEFAULT_FRONT_CAMERA
        else
            _cameraFacing.value = CameraSelector.DEFAULT_BACK_CAMERA
    }
}