package com.xjh.xinwo.mvp.presenter;

import android.content.Context;
import android.util.SparseArray;

import androidx.collection.ArrayMap;

import com.alibaba.fastjson.JSON;

import com.cavalry.androidlib.mvp.presenter.LibTagPresenter;
import com.cavalry.androidlib.mvp.service.GetService;
import com.cavalry.androidlib.mvp.service.PostBodyService;
import com.cavalry.androidlib.mvp.service.PostFormService;
import com.cavalry.androidlib.mvp.view.IView;
import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.toolbox.utils.LibLog;
import com.xjh.xinwo.enity.base.BaseBean;
import com.xjh.xinwo.manager.CacheManager;
import com.xjh.xinwo.mvp.service.ServiceGenerator;
import com.xjh.xinwo.mvp.subscriber.SampleSubscriber;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class TagPresenter extends LibTagPresenter<IView> {
    private final static String TAG = "TagPresenter";

    private SparseArray<Subscription> subscriptionSA = new SparseArray<>();

    public TagPresenter(Context context, IView view) {
        super(context, view);
    }


    @Override
    public void loadData(String url, final int tag, Class beanClazz, int method, boolean cache) {
        Map<String, String> params = mView.getParams(tag);
        if(params==null)
            params = new ArrayMap<String,String>();

        Subscription subscription = null;
        if (method == GET) {
            subscription = get(url, tag, beanClazz, params, cache);
        } else if (method == POST) {
            subscription = postForm(url, tag, beanClazz, params);
        } else {
            throw new RuntimeException("loadData 参数 method 不正确");
        }

        if(subscription!=null){
            subscriptionSA.put(tag,subscription);
        }
    }



    @Override
    public void loadData(String url, final int tag, Type typeOfT, int method, boolean cache) {
        Map<String, String> params = mView.getParams(tag);
        if(params==null)
            params = new ArrayMap<String,String>();

        Subscription subscription = null;

        if (method == GET) {
            subscription = get(url, tag, typeOfT, params, cache);
        } else if (method == POST) {
            subscription = postForm(url, tag, typeOfT, params);
        }else {
            throw new RuntimeException("loadData 参数 method 不正确");
        }

        if(subscription!=null){
            subscriptionSA.put(tag,subscription);
        }
    }


    private Subscription get(final String url, final int tag, final Type typeOfT, Map<String, String> params, final boolean cache) {
        if (loadFromCache(url, tag, typeOfT, cache)) return null;

        LibLog.d(TAG,"从网络获取: url="+url);

        GetService service = createGetService();
        return createServiceObservable(service.get(url, params))
                .subscribe(new SampleSubscriber(typeOfT,url,cache) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }




    private Subscription get(final String url, final int tag, final Class beanClazz, Map<String, String> params, final boolean cache) {
        if(loadFromCache(url,tag,beanClazz,cache)) return null;

        LibLog.d(TAG,"从网络获取: url="+url);

        GetService service = createGetService();
        return createServiceObservable(service.get(url, params))
                .subscribe(new SampleSubscriber(beanClazz,url,cache) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }

    // form提交post请求
    private Subscription postForm(String url, final int tag, final Type typeOfT, Map<String, String> params) {
        PostFormService service = createPostFormService();
        return createServiceObservable(service.post(url, params))
                .subscribe(new SampleSubscriber(typeOfT) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }

    // form提交post请求
    private Subscription postForm(String url, final int tag, final Class beanClazz, Map<String, String> params) {
        PostFormService service = createPostFormService();
        return createServiceObservable(service.post(url, params))
                .subscribe(new SampleSubscriber(beanClazz) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }


    // body提交post请求
    public Subscription postBody(Map<String,String> headers, String url, final int tag, final Type typeOfT, String json) {
        PostBodyService service = createPostBodyService();
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        return createServiceObservable(service.post(headers, url, body))
                .subscribe(new SampleSubscriber(typeOfT) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }

    // body提交post请求
    public Subscription postBody(Map<String,String> headers, String url, final int tag, final Class beanClazz, String json) {
        PostBodyService service = createPostBodyService();
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        return createServiceObservable(service.post(headers, url, body))
                .subscribe(new SampleSubscriber(beanClazz) {
                    @Override
                    public void onLoadError(Throwable e) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onError(e, tag);
                    }

                    @Override
                    public void onLoadSuccess(Object o) {
                        subscriptionSA.remove(tag);
                        if(mView!=null)
                            mView.onSuccess(o, tag);
                    }
                });
    }

    /**
     * 从缓存加载数据
     * @param url
     * @param tag
     * @param typeOfT
     * @param cache     true,从缓存加载成功; false,从缓存加载失败
     * @return
     */
    private boolean loadFromCache(String url, int tag, Type typeOfT, boolean cache) {
        if(cache){
            String value = CacheManager.get(url);
            if(value!=null){
                if(mView!=null) {
                    try{
                        BaseBean bean = JSON.parseObject(value, typeOfT);
                        if(bean.message.code == 200){
                            mView.onSuccess(bean,tag);
                        }else{
                            mView.onError(new LibException(LibException.CODE_SELF_DEFINE,"从缓存读取,error=true"),tag);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mView.onError(new LibException(LibException.CODE_JSON_PARSE_ERROR),tag);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 从缓存加载数据
     * @param url
     * @param tag
     * @param beanClazz
     * @param cache     true,从缓存加载成功; false,从缓存加载失败
     * @return
     */
    private boolean loadFromCache(String url, int tag, Class beanClazz, boolean cache) {
        if(cache){
            String value = CacheManager.get(url);
            if(value!=null){
                if(mView!=null) {
                    try{
                        BaseBean bean = (BaseBean) JSON.parseObject(value, beanClazz);
                        if(bean.message.code == 200){
                            mView.onSuccess(bean,tag);
                        }else{
                            mView.onError(new LibException(LibException.CODE_SELF_DEFINE,"从缓存读取,error=true"),tag);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mView.onError(new LibException(LibException.CODE_JSON_PARSE_ERROR),tag);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected GetService createGetService() {
        return ServiceGenerator.getInstance().createService(GetService.class);
    }

    @Override
    protected PostFormService createPostFormService() {
        return ServiceGenerator.getInstance().createService(PostFormService.class);
    }

    @Override
    protected PostBodyService createPostBodyService() {
        return ServiceGenerator.getInstance().createService(PostBodyService.class);
    }

    /**
     * 创建网络访问的Observable,实现了线程的切换
     *
     * @param serviceObservable 通过service.xxx()创建的Observable
     * @return
     */
    private Observable createServiceObservable(Observable serviceObservable) {
        return serviceObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消请求
     * @param tag
     */
    public void cancel(int tag){
        Subscription subscription = subscriptionSA.get(tag);
        if(subscription!=null){
            if(!subscription.isUnsubscribed())
                subscription.unsubscribe();
            subscriptionSA.remove(tag);
        }
    }

    /**
     * 取消所有请求
     */
    public void cancelAll(){
        int size = subscriptionSA.size();
        Subscription subscription = null;
        for(int i=0;i<size;i++){
            subscription = subscriptionSA.valueAt(i);
            if(subscription!=null && !subscription.isUnsubscribed())
                subscription.unsubscribe();
            subscriptionSA.removeAt(i);
        }
    }
}
