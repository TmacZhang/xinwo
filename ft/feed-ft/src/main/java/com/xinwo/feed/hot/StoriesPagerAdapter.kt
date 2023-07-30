package com.xinwo.feed.hot

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StoriesPagerAdapter(
    fragment: Fragment,
    private val dataList: MutableList<StoriesDataModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(dataList[position])
    }
}