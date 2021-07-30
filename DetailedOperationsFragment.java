package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

public class DetailedOperationsFragment extends Fragment {

    private RequestQueue queue;
    String phoneNumber, name, cln, uncln, mess;
    ImageView statusImage, x;
    TextView statusText;
    Button buttonRemove, buttonAppoint;

    Button c1, c2, c3, c4, c5, c6, c7, c8, c9, c0, cP, cM, cC, cCE, cE;
    String s1, s1s = "", s1_old, s1s_old;
    String s1_first = "";
    String s1_last = "";
    boolean s1_equalsPressed = true, s1_oPressed = false;

    Button u1, u2, u3, u4, u5, u6, u7, u8, u9, u0, uP, uM, uC, uCE, uE;
    String s2, s2s = "", s2_old, s2s_old;
    String s2_first = "";
    String s2_last = "";
    boolean s2_equalsPressed = true, s2_oPressed = false;

    long saveClickTime, setCashierClickTime, removeClickTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_op, container, false);

        //getActivity().setTitle("Detailed operations");

        queue = Volley.newRequestQueue(getActivity());

        statusImage = view.findViewById(R.id.statusImageViewDetailedOP);
        statusText = view.findViewById(R.id.studentStatusDetailedOP);
        x = view.findViewById(R.id.deleteMessage);

        TextView messy = view.findViewById(R.id.message);
        messy.setText(mess);

        x.setOnClickListener(v -> {
            messy.setText("");
        });

        TextView studentName = view.findViewById(R.id.studentNameDetailedOP);
        studentName.setText(name);

        TextView cleanTV = view.findViewById(R.id.cleanablesTextViewDetailedOP);
        ConstraintLayout cleanKeys = view.findViewById(R.id.cleKeys);
        cleanTV.setOnClickListener(v -> {
            if(cleanKeys.isShown()) {
                cleanKeys.setVisibility(View.GONE);
            }
            else {
                cleanKeys.setVisibility(View.VISIBLE);
            }
        });

        TextView uncleanTV = view.findViewById(R.id.uncleanablesTextViewDetailedOP);
        ConstraintLayout uncleanKeys = view.findViewById(R.id.uncleKeys);
        uncleanTV.setOnClickListener(v -> {
            if(uncleanKeys.isShown()) {
                uncleanKeys.setVisibility(View.GONE);
            }
            else {
                uncleanKeys.setVisibility(View.VISIBLE);
            }
        });

        TextView clean = view.findViewById(R.id.cleanablesDetailedOP);
        clean.setText(cln);
        s1 = cln;
        s1_old = s1;

        TextView unclean = view.findViewById(R.id.uncleanablesDetailedOP);
        unclean.setText(uncln);
        s2 = uncln;
        s2_old = s2;

        clean.setOnClickListener(v -> {
            if(cleanKeys.isShown()) {
                cleanKeys.setVisibility(View.GONE);
            }
            else {
                cleanKeys.setVisibility(View.VISIBLE);
            }
        });

        unclean.setOnClickListener(v -> {
            if(uncleanKeys.isShown()) {
                uncleanKeys.setVisibility(View.GONE);
            }
            else {
                uncleanKeys.setVisibility(View.VISIBLE);
            }
        });

        Button buttonSave = view.findViewById(R.id.buttonSaveChanges);
        buttonSave.setOnClickListener(v -> {
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
                if (SystemClock.elapsedRealtime() - saveClickTime > 2000) {
                    String result1 = solve1();
                    String result2 = solve2();
                    if ((Integer.parseInt(result1) > 2000) || (Integer.parseInt(result1) < -2000)
                            || (Integer.parseInt(result2) > 2000) || (Integer.parseInt(result2) < -2000))
                        outOfLimit();
                    else {
                        clean.setText(result1);
                        unclean.setText(result2);
                        s1 = result1;
                        s1_equalsPressed = true;
                        s1_oPressed = false;
                        s1_old = clean.getText().toString();
                        s2 = result2;
                        s2_equalsPressed = true;
                        s2_oPressed = false;
                        s2_old = unclean.getText().toString();
                        saveChanges();
                        customizeDetailedOP(clean.getText().toString(), unclean.getText().toString());
                    }
                }
                saveClickTime = SystemClock.elapsedRealtime();
            }
        });

        buttonRemove = view.findViewById(R.id.buttonRemoveStudent);
        buttonAppoint = view.findViewById(R.id.buttonSetCashier);

        String cid = load("cashier.txt");
        if (cid.equals(phoneNumber)) {
            buttonRemove.setVisibility(View.GONE);
            buttonAppoint.setVisibility(View.GONE);
        }

        buttonRemove.setOnClickListener(v -> {
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
                if (SystemClock.elapsedRealtime() - removeClickTime > 3000) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("You are removing " + name + " from the group")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goodbye();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
                removeClickTime = SystemClock.elapsedRealtime();
            }
        });

        buttonAppoint.setOnClickListener(v -> {
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
                if (SystemClock.elapsedRealtime() - setCashierClickTime > 3000) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("You are appointing " + name + " as the new cashier of the group")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    appoint();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
                setCashierClickTime = SystemClock.elapsedRealtime();
            }
        });

        customizeDetailedOP(cln, uncln);

        // Cleanable calculator stuff

        cC = view.findViewById(R.id.calcC);
        cC.setOnClickListener(v -> {
            s1 = s1_old;
            clean.setText(s1);
            s1s = "";
            s1_equalsPressed = true;
            s1_oPressed = false;
        });

        cCE = view.findViewById(R.id.calcCE);
        cCE.setOnClickListener(v -> {
            if (s1.length() > 1) {
                if ((s1.length() == 2) && s1.charAt(0) == '-') {
                    s1 = "0";
                    s1s = "0";
                    s1_equalsPressed = true;
                }
                else {
                    if ((s1.charAt(s1.length()-1) == '+') || (s1.charAt(s1.length()-1) == '-')) {
                        s1s = s1s_old;
                        s1 = s1.substring(0, s1.length() - 1);
                        s1_oPressed = false;
                    } else {
                        s1 = s1.substring(0, s1.length() - 1);
                        if (s1s.length() > 1)
                            s1s = s1s.substring(0, s1s.length() - 1);
                        else
                            s1s = "";
                        s1_equalsPressed = false;
                    }
                }
            }
            else {
                s1 = "0";
                s1s = "0";
                s1_equalsPressed = true;
            }
            clean.setText(s1);
        });

        cP = view.findViewById(R.id.calcPlus);
        cP.setOnClickListener(v -> {
            if (!s1_oPressed) {
                s1 += "+";
                s1s_old = s1s;
                s1s = "";
                s1_oPressed = true;
                s1_equalsPressed = false;
            } else {
                if (s1.charAt(s1.length()-1) ==  '-') {
                    s1 = s1.substring(0, s1.length() - 1);
                    s1 += "+";
                }
                else {
                    String result = solve1();
                    if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                        outOfLimit();
                    else {
                        clean.setText(result);
                        s1 = result;
                        if (!s1.contains("-"))
                            s1s = s1;
                        else
                            s1s = s1.substring(1, s1.length()-1);
                        s1_equalsPressed = true;
                        s1_oPressed = false;

                        s1 += "+";
                        s1s_old = s1s;
                        s1s = "";
                        s1_oPressed = true;
                        s1_equalsPressed = false;
                    }
                }
            }
            clean.setText(s1);
        });

        cM = view.findViewById(R.id.calcMinus);
        cM.setOnClickListener(v -> {
            if (!s1_oPressed) {
                s1 += "-";
                s1s_old = s1s;
                s1s = "";
                s1_oPressed = true;
                s1_equalsPressed = false;
            } else {
                if (s1.charAt(s1.length()-1) ==  '+') {
                    s1 = s1.substring(0, s1.length() - 1);
                    s1 += "-";
                }
                else {
                    String result = solve1();
                    if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                        outOfLimit();
                    else {
                        clean.setText(result);
                        s1 = result;
                        if (!s1.contains("-"))
                            s1s = s1;
                        else
                            s1s = s1.substring(1, s1.length() - 1);
                        s1_equalsPressed = true;
                        s1_oPressed = false;

                        s1 += "-";
                        s1s_old = s1s;
                        s1s = "";
                        s1_oPressed = true;
                        s1_equalsPressed = false;
                    }
                }
            }
            clean.setText(s1);
        });

        cE = view.findViewById(R.id.calcEquals);
        cE.setOnClickListener(v -> {
            String result = solve1();
            if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                outOfLimit();
            else {
                clean.setText(result);
                s1 = result;
                if (!s1.contains("-"))
                    s1s = s1;
                else
                    s1s = s1.substring(1, s1.length()-1);
                s1_equalsPressed = true;
                s1_oPressed = false;
            }
        });

        c0 = view.findViewById(R.id.calcZero);
        c0.setOnClickListener(v -> {
            if (s1_equalsPressed) {
                s1 = "0";
                s1s = "0";
                s1_equalsPressed = false;
            } else {
                if (s1s.length() < 3) {
                    if (!s1s.equals("0")) {
                        s1 += "0";
                        s1s += "0";
                    }
                }
            }
            clean.setText(s1);
        });

        c1 = view.findViewById(R.id.calcOne);
        c1.setOnClickListener(v -> {
            String result = add1("1");
            clean.setText(result);
        });

        c2 = view.findViewById(R.id.calcTwo);
        c2.setOnClickListener(v -> {
            String result = add1("2");
            clean.setText(result);
        });

        c3 = view.findViewById(R.id.calcThree);
        c3.setOnClickListener(v -> {
            String result = add1("3");
            clean.setText(result);
        });

        c4 = view.findViewById(R.id.calcFour);
        c4.setOnClickListener(v -> {
            String result = add1("4");
            clean.setText(result);
        });

        c5 = view.findViewById(R.id.calcFive);
        c5.setOnClickListener(v -> {
            String result = add1("5");
            clean.setText(result);
        });

        c6 = view.findViewById(R.id.calcSix);
        c6.setOnClickListener(v -> {
            String result = add1("6");
            clean.setText(result);
        });

        c7 = view.findViewById(R.id.calcSeven);
        c7.setOnClickListener(v -> {
            String result = add1("7");
            clean.setText(result);
        });

        c8 = view.findViewById(R.id.calcEight);
        c8.setOnClickListener(v -> {
            String result = add1("8");
            clean.setText(result);
        });

        c9 = view.findViewById(R.id.calcNine);
        c9.setOnClickListener(v -> {
            String result = add1("9");
            clean.setText(result);
        });

        // Cleanable calculator stuff

        // Uncleanable calculator stuff

        uC = view.findViewById(R.id.uC);
        uC.setOnClickListener(v -> {
            s2 = s2_old;
            unclean.setText(s2);
            s2s = "";
            s2_equalsPressed = true;
            s2_oPressed = false;
        });

        uCE = view.findViewById(R.id.uCE);
        uCE.setOnClickListener(v -> {
            if (s2.length() > 1) {
                if ((s2.length() == 2) && s2.charAt(0) == '-') {
                    s2 = "0";
                    s2s = "0";
                    s2_equalsPressed = true;
                }
                else {
                    if ((s2.charAt(s2.length()-1) == '+') || (s2.charAt(s2.length()-1) == '-')) {
                        s2s = s2s_old;
                        s2 = s2.substring(0, s2.length() - 1);
                        s2_oPressed = false;
                    } else {
                        s2 = s2.substring(0, s2.length() - 1);
                        if (s2s.length() > 1)
                            s2s = s2s.substring(0, s2s.length() - 1);
                        else
                            s2s = "";
                        s2_equalsPressed = false;
                    }
                }
            }
            else {
                s2 = "0";
                s2s = "0";
                s2_equalsPressed = true;
            }
            unclean.setText(s2);
        });

        uP = view.findViewById(R.id.uPlus);
        uP.setOnClickListener(v -> {
            if (!s2_oPressed) {
                s2 += "+";
                s2s_old = s2s;
                s2s = "";
                s2_oPressed = true;
                s2_equalsPressed = false;
            } else {
                if (s2.charAt(s2.length()-1) ==  '-') {
                    s2 = s2.substring(0, s2.length() - 1);
                    s2 += "+";
                }
                else {
                    String result = solve2();
                    if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                        outOfLimit();
                    else {
                        unclean.setText(result);
                        s2 = result;
                        if (!s2.contains("-"))
                            s2s = s2;
                        else
                            s2s = s2.substring(1, s2.length()-1);
                        s2_equalsPressed = true;
                        s2_oPressed = false;

                        s2 += "+";
                        s2s_old = s2s;
                        s2s = "";
                        s2_oPressed = true;
                        s2_equalsPressed = false;
                    }
                }
            }
            unclean.setText(s2);
        });

        uM = view.findViewById(R.id.uMinus);
        uM.setOnClickListener(v -> {
            if (!s2_oPressed) {
                s2 += "-";
                s2s_old = s2s;
                s2s = "";
                s2_oPressed = true;
                s2_equalsPressed = false;
            } else {
                if (s2.charAt(s2.length()-1) ==  '+') {
                    s2 = s2.substring(0, s2.length() - 1);
                    s2 += "-";
                }
                else {
                    String result = solve2();
                    if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                        outOfLimit();
                    else {
                        unclean.setText(result);
                        s2 = result;
                        if (!s2.contains("-"))
                            s2s = s2;
                        else
                            s2s = s2.substring(1, s2.length()-1);
                        s2_equalsPressed = true;
                        s2_oPressed = false;

                        s2 += "-";
                        s2s_old = s2s;
                        s2s = "";
                        s2_oPressed = true;
                        s2_equalsPressed = false;
                    }
                }
            }
            unclean.setText(s2);
        });

        uE = view.findViewById(R.id.uEquals);
        uE.setOnClickListener(v -> {
            String result = solve2();
            if ((Integer.parseInt(result) > 5000) || (Integer.parseInt(result) < -5000))
                outOfLimit();
            else {
                unclean.setText(result);
                s2 = result;
                if (!s2.contains("-"))
                    s2s = s2;
                else
                    s2s = s2.substring(1, s2.length()-1);
                s2_equalsPressed = true;
                s2_oPressed = false;
            }
        });

        u0 = view.findViewById(R.id.uZero);
        u0.setOnClickListener(v -> {
            if (s2_equalsPressed) {
                s2 = "0";
                s2s = "0";
                s2_equalsPressed = false;
            } else {
                if (s2s.length() < 3) {
                    if (!s2s.equals("0")) {
                        s2 += "0";
                        s2s += "0";
                    }
                }
            }
            unclean.setText(s2);
        });

        u1 = view.findViewById(R.id.uOne);
        u1.setOnClickListener(v -> {
            String result = add2("1");
            unclean.setText(result);
        });

        u2 = view.findViewById(R.id.uTwo);
        u2.setOnClickListener(v -> {
            String result = add2("2");
            unclean.setText(result);
        });

        u3 = view.findViewById(R.id.uThree);
        u3.setOnClickListener(v -> {
            String result = add2("3");
            unclean.setText(result);
        });

        u4 = view.findViewById(R.id.uFour);
        u4.setOnClickListener(v -> {
            String result = add2("4");
            unclean.setText(result);
        });

        u5 = view.findViewById(R.id.uFive);
        u5.setOnClickListener(v -> {
            String result = add2("5");
            unclean.setText(result);
        });

        u6 = view.findViewById(R.id.uSix);
        u6.setOnClickListener(v -> {
            String result = add2("6");
            unclean.setText(result);
        });

        u7 = view.findViewById(R.id.uSeven);
        u7.setOnClickListener(v -> {
            String result = add2("7");
            unclean.setText(result);
        });

        u8 = view.findViewById(R.id.uEight);
        u8.setOnClickListener(v -> {
            String result = add2("8");
            unclean.setText(result);
        });

        u9 = view.findViewById(R.id.uNine);
        u9.setOnClickListener(v -> {
            String result = add2("9");
            unclean.setText(result);
        });

        // Uncleanable calculator stuff

        return view;
    }

    public String solve1() {
        String solvent = s1;
        if (s1_oPressed) {
            if (s1.contains("+")) {
                if (s1.charAt(s1.length()-1) == '+')
                    s1 += "0";
                int index = s1.indexOf("+");
                s1_first = s1.substring(0, index);
                s1_last = s1.substring(index+1);
                solvent = String.valueOf(Integer.parseInt(s1_first) + Integer.parseInt(s1_last));
            } else {
                if (s1.charAt(s1.length()-1) == '-')
                    s1 += "0";
                int index = s1.lastIndexOf("-");
                s1_first = s1.substring(0, index);
                s1_last = s1.substring(index+1);
                solvent = String.valueOf(Integer.parseInt(s1_first) - Integer.parseInt(s1_last));
            }
        }
        return solvent;
    }

    public String add1(String addon) {
        if (s1_equalsPressed) {
            s1 = addon;
            s1s = addon;
            s1_equalsPressed = false;
        } else {
            if (s1s.length() < 3) {
                if (!s1s.equals("0")) {
                    s1 += addon;
                    s1s += addon;
                } else {
                    s1 = s1.substring(0, s1.length() - 1);
                    s1 += addon;
                    s1s = addon;
                }
            }
        }
        return s1;
    }

    public String solve2() {
        String solvent = s2;
        if (s2_oPressed) {
            if (s2.contains("+")) {
                if (s2.charAt(s2.length()-1) == '+')
                    s2 += "0";
                int index = s2.indexOf("+");
                s2_first = s2.substring(0, index);
                s2_last = s2.substring(index+1);
                solvent = String.valueOf(Integer.parseInt(s2_first) + Integer.parseInt(s2_last));
            } else {
                if (s2.charAt(s2.length()-1) == '-')
                    s2 += "0";
                int index = s2.lastIndexOf("-");
                s2_first = s2.substring(0, index);
                s2_last = s2.substring(index+1);
                solvent = String.valueOf(Integer.parseInt(s2_first) - Integer.parseInt(s2_last));
            }
        }
        return solvent;
    }

    public String add2(String addon) {
        if (s2_equalsPressed) {
            s2 = addon;
            s2s = addon;
            s2_equalsPressed = false;
        } else {
            if (s2s.length() < 3) {
                if (!s2s.equals("0")) {
                    s2 += addon;
                    s2s += addon;
                } else {
                    s2 = s2.substring(0, s2.length() - 1);
                    s2 += addon;
                    s2s = addon;
                }
            }
        }
        return s2;
    }

    public DetailedOperationsFragment(String phoneNumber, String name, String cln, String uncln, String message) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.cln = cln;
        this.uncln = uncln;
        this.mess = message;
    }

    public void outOfLimit() {
        OutOfLimitDialog outOfLimitDialog = new OutOfLimitDialog();
        outOfLimitDialog.show(getActivity().getSupportFragmentManager(), "OUT OF LIMIT");
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
    TextView cleanables, uncleanables, message;

    public void saveChanges() {

        View view = getView();

        cleanables = view.findViewById(R.id.cleanablesDetailedOP);
        uncleanables = view.findViewById(R.id.uncleanablesDetailedOP);
        message = view.findViewById(R.id.message);

        String phone = phoneNumber;
        String cle = cleanables.getText().toString();
        String uncle = uncleanables.getText().toString();
        String mess = message.getText().toString();
        String url = buildURL(phone, cle, uncle, mess);

        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Saving changes...");
        update(url);
    }

    public void update(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/update?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            loading.dismissDialog();
                        }
                        else {
                            operationError();
                            loading.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        operationError();
                        loading.dismissDialog();
                    }
                },
                e -> {
                    e.printStackTrace();
                    noInternet();
                    loading.dismissDialog();
                }
        );

        queue.add(request);
    }

    public void appoint() {
        String gid = load("group_id.txt");
        String sid = phoneNumber;
        String url = buildURLtoAppoint(gid, sid, name);
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Appointing as cashier...");
        setCashier(url);
    }

    BottomNavigationView bottomNavigationView;

    private void setCashier(String url) {

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        String URL = "https://no-more-cashier-list.herokuapp.com/setCashier?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            save(phoneNumber, "cashier.txt");
                            bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                        } else {
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

    public void goodbye() {
        String gid = load("group_id.txt");
        String sid = phoneNumber;
        String url = buildURLtoRemove(gid, sid);
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Removing student...");
        leaveGroup(url);
    }

    private void leaveGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/leaveGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new OperationsFragment()).commit();
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

    public void customizeDetailedOP(String cle, String uncle) {

        int cl = Integer.parseInt(cle);
        int uncl = Integer.parseInt(uncle);

        if ((cl < -14) || (uncl < -14)) {
            statusText.setTextColor(Color.parseColor("#FF0000"));
            statusText.setText("Not eligible");
            statusImage.setBackgroundResource(R.drawable.ic_negative);
        }
        else {
            statusText.setTextColor(Color.parseColor("#278834"));
            statusText.setText("Eligible");
            statusImage.setBackgroundResource(R.drawable.ic_positive);
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

    public String buildURL(String phone, String cln, String uncln, String msg) {
        String URL = ("phoneNumber=" + phone);
        URL += "&cln=" + cln;
        URL += "&uncln=" + uncln;
        URL += "&msg=" + msg;
        return URL;
    }

    public String buildURLtoRemove(String groupID,
                           String phoneNumber) {
        String URL = ("groupID=" + groupID);
        URL += ("&phoneNumber=" + phoneNumber);
        return URL;
    }

    public String buildURLtoAppoint(String groupID,
                                    String phoneNumber,
                                    String name) {
        String URL = ("groupID=" + groupID);
        URL += ("&phoneNumber=" + phoneNumber);
        URL += ("&cashierName=" + name);
        return URL;
    }

}