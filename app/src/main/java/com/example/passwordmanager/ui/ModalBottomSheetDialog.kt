package com.example.passwordmanager.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.example.passwordmanager.data.model.PasswordData
import com.example.passwordmanager.databinding.ItemBottomsheetBinding
import com.example.passwordmanager.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ModalBottomSheetDialog(private val passViewModel: PassViewModel) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ItemBottomsheetBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ItemBottomsheetBinding.inflate(inflater, container, false)
        binding.strengthTv.visibility = View.VISIBLE


        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.strengthTv.visibility = View.VISIBLE
                val password = s.toString()
                val strength = AppConstants.calculatePasswordStrength(password)
                AppConstants.displayPasswordStrength(strength, binding.strengthTv,requireContext())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.addButton.setOnClickListener {

            if (binding.edName.text!!.isBlank()) {
                Toast.makeText(requireContext(), "Please enter account name", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.email.text!!.isBlank()) {
                Toast.makeText(
                    requireContext(), "Please enter username or email", Toast.LENGTH_SHORT
                ).show()
            } else if (binding.password.text!!.isBlank()) {
                Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT).show()
            } else {



                val accountName = binding.edName.text.toString()
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()


                val encryptedPass =
                    AppConstants.encrypt(text = password, secretKey = AppConstants.SECRET_KEY)

                val passwordData = PasswordData(
                    accountType = accountName, username = email, password = encryptedPass
                )

                CoroutineScope(Dispatchers.IO).launch {
                    passViewModel.addAccount(passwordData = passwordData)
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Account Added", Toast.LENGTH_SHORT).show()
                        binding.edName.text!!.clear()
                        binding.email.text!!.clear()
                        binding.password.text!!.clear()
                        dismiss()
                        passViewModel.fetchUserList()
                    }

                }

            }

        }


        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // used to show the bottom sheet dialog
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }


}