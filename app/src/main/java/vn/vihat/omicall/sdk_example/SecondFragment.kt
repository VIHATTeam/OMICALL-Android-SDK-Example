package vn.vihat.omicall.sdk_example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.*
import vn.vihat.omicall.omisdk.OmiClient
import vn.vihat.omicall.omisdk.utils.SipServiceConstants
import vn.vihat.omicall.sdk_example.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateToken() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val tokenProvider = (activity as ExampleActivity).tokenProvider
                    OmiClient.instance.updatePushToken(
                        "",
                        tokenProvider.getToken() ?: "",
                    )
                    Log.d("aaa", tokenProvider.getToken() ?: "")
                } catch (e: Throwable) {
                    Log.d("OmiKit", "Caught exception: $e")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToken()
        binding.btnCall.setOnClickListener {
//            mainScope.launch {
//                withContext(Dispatchers.Default) {
//                    val result = OmiClient.instance.startCallWithUuid(
//                        "123aaa",
//                        true,
//                    )
//                }
//                val intent = Intent(context, CallingActivity::class.java)
//                intent.putExtra(SipServiceConstants.PARAM_NUMBER, binding.txtPhone.text.toString())
//                intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, true)
//                startActivity(intent)
//            }
            OmiClient.instance.startCall("110",
                isVideo = false
            )
            val intent = Intent(context, CallingActivity::class.java)
            intent.putExtra(SipServiceConstants.PARAM_NUMBER, binding.txtPhone.text.toString())
            intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, false)
            startActivity(intent)
        }
        binding.btnLogout.setOnClickListener {
            mainScope.launch {
                withContext(Dispatchers.Default) {
                    OmiClient.instance.logout()
                }
            }
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}