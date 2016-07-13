package com.sxdsf.echo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.echo.Caster;
import com.sxdsf.echo.Converter;
import com.sxdsf.echo.OnCast;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.echo.Voice;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Caster.
                create(new OnCast<Voice>() {
                    @Override
                    public void call(Receiver<Voice> voiceReceiver) {

                    }
                }).
                convert(new Converter<Voice, Voice>() {

                    @Override
                    public Caster<Voice> call(Caster<Voice> voiceCaster) {
                        return voiceCaster;
                    }
                }).
                receiveOn(new Switcher<Voice>() {
                    @Override
                    public Receiver<Voice> switches(Receiver<Voice> receiver) {
                        return receiver;
                    }
                }).
                cast(new Receiver<Voice>() {
                });
    }
}
