package com.xinwo.feed

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.xinwo.base.BaseFragment
import com.xinwo.feed.model.FeedModel
import com.xinwo.network.NetManager
import java.util.ArrayList

class FeedFragment : BaseFragment() {
    var mRecycleView: StaggerdRecyclerView? = null;
    var mFeedApdater: FeedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_feed, container, false)
        return root
    }

    override fun initData() {
    }

    override fun initPresenter() {
    }

    override fun initView() {
        mRecycleView = view?.findViewById(R.id.feed_rv)
        if (mFeedApdater == null) {
            mFeedApdater = FeedAdapter(this.context)
            mRecycleView?.link(mFeedApdater, 2)
        }


        mFeedApdater?.datas?.add(FeedModel(500, 500, R.drawable.a2))
        mFeedApdater?.datas?.add(FeedModel(500, 1000, R.drawable.a2))
        mFeedApdater?.datas?.add(FeedModel(500, 750, R.drawable.a3))
        mFeedApdater?.datas?.add(FeedModel(500, 530, R.drawable.a4))
        mFeedApdater?.datas?.add(FeedModel(500, 400, R.drawable.a5))
        mFeedApdater?.datas?.add(FeedModel(500, 980, R.drawable.a6))
        mFeedApdater?.datas?.add(FeedModel(500, 600, R.drawable.a7))
        mFeedApdater?.datas?.add(FeedModel(500, 620, R.drawable.a8))
        mFeedApdater?.datas?.add(FeedModel(500, 680, R.drawable.c1))
        mFeedApdater?.datas?.add(FeedModel(500, 705, R.drawable.c2))
        mFeedApdater?.datas?.add(FeedModel(500, 885, R.drawable.c3))

        mRecycleView?.addCallbackListener(object : LoadMoreAndRefresh {
            override fun onLoadMore() {
                //模拟加载更多
                val datas = ArrayList<FeedModel>();
                datas.add(FeedModel(500, 840, R.drawable.girl_photo_01_small))
                datas.add(FeedModel(500, 712, R.drawable.c5))
                datas.add(FeedModel(500, 624, R.drawable.c6))
                datas.add(FeedModel(500, 888, R.drawable.c7))
                mFeedApdater?.loadMore(datas)
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