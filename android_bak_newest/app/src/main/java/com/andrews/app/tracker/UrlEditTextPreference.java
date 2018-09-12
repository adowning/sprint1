/*
 * Copyright (c) 2017 Bartek Fabiszewski
 * http://www.fabiszewski.net
 *
 * This file is part of μlogger-android.
 * Licensed under GPL, either version 3, or any later.
 * See <http://www.gnu.org/licenses/>
 */

package com.andrews.app.tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.andrews.app.R;

/**
 * URL edit text preference
 * Validates and trims URL
 */

class UrlEditTextPreference extends EditTextPreference {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UrlEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public UrlEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UrlEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlEditTextPreference(Context context) {
        super(context);
    }

    @Override
    public void setText(String text) {
        super.setText(text.trim());
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        getEditText().setError(null);
        final AlertDialog dialog = (AlertDialog) getDialog();
        View positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveButtonClicked();
            }
        });
    }

    private void onPositiveButtonClicked() {
        final String url = getEditText().getText().toString().trim();
        if (url.length() == 0 || WebHelper.isValidURL(url)) {
            getEditText().setError(null);
            onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
            getDialog().dismiss();
        } else {
            getEditText().setError("error");
        }
    }
}
