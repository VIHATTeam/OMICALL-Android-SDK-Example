package vn.vihat.omicall.sdk_example

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateToken() {
        val tokenProvider = (activity as ExampleActivity).tokenProvider
        OmiClient.instance.updatePushToken(
            "",
            tokenProvider.getToken() ?: "",
            tokenProvider.getDeviceId() ?: "",
            BuildConfig.APPLICATION_ID,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToken()
        binding.btnCall.setOnClickListener {
            OmiClient.instance.startCall(binding.txtPhone.text.toString(),
                isVideo = true
            )
            val intent = Intent(context, CallingActivity::class.java)
            intent.putExtra(SipServiceConstants.PARAM_NUMBER, binding.txtPhone.text.toString())
            intent.putExtra(SipServiceConstants.PARAM_IS_VIDEO, true)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}