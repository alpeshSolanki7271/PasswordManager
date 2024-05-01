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
import com.example.passwordmanager.App
import com.example.passwordmanager.R
import com.example.passwordmanager.data.model.PasswordData
import com.example.passwordmanager.databinding.ItemAccountDetailsLayoutBinding
import com.example.passwordmanager.util.AppConstants
import com.example.passwordmanager.util.AppConstants.Companion.decrypt
import com.example.passwordmanager.util.AppConstants.Companion.encrypt
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ModalBottomSheetDialogDetails(
    val passwordData: PasswordData, private val viewModel: PassViewModel
) : BottomSheetDialogFragment() {

    private lateinit var binding: ItemAccountDetailsLayoutBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ItemAccountDetailsLayoutBinding.inflate(inflater, container, false)


        binding.tvAccountType.text = passwordData.accountType
        binding.tvUsername.text = passwordData.username

        binding.etPassword.addTextChangedListener(object : TextWatcher {
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
                AppConstants.displayPasswordStrength(strength, binding.strengthTv, requireContext())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        binding.editBtn.setOnClickListener {
            if (binding.editBtn.text == "Edit") {
                binding.editBtn.text = resources.getString(R.string.update)
                binding.layoutEdit.visibility = View.VISIBLE
                binding.layoutShow.visibility = View.GONE
                binding.edName.setText(passwordData.accountType)
                binding.email.setText(passwordData.username)
                val decryptPassword = decrypt(passwordData.password, AppConstants.SECRET_KEY)
                binding.etPassword.setText(decryptPassword)
            } else {
                binding.editBtn.text = resources.getString(R.string.edit)

                if (binding.edName.text!!.isBlank()) {
                    Toast.makeText(
                        requireContext(), "Please enter account name", Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.email.text!!.isBlank()) {
                    Toast.makeText(
                        requireContext(), "Please enter username or email", Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etPassword.text!!.isBlank()) {
                    Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    val accountName = binding.edName.text.toString()
                    val email = binding.email.text.toString()
                    val password = binding.etPassword.text.toString()

                    val encryptedPass = encrypt(text = password, secretKey = AppConstants.SECRET_KEY)

                    val passwordData = PasswordData(
                        id = passwordData.id,
                        accountType = accountName,
                        username = email,
                        password = encryptedPass
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateData(passwordData = passwordData)
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(), "Account details updated", Toast.LENGTH_SHORT
                            ).show()
                            binding.edName.text!!.clear()
                            binding.email.text!!.clear()
                            binding.etPassword.text!!.clear()
                            dismiss()
                            viewModel.fetchUserList()
                        }
                    }
                }
            }
        }

        binding.deleteBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteData(id = passwordData.id)
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(), "Account Deleted", Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                    viewModel.fetchUserList()

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