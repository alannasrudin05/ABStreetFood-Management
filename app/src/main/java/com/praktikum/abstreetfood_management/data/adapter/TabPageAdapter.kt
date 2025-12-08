package com.praktikum.abstreetfood_management.data.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.praktikum.abstreetfood_management.ui.HistoryFragment
import com.praktikum.abstreetfood_management.ui.ListProductFragment

class TabPageAdapter (fragment: Fragment): FragmentStateAdapter(fragment) {
    private val tabTitles = listOf("Top Sales Product", "History")

    override fun getItemCount(): Int = tabTitles.size
//    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            0 -> ListProductFragment()
            1 -> HistoryFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
    fun getTabTitle(position: Int): String = tabTitles[position]
}