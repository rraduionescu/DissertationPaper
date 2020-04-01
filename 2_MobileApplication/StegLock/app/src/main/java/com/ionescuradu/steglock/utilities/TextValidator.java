package com.ionescuradu.steglock.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

//  Created by Ionescu Radu Stefan  //

public abstract class TextValidator implements TextWatcher
{
    private final TextView textView;

    protected TextValidator(TextView textView)
    {
        this.textView = textView;
    }

    protected abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s)
    {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }
}