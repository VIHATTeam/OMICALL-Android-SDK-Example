# OMICALL SDK FOR Android

The OmiKit exposes the <a href="https://github.com/VIHATTeam/OMICALL-Android-SDK-Example/blob/main/settings.gradle">omicall_flutter_plugin</a> library.

The most important part of the framework is :
- Help to easy integrate with Omicall.
- Easy custom Call UI/UX.
- Optimize codec voip for you.
- Full inteface to interactive with core function like sound/ringtone/codec.

### Status
Currently active maintenance and improve performance


### Running
Install via build.gradle:

```
api 'vn.vihat.omicall:omi-sdk:1.3.6'
```

### Configuration

#### Android:

- Add these settings in `build.gradle`:

```
jcenter() 
maven {
    url("https://vihatgroup.jfrog.io/artifactory/omi-voice/")
    credentials {
        username = "downloader"
        password = "Omi@2022"
    }
}
```

```
//in dependencies
classpath 'com.google.gms:google-services:4.3.13'
```

You can refer <a href="https://github.com/VIHATTeam/OMICALL-Android-SDK-Example/blob/main/settings.gradle">build.gradle</a> to know more informations.

- Add these settings in `app/build.gradle`:

```
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'
}'
```

```
api 'vn.vihat.omicall:omi-sdk:1.2.37'
//    compileOnly files('libs/OmiSDK.aar')
implementation 'com.google.android.material:material:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.navigation:navigation-fragment-ktx:2.5.1'
implementation 'androidx.navigation:navigation-ui-ktx:2.5.1'
implementation "com.google.code.gson:gson:2.8.9"
implementation 'com.google.firebase:firebase-messaging-ktx:23.1.2'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2'
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.4.0"
implementation platform('com.google.firebase:firebase-bom:31.2.3')
implementation 'com.google.firebase:firebase-messaging-ktx:23.1.2'
implementation "androidx.security:security-crypto:1.1.0-alpha06"
implementation("org.greenrobot:eventbus:3.3.1")
//in dependencies
```

You can refer <a href="https://github.com/VIHATTeam/OMICALL-Android-SDK-Example/blob/main/app/build.gradle">app/build.gradle</a> to know more informations.

- Update AndroidManifest.xml:

```
//need request this permission
<uses-permission android:name="android.permission.INTERNET" />
//your activity => calling activity
<activity
    android:name="vn.vihat.omicall.sdk_example.ExampleActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.OMICall">
    <intent-filter>
        <action android:name="com.omicall.sdk.CallingActivity"/>
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity
    android:name="vn.vihat.omicall.sdk_example.CallingActivity"
    android:exported="true"
    android:launchMode="singleTop"
    android:showOnLockScreen="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.OMICall">
</activity>
<receiver
    android:name="vn.vihat.omicall.omisdk.receiver.FirebaseMessageReceiver"
    android:exported="true"
    android:enabled="true"
    android:permission="com.google.android.c2dm.permission.SEND">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</receiver>
<service
    android:name="vn.vihat.omicall.omisdk.service.NotificationService"
    android:enabled="true"
    android:exported="false">
</service>

//add these lines into application
android:allowBackup="false"
android:fullBackupContent="false"

```
You can refer <a href="https://github.com/VIHATTeam/OMICALL-Android-SDK-Example/blob/main/app/src/main/AndroidManifest.xml">AndroidManifest</a> to know more informations.


