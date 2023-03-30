package vn.vihat.omicall.sdk_example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import vn.vihat.omicall.omisdk.OmiAccountListener
import vn.vihat.omicall.omisdk.OmiClient
import vn.vihat.omicall.omisdk.utils.OmiSDKUtils
import vn.vihat.omicall.sdk_example.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OmiAccountListener {

    private var first: Boolean = true
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnInit.setOnClickListener {
            if (arrayOf(
                    binding.txtUserName,
                    binding.txtPassword,
                    binding.txtRealm,
                    binding.txtHost,
                ).none { it.text.isNullOrEmpty() }
            ) {
                OmiClient.register(
                    requireContext(),
                    binding.txtUserName.text.toString(),
                    binding.txtPassword.text.toString(),
                    true,
                    binding.txtRealm.text.toString(),
                    binding.txtHost.text.toString(),
                    isTcp = true,
                    customUI = true
                )
                OmiClient.instance.addAccountListener(this)
            } else {
                Toast.makeText(requireContext(), R.string.omi_sdk_empty, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        OmiSDKUtils.handlePermissionRequest(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onAccountStatus(online: Boolean) {
        if (online && this.isVisible) {
            first = false
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}