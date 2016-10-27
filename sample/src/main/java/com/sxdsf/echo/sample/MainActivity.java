package com.sxdsf.echo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sxdsf.echo.Acceptor;
import com.sxdsf.echo.Cast;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.switchers.Switchers;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private boolean mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                echo();
                //                rxJava();
            }
        });
    }

    private void echo() {
        Cast cast = Call.
                create(new OnCall() {
                    @Override
                    public void call(Receiver<Response> callbackReceiver) {
                        System.out.println("callOn" + Thread.currentThread());
                        if (callbackReceiver instanceof Acceptor) {
                            Acceptor acceptor = (Acceptor) callbackReceiver;
                            if (!acceptor.isUnReceived() && callbackReceiver instanceof Callback) {
                                Callback callback = (Callback) callbackReceiver;
                                callback.onStart();
                                callback.onSuccess(new Response());
                                callback.onError(new Exception());
                                callback.onCancel();
                            }
                        }
                    }
                }).
                callOn(Switchers.io()).
                callbackOn(Switchers.main()).
                execute(new Callback() {

                    @Override
                    public void onStart() {
                        System.out.println("调用onStart在" + Thread.currentThread());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("调用onError在" + Thread.currentThread());
                    }

                    @Override
                    public void onSuccess(Response response) {
                        System.out.println("调用onSuccess在" + Thread.currentThread());
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("调用onCancel在" + Thread.currentThread());
                    }
                });
        if (mCancel) {
            if (!cast.isUnReceived()) {
                cast.unReceive();
            }
        }
        mCancel = !mCancel;
    }

    private void rxJava() {
        Subscription subscription = Observable.
                create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        System.out.println("callOn" + Thread.currentThread());
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext("测试");
                        }
                    }
                }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext" + s);
                    }
                });
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        Observable.
                create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        System.out.println("第二次callOn" + Thread.currentThread());
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext("第二次测试");
                        }
                    }
                }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("第二次onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("第二次onError");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("第二次onNext" + s);
                    }
                });
    }
}
