package vn.vihat.omicall.sdk_example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.vihat.omicall.omisdk.OmiClient
import vn.vihat.omicall.omisdk.utils.SipServiceConstants
import vn.vihat.omicall.sdk_example.databinding.ActivityCallingBinding
import vn.vihat.omicall.sdk_example.event.CallEndEvent
import vn.vihat.omicall.sdk_example.event.CallEstablishedEvent


class CallingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallingBinding
    private var inComing: Boolean = false
    private var isVideo: Boolean = false
    private var addressString: String? = null
    private var phone: String? = null
    private var callId : Int = 0

    private fun setUIFromStatus(isInComing: Boolean) {
        if (isInComing) {
            binding.pannelConfirm.visibility = View.VISIBLE
            binding.pannelCalling.visibility = View.INVISIBLE
            binding.switchCameraButton.visibility = View.INVISIBLE
        } else {
            binding.pannelConfirm.visibility = View.INVISIBLE
            binding.pannelCalling.visibility = View.VISIBLE
            if (isVideo) {
                binding.switchCameraButton.visibility = View.VISIBLE
                binding.localTextureView.visibility = View.VISIBLE
                binding.remoteTextureView.visibility = View.VISIBLE
                binding.incallAvatar.visibility = View.GONE
                binding.switchCameraButton.visibility = View.VISIBLE
            } else {
                binding.switchCameraButton.visibility = View.INVISIBLE
                binding.incallAvatar.visibility = View.VISIBLE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCallEndEvent(event: CallEndEvent) {
        finish()
    }

    // This method will be called when a SomeOtherEvent is posted
    @Subscribe
    fun onCallEstablishedEvent(event: CallEstablishedEvent) {
        setUIFromStatus(false)
        binding.localTextureView.surfaceTexture?.let {
            OmiClient.instance.setupLocalVideoFeed(Surface(it))
            binding.localTextureView.scaleX = 1.5F

        }
        binding.remoteTextureView.surfaceTexture?.let {
            OmiClient.instance.setupIncomingVideoFeed(Surface(it))
            binding.remoteTextureView.scaleX = 1.5F
        }
    }

    override fun onStart() {
        active = true
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        active = false
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        binding = ActivityCallingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        phone = intent?.getStringExtra(SipServiceConstants.PARAM_NUMBER)
        inComing = intent!!.getBooleanExtra(EXTRA_IN_COMING, false)
        isVideo = intent!!.getBooleanExtra(SipServiceConstants.PARAM_IS_VIDEO,false)
        callId = intent?.getIntExtra(SipServiceConstants.PARAM_CALL_ID,0) ?: 0
        inComing = intent!!.getBooleanExtra(EXTRA_IN_COMING, false)

        setUIFromStatus(inComing)
        binding.backButton.setOnClickListener {
            OmiClient.instance.hangUp()
            finish()
        }

        if (inComing) {
            binding.acceptCallBt.setOnClickListener {
                OmiClient.instance.pickUp(isVideo)
            }
        }

        binding.hangupButtonWhenConfirm.setOnClickListener {
            OmiClient.instance.hangUp()
            finish()
        }

        binding.switchCameraButton.setOnClickListener {
            OmiClient.instance.switchCamera()
        }

        binding.videoOnOffButton.setOnClickListener {
            OmiClient.instance.toggleCamera()
        }

        binding.micOnOffButton.setOnClickListener {
            OmiClient.instance.toggleMute()
        }

        binding.hangupButton.setOnClickListener {
            OmiClient.instance.hangUp()
            finish()
        }

        binding.moreButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            with(builder) {
                setItems(arrayOf("Audio input devices", "Audio output devices")) { dialog, which ->
                    if (which < 1) {
                        showAudioInputs()
                    } else {
                        showAudioOutputs()
                    }
                }
                show()
            }
        }
        setupSIP()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
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

    private fun setupSIP() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val realm = pref.getString("realm", null)
        if (phone != null) {
            addressString = "sip:$phone@$realm"
        }

        if (packageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                packageName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_PERMISSION
                )
            }
            finish()
            return
        }

    }

    companion object {
        const val REQUEST_PERMISSION = 100
        const val EXTRA_IN_COMING = "extra_in_coming"
        var active = false
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