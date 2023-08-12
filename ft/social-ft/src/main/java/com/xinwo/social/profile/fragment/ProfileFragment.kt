package com.xinwo.social.profile.fragment;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.atech.staggedrv.StaggerdRecyclerView
import com.atech.staggedrv.callbacks.LoadMoreAndRefresh
import com.xinwo.base.BaseFragment
import com.xinwo.social.R
import com.xinwo.social.profile.fragment.viewmodel.ProfilerModel
import com.xinwo.social.profile.fragment.viewmodel.ProfilerViewModel

class ProfileFragment : BaseFragment() {
    var mProfileViewModel: ProfilerViewModel? = null
    var mNameView: TextView? = null
    var mIdView: TextView? = null
    var mRecycleView : StaggerdRecyclerView? = null
    var mProfileAdapter : ProfileAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.fragment_profile, container, false)
        mIdView = root.findViewById(R.id.profile_id)
        mNameView = root.findViewById(R.id.profile_name)
        mRecycleView = root.findViewById(R.id.profile_rv) as StaggerdRecyclerView
        setViewModel()
        getUserInfoFromServer()
        setAdapter()
        return root
    }

    private fun setAdapter() {
        if (mProfileAdapter == null) {
            mProfileAdapter = ProfileAdapter(this.context)
            mRecycleView?.link(mProfileAdapter, 2)
        }

        mProfileAdapter?.datas?.add(ProfileItemModel(500, 500, R.drawable.a2))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 1000, R.drawable.a2))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 750, R.drawable.a3))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 530, R.drawable.a4))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 400, R.drawable.a5))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 980, R.drawable.a6))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 600, R.drawable.a7))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 620, R.drawable.a8))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 680, R.drawable.c1))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 705, R.drawable.c2))
        mProfileAdapter?.datas?.add(ProfileItemModel(500, 885, R.drawable.c3))

        mRecycleView?.addCallbackListener(object : LoadMoreAndRefresh {
            override fun onLoadMore() {
                //模拟加载更多
                val datas = ArrayList<ProfileItemModel>();
                datas.add(ProfileItemModel(500, 840, R.drawable.girl_photo_01_small))
                datas.add(ProfileItemModel(500, 712, R.drawable.c5))
                datas.add(ProfileItemModel(500, 624, R.drawable.c6))
                datas.add(ProfileItemModel(500, 888, R.drawable.c7))
                mProfileAdapter?.loadMore(datas)
            }

            override fun onRefresh() {

            }
        })
    }

    override fun initData() {
    }

    override fun initPresenter() {
    }

    override fun initView() {

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

    fun getUserInfoFromServer() {
        //假设从网络获取数据
        Thread.sleep(100)
        mProfileViewModel?.getMutableLiveData()?.let {
            val profilerViewModel= it.value
            profilerViewModel?.mId = 1234
            profilerViewModel?.mName = "zhangsan"
            it.postValue(profilerViewModel)
        }
    }

    fun setViewModel() {
        mProfileViewModel = ViewModelProviders.of(this).get(ProfilerViewModel::class.java)
        mProfileViewModel?.getMutableLiveData()?.value = ProfilerModel()
        mProfileViewModel?.getMutableLiveData()?.observe(this) { model ->
            mNameView?.setText(model.mName)
            mIdView?.setText(model.mId.toString())
        }
    }
}