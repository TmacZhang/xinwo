package com.cavalry.androidlib.ui.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 可以设置title的FragmentPagerAdapter,通常用于TabLayout+ViewPager需要title的情况
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class TitleFragmentPagerAdapter extends FragmentPagerAdapter {
    protected List<String> mPageTitleList;

    public TitleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TitleFragmentPagerAdapter(FragmentManager fm, List<String> pageTitleList) {
        super(fm);
        mPageTitleList = pageTitleList;
    }

    public void setPageTitle(List<String> pageTitleList){
        mPageTitleList = pageTitleList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mPageTitleList ==null ? 0 : mPageTitleList.size();
    }
}