- We registered permissions into my plugin:
```
<uses-permission android:name="android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS"
    tools:ignore="ProtectedPermissions" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.USE_SIP" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

- Setup push notification: Only support Firebase for remote push notification.
  - Add `google-service.json` in `/app` (For more information, you can refer <a href="https://firebase.google.com/docs/android/setup">firebase</a>)
  - Add Fire Messaging to receive `fcm_token` (You can refer <a href="https://firebase.google.com/docs/cloud-messaging/android/client">firebase_messaging</a> to setup notification for Flutter)

  - For more setting information, please refer <a href="https://api.omicall.com/web-sdk/mobile-sdk/android-sdk/cau-hinh-push-notification">Config Push for Android</a>

## Implement
- Important function.
  - Start Serivce: OmiKit need start services and register some events.
    ```
    //Call in the root activity
    OmiClient(this);
    ```
  - Create OmiKit: OmiKit need userName, password, realm, host to init enviroment. ViHAT Group will provides these informations for you. Please contact for my sales:
    ```
    OmiClient.register(
      userName: "", 
      password: "",
      realm: "",
      host: "",
      isVideo: true/false,
    );
    //if user login success we will send event to OmiClient.instance.setListener. You need listen it to move another screen.
    ```
  - Create OmiKit With ApiKey: OmiKit need apikey, username, user id to init enviroment. ViHAT Group will provides api key for you. Please contact for my sales:
    ```
    OmiClient.registerWithApiKey(
      usrName: "",
      usrUuid: "",
      isVideo: true/false,
      apiKey: "",
    );
    //if user login success we will send event to OmiClient.instance.setListener. You need listen it to move another screen.
    ```
  - Config push notification:
    ```
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
        userNameKey = "uuid", //we have 3 values: uuid, full_name, extension.
        channelId = "call.channel.sample", //custom your call notification channel id
        ringtone = "calling_ringtone", //add this file in res/raw, if you don't want to custom it, just pass empty/null value.
    )
    //incomingAcceptButtonImage, incomingDeclineButtonImage, backImage, userImage: Add these into `android/app/src/main/res/drawble`
    ```
  - Upload token: OmiKit need FCM for Android and APNS to push notification on user devices.
  ```
  OmiClient.instance.updatePushToken(
      "",
      deviceTokenAndroid,
  )
  ```

- Other functions:
  -  Call with phone number (mobile phone or internal number):
    ```
    OmiClient.instance.startCall(
        phone, //phone number
        _isVideoCall, //call video or audio. If true is video call. 
    );
    ```
  -  Call with UUID (only support with Api key):
    ```
    OmiClient.instance.startCallWithUuid(uuid = uuid, isVideo = isVideo)
    ```
  - Accept a call:
    ```
    OmiClient.instance.pickUp()
    ```
  - End a call: We will push a event `endCall` for you.
    ```
    OmiClient.instance.hangUp()
    ```
  - Toggle the audio: On/off audio a call
    ```
    val newStatus = OmiClient.instance.toggleMute()
    ```
  - Toggle the speaker: On/off the phone speaker
    ```
    val newStatus = OmiClient.instance.toggleSpeaker()
    ```
  - Send character: We only support `1 to 9` and `* #`.
    ```
    OmiClient.instance.sendDtmf(value);
    ```
  - Get current user information:
    ```
    mainScope.launch {
        var callResult: Any? = null
        withContext(Dispatchers.Default) {
            try {
                callResult = OmiClient.instance.getCurrentUser()
            } catch (_: Throwable) {

            }
        }
    }
    Output Sample:  
    {
        "extension": "111",
        "full_name": "chau1",
        "avatar_url": "",
        "uuid": "122aaa"
    }
    ```
  - Get guest user information:
    ```
    mainScope.launch {
        var callResult: Any? = null
        withContext(Dispatchers.Default) {
            try {
                callResult = OmiClient.instance.getIncomingCallUser()
            } catch (_: Throwable) {

            }
        }
    }
    Output Sample:  
    {
        "extension": "111",
        "full_name": "chau1",
        "avatar_url": "",
        "uuid": "122aaa"
    }
    ```
  - Get user information from sip:
    ```
    mainScope.launch {
        var callResult: Any? = null
        withContext(Dispatchers.Default) {
            try {
                callResult = OmiClient.instance.getUserInfo("111")
            } catch (_: Throwable) {

            }
        }
    }
    Output Sample:  
    {
        "extension": "111",
        "full_name": "chau1",
        "avatar_url": "",
        "uuid": "122aaa"
    }
    ```
  - Logout:
    ```
    OmicallClient.instance.logout();
    ```
- Video Call functions: Support only video call, We need enable video in `init functions` and `start call` to implements under functions.
  - Switch front/back camera: We use the front camera for first time.
  ```
  OmiClient.instance.switchCamera()
  ```
  - Toggle a video in video call: On/off video in video call
  ```
  OmiClient.instance.toggleCamera()
  ```
  - Local Camera Widget: Your camera view in a call => Need refresh when the call establised
  ```
  localView.surfaceTexture?.let {
        OmiClient.instance.setupLocalVideoFeed(Surface(it))
        ScaleManager.adjustAspectRatio(localView,
            Size(localView.width, localView.height),
            Size(1280,720)
        )
    }
  ```
  - Remote Camera Widget: Remote camera view in a call => Need refresh when the call establised
  ```
   remoteView.surfaceTexture?.let {
        OmiClient.instance.setupIncomingVideoFeed(Surface(it))
        ScaleManager.adjustAspectRatio(remoteView,
            Size(remoteView.width, remoteView.height),
            Size(1280,720)
        )
    }
  ```

- Event listener:
  - Important event `setListener`: We provide it to listen call state change.
 ```
 OmiClient.instance.setListener(callListener)
 private val callListener = object : OmiListener {
        override fun onConnectionTimeout() {
        }

        override fun onCallEstablished(
            callerId: Int,
            phoneNumber: String?,
            isVideo: Boolean?,
            startTime: Long,
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

        override fun onCallEnd(callInfo: Any?, statusCode: Int) {
            //call information 
            //statusCode is call ending reason.
        }
        
        /*Sample call info output:
        {
           "transaction_id":ea7dff38-cb1e-483d-8576...........,
           "direction":"inbound",
           "source_number":111,
           "destination_number":110,
           "time_start_to_answer":1682858097393,
           "time_end":1682858152181,
           "sip_user":111,
           "disposition":"answered"
        }*/

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
        
        override fun networkHealth(stat: Map<String, *>, quality: Int) {
            //`quality` value: 
            //0 is GOOD
            //1 is NORMAL
            //2 is BAD
            
            //`stat` value is call information. 
        }
    }
 ```
    - Method value: 
        - `incomingReceived`: Have a incoming call. On Android this event work only foreground
        - `onCallEstablished`: Connected a call.
        - `onCallEnd`: End a call.
        - `onHold`: `Comming soon....`
        - `onMuted`: `Comming soon...`
        - `networkHealth`: The calling quality.
    - Data value: We return `callerNumber`, `isVideo: true/false` information
- Other events:
  - Account event: Listen account event
  ```
  OmiClient.instance.addAccountListener(this)
  //override OmiAccountListener
  ```
  - User tab a missed call: In the main activity, you need implement onNewIntent and check `SipServiceConstants.PARAM_NUMBER`
  ```
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(SipServiceConstants.PARAM_NUMBER)) {
            //do your Stuff
            Log.d("Aaa", "Aaaa")
            val phone = intent.getStringExtra(SipServiceConstants.PARAM_NUMBER)
            val isVideo = intent.getBooleanExtra(SipServiceConstants.PARAM_IS_VIDEO, false)
        }
    }
  ```