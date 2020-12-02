package com.example.playlistytsp;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * This class was created to override default EditText by adding isEmpty() method. It can shows if "MyEditText".toString() equals null
 */

public class MyEditText extends AppCompatEditText {


    public MyEditText(@NonNull Context context) {
        super(context);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isEmpty() {
        if (this.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}