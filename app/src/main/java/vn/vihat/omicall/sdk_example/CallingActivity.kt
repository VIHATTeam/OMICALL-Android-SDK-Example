package vn.vihat.omicall.sdk_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.vihat.omicall.omisdk.OmiClient
import vn.vihat.omicall.omisdk.service.NotificationService
import vn.vihat.omicall.omisdk.utils.SipServiceConstants
import vn.vihat.omicall.omisdk.videoutils.ScaleManager
import vn.vihat.omicall.omisdk.videoutils.Size
import vn.vihat.omicall.sdk_example.databinding.ActivityCallingBinding
import vn.vihat.omicall.sdk_example.event.CallStateChange
import vn.vihat.omicall.sdk_example.event.OmiCallState

class CallingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallingBinding
    private var phone: String? = null
    private var callId : Int = 0
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private var hasConnect = false

    private fun setUIFromStatus(isInComing: Boolean) {
        if (isInComing) {
            binding.pannelConfirm.visibility = View.VISIBLE
            binding.pannelCalling.visibility = View.INVISIBLE
            binding.switchCameraButton.visibility = View.INVISIBLE
        } else {
            binding.pannelConfirm.visibility = View.INVISIBLE
            binding.pannelCalling.visibility = View.VISIBLE
            if (NotificationService.isVideo) {
                binding.switchCameraButton.visibility = View.VISIBLE
                binding.localTextureView.visibility = View.VISIBLE
                binding.remoteTextureView.visibility = View.VISIBLE
                binding.incallAvatar.visibility = View.INVISIBLE
            } else {
                binding.switchCameraButton.visibility = View.INVISIBLE
                binding.incallAvatar.visibility = View.VISIBLE
                binding.localTextureView.visibility = View.INVISIBLE
                binding.remoteTextureView.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateAudio(status: Boolean) {
        if (status) {
            binding.micOnOffButton.setImageDrawable(getDrawable(R.drawable.mic_off))
        } else {
            binding.micOnOffButton.setImageDrawable(getDrawable(R.drawable.mic))
        }
    }

    private fun setStatusFromInt(state: OmiCallState) {
        if (state == OmiCallState.calling) {
            binding.lblStatus.text = "Chờ kết nối!"
        }
        if (state == OmiCallState.early) {
            binding.lblStatus.text = "Đang đổ chuông"
        }
        if (state == OmiCallState.connecting) {
            binding.lblStatus.text = "Đang chờ kết nối"
        }
        if (state == OmiCallState.confirmed) {
            binding.lblStatus.text = "Đã kết nối"
        }
        if (state == OmiCallState.disconnected) {
            binding.lblStatus.text = "Ngắt kết nối"
        }
        if (state == OmiCallState.incoming) {
            binding.lblStatus.text = "Có gọi tới!"
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCallStateChange(event: CallStateChange) {
        val state = event.state
        setStatusFromInt(state)
        if (state == OmiCallState.confirmed) {
            hasConnect = true
            setUIFromStatus(false)
            if (NotificationService.isVideo) {
                binding.localTextureView.surfaceTexture?.let {
                    OmiClient.instance.setupLocalVideoFeed(Surface(it))
                    ScaleManager.adjustAspectRatio(
                        binding.localTextureView,
                        Size(binding.localTextureView.width, binding.localTextureView.height),
                        Size(1280, 720)
                    )
                }
                binding.remoteTextureView.surfaceTexture?.let {
                    OmiClient.instance.setupIncomingVideoFeed(Surface(it))
                    ScaleManager.adjustAspectRatio(
                        binding.remoteTextureView,
                        Size(binding.remoteTextureView.width, binding.remoteTextureView.height),
                        Size(1280, 720)
                    )
                }
            }
            return
        }
        if (state == OmiCallState.disconnected) {
            finish()
        }
    }


    override fun onStop() {
        EventBus.getDefault().unregister(this)
        OmiClient.instance.stopVideoPreview()
        instance = null
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phone = intent?.getStringExtra(SipServiceConstants.PARAM_NUMBER)
        val isIncoming = intent!!.getBooleanExtra(EXTRA_IN_COMING, false)
        callId = intent?.getIntExtra(SipServiceConstants.PARAM_CALL_ID,0) ?: 0
        setUIFromStatus(isIncoming)
        instance = this
        binding.backButton.setOnClickListener {
            if (hasConnect) {
                OmiClient.instance.hangUp()
            } else {
                OmiClient.instance.decline()
            }
            finish()
        }
        if (isIncoming) {
            binding.acceptCallBt.setOnClickListener {
                OmiClient.instance.pickUp()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.localTextureView.surfaceTexture?.let {
                    OmiClient.instance.setupLocalVideoFeed(Surface(it))
                    ScaleManager.adjustAspectRatio(binding.localTextureView,Size(binding.localTextureView.width,binding.localTextureView.height),Size(1280,720))
                }
                binding.remoteTextureView.surfaceTexture?.let {
                    OmiClient.instance.setupIncomingVideoFeed(Surface(it))
                    ScaleManager.adjustAspectRatio(binding.remoteTextureView,Size(binding.remoteTextureView.width,binding.remoteTextureView.height),Size(1280,720))
                }
            }, 500)
        }

        binding.hangupButtonWhenConfirm.setOnClickListener {
            if (hasConnect) {
                OmiClient.instance.hangUp()
            } else {
                OmiClient.instance.decline()
            }
            instance = null
            finish()
        }

        binding.switchCameraButton.setOnClickListener {
            OmiClient.instance.switchCamera()
        }

        binding.videoOnOffButton.setOnClickListener {
            OmiClient.instance.toggleCamera()
        }

        binding.micOnOffButton.setOnClickListener {
            mainScope.launch {
                var result: Boolean? = null
                withContext(Dispatchers.Default) {
                    try {
                        result = OmiClient.instance.toggleSpeaker()
                    } catch (_ : Throwable) {

                    }
                }
                updateAudio(result ?: false)
                Log.d("toggleResult", result.toString())
            }
        }

        binding.hangupButton.setOnClickListener {
            OmiClient.instance.hangUp()
            finish()
        }

        binding.moreButton.setOnClickListener {
//            val builder = AlertDialog.Builder(this)
//            with(builder) {
//                setItems(arrayOf("Audio input devices", "Audio output devices")) { dialog, which ->
//                    if (which < 1) {
//                        showAudioInputs()
//                    } else {
//                        showAudioOutputs()
//                    }
//                }
//                show()
//            }
            OmiClient.instance.forwardCallTo("100");
        }
    }

    private fun showAudioInputs() {
        val inputs = OmiClient.instance.getAudioInputs()
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Select input devices:")
            setItems(inputs.map { it.first }.toTypedArray()) { dialog, which ->
                inputs[which].second()
            }
            setPositiveButton("OK") { dialog, which ->
            }
            show()
        }
    }

    private fun showAudioOutputs() {
        val outputs = OmiClient.instance.getAudioOutputs()

        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Select output devices:")
            setItems(outputs.map { it.first }.toTypedArray()) { dialog, which ->
                outputs[which].second()
            }
            setPositiveButton("OK") { dialog, which ->
            }
            show()
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 100
        const val EXTRA_IN_COMING = "extra_in_coming"
        var instance : CallingActivity? = null
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (packageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        }
    }
}