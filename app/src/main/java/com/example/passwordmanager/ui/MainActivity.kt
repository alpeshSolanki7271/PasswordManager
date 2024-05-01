package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.R
import com.example.passwordmanager.data.model.PasswordData
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.util.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PassViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var passAdapter: PassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this@MainActivity)[PassViewModel::class.java]


        CoroutineScope(Dispatchers.Main).launch {

            viewModel.fetchUserList()

            viewModel.passwordDataList.observe(this@MainActivity, Observer { passwordList ->
                if (passwordList.isNullOrEmpty()) {
                    binding.noData.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                } else {
                    binding.noData.visibility = View.GONE
                    binding.rv.visibility = View.VISIBLE
                    passAdapter =
                        PassAdapter(userList = passwordList, onItemClicked = { passwordData ->
                            val modal = ModalBottomSheetDialogDetails(passwordData, viewModel)
                            supportFragmentManager.let {
                                modal.show(
                                    it, AppConstants.TAG
                                )
                            }
                        })
                    binding.rv.adapter = passAdapter
                }
            })
        }

        binding.add.setOnClickListener {
            val modal = ModalBottomSheetDialog(passViewModel = viewModel)
            supportFragmentManager.let { modal.show(it, AppConstants.TAG) }
        }


    }
}