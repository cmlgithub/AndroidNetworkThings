package com.cml.androidnetworkthings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TwoActivity extends AppCompatActivity {

    private TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        mtextView = (TextView) findViewById(R.id.textView);
        mtextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.pop, null);
                PopupWindow popupWindow = new PopupWindow(view,400,600);
                popupWindow.showAsDropDown(mtextView);
            }
        });


    }
}
