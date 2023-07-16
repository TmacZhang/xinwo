package com.xinwo.feed

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.xinwo.base.BaseFragment
import com.xinwo.feed.model.FeedModel
import com.xinwo.network.NetManager
import java.util.ArrayList

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

        var feedApdater: FeedAdapter?
        feedApdater = FeedAdapter(this.context)
        recyclerView?.link(feedApdater, 2)

        feedApdater.datas?.add(FeedModel(500, 500, R.drawable.a2))
        feedApdater.datas?.add(FeedModel(500, 1000, R.drawable.a2))
        feedApdater.datas?.add(FeedModel(500, 750, R.drawable.a3))
        feedApdater.datas?.add(FeedModel(500, 530, R.drawable.a4))
        feedApdater.datas?.add(FeedModel(500, 400, R.drawable.a5))
        feedApdater.datas?.add(FeedModel(500, 980, R.drawable.a6))
        feedApdater.datas?.add(FeedModel(500, 600, R.drawable.a7))
        feedApdater.datas?.add(FeedModel(500, 620, R.drawable.a8))
        feedApdater.datas?.add(FeedModel(500, 680, R.drawable.c1))
        feedApdater.datas?.add(FeedModel(500, 705, R.drawable.c2))
        feedApdater.datas?.add(FeedModel(500, 885, R.drawable.c3))

        recyclerView?.addCallbackListener(object : LoadMoreAndRefresh {
            override fun onLoadMore() {
                //模拟加载更多
                val datas = ArrayList<FeedModel>();
                datas.add(FeedModel(500, 840, R.drawable.girl_photo_01_small))
                datas.add(FeedModel(500, 712, R.drawable.c5))
                datas.add(FeedModel(500, 624, R.drawable.c6))
                datas.add(FeedModel(500, 888, R.drawable.c7))
                feedApdater.loadMore(datas)
            }

            override fun onRefresh() {
                //测试下网络接口
                Thread {
                    val netManager = NetManager()
                    val url = "http://180.76.242.204:18101/test/user?speed=8"
                    val result = netManager.get(url)
                    val activity = context as Activity
                    activity.runOnUiThread {
                        Toast.makeText(activity, "从服务端拿数据了 ： " + result, Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.i("jin", "ss = " + result)
                }.start()

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