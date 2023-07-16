package com.xinwo.feed

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class FeedPagerAdapter(viewList: ArrayList<View>, titleList: ArrayList<String>) : PagerAdapter() {
    private val mViewList = viewList
    private val mTitleList = titleList

    override fun getCount(): Int {
        return mViewList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        container.addView(mViewList[position])
        return mViewList[position]
    }

    override fun isViewFromObject(view: View,  object1: Any): Boolean {
        return view == object1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViewList[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mTitleList[position]
    }


}