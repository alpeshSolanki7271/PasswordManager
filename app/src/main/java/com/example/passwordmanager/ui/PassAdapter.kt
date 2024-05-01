package com.example.passwordmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R
import com.example.passwordmanager.data.model.PasswordData
import com.example.passwordmanager.databinding.ItemPasswordLayoutBinding

class PassAdapter(
    var userList: List<PasswordData>,
    val onItemClicked: (passwordData: PasswordData) -> Unit,
) : RecyclerView.Adapter<PassAdapter.PasswordHolder>() {

    class PasswordHolder(var binding: ItemPasswordLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordHolder {
        val view = DataBindingUtil.inflate<ItemPasswordLayoutBinding>(
            LayoutInflater.from(parent.context), R.layout.item_password_layout, parent, false
        )

        return PasswordHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: PasswordHolder, position: Int) {
        val data = userList[position]
        holder.binding.root.setOnClickListener {
            onItemClicked(data)
        }

    }
}