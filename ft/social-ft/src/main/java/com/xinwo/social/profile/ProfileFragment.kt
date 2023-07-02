package com.xinwo.social.profile;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xinwo.base.BaseFragment
import com.xinwo.social.R
import com.xinwo.social.profile.viewmodel.ProfilerModel
import com.xinwo.social.profile.viewmodel.ProfilerViewModel

class ProfileFragment : BaseFragment() {
    var mProfileViewModel: ProfilerViewModel? = null
    var mNameView: TextView? = null
    var mIdView: TextView? = null

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
        setViewModel()
        getUserInfoFromServer()
        return root
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
        mProfileViewModel?.getMutableLiveData()?.observe(this, Observer { model ->
            mNameView?.setText(model.mName)
            mIdView?.setText(model.mId.toString())

        })
    }
}