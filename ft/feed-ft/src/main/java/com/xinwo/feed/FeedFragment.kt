package com.xinwo.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.material.tabs.TabLayout
import com.xinwo.base.BaseFragment
import com.xinwo.feed.viewmodel.FeedFragmentViewModel

class FeedFragment : BaseFragment() {
    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mRecyclerView1: StaggerdRecyclerView? = null
    var mRecyclerView2: StaggerdRecyclerView? = null
    var mRecyclerView3: StaggerdRecyclerView? = null
    var mRefresh: Boolean = false
    var mFeedFragmentViewModel1: FeedFragmentViewModel? = null
    var mFeedFragmentViewModel2: FeedFragmentViewModel? = null
    var mFeedFragmentViewModel3: FeedFragmentViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.feed_viewpager, container, false)
        return root
    }

    override fun initData() {
    }

    override fun initPresenter() {
    }

    override fun initView() {
        initViewPager()
        mFeedFragmentViewModel1 = ViewModelProviders.of(this).get(FeedFragmentViewModel::class.java)
        mFeedFragmentViewModel2 = FeedFragmentViewModel()
        mFeedFragmentViewModel3 = FeedFragmentViewModel()
        initRecyclerView(mRecyclerView1!!, mFeedFragmentViewModel1!!)
        initRecyclerView(mRecyclerView2!!, mFeedFragmentViewModel2!!)
        initRecyclerView(mRecyclerView3!!, mFeedFragmentViewModel3!!)
        mViewPager?.setCurrentItem(1)
        // 初始化Glide
        context?.let { Glide.get(it).setMemoryCategory(MemoryCategory.HIGH) };
    }

    private fun initViewPager() {
        mViewPager = view?.findViewById(R.id.feed_viewpager)
        mRecyclerView1 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mRecyclerView2 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mRecyclerView3 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        val list: ArrayList<View> = ArrayList<View>().apply {
            this.add(mRecyclerView1!!)
            this.add(mRecyclerView2!!)
            this.add(mRecyclerView3!!)
        }

        val titleList = ArrayList<String>().apply {
            add("关注")
            add("发现")
            add("同城")
        }
        val feedPagerAdapter = FeedPagerAdapter(list, titleList)
        mViewPager?.adapter = feedPagerAdapter

        mTabLayout = view?.findViewById(R.id.feed_tablayout)
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    private fun initRecyclerView(
        recyclerView: StaggerdRecyclerView,
        feedFragmentViewModel: FeedFragmentViewModel
    ) {
        val feedApdater = FeedAdapter(this.context)
        recyclerView.link(feedApdater, 2)
        setViewModel(feedApdater, feedFragmentViewModel)
        feedFragmentViewModel.getHotFeed()
        recyclerView.addCallbackListener(object : LoadMoreAndRefresh {
            override fun onLoadMore() {
                mRefresh = false
                //feedFragmentViewModel.getHotFeed()
            }

            override fun onRefresh() {
                mRefresh = true
                //feedFragmentViewModel.getHotFeed()
            }
        })

    }

    override fun loadData() {
    }

    override fun getParams(tag: Int): MutableMap<String, String> {
        return mutableMapOf();
    }

    private fun setViewModel(feedApdater: FeedAdapter, fragmentViewModel: FeedFragmentViewModel) {
        fragmentViewModel.getMutableLiveData()?.observe(this) { model ->
            val newData = model.listModel.filter {
                !it.bucketName.equals("mall")
            }
            if (mRefresh) {
                feedApdater.refresh(newData)
            } else {
                feedApdater.loadMore(newData)
            }
        }
    }
}