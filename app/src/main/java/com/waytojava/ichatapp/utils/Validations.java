package com.waytojava.ichatapp.utils;

import android.content.Context;
import android.text.TextUtils;

import com.waytojava.ichatapp.R;

public class Validations {
    public static boolean validateInputFieldsSignup(Context context, String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Utils.toast(context, context.getString(R.string.txt_please_enter_your_name));
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Utils.toast(context, context.getString(R.string.txt_pleas_enter_email));
            return false;
        } else if (!Utils.isValidEmail(email)) {
            Utils.toast(context, context.getString(R.string.txt_not_a_valid_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Utils.toast(context, context.getString(R.string.txt_please_enter_password));
            return false;
        } else {
            return true;
        }
    }
}
