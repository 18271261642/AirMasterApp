package com.app.airmaster.car.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R

class CarFaultNotifyAdapter(private val context: Context,private val list : MutableList<String>) : RecyclerView.Adapter<CarFaultNotifyAdapter.FaultViewHolder>() {


    class FaultViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val itemCarFaultNotifyTv : TextView = view.findViewById(R.id.itemCarFaultNotifyTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaultViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.item_car_notify_layout,parent,false)
        return FaultViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: FaultViewHolder, position: Int) {
       holder.itemCarFaultNotifyTv.text = list[position]
    }
}