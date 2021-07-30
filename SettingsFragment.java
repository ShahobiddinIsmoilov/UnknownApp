package com.rollermine.unknownapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SettingsFragment extends Fragment {

    private RequestQueue queue;
    TextView group, studentName, changeName, cashier, cashierTV, avatar, codeTextView,
            code, isCashier, updateTextView, note60, noteUpdate;
    CardView settings, versionCardView, about;
    String oldname, versionName, versionCode;
    EditText nameEdit;
    Button saveName, cancelName, delete, generate, updateApp;
    long leaveClickTime, deleteClickTime, updateClickTime;
    ImageView noService, newVersionMark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        queue = Volley.newRequestQueue(getActivity());

        getActivity().setTitle("Settings");

        avatar = view.findViewById(R.id.avatarSettingsText);
        studentName = view.findViewById(R.id.studentNameSettings);
        group = view.findViewById(R.id.groupSettings);
        cashier = view.findViewById(R.id.cashierSettings);
        cashierTV = view.findViewById(R.id.cashierTextViewSettings);
        settings = view.findViewById(R.id.settingsView);
        versionCardView = view.findViewById(R.id.appVersion);
        nameEdit = view.findViewById(R.id.studentNameEdit);
        saveName = view.findViewById(R.id.saveName);
        cancelName = view.findViewById(R.id.cancelName);
        codeTextView = view.findViewById(R.id.inviteCodeTextView);
        code = view.findViewById(R.id.inviteCode);
        isCashier = view.findViewById(R.id.isCashierSettings);
        updateApp = view.findViewById(R.id.buttonUpdateApp);
        updateTextView = view.findViewById(R.id.updateAppTextView);
        note60 = view.findViewById(R.id.inviteCodeTimeLimitNote);
        noteUpdate = view.findViewById(R.id.updateAppRecommend);
        about = view.findViewById(R.id.aboutApp);
        noService = view.findViewById(R.id.offlineSettings);
        newVersionMark = view.findViewById(R.id.updateExclamationMark);
        oldname = studentName.getText().toString();

        changeName = view.findViewById(R.id.changeName);
        changeName.setOnClickListener(v -> {
            studentName.setVisibility(View.INVISIBLE);
            nameEdit.setText(oldname);
            nameEdit.setVisibility(View.VISIBLE);
            saveName.setVisibility(View.VISIBLE);
            cancelName.setVisibility(View.VISIBLE);
            changeName.setVisibility(View.INVISIBLE);
        });

        cancelName.setOnClickListener(v -> {
            nameEdit.setVisibility(View.INVISIBLE);
            studentName.setText(oldname);
            studentName.setVisibility(View.VISIBLE);
            saveName.setVisibility(View.INVISIBLE);
            cancelName.setVisibility(View.INVISIBLE);
            changeName.setVisibility(View.VISIBLE);
        });

        saveName.setOnClickListener(v -> {
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
                changeStudentName();
            }
        });

        generate = view.findViewById(R.id.generateCode);
        generate.setOnClickListener(v -> {
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
                generateInvitationCode();
            }
        });

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        about.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                    .add(R.id.fragment, new AboutFragment()).addToBackStack(null).commit();
            /*new AlertDialog.Builder(getContext())
                    .setTitle("")
                    .setIcon(R.drawable.ic_night)
                    .setView(inflater.inflate(R.layout.dialog_enter_password, null))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false).show();*/
        });

        Button leave = view.findViewById(R.id.leave);
        leave.setOnClickListener(v -> {
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
                String sid = load("student_id.txt");
                String cid = load("cashier.txt");
                if (sid.equals(cid))
                    cantLeaveSorry();
                else {
                    if (SystemClock.elapsedRealtime() - leaveClickTime > 3000) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning")
                                .setMessage("You are leaving the group")
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        goodbye();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, null).show();
                    }
                    leaveClickTime = SystemClock.elapsedRealtime();
                }
            }
        });

        delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
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
                if (SystemClock.elapsedRealtime() - deleteClickTime > 3000) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("You are deleting the group")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    noGroup();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
                deleteClickTime = SystemClock.elapsedRealtime();
            }
        });

        updateApp.setOnClickListener(v -> {
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
                if (SystemClock.elapsedRealtime() - updateClickTime > 2000) {
                    updateApplication();
                }
                updateClickTime = SystemClock.elapsedRealtime();
            }
        });

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
            info();
            version();
        }

        //register receiver for when .apk download is compete

        return view;
    }

    public void cantLeaveSorry() {
        CantLeaveSorry cantLeaveSorry = new CantLeaveSorry();
        cantLeaveSorry.show(getActivity().getSupportFragmentManager(), "NO CONNECTION");
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

    public void generateInvitationCode() {
        String gid = load("group_id.txt");
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Generating new code...");
        generateCode(gid);
    }

    private void generateCode(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/generateCode?groupID=" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String inv = response.getString("code");
                            code.setText(inv);
                            code.setVisibility(View.VISIBLE);
                            loading.dismissDialog();
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

    public void info() {
        String gid = load("group_id.txt");
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Loading...");
        getInfo(gid);
    }

    public void getInfo(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/groupInfo?groupID=" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String level = response.getString("level");
                            String days = response.getString("days");
                            String time = response.getString("time");
                            String teacher = response.getString("teacher");
                            String cash = response.getString("cashierName");

                            String sid = load("student_id.txt");
                            JSONArray jsonArray = response.getJSONArray("students");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject student = jsonArray.getJSONObject(i);
                                String phoneNumber = student.getString("phoneNumber");
                                if (phoneNumber.equals(sid)) {
                                    String name = student.getString("name");

                                    // Avatar stuff

                                    String color = student.getString("avatar");
                                    int[] colors = new int[2];
                                    int k = 0;
                                    StringBuilder sub = new StringBuilder();
                                    for (int j = 0; j < color.length(); j++) {
                                        if (color.charAt(j) != '-')
                                            sub.append(color.charAt(j));
                                        else {
                                            colors[k] = Color.parseColor(sub.toString());
                                            sub = new StringBuilder();
                                            k++;
                                        }
                                    }
                                    colors[k] = Color.parseColor(sub.toString());
                                    GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                                    gd.setCornerRadius(0f);
                                    String init = "";
                                    init += name.charAt(0);
                                    avatar.setText(init);
                                    avatar.setBackground(gd);

                                    // Avatar stuff

                                    oldname = name;
                                    studentName.setText(name);
                                    break;
                                }
                            }

                            String gif = buildGIF(level, days, time, teacher);
                            group.setText(gif);
                            cashier.setText(cash);
                            String cid = load("cashier.txt");
                            if (sid.equals(cid)) {
                                cashierTV.setText("No. of students:");
                                String no = String.valueOf(jsonArray.length());
                                cashier.setText(no);
                                isCashier.setVisibility(View.VISIBLE);
                                codeTextView.setVisibility(View.VISIBLE);
                                String inv = response.getString("code");
                                if (inv.equals("null")) {
                                    generate.setVisibility(View.VISIBLE);
                                    code.setVisibility(View.GONE);
                                    note60.setVisibility(View.GONE);
                                }
                                else {
                                    generate.setVisibility(View.VISIBLE);
                                    code.setText(inv);
                                    code.setVisibility(View.VISIBLE);
                                    //note60.setVisibility(View.VISIBLE);
                                }
                                delete.setVisibility(View.VISIBLE);
                            }

                            settings.setVisibility(View.VISIBLE);
                            versionCardView.setVisibility(View.VISIBLE);
                            about.setVisibility(View.VISIBLE);
                            loading.dismissDialog();
                        }
                        else {
                            operationError();
                            loading.dismissDialog();
                            noService.setImageResource(R.drawable.sad_face);
                            noService.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        operationError();
                        loading.dismissDialog();
                        noService.setImageResource(R.drawable.sad_face);
                        noService.setVisibility(View.VISIBLE);
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    noInternet();
                    loading.dismissDialog();
                    noService.setVisibility(View.VISIBLE);
                    noService.setImageResource(R.drawable.cloud_offline);
                }
        );

        queue.add(request);
    }

    public void changeStudentName() {
        String sid = load("student_id.txt");
        String newName = nameEdit.getText().toString();
        String url = sid + "&name=" + newName;
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Updating account...");
        updateName(url, newName);
    }

    public void updateName(String url, String nme) {

        String URL = "https://no-more-cashier-list.herokuapp.com/changeName?phoneNumber=" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            nameEdit.setVisibility(View.INVISIBLE);
                            studentName.setText(nme);
                            studentName.setVisibility(View.VISIBLE);
                            saveName.setVisibility(View.INVISIBLE);
                            cancelName.setVisibility(View.INVISIBLE);
                            changeName.setVisibility(View.VISIBLE);
                            loading.dismissDialog();
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

    public void goodbye() {
        String gid = load("group_id.txt");
        String sid = load("student_id.txt");
        String url = buildURL(gid, sid);
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Saying goodbye...");
        checkCashier(url, gid, sid);
    }

    public void checkCashier(String url, String gid, String sid) {

        String URL = "https://no-more-cashier-list.herokuapp.com/groupInfo?groupID=" + gid;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String cid = response.getString("cashier");
                            if (cid.equals(sid))
                                operationError();
                            else
                                leaveGroup(url);
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

    private void leaveGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/leaveGroup?" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            save("null", "group_id.txt");
                            save("0", "cle.txt");
                            save("0", "uncle.txt");
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                    new StartFragment()).commit();
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

    public void noGroup() {
        String gid = load("group_id.txt");
        loading = new CreateGroupLoading(getActivity());
        loading.startLoading("Deleting group...");
        deleteGroup(gid);
    }

    private void deleteGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/deleteGroup?groupID=" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            save("null", "group_id.txt");
                            save("0", "cle.txt");
                            save("0", "uncle.txt");
                            loading.dismissDialog();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                    new StartFragment()).commit();
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

    private void version() {
        getVersion();
    }

    String link = "";

    public void getVersion() {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkUpdate";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            versionCode = response.getString("version_code");
                            versionName = response.getString("version_name");
                            int thisVersionCode = BuildConfig.VERSION_CODE;
                            if (Integer.parseInt(versionCode) > thisVersionCode) {
                                link = response.getString("apk_url");
                                updateTextView.setTextColor(Color.parseColor("#3F51B5"));
                                updateTextView.setText("New version available");
                                updateApp.setVisibility(View.VISIBLE);
                                noteUpdate.setVisibility(View.VISIBLE);
                                newVersionMark.setVisibility(View.VISIBLE);
                                newVersionMark.setImageResource(R.drawable.ic_info);
                            }
                            else {
                                updateTextView.setTextColor(Color.parseColor("#278834"));
                                updateTextView.setText("Latest version installed");
                                newVersionMark.setVisibility(View.VISIBLE);
                                newVersionMark.setImageResource(R.drawable.ic_check);
                            }
                        }
                        else {
                            updateTextView.setTextColor(Color.parseColor("#FF6060"));
                            updateTextView.setText("Couldn't check for updates");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        updateTextView.setTextColor(Color.parseColor("#FF6060"));
                        updateTextView.setText("Couldn't check for updates");
                    }
                },
                e -> {
                    e.printStackTrace();
                    updateTextView.setTextColor(Color.parseColor("#FF6060"));
                    updateTextView.setText("Couldn't check for updates");
                }
        );

        queue.add(request);
    }

    // ----- App update stuff -----

    public void updateApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if OS is Marshmallow or above, handle runtime permission
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                // permission denied, request it
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            else {
                // permission granted, perform download
                startDownloading();
            }
        }
        else {
            // system OS is below Marshmallow, perform download
            startDownloading();
        }
    }

    private void startDownloading() {
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //application with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String destinationZip = destination;
        String fileName = "Eclipse_v" + versionName + ".zip";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists()) {
            //file.delete() - test this, I think sometimes it doesn't work
            file.delete();
            /*boolean fool = unpackZip(destinationZip, fileName);
            if (fool)
                installAPK();*/
        }

        //get url of app on server
        String url = link;

        //set DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading new version...");
        request.setTitle("Eclipse");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    boolean unzip = unpackZip(destinationZip, fileName);
                    if (unzip) {
                        //Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                        installAPK();
                    } else
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                //context.unregisterReceiver(onComplete);
            }
        };

        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    void installAPK() {

        String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "Eclipse_v" + versionName + ".apk";
        File file = new File(PATH);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(getActivity(), new File(PATH)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getActivity().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getActivity(),"Installing", Toast.LENGTH_LONG).show();
        }
    }

    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    // handle permission result
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startDownloading();
                }
                else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // ----- App update stuff -----

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

    public String buildURL(String groupID,
                           String phoneNumber) {
        String URL = ("groupID=" + groupID);
        URL += ("&phoneNumber=" + phoneNumber);
        return URL;
    }

    public String buildGIF(String level,
                           String days,
                           String time,
                           String teacher) {
        String GIF = "Primary 1";
        if (level.equals("2"))
            GIF = "Primary 2";
        if (level.equals("3"))
            GIF = "PreTOEFL 1";
        if (level.equals("4"))
            GIF = "PreTOEFL 2";
        if (level.equals("5"))
            GIF = "TOEFL 1 Junior";
        if (level.equals("55"))
            GIF = "TOEFL 1 Senior";
        if (level.equals("6"))
            GIF = "TOEFL 2 Junior";
        if (level.equals("66"))
            GIF = "TOEFL 2 Senior";
        if (days.equals("f"))
            GIF += ", Mondays ";
        else
            GIF += ", Tuesdays ";
        GIF += time + ", " + "\n" + teacher;
        if (!teacher.contains("eadmaster"))
            if (!teacher.contains("eacher"))
                GIF += " teacher";
        return GIF;
    }
}
