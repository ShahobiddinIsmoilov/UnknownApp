package com.rollermine.unknownapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CreateGroupLoading extends Fragment {

    private Activity activity;
    private AlertDialog dialog;
    TextView loadingMessage;

    CreateGroupLoading (Activity myActivity) {
        activity = myActivity;
    }

    void startLoading(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_please_wait, null);
        builder.setView(view);
        loadingMessage = view.findViewById(R.id.askPasswordText);
        loadingMessage.setText(text);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.cancel();
        //dialog.dismiss();
    }

}
