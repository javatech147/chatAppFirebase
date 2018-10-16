package com.waytojava.ichatapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.waytojava.ichatapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Utils {
    private static final String TAG = "iChatApp";
    private static ProgressDialog progressDialog;
    public static String FOLDER_NAME = "iChatTemp";

    public static void log(String tag, String message) {
        Log.d(String.valueOf(tag), String.valueOf(message));
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            Utils.log(TAG, "Path ---- " + path);
            return path;
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String saveImageToGivenFolder(Bitmap finalBitmap, String folderName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + folderName);
        myDir.mkdirs();
        String fileName = Utils.setImageName();
        File file = new File(myDir, fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static boolean deleteFolderInRoot(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteFolderInRoot(f);
            }
        }
        if (file.delete()) {
            return true;
        }
        return false;
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        ++month;
        return "" + day + "" + month + "" + year + "" + hour + "" + minute;
    }

    public static String setImageName() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String imageFileName = "IMG_" + day + month + year + "_" + hour + minute + second + ".jpg";
        return imageFileName;
    }

    public static String getStringFromLast(String word, int lengthFromLast) {
        if (word.length() == lengthFromLast) {
            return word;
        } else if (word.length() > lengthFromLast) {
            return word.substring(word.length() - lengthFromLast);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than " + lengthFromLast + " characters!");
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static ProgressDialog showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setMessage("please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) context).findViewById(android.R.id.content).getWindowToken(), 0);
    }

    public static void snackbar(Context context, String message) {
        // Convert Toast into Snackbar
        Snackbar sb = Snackbar.make(((Activity) context).findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        sb.show();
    }
}
