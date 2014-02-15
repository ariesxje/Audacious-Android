package com.Audacious.audacious;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class SpinningOnlyProgressDialog extends Dialog {

    public static SpinningOnlyProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false);
    }

    public static SpinningOnlyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static SpinningOnlyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static SpinningOnlyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
        SpinningOnlyProgressDialog dialog = new SpinningOnlyProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.getWindow().getAttributes().verticalMargin = 0.2F;
        dialog.show();

        return dialog;
    }

    public SpinningOnlyProgressDialog(Context context) {
        super(context, R.style.SpinningDialog);
    }
}