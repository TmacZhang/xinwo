package com.xinwo.feed.hot

import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StoriesPagerAdapter(
    fragment: Fragment,
    private val dataList: MutableList<StoriesDataModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    private var mIndex: Long = 0
    private val mFragments = LongSparseArray<Fragment>()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        mIndex = position.toLong()
        return StoryViewFragment.newInstance(dataList[position])
    }

    fun OnFragmentResume(){
        mFragments.get(mIndex)?.onResume()
    }

    fun OnFragmentPause(){
        mFragments.get(mIndex)?.onPause()
    }
}