package vn.vihat.omicall.sdk_example


import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import vn.vihat.omicall.omisdk.utils.SipServiceConstants
import vn.vihat.omicall.sdk_example.databinding.ActivityExampleBinding
import vn.vihat.omicall.sdk_example.event.CallEndEvent
import vn.vihat.omicall.sdk_example.event.CallEstablishedEvent

class ExampleActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityExampleBinding

    private val callListener = object : OmiListener {

        override fun onCallEnd(callInfo: Any?) {
            EventBus.getDefault().post(CallEndEvent())
        }

        override fun onCallEstablished(
            callerId: Int,
            phoneNumber: String?,
            isVideo: Boolean?,
            startTime: Long,
            transactionId: String?,
        ) {
            if (CallingActivity.instance != null) {
                EventBus.getDefault().post(CallEstablishedEvent())
            } else {
                val intent = Intent(applicationContext, CallingActivity::class.java)
                intent.putExtra(SipServiceConstants.PARAM_NUMBER, phoneNumber)
                intent.putExtra(CallingActivity.EXTRA_IN_COMING, false)
                intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, isVideo ?: false)
                startActivity(intent)
            }
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

        override fun onConnectionTimeout() {

        }

        override fun onVideoSize(width: Int, height: Int) {

        }

        override fun onSwitchBoardAnswer(sip: String) {
            Log.d("aa", sip);
        }

        override fun networkHealth(quality: Int) {
            Log.d("aaaa", "quality $quality")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        OmiClient(this)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
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
        // Check if Android M or higher
        // Check if Android M or higher
        if (!Settings.canDrawOverlays(this)) {
            // You don't have permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
        val navController = findNavController(R.id.nav_host_fragment_content_example)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        OmiClient.instance.setListener(callListener)
        checkHasRegister()
        OmiClient.instance.configPushNotification(
            notificationIcon = "notification",
            prefix = "Cuộc gọi tới từ: ",
            incomingBackgroundColor = "#FFFFFFFF",
            incomingAcceptButtonImage = "join_call",
            incomingDeclineButtonImage = "hangup",
            backImage = "ic_back",
            userImage = "calling_face",
            missedCallTitle = "Cuộc gọi nhỡ",
            prefixMissedCallMessage = "Cuộc gọi nhỡ từ ",
            userNameKey = "uuid",
            channelId = "callchannelsample"
        )
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        if (intent.hasExtra(SipServiceConstants.PARAM_NUMBER)) {
//            //do your Stuff
//            Log.d("Aaa", "Aaaa")
//        }
//    }

    private fun checkHasRegister() {
        if (OmiClient.instance.getIsRegister()) {
            findNavController(R.id.nav_host_fragment_content_example).navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}