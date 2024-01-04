package com.app.airmaster.car.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.car.bean.TimerBean

/**
 * Created by Admin
 *Date 2023/7/14
 */
class CarTimerAdapter(private val context: Context,private val list : MutableList<TimerBean>) : RecyclerView.Adapter<CarTimerAdapter.TimerViewHolder>() {



    //item点击
    private var onItemClickListener : OnCommItemClickListener?= null

    fun setOnCommClickListener(onclick : OnCommItemClickListener){
        this.onItemClickListener = onclick
    }

     class TimerViewHolder(view : View) : ViewHolder(view){
        val timeTv : TextView = view.findViewById(R.id.itemCarTimerTimeTv)
         val itemTimerCheckImageView : ImageView = view.findViewById(R.id.itemTimerCheckImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.item_car_timer_layout,parent,false)
        return TimerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(holder.layoutPosition)
        }


       holder.timeTv.text = list[position].timeValue

        val isChecked = list[position].isChecked
        holder.itemTimerCheckImageView.setImageResource(if(isChecked)R.mipmap.ic_scan_conn_checked else R.mipmap.ic_scan_conn_no_checked)
    }
}