package com.sxdsf.echo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Cast cast = Call.
                //                        create(new OnCall() {
                //                            @Override
                //                            public void call(Receiver<Response> callbackReceiver) {
                //                                System.out.println("callOn" + Thread.currentThread());
                //                                try {
                //                                    Thread.sleep(3000);
                //                                } catch (InterruptedException e) {
                //                                    e.printStackTrace();
                //                                }
                //                                if (callbackReceiver instanceof Acceptor) {
                //                                    Acceptor acceptor = (Acceptor) callbackReceiver;
                //                                    if (!acceptor.isUnReceived() && callbackReceiver instanceof Callback) {
                //                                        Callback callback = (Callback) callbackReceiver;
                //                                        callback.onStart();
                //                                        callback.onSuccess(new Response());
                //                                        callback.onError(new Exception());
                //                                        callback.onCancel();
                //                                    }
                //                                }
                //                            }
                //                        }).
                //                        unify(new Converter() {
                //                            @Override
                //                            public Call convert(Call call) {
                //                                return call.callOn(Switchers.asyncThread()).callbackOn(Switchers.mainThread());
                //                            }
                //                        }).
                //                        execute(new Callback() {
                //                            @Override
                //                            public void onStart() {
                //                                System.out.println("调用onStart在" + Thread.currentThread());
                //                            }
                //
                //                            @Override
                //                            public void onError(Throwable t) {
                //                                System.out.println("调用onError在" + Thread.currentThread());
                //                            }
                //
                //                            @Override
                //                            public void onSuccess(Response response) {
                //                                System.out.println("调用onSuccess在" + Thread.currentThread());
                //                            }
                //
                //                            @Override
                //                            public void onCancel() {
                //                                System.out.println("调用onCancel在" + Thread.currentThread());
                //                            }
                //                        });
                //                if (!cast.isUnReceived()) {
                //                    cast.unReceive();
                //                }
                Subscription subscription = Observable.
                        create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                System.out.println("开始调用");
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext("测试");
                                }
                            }
                        }).
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
            }
        });
    }
}
