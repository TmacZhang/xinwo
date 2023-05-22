package com.cavalry.androidlib.mvp.presenter;

import android.content.Context;
import android.util.SparseArray;

import com.cavalry.androidlib.mvp.service.GetService;
import com.cavalry.androidlib.mvp.service.PostBodyService;
import com.cavalry.androidlib.mvp.service.PostFormService;
import com.cavalry.androidlib.mvp.subscriber.FastJsonSubscriber;
import com.cavalry.androidlib.mvp.view.IView;

import java.lang.reflect.Type;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>子类需覆写{@link #createGetService()} 和 {@link #createPostFormService()}, 可以参照{@link DefaultFastJsonTagPresenter}</p>
 * @author Cavalry Lin
 * @since 1.0.0
 */
public abstract class LibFastJsonTagPresenter extends LibTagPresenter<IView> {
    private final String TAG = "LibFastJsonTagPresenter";

    private SparseArray<Subscription> subscriptionSA = new SparseArray<>();

    public LibFastJsonTagPresenter(Context context, IView view) {
        super(context, view);
    }

    /**
     * 获取数据
     *
     * @param url       除参数外的完整url
     * @param tag      自己定义的一个整形,用于区分不同的请求
     * @param beanClazz bean对象的Class
     * @param method    {@link #GET} 或者 {@link #POST}
     * @param cache     是否缓存
     */
    @Override
    public void loadData(String url, final int tag, Class beanClazz, int method, boolean cache) {
        Map<String, String> params = mView.getParams(tag);

        Subscription subscription = null;
        if (method == GET) {
            GetService service = createGetService();
            subscription = createServiceObservable(service.get(url, params))
                    .subscribe(new FastJsonSubscriber(beanClazz) {
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
        } else if (method == POST) {
            PostFormService service = createPostFormService();
            subscription = createServiceObservable(service.post(url, params))
                    .subscribe(new FastJsonSubscriber(beanClazz) {
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
        } else {
            throw new RuntimeException("loadData 参数 method 不正确");
        }

        if(subscription!=null){
            subscriptionSA.put(tag,subscription);
        }
    }


    /**
     * 获取数据
     *
     * @param url     除参数外的完整url
     * @param tag    自己定义的一个整形,用于区分不同的请求
     * @param typeOfT 有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param method  {@link #GET} 或者 {@link #POST}
     * @param cache   是否缓存
     */
    @Override
    public void loadData(String url, final int tag, Type typeOfT, int method, boolean cache) {
        Map<String, String> params = mView.getParams(tag);

        Subscription subscription = null;

        if (method == GET) {
            GetService service = createGetService();
            createServiceObservable(service.get(url, params))
                    .subscribe(new FastJsonSubscriber(typeOfT) {
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
        } else if (method == POST) {
            PostFormService service = createPostFormService();
            createServiceObservable(service.post(url, params))
                    .subscribe(new FastJsonSubscriber(typeOfT) {
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
        } else {
            throw new RuntimeException("loadData 参数 method 不正确");
        }

        if(subscription!=null){
            subscriptionSA.put(tag,subscription);
        }
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

    protected abstract GetService createGetService();

    protected abstract PostFormService createPostFormService();

    protected abstract PostBodyService createPostBodyService();


}
