package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class JoinGroupFragment extends Fragment {

    private RequestQueue queue;
    EditText code, passcode, phoneNumber, name;
    TextView note;
    CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);

        getActivity().setTitle("Join a group");

        queue = Volley.newRequestQueue(getActivity());
        code = view.findViewById(R.id.codeDataJG);
        passcode = view.findViewById(R.id.passwordDataJG);
        note = view.findViewById(R.id.linkHelpNote);
        name = view.findViewById(R.id.studentNameDataJG);
        phoneNumber = view.findViewById(R.id.phoneNumberDataJG);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        new StartFragment()).commit();
            }
        });

        Button buttonJoinGroup = view.findViewById(R.id.buttonJoin);
        buttonJoinGroup.setOnClickListener(v -> {
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
                if (name.getText().toString().length() > 0 && notJustSpaces(name.getText().toString()) &&
                        phoneNumberIsCorrect(phoneNumber.getText().toString())) {
                    joinGroup();
                } else {
                    fillAll();
                }
            }
        });

        Button buttonEnterGroup = view.findViewById(R.id.buttonEnter);
        buttonEnterGroup.setOnClickListener(v -> {
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
                enterGroup();
            }
        });

        checkBox = view.findViewById(R.id.isCashier);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passcode.setVisibility(View.VISIBLE);
                buttonEnterGroup.setVisibility(View.VISIBLE);
                code.setVisibility(View.GONE);
                note.setVisibility(View.GONE);
            } else {
                passcode.setVisibility(View.GONE);
                buttonEnterGroup.setVisibility(View.GONE);
                code.setVisibility(View.VISIBLE);
                note.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private boolean notJustSpaces(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ')
                return true;
        }
        return false;
    }

    private boolean phoneNumberIsCorrect(String s) {
        String numbers = "0123456789";
        int k = 0;
        if (s.length() < 9)
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (numbers.contains(String.valueOf(s.charAt(i)))) {
                k++;
            }
        }
        if (k == 9)
            return true;
        return false;
    }

    public void fillAll() {
        FillAllDialog fillAllDialog = new FillAllDialog();
        fillAllDialog.show(getActivity().getSupportFragmentManager(), "PLEASE FILL OUT");
    }

    public void wrongPassword() {
        WrongPassword wrongPassword = new WrongPassword();
        wrongPassword.show(getActivity().getSupportFragmentManager(), "WRONG PASSWORD");
    }

    public void invalidCode() {
        InvalidCodeDialog invalidCodeDialog = new InvalidCodeDialog();
        invalidCodeDialog.show(getActivity().getSupportFragmentManager(), "INVALID CODE");
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
    StudentFoundDialog studentFoundDialog;

    public void enterGroup() {

        String url = "cashier=" + phoneNumber.getText().toString();

        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Please wait...");
        enter(url);
    }

    public void enter(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkCashier?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                String gid = response.getString("groupID");
                                String sid = phoneNumber.getText().toString();
                                String password = response.getString("password");
                                String pass = passcode.getText().toString();
                                if (pass.equals(password)) {
                                    save(gid, "group_id.txt");
                                    save(sid, "student_id.txt");
                                    save(sid, "cashier.txt");
                                    loading.dismissDialog();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new HomeFragment()).commit();
                                }
                                else {
                                    loading.dismissDialog();
                                    wrongPassword();
                                }
                            }
                            else {
                                wrongPassword();
                                loading.dismissDialog();
                            }
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

    public void joinGroup() {

        View view = getView();
        phoneNumber = view.findViewById(R.id.phoneNumberDataJG);

        String url = buildURL(name.getText().toString(),
                phoneNumber.getText().toString(),
                code.getText().toString());

        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Please wait...");
        checkCode(url);
    }

    public void checkCode(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkCode?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                String groupID = response.getString("groupID");
                                String cashier = response.getString("cashier");
                                checkStudent(url, groupID, cashier);
                            }
                            else {
                                invalidCode();
                                loading.dismissDialog();
                            }
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

    BottomNavigationView bottomNavigationView;

    public void checkStudent(String url, String groupID, String cashier) {

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        String URL = "https://no-more-cashier-list.herokuapp.com/checkStudent?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                String gid = response.getString("groupID");
                                phoneNumber = getView().findViewById(R.id.phoneNumberDataJG);
                                String sid = phoneNumber.getText().toString();
                                if (gid.equals(groupID)) {
                                    if (!sid.equals(cashier)) {
                                        save(gid, "group_id.txt");
                                        save(sid, "student_id.txt");
                                        save(cashier, "cashier.txt");
                                        bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new HomeFragment()).commit();
                                    }
                                    else
                                        wrongPassword();
                                    loading.dismissDialog();
                                } else {
                                    studentFoundDialog = new StudentFoundDialog(getActivity());
                                    String level = response.getString("level");
                                    String gl = buildGL(level);
                                    String days = response.getString("days");
                                    String gd = "Mondays";
                                    if (days.equals("s"))
                                        gd = "Tuesdays";
                                    String gt = response.getString("time");
                                    String gp = response.getString("teacher");
                                    String cid = response.getString("cashier");
                                    boolean boo = false;
                                    if (sid.equals(cid))
                                        boo = true;
                                    studentFoundDialog.startDialog(gid, sid, gl, gd, gt, gp, boo);
                                    loading.dismissDialog();
                                }
                            }
                            else
                                insertStudent(url, groupID, cashier);
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

    public void insertStudent(String url, String ID, String cashier) {

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        String URL = "https://no-more-cashier-list.herokuapp.com/joinGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String groupID = ID;
                            phoneNumber = getView().findViewById(R.id.phoneNumberDataJG);
                            String studentID = phoneNumber.getText().toString();
                            save(groupID, "group_id.txt");
                            save(studentID, "student_id.txt");
                            save(cashier, "cashier.txt");
                            loading.dismissDialog();
                            bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
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

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

    public String buildURL(String name,
                           String phoneNumber,
                           String code) {
        String URL;
        if (!checkBox.isChecked())
            URL = ("code=" + code);
        else
            URL = ("password=" + passcode);
        URL += ("&phoneNumber=" + phoneNumber);
        URL += ("&name=" + name);
        return URL;
    }

    public String buildGL(String lvl) {

        if (lvl.equals("1"))
            return "Primary 1";
        if (lvl.equals("2"))
            return "Primary 2";
        if (lvl.equals("3"))
            return "PreTOEFL 1";
        if (lvl.equals("4"))
            return "PreTOEFL 2";
        if (lvl.equals("5"))
            return "TOEFL 1 Junior";
        if (lvl.equals("55"))
            return "TOEFL 1 Senior";
        if (lvl.equals("6"))
            return "TOEFL 2 Junior";

        return "TOEFL 2 Senior";
    }
}