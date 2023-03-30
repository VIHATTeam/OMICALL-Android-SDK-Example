package vn.vihat.omicall.sdk_example


import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import org.greenrobot.eventbus.EventBus
import vn.vihat.omicall.omisdk.OmiClient
import vn.vihat.omicall.omisdk.OmiListener
import vn.vihat.omicall.omisdk.utils.FirebaseUtils
import vn.vihat.omicall.omisdk.utils.SipServiceConstants
import vn.vihat.omicall.omisdk.utils.Status
import vn.vihat.omicall.omisdk.utils.TokenProvider
import vn.vihat.omicall.sdk_example.databinding.ActivityExampleBinding
import vn.vihat.omicall.sdk_example.event.CallEndEvent
import vn.vihat.omicall.sdk_example.event.CallEstablishedEvent
import javax.inject.Inject

class ExampleActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityExampleBinding
    private val installationViewModel = FirebaseUtils()
    private var installationId: String? = null

    @Inject
    lateinit var tokenProvider: TokenProvider

    private val callListener = object : OmiListener {
        override fun onConnectionTimeout() {
        }

        override fun onCallEstablished(
            callerId: Int,
            phoneNumber: String?,
            isVideo: Boolean?,
            startTime: Long
        ) {
            if (CallingActivity.active) {
                EventBus.getDefault().post(CallEstablishedEvent())
            } else {
                val intent = Intent(applicationContext, CallingActivity::class.java)
                intent.putExtra(SipServiceConstants.PARAM_NUMBER, phoneNumber)
                intent.putExtra(CallingActivity.EXTRA_IN_COMING, false)
                intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, isVideo ?: false)
                startActivity(intent)
            }
        }

        override fun onCallEnd() {
            EventBus.getDefault().post(CallEndEvent())
        }

        override fun incomingReceived(callerId: Int, phoneNumber: String?, isVideo: Boolean?) {
            ///work only foreground
            val intent = Intent(applicationContext, CallingActivity::class.java)
            intent.putExtra(SipServiceConstants.PARAM_NUMBER, phoneNumber)
            intent.putExtra(CallingActivity.EXTRA_IN_COMING, true)
            intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, isVideo ?: false)
            startActivity(intent)
        }

        override fun onOutgoingStarted(callerId: Int, phoneNumber: String?, isVideo: Boolean?) {

        }

        override fun onMuted(isMuted: Boolean) {

        }

        override fun onHold(isHold: Boolean) {

        }

        override fun onRinging() {

        }

        override fun onVideoSize(width: Int, height: Int) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.USE_SIP,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
                0,
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.USE_SIP,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO,
                ),
                0,
            )
        }
        tokenProvider = TokenProvider(this)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_example)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        OmiClient(this)
        OmiClient.instance.setListener(callListener)
        val cm = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        OmiClient.instance.setCameraManager(cm)
//        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val microphones = audioManager.microphones
//            Log.d("AudioManager", "===>>> microphones:" + microphones.map { it.description + " | " + it.type }.toString())
//        }
        installationViewModel.getFirebaseInstallationsId()

        installationViewModel.installationId.observe(this) {
            when (it.status) {
                Status.ERROR -> {
                    Log.d("error message Firebase", " error firebase: " + it.data)

                }
                Status.SUCCESS -> {
                    installationId = it.data
                    installationViewModel.getFirebaseToken()
                }
                else -> {}
            }
        }
        installationViewModel.firebaseToken.observe(this) {
            when (it.status) {

                Status.SUCCESS -> {
                    tokenProvider.setDeviceId(installationId!!)
                    tokenProvider.setToken(it.data)
                }
                else -> {}
            }
        }
//        Log.d("AudioManager", "===>>> outputs:" + outputs.map { it.type.toString() + " | " + it.productName }.toString())
        checkHasRegister()
    }


    private fun checkHasRegister() {
        if (OmiClient.instance.getIsRegister()) {
            findNavController(R.id.nav_host_fragment_content_example).navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}