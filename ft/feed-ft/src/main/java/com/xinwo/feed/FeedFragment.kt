package com.xinwo.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.material.tabs.TabLayout
import com.xinwo.base.BaseFragment
import com.xinwo.feed.api.IGetHostService
import com.xinwo.feed.model.FeedListModel
import com.xinwo.network.NetManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FeedFragment : BaseFragment() {
    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mRecyclerView1: StaggerdRecyclerView? = null
    var mRecyclerView2: StaggerdRecyclerView? = null
    var mRecyclerView3: StaggerdRecyclerView? = null

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
        initRecyclerView(mRecyclerView1!!)
        initRecyclerView(mRecyclerView2!!)
        initRecyclerView(mRecyclerView3!!)
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

    private fun initRecyclerView(recyclerView: StaggerdRecyclerView) {
        val feedApdater: FeedAdapter?
        feedApdater = FeedAdapter(this.context)
        recyclerView.link(feedApdater, 2)
        getHotFeed(feedApdater, false)

        recyclerView.addCallbackListener(object : LoadMoreAndRefresh {
            override fun onLoadMore() {
                //模拟加载更多
                getHotFeed(feedApdater, false)
            }

            override fun onRefresh() {
                //测试下网络接口
                getHotFeed(feedApdater, true)
            }
        })
    }

    private fun getHotFeed(feedApdater: FeedAdapter, refresh: Boolean) {
        val retrofit = NetManager.getRetrofit("http://180.76.242.204:18080")
        retrofit.create(IGetHostService::class.java)
            .getData(1, 10, "img")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<FeedListModel>() {
                override fun onNext(data: FeedListModel) {
                    Log.i("jin", data.toString())
                    val newData = data.listModel.filter {
                        it.bucketName.equals("img")
                    }
                    if (refresh) {
                        feedApdater.refresh(newData)
                    } else {
                        feedApdater.loadMore(newData)
                    }
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }

    override fun loadData() {
    }

    override fun getParams(tag: Int): MutableMap<String, String> {
        return mutableMapOf();
    }

    override fun success(bean: Any?, tag: Int) {
    }

    override fun error(e: Throwable?, tag: Int) {
    }
}