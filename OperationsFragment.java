package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class OperationsFragment extends Fragment {

    private RequestQueue queue;
    ArrayList<StudentListItem> exampleList;
    View view;
    RecyclerView mRecyclerView;
    ConstraintLayout noService;
    ImageView offlineImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operations, container, false);

        queue = Volley.newRequestQueue(getActivity());

        getActivity().setTitle("Cashierworks");

        noService = view.findViewById(R.id.offlineOperations);
        offlineImage = view.findViewById(R.id.offlineImage);

        // ----- Recycler View -----

        exampleList = new ArrayList<>();

//        RecyclerView mRecyclerView = view.findViewById(R.id.studentListRecyclerView);
//        mRecyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(f);
//        RecyclerView.Adapter mAdapter = new StudentListAdapter(exampleList);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);

        // ----- Recycler View -----

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
            getStudents();
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
    ArrayList<String> colorArray = new ArrayList<String>();
    ArrayList<String> nameArray = new ArrayList<String>();
    ArrayList<String> phoneArray = new ArrayList<String>();
    ArrayList<String> cleanArray = new ArrayList<String>();
    ArrayList<String> uncleanArray = new ArrayList<String>();
    ArrayList<String> messageArray = new ArrayList<String>();

    public void getStudents() {

        String gid = load("group_id.txt");
        String url = "https://no-more-cashier-list.herokuapp.com/studentsList?groupID=" + gid;

        if (nameArray.size() == 0) {
            loading = new CreateGroupLoading(getActivity());
            loading.startLoading("Loading...");
            students(url);
        }
        else {
            for (int i = 0; i < nameArray.size(); i++) {
                exampleList.add(new StudentListItem(/*R.drawable.boat,*/colorArray.get(i),
                        nameArray.get(i),
                        phoneArray.get(i),
                        cleanArray.get(i),
                        uncleanArray.get(i),
                        messageArray.get(i)));
            }
        }
    }

    public void students(String URL) {

        mRecyclerView = view.findViewById(R.id.studentListRecyclerView);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            JSONArray jsonArray = response.getJSONArray("students");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject student = jsonArray.getJSONObject(i);
                                String phoneNumber = student.getString("phoneNumber");
                                String color = student.getString("avatar");
                                String name = student.getString("name");
                                String cle = student.getString("cleanables");
                                String uncle = student.getString("uncleanables");
                                String message = student.getString("message");
                                colorArray.add(color);
                                nameArray.add(name);
                                phoneArray.add(phoneNumber);
                                cleanArray.add(cle);
                                uncleanArray.add(uncle);
                                exampleList.add(new StudentListItem(/*R.drawable.boat*/color, phoneNumber, name, cle, uncle, message));
                            }

                            mRecyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            RecyclerView.Adapter mAdapter = new StudentListAdapter(getActivity(), exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                            loading.dismissDialog();
                        }
                        else {
                            operationError();
                            loading.dismissDialog();
                            offlineImage.setImageResource(R.drawable.sad_face);
                            noService.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        operationError();
                        loading.dismissDialog();
                        offlineImage.setImageResource(R.drawable.sad_face);
                        noService.setVisibility(View.VISIBLE);
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    noInternet();
                    loading.dismissDialog();
                    noService.setVisibility(View.VISIBLE);
                    offlineImage.setImageResource(R.drawable.cloud_offline);
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

    public void loadList() {
        FileInputStream fis = null;

        try {
            fis = getActivity().openFileInput("error.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            TextView w = getView().findViewById(R.id.welcome);
            w.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

}
