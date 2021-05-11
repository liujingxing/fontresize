package com.example.fontreszie;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.ljx.view.FontResizeView;
import com.ljx.view.FontResizeView.OnFontChangeListener;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);

        FontResizeView fontResizeView = findViewById(R.id.font_resize_view);
//        fontResizeView.setFontSize(20);
//        fontResizeView.setSliderGrade(5);
        fontResizeView.setOnFontChangeListener(new OnFontChangeListener() {
            @Override
            public void onFontChange(float fontSize) {
                mTextView.setTextSize(fontSize);
                mTextView.setText("当前字体大小:" + fontSize + "sp");
            }
        });

        float fontSize = fontResizeView.getFontSize();
        mTextView.setTextSize(fontSize);
        mTextView.setText("当前字体大小:" + fontResizeView.getFontSize() + "sp");
    }
}
