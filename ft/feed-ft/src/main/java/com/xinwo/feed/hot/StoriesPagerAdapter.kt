package com.xinwo.feed.hot

import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

class StoriesPagerAdapter(
    fragment: Fragment,
    private val dataList: MutableList<StoriesDataModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {
    private val mFragments = SparseArray<Fragment>()
    val onPageChangeCallback = StoriesOnPagrChangeCallback()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = StoryViewFragment.newInstance(dataList[position])
        mFragments.put(position, fragment)
        return fragment
    }

    fun OnFragmentResume() {
        mFragments.get(onPageChangeCallback.mIndex)?.onResume()
    }

    fun OnFragmentPause() {
        mFragments.get(onPageChangeCallback.mIndex)?.onPause()
    }

    class StoriesOnPagrChangeCallback : OnPageChangeCallback() {
        var mIndex: Int = 0
        override fun onPageSelected(position: Int) {
            mIndex = position
        }
    }
}