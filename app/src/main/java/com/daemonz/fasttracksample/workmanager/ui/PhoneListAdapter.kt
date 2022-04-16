package com.daemonz.fasttracksample.workmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daemonz.fasttracksample.databinding.PhoneItemBinding

class PhoneListAdapter(private val list : List<String>) :
    RecyclerView.Adapter<PhoneListAdapter.PhoneViewHolder>() {

    inner class  PhoneViewHolder(private val binding:PhoneItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(phoneNum:String){
            binding.apply {
                tvNumber.text = phoneNum
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhoneItemBinding.inflate(inflater)
        return PhoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int  = list.size
}