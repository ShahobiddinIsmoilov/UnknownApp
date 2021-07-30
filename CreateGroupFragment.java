package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CreateGroupFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private RequestQueue queue;
    EditText teacher, name, phoneNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        getActivity().setTitle("Create a group");

        queue = Volley.newRequestQueue(getActivity());

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        new StartFragment()).commit();
            }
        });

        // Level spinner
        Spinner levelsList = view.findViewById(R.id.levelSpinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.levels, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelsList.setAdapter(levelAdapter);

        levelsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String levelText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Days spinner
        Spinner daysList = view.findViewById(R.id.daysSpinner);
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.days, R.layout.spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysList.setAdapter(daysAdapter);
        daysList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String daysText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Time spinner
        Spinner timeList = view.findViewById(R.id.timeSpinner);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.time, R.layout.spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeList.setAdapter(timeAdapter);
        timeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Month spinner
        Spinner monthList = view.findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.month, R.layout.spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthList.setAdapter(monthAdapter);
        monthList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String monthText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Year spinner
        Spinner yearList = view.findViewById(R.id.yearSpinner);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.year, R.layout.spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearList.setAdapter(yearAdapter);
        yearList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String yearText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        teacher = view.findViewById(R.id.codeDataJG);
        name = view.findViewById(R.id.nameData);
        phoneNumber = view.findViewById(R.id.phoneNumberData);

        Button buttonAddGroup = view.findViewById(R.id.buttonCreate);
        buttonAddGroup.setOnClickListener(v -> {
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
                if (teacher.getText().toString().length() > 0 && notJustSpaces(teacher.getText().toString()) &&
                        name.getText().toString().length() > 0 && notJustSpaces(name.getText().toString()) &&
                        phoneNumberIsCorrect(phoneNumber.getText().toString())) {
                    createGroup();
                } else {
                    fillAll();
                }
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

    public void groupFound() {
        GroupExistsDialog groupFoundDialog = new GroupExistsDialog();
        groupFoundDialog.show(getActivity().getSupportFragmentManager(), "GROUP EXISTS");
    }

    public void noInternet() {
        NoInternetDialog noInternetDialog = new NoInternetDialog();
        noInternetDialog.show(getActivity().getSupportFragmentManager(), "NO CONNECTION");
    }

    public void operationError() {
        OperationErrorDialog operationErrorDialog = new OperationErrorDialog();
        operationErrorDialog.show(getActivity().getSupportFragmentManager(), "OPERATION ERROR");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    CreateGroupLoading loading;

    public void createGroup() {

        View view = getView();
        Spinner levelsList = view.findViewById(R.id.levelSpinner);
        Spinner daysList = view.findViewById(R.id.daysSpinner);
        Spinner timeList = view.findViewById(R.id.timeSpinner);
        Spinner monthList = view.findViewById(R.id.monthSpinner);
        Spinner yearList = view.findViewById(R.id.yearSpinner);

        String url = buildURL(levelsList.getSelectedItem().toString(),
                daysList.getSelectedItem().toString(),
                timeList.getSelectedItem().toString(),
                teacher.getText().toString(),
                monthList.getSelectedItem().toString(),
                yearList.getSelectedItem().toString(),
                name.getText().toString(),
                phoneNumber.getText().toString());

        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Please wait...");
        checkGroup(url);
    }

    private void checkGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                groupFound();
                                loading.dismissDialog();
                            }
                            else {
                                checkStudent(url);
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

    StudentFoundDialog studentFoundDialog;

    public void checkStudent(String url) {

        String ph = phoneNumber.getText().toString();

        String URL = "https://no-more-cashier-list.herokuapp.com/checkStudent?phoneNumber=" + ph;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                studentFoundDialog = new StudentFoundDialog(getActivity());
                                String gid = response.getString("groupID");
                                String level = response.getString("level");
                                String gl = buildGL(level);
                                String days = response.getString("days");
                                String gd = "Mondays";
                                if (days.equals("s"))
                                    gd = "Tuesdays";
                                String gt = response.getString("time");
                                String gp = response.getString("teacher");
                                String cashier = response.getString("cashier");
                                boolean boo = false;
                                if (ph.equals(cashier))
                                    boo = true;
                                studentFoundDialog.startDialog(gid, ph, gl, gd, gt, gp, boo);
                                loading.dismissDialog();
                            }
                            else {
                                addGroup(url);
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

    private void addGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/addGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if(okay.equals("true")) {
                            String groupID = response.getString("groupID");
                            save(groupID, "group_id.txt");
                            phoneNumber = getView().findViewById(R.id.phoneNumberData);
                            String studentID = phoneNumber.getText().toString();
                            save(studentID, "student_id.txt");
                            save(studentID, "cashier.txt");
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                    new PasswordFragment(groupID)).commit();
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

    private String buildURL(String level,
                            String days,
                            String time,
                            String teacher,
                            String month,
                            String year,
                            String name,
                            String phoneNumber) {
        if (level.equals("Primary 1"))
            level = "1";
        if (level.equals("Primary 2"))
            level = "2";
        if (level.equals("PreTOEFL 1"))
            level = "3";
        if (level.equals("PreTOEFL 2"))
            level = "4";
        if (level.equals("TOEFL 1 Junior"))
            level = "5";
        if (level.equals("TOEFL 1 Senior"))
            level = "55";
        if (level.equals("TOEFL 2 Junior"))
            level = "6";
        if (level.equals("TOEFL 2 Senior"))
            level = "66";
        if (days.equals("Mondays"))
            days = "f";
        if (days.equals("Tuesdays"))
            days = "s";
        if (month.equals("January"))
            month = "01";
        if (month.equals("February"))
            month = "02";
        if (month.equals("March"))
            month = "03";
        if (month.equals("April"))
            month = "04";
        if (month.equals("May"))
            month = "05";
        if (month.equals("June"))
            month = "06";
        if (month.equals("July"))
            month = "07";
        if (month.equals("August"))
            month = "08";
        if (month.equals("September"))
            month = "09";
        if (month.equals("October"))
            month = "10";
        if (month.equals("November"))
            month = "11";
        if (month.equals("December"))
            month = "12";
        year = year.substring(2);
        String URL = ("level=" + level);
        URL += ("&days=" + days);
        URL += ("&time=" + time);
        URL += ("&teacher=" + teacher);
        URL += ("&date=" + month + year);
        URL += ("&phoneNumber=" + phoneNumber);
        URL += ("&name=" + name);
        URL += ("&cashierName=" + name);
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