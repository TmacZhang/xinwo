package com.xinwo.feed

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.material.tabs.TabLayout
import com.xinwo.base.BaseFragment
import com.xinwo.feed.find.FeedFindapter
import com.xinwo.feed.hot.DataRepository
import com.xinwo.feed.hot.FeedHotViewModel
import com.xinwo.feed.hot.Mock
import com.xinwo.feed.hot.ResultData
import com.xinwo.feed.hot.StoriesPagerAdapter
import com.xinwo.feed.viewmodel.FeedFragmentViewModel

class FeedFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mFeedFollowView: StaggerdRecyclerView? = null
    var mFeedFindView: StaggerdRecyclerView? = null
    var mFeedHotView: View? = null
    var mFeedHotViewViewPager: ViewPager2? = null
    var mFeedTongchengView: StaggerdRecyclerView? = null

    var mFeedFollowFragmentViewModel: FeedFragmentViewModel? = null
    var mFeedFindFragmentViewModel: FeedFragmentViewModel? = null
    var mFeedTongchengFragmentViewModel: FeedFragmentViewModel? = null

    var mRefresh: Boolean = false

    private var mHomeViewModel: FeedHotViewModel? = null
    private lateinit var mStoriesPagerAdapter: StoriesPagerAdapter
    private var mPostion = -1
    private var mFindPostion = 1
    private var mHotPosition = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feed_viewpager, container, false)
    }

    override fun initView() {
        initViewPager()
        initFollowView()
        initFindView()
        initHotView()
        initTongchengView()
        mViewPager?.setCurrentItem(mHotPosition)
        // 初始化Glide
        context?.let {
            Glide.get(it).setMemoryCategory(MemoryCategory.HIGH)
        }
    }

    private fun initFollowView() {
        mFeedFollowFragmentViewModel = FeedFragmentViewModel()
    }

    private fun initFindView() {
        mFeedFindFragmentViewModel = FeedFragmentViewModel()
        initRecyclerView(mFeedFindView!!, mFeedFindFragmentViewModel!!)
    }

    private fun initHotView() {
        mHomeViewModel = FeedHotViewModel(dataRepository = DataRepository(Mock(requireContext())))
        val storiesData = mHomeViewModel?.getDataList()
        storiesData?.observe(viewLifecycleOwner, Observer { value ->
            when (value) {
                is ResultData.Loading -> {
                }

                is ResultData.Success -> {
                    if (!value.data.isNullOrEmpty()) {
                        val dataList = value.data
                        mStoriesPagerAdapter = StoriesPagerAdapter(this, dataList)
                        mFeedHotViewViewPager?.adapter = mStoriesPagerAdapter
                        mFeedHotViewViewPager?.registerOnPageChangeCallback(mStoriesPagerAdapter.onPageChangeCallback)
                    }
                }

                else -> {

                }
            }
        })
    }

    private fun initTongchengView() {
        mFeedTongchengFragmentViewModel = FeedFragmentViewModel()
    }

    private fun initViewPager() {
        mViewPager = view?.findViewById(R.id.feed_viewpager)
        mFeedFollowView = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mFeedFindView = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        mFeedHotView = LayoutInflater.from(context)
            .inflate(R.layout.feed_hot, null, false)
        mFeedHotViewViewPager = mFeedHotView?.findViewById(R.id.view_pager_stories)
        mFeedTongchengView = LayoutInflater.from(context)
            .inflate(R.layout.feed_fragment, null, false) as StaggerdRecyclerView
        val list: ArrayList<View> = ArrayList<View>().apply {
            this.add(mFeedFollowView!!)
            this.add(mFeedFindView!!)
            this.add(mFeedHotView!!)
            this.add(mFeedTongchengView!!)
        }

        val titleList = ArrayList<String>().apply {
            add("关注")
            add("发现")
            add("热门")
            add("同城")
        }
        val feedPagerAdapter = FeedPagerAdapter(list, titleList)
        mViewPager?.adapter = feedPagerAdapter
        mViewPager?.addOnPageChangeListener(this)
        mTabLayout = view?.findViewById(R.id.feed_tablayout)
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    private fun initRecyclerView(
        recyclerView: StaggerdRecyclerView,
        feedFragmentViewModel: FeedFragmentViewModel
    ) {
        val feedApdater = FeedFindapter(this.context)
        recyclerView.link(feedApdater, 2)
        setViewModel(feedApdater, feedFragmentViewModel)
        feedFragmentViewModel.getHotFeed()
        recyclerView.addCallbackListener(object : LoadMoreAndRefresh {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onLoadMore() {
                mRefresh = false
                feedApdater.loadMoreOrRefresh()
                feedFragmentViewModel.getHotFeed()
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onRefresh() {
                mRefresh = true
                feedApdater.loadMoreOrRefresh()
                feedFragmentViewModel.getHotFeed()
            }
        })

    }

    private fun setViewModel(feedApdater: FeedFindapter, fragmentViewModel: FeedFragmentViewModel) {
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

    override fun onResume() {
        super.onResume()
        Log.i("jin", "currentItem = " + mViewPager?.currentItem)
        if (mViewPager?.currentItem == mHotPosition) {
            val adapter = mFeedHotViewViewPager?.adapter as StoriesPagerAdapter?
            adapter?.OnFragmentResume()
        } else {
            onOtherFragmentClickedForHotView()
        }
    }

    override fun onPause() {
        super.onPause()
        onOtherFragmentClickedForHotView()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mPostion = position
        onStateChange()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onOtherFragmentClicked() {
        super.onOtherFragmentClicked()
        onOtherFragmentClickedForHotView()
    }

    override fun OnFragmentClicked() {
        super.OnFragmentClicked()
        onStateChange()
    }

    private fun onStateChange() {
        onStateChangeForHotView()
        onStateChangeForFindView()
    }

    private fun onStateChangeForFindView() {
        if (mPostion != mFindPostion) {
            (mFeedFindView?.staggedAdapter as FeedFindapter).stopPlayVideo()
        }
    }

    private fun onStateChangeForHotView() {
        if (mPostion == mHotPosition) {
            val adapter = mFeedHotViewViewPager?.adapter as StoriesPagerAdapter?
            adapter?.OnFragmentResume()
        } else {
            onOtherFragmentClickedForHotView()
        }
    }

    private fun onOtherFragmentClickedForHotView() {
        val adapter = mFeedHotViewViewPager?.adapter as StoriesPagerAdapter?
        adapter?.OnFragmentPause()
    }
}