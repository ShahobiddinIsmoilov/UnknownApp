package com.rollermine.unknownapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AskPassword extends Fragment {

    private Activity activity;
    private AlertDialog dialog;
    TextView loadingMessage;
    Button cancel;

    AskPassword(Activity myActivity) {
        activity = myActivity;
    }

    void startAsking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enter_password, null);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissAsking() {
        dialog.cancel();
        //dialog.dismiss();
    }

}
