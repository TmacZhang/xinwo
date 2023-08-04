package com.xinwo.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.material.tabs.TabLayout
import com.xinwo.base.BaseFragment
import com.xinwo.feed.hot.DataRepository
import com.xinwo.feed.hot.FeedHotViewModel
import com.xinwo.feed.hot.Mock
import com.xinwo.feed.hot.ResultData
import com.xinwo.feed.hot.StoriesPagerAdapter
import com.xinwo.feed.util.PreCacheUtil
import com.xinwo.feed.viewmodel.FeedFragmentViewModel

class FeedFragment : BaseFragment() {
    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mRecyclerView1: StaggerdRecyclerView? = null
    var mRecyclerView2: StaggerdRecyclerView? = null
    var mFeedHotView: View? = null
    var view_pager_stories: ViewPager2? = null
    var mRecyclerView4: StaggerdRecyclerView? = null

    var mFeedFragmentViewModel1: FeedFragmentViewModel? = null
    var mFeedFragmentViewModel2: FeedFragmentViewModel? = null
    var mFeedFragmentViewModel4: FeedFragmentViewModel? = null

    var mRefresh: Boolean = false

    private var homeViewModel: FeedHotViewModel? = null
    private lateinit var storiesPagerAdapter: StoriesPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.feed_viewpager, container, false)
        return root
    }

    override fun initView() {
        initViewPager()
        mFeedFragmentViewModel1 = ViewModelProviders.of(this).get(FeedFragmentViewModel::class.java)
        mFeedFragmentViewModel2 = FeedFragmentViewModel()
        mFeedFragmentViewModel4 = FeedFragmentViewModel()
        initRecyclerView(mRecyclerView1!!, mFeedFragmentViewModel1!!)
        initRecyclerView(mRecyclerView2!!, mFeedFragmentViewModel2!!)
        initRecyclerView(mRecyclerView4!!, mFeedFragmentViewModel4!!)

        homeViewModel = FeedHotViewModel(dataRepository = DataRepository(Mock(requireContext())))
        val storiesData = homeViewModel?.getDataList()

        storiesData?.observe(viewLifecycleOwner, Observer { value ->
            when (value) {
                is ResultData.Loading -> {
                }

                is ResultData.Success -> {
                    if (!value.data.isNullOrEmpty()) {
                        val dataList = value.data
                        storiesPagerAdapter = StoriesPagerAdapter(this, dataList)
                        view_pager_stories?.adapter = storiesPagerAdapter
                        PreCacheUtil.startPreCaching(requireContext(), dataList)
                    }
                }

                else -> {

                }
            }
        })

        mViewPager?.setCurrentItem(2)
        // 初始化Glide
        context?.let { Glide.get(it).setMemoryCategory(MemoryCategory.HIGH) }
    }

    private fun initViewPager() {
        mViewPager = view?.findViewById(R.id.feed_viewpager)
        mRecyclerView1 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mRecyclerView2 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mFeedHotView = LayoutInflater.from(context)
            .inflate(R.layout.feed_hot, null, false)
        view_pager_stories = mFeedHotView?.findViewById(R.id.view_pager_stories)
        mRecyclerView4 = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        val list: ArrayList<View> = ArrayList<View>().apply {
            this.add(mRecyclerView1!!)
            this.add(mRecyclerView2!!)
            this.add(mFeedHotView!!)
            this.add(mRecyclerView4!!)
        }

        val titleList = ArrayList<String>().apply {
            add("关注")
            add("发现")
            add("热门")
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
                feedFragmentViewModel.getHotFeed()
            }

            override fun onRefresh() {
                mRefresh = true
                feedFragmentViewModel.getHotFeed()
            }
        })

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