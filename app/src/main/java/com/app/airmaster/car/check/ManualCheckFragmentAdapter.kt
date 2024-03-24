package com.app.airmaster.car.check

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ManualCheckFragmentAdapter(private val fragmentList : MutableList<Fragment>, private val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemId(position: Int): Long {
        val longId = fragmentList[position].hashCode().toLong()
        // Timber.e("----longId="+longId)
        return longId
    }

    override fun containsItem(itemId: Long): Boolean {
        fragmentList.forEach {
            if(it.hashCode().toLong() == itemId){
                return true
            }
        }
        return false
    }
}