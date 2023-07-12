package com.xinwo.social.profile.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfilerViewModel : ViewModel() {
    var mMutableLiveData: MutableLiveData<ProfilerModel>? = null

    fun getMutableLiveData() : MutableLiveData<ProfilerModel>? {
        if (mMutableLiveData == null) {
            mMutableLiveData = MutableLiveData<ProfilerModel>()
        }
        return mMutableLiveData
    }

}