package com.app.airmaster.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R
import com.app.airmaster.bean.BleBean

/**
 * Created by Admin
 *Date 2023/7/12
 */
class SecondScanAdapter(private val context: Context,private val list : MutableList<BleBean>,private val isBind : Boolean) : RecyclerView.Adapter<SecondScanAdapter.ScanDeviceViewHolder>() {

    private var onItemClickListener : OnCommItemClickListener ?= null

    fun setOnItemClick(click : OnCommItemClickListener){
        this.onItemClickListener = click
    }


    class ScanDeviceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var itemScanName  = itemView.findViewById<TextView>(R.id.itemSecondNameTv)
        var itemDeviceMacTv = itemView.findViewById<TextView>(R.id.itemDeviceMacTv)
        var itemScanCheckImageView = itemView.findViewById<ImageView>(R.id.itemScanCheckImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanDeviceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_second_scan_layout,parent,false)
        return ScanDeviceViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: SecondScanAdapter.ScanDeviceViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            val position = holder.layoutPosition
            onItemClickListener?.onItemClick(position)
        }

        holder.itemScanName.text = list[position].bluetoothDevice.name
        holder.itemDeviceMacTv.text = list[position].bluetoothDevice.address

        if(isBind){
            val isConnected = list[position].isConnected
            holder.itemScanCheckImageView.setImageResource(if(isConnected)R.mipmap.ic_scan_conn_checked else R.mipmap.ic_scan_conn_no_checked)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}