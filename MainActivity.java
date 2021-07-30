package com.rollermine.unknownapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    String sid, cid, gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        //setTitle("Eclipse");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                new UniversalLoading()).commit();

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay > 22 || timeOfDay < 5) {
            new AlertDialog.Builder(this)
                    .setTitle("It's sleep time")
                    .setIcon(R.drawable.ic_night)
                    .setMessage("App does not operate between 11:00 PM and 5:00 AM for reasons that are better left unsaid. Sorry for the inconvenience. Meanwhile, you should get some rest too.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setCancelable(false).show();
        }
        else {

            queue = Volley.newRequestQueue(this);

            String versionName = BuildConfig.VERSION_NAME;
            String PATH_ZIP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "Eclipse_v" + versionName + ".zip";
            String PATH_APK = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "Eclipse_v" + versionName + ".apk";
            File fileZip = new File(PATH_ZIP);
            File fileApk = new File(PATH_APK);
            if (fileZip.exists()) {
                boolean deletedZip = fileZip.delete();
                //Toast.makeText(this, "Zip exists", Toast.LENGTH_SHORT).show();
            }
            if (fileApk.exists()) {
                boolean deletedApk = fileApk.delete();
                //Toast.makeText(this, "Apk exists", Toast.LENGTH_SHORT).show();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                    new UniversalLoading()).commit();

            gid = load("group_id.txt");
            sid = load("student_id.txt");
            cid = load("cashier.txt");

            if (gid.equals("null")) {
                new CountDownTimer(1500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                new StartFragment()).commit();
                    }
                }.start();
            } else {
                String url = buildURL(gid, sid);
                checkGroup(url);
            }

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        }

    }

    public void checkGroup(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkGroupID?" + url;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                checkStudent(url);
                            }
                            else {
                                sad();
                                save("null", "group_id.txt");
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                        new StartFragment()).commit();
                            }
                        }
                        else {
                            if (!cid.equals(sid))
                                bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        if (!cid.equals(sid))
                            bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment()).commit();
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    if (!cid.equals(sid))
                        bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }
        );

        queue.add(request);
    }

    public void checkStudent(String url) {

        String URL = "https://no-more-cashier-list.herokuapp.com/checkStudent?" + url;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String okay = response.getString("ok");
                        if (okay.equals("true")) {
                            String found = response.getString("found");
                            if (found.equals("true")) {
                                String cash = response.getString("cashier");
                                if (!sid.equals(cash)) {
                                    bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new HomeFragment()).commit();
                                }
                                else {
                                    String passcode = response.getString("password");
                                    if (passcode.equals("null")) {
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                                new PasswordFragment(gid)).commit();
                                    }
                                    else {
                                        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new HomeFragment()).commit();
                                    }
                                }
                            }
                            else {
                                pity();
                                save("null", "group_id.txt");
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                        new StartFragment()).commit();
                            }
                        }
                        else {
                            if (!cid.equals(sid))
                                bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment()).commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        save(e.getMessage(), "error.txt");
                        if (!cid.equals(sid))
                            bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment()).commit();
                    }
                },
                e -> {
                    e.printStackTrace();
                    save(e.getMessage(), "error.txt");
                    if (!cid.equals(sid))
                        bottomNavigationView.getMenu().removeItem(R.id.nav_operations);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }
        );

        queue.add(request);
    }

    public void sad() {
        NoGroupDialog noGroupDialog = new NoGroupDialog();
        noGroupDialog.show(getSupportFragmentManager(), "SAD");
    }

    public void pity() {
        NoStudentDialog noStudentDialog = new NoStudentDialog();
        noStudentDialog.show(getSupportFragmentManager(), "PITY");
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*/
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        else {
            getSupportFragmentManager().popBackStack();
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_operations:
                        selectedFragment = new OperationsFragment();
                        break;
                    case R.id.nav_settings:
                        selectedFragment = new SettingsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            };

    public void save(String data, String filename) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
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
            fis = openFileInput(filename);
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
}









