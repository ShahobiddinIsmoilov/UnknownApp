package com.rollermine.unknownapp;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutFragment extends Fragment {

    TextView aboutVersion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        //getActivity().setTitle("About");

        aboutVersion = view.findViewById(R.id.aboutVersion);
        String version = "Version: " + BuildConfig.VERSION_NAME;
        aboutVersion.setText(version);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        /*requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                requireActivity().onBackPressed();
            }
        });*/

        return view;
    }

    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = getActivity().openFileInput("cle.txt");
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
}
