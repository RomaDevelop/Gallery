package com.example.gallery;

import android.widget.TextView;
import android.widget.ScrollView;

public class Logs {

    private static TextView output;
    private static ScrollView scroll;

    public static void init(TextView textView, ScrollView scroll_) {
        output = textView;
        scroll = scroll_;
    }

    public static void log(String text) {
        if (output == null) return;

        output.post(() -> {
            output.append(text + "\n");
            scroll.post(() -> {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            });
        });
    }
}
