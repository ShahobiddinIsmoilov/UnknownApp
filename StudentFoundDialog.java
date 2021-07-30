package com.rollermine.unknownapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class StudentFoundDialog extends Fragment {

    private final Activity activity;
    private AlertDialog dialog;
    private RequestQueue queue;
    TextView loading;

    StudentFoundDialog(Activity myActivity) { activity = myActivity; }

    void startDialog(String gid,
                     String sid,
                     String gl,
                     String gd,
                     String gt,
                     String gp,
                     boolean isCashier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        queue = Volley.newRequestQueue(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_student_found, null);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();

        String ginf = gl + ", " + gd + " " + gt + ", " + gp;
        if (!gp.contains("eacher") && !gp.contains("eadmaster"))
            ginf += " teacher";
        TextView groupInfo = view.findViewById(R.id.studentFoundGroupInfo);
        groupInfo.setText(ginf);

        Button cancel = view.findViewById(R.id.buttonLeavePreviousCancel);
        cancel.setOnClickListener(v -> {
            dialog.cancel();
        });

        loading = view.findViewById(R.id.studentFoundLoadingBar);

        Button leave = view.findViewById(R.id.buttonLeavePrevious);
        leave.setOnClickListener(v -> {
            if (isCashier) {
                loading.setText("Something went wrong");
                loading.setVisibility(View.VISIBLE);
            }
            else
                removeStudent(gid, sid);
        });

        dialog.show();
    }

    public void removeStudent(String gid, String sid) {

        String url = buildURL(gid, sid);
        loading.setText("Please wait...");
        loading.setVisibility(View.VISIBLE);
        leaveGroup(url);
    }

    public void leaveGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/leaveGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            loading.setText("Successful");
                            dismissDialog();
                        }
                        else {
                            loading.setText("Something went wrong");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loading.setText("Something went wrong");
                    }
                },
                e -> {
                    e.printStackTrace();
                    loading.setText("No connection");
                }
        );

        queue.add(request);
    }

    public String buildURL(String groupID,
                           String phoneNumber) {
        String URL = ("groupID=" + groupID);
        URL += ("&phoneNumber=" + phoneNumber);
        return URL;
    }

    void dismissDialog() {
        dialog.cancel();
    }

}
