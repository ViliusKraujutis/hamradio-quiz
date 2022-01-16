package com.lt.lrmd.hamradio.quiz.activity;

import com.lt.lrmd.hamradio.quiz.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.text.Html;

public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView tv = (TextView)  findViewById(R.id.hello_world);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(Html.fromHtml(getString(R.string.hello_world)));

    }

}
