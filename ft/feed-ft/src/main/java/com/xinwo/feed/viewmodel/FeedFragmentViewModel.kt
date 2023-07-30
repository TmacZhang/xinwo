package com.xinwo.feed.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinwo.feed.api.IGetHostService
import com.xinwo.feed.find.model.FeedListModel
import com.xinwo.network.NetManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FeedFragmentViewModel: ViewModel() {
    var mMutableLiveData: MutableLiveData<FeedListModel>? = null

    fun getMutableLiveData() : MutableLiveData<FeedListModel>? {
        if (mMutableLiveData == null) {
            mMutableLiveData = MutableLiveData<FeedListModel>()
        }
        return mMutableLiveData
    }

     fun getHotFeed() {
        val retrofit = NetManager.getRetrofit("http://180.76.242.204:18080")
        retrofit.create(IGetHostService::class.java)
            .getData(1, 10, "img")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<FeedListModel>() {
                override fun onNext(data: FeedListModel) {
                    Log.i("jin", data.toString())
                    getMutableLiveData()?.postValue(data)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }
}