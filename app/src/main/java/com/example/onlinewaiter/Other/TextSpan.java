package com.example.onlinewaiter.Other;

import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TextSpan extends ClickableSpan {
    @Override
    public void onClick(@NonNull View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        //int start = s.getSpanStart(this);
        //int end = s.getSpanEnd(this);
    }
}
