package com.xinwo.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xinwo.base.BaseFragment
import com.xjh.xinwo.mvp.model.BaseBean

class FeedFragment : BaseFragment() {
    var mRecycleView: RecyclerView? = null;
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
            val baseBeanList: MutableList<BaseBean> = ArrayList()
            for (i in 0 .. 100)  {
                baseBeanList.add(BaseBean())
            }

            mFeedApdater = FeedAdapter(R.layout.item_feed, baseBeanList)

            mRecycleView?.apply {
                this.setLayoutManager(GridLayoutManager(context, 2))
                this.adapter = mFeedApdater
            }
        }
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