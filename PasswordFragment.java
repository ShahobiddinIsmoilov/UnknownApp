package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class PasswordFragment extends Fragment {

    private RequestQueue queue;
    String gid;

    public PasswordFragment(String groupID) {
        this.gid = groupID;
    }

    EditText passcode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);

        getActivity().setTitle("Choose a password");

        queue = Volley.newRequestQueue(getActivity());
        passcode = view.findViewById(R.id.password);

        Button enter = view.findViewById(R.id.buttonEnter);
        enter.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
            if (timeOfDay > 22 || timeOfDay < 5) {
                new AlertDialog.Builder(getContext())
                        .setTitle("It's sleep time")
                        .setIcon(R.drawable.ic_night)
                        .setMessage("App does not operate between 11:00 PM and 5:00 AM for reasons that are better left unsaid. Sorry for the inconvenience. Meanwhile, you should get some rest too")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                System.exit(0);
                            }
                        })
                        .setCancelable(false).show();
            }
            else {
                if (passcode.getText().toString().length() > 5 && notJustSpaces(passcode.getText().toString())) {
                    pass();
                } else {
                    passwordWrong();
                }
            }
        });

        TextView show = view.findViewById(R.id.showPassword);
        show.setOnClickListener(v -> {
            if (show.getText().toString().equals("Show")) {
                passcode.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                show.setText("Hide");
                passcode.setSelection(passcode.getText().length());
            } else {
                passcode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                show.setText("Show");
                passcode.setSelection(passcode.getText().length());
            }
        });

        return view;
    }

    private boolean notJustSpaces(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ')
                return false;
        }
        return true;
    }

    CreateGroupLoading loading;

    public void pass() {

        String url = buildURL(gid, passcode.getText().toString());
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Please wait...");
        setNewPassword(url);

    }

    public void setNewPassword(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/changePassword?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String sid = load("student_id.txt");
                            save(sid, "cashier.txt");
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                        }
                        else {
                            operationError();
                            loading.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        operationError();
                        loading.dismissDialog();
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    noInternet();
                    loading.dismissDialog();
                }
        );

        queue.add(request);
    }

    public void save(String data, String filename) {
        FileOutputStream fos = null;

        try {
            fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String load(String filename) {

        FileInputStream fis = null;

        try {
            fis = getActivity().openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            return br.readLine();
        } catch (FileNotFoundException e) {
            return "null";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "null";
    }

    public String buildURL(String gid,
                           String pass) {
        String URL = ("groupID=" + gid);
        URL += ("&password=" + pass);
        return URL;
    }

    public void passwordWrong() {
        PasswordRequirements passwordRequirements = new PasswordRequirements();
        passwordRequirements.show(getActivity().getSupportFragmentManager(), "STUDENT EXISTS");
    }

    public void noInternet() {
        NoInternetDialog noInternetDialog = new NoInternetDialog();
        noInternetDialog.show(getActivity().getSupportFragmentManager(), "NO CONNECTION");
    }

    public void operationError() {
        OperationErrorDialog operationErrorDialog = new OperationErrorDialog();
        operationErrorDialog.show(getActivity().getSupportFragmentManager(), "OPERATION ERROR");
    }
}