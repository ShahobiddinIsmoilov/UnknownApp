package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private RequestQueue queue;
    TextView cleanablesData;
    TextView uncleanablesData;
    TextView statusInfo;
    TextView mess;
    TextView crazyText, crazyTitle;
    ConstraintLayout cleBack;
    ConstraintLayout uncleBack;
    CardView crazyCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        queue = Volley.newRequestQueue(getActivity());

        getActivity().setTitle("Status");

        cleanablesData = view.findViewById(R.id.cleanablesValue);
        uncleanablesData = view.findViewById(R.id.uncleanablesValue);
        mess = view.findViewById(R.id.messageHome);
        crazyCard = view.findViewById(R.id.crazyText);
        crazyText = view.findViewById(R.id.crazyTextView);
        crazyTitle = view.findViewById(R.id.crazyTitle);

        String clean = load("cle.txt");
        String unclean = load("uncle.txt");

        cleanablesData.setText(clean);
        uncleanablesData.setText(unclean);

        statusInfo = view.findViewById(R.id.statusInfoTextView);
        cleBack = view.findViewById(R.id.cleBackground);
        uncleBack = view.findViewById(R.id.uncleBackground);

        /*requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
                System.exit(0);
            }
        });*/

        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++)
            fragmentManager.popBackStack();*/

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
            getStatus();
        }

        return view;
    }

    public void noInternet() {
        NoInternetDialog noInternetDialog = new NoInternetDialog();
        noInternetDialog.show(getActivity().getSupportFragmentManager(), "NO CONNECTION");
    }

    public void operationError() {
        OperationErrorDialog operationErrorDialog = new OperationErrorDialog();
        operationErrorDialog.show(getActivity().getSupportFragmentManager(), "OPERATION ERROR");
    }

    CreateGroupLoading loading;

    public void getStatus() {

        String gid = load("group_id.txt");
        String sid = load("student_id.txt");

        String url = buildURL(gid, sid);

        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Loading...");
        status(url);
    }

    public void status(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/status?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String cleanables = response.getString("cleanables");
                            String uncleanables = response.getString("uncleanables");
                            String message = response.getString("message");
                            cleanablesData.setText(cleanables);
                            uncleanablesData.setText(uncleanables);
                            mess.setText(message);
                            customize(cleanables, uncleanables);
                            loading.dismissDialog();
                            save(cleanables, "cle.txt");
                            save(uncleanables, "uncle.txt");
                            getCrazyText();
                        }
                        else {
                            operationError();
                            customize(cleanablesData.getText().toString(), uncleanablesData.getText().toString());
                            loading.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        operationError();
                        customize(cleanablesData.getText().toString(), uncleanablesData.getText().toString());
                        loading.dismissDialog();
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    noInternet();
                    customize(cleanablesData.getText().toString(), uncleanablesData.getText().toString());
                    loading.dismissDialog();
                }
        );

        queue.add(request);
    }

    public void getCrazyText() {

        String URL = "https://no-more-cashier-list.herokuapp.com/crazyMessage";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String crazy = response.getString("crazyText");
                            String title = response.getString("crazyTitle");
                            if (crazy.length() > 0) {
                                crazyText.setText(crazy);
                                title = "   " + title;
                                crazyTitle.setText(title);
                                crazyCard.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                e -> {
                    e.printStackTrace();
                }
        );

        queue.add(request);
    }

    public String load(String filename) {

        FileInputStream fis = null;

        try {
            fis = getActivity().openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            return br.readLine();
        } catch (FileNotFoundException e) {
            return "0";
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

    public void customize(String cle, String uncle) {
        int cl = Integer.parseInt(cle);
        int uncl = Integer.parseInt(uncle);

        if (cl < 0)
            if (cl < -14) {
                cleBack.setBackgroundColor(Color.parseColor("#FF6060"));
                statusInfo.setTextColor(Color.parseColor("#FF0000"));
                statusInfo.setText("YOU ARE NOT ELIGIBLE");
            }
            else
                cleBack.setBackgroundColor(Color.parseColor("#FFB241"));

        if (uncl < 0)
            if (uncl < -14) {
                uncleBack.setBackgroundColor(Color.parseColor("#FF6060"));
                statusInfo.setTextColor(Color.parseColor("#FF0000"));
                statusInfo.setText("YOU ARE NOT ELIGIBLE");
            }
            else
                uncleBack.setBackgroundColor(Color.parseColor("#FFB241"));

    }

    public String buildURL(String groupID,
                           String phoneNumber) {
        String URL = ("groupID=" + groupID);
        URL += ("&phoneNumber=" + phoneNumber);
        return URL;
    }
}
