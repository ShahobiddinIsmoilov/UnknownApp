package com.rollermine.unknownapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Calculator extends Fragment {

    Button c1, c2, c3, c4, c5, c6, c7, c8, c9, c0, cP, cM, cC, cCE, cE;
    MainActivity view;
    String screen;
    String screenOld = "0";
    boolean equalsPressed = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator_cleanable, container, false);

        /*cC = view.findViewById(R.id.calcC);
        c1.setOnClickListener(v -> {
            screen = screenOld;
        });

        c0 = view.findViewById(R.id.calcZero);
        c0.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "0";
                equalsPressed = false;
            }
            else
                screen += "0";
        });

        c1 = view.findViewById(R.id.calcOne);
        c1.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "1";
                equalsPressed = false;
            }
            else
                screen += "1";
        });

        c2 = view.findViewById(R.id.calcTwo);
        c2.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "2";
                equalsPressed = false;
            }
            else
                screen += "2";
        });

        c3 = view.findViewById(R.id.calcThree);
        c3.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "3";
                equalsPressed = false;
            }
            else
                screen += "3";
        });

        c4 = view.findViewById(R.id.calcFour);
        c4.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "4";
                equalsPressed = false;
            }
            else
                screen += "4";
        });

        c5 = view.findViewById(R.id.calcFive);
        c5.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "5";
                equalsPressed = false;
            }
            else
                screen += "5";
        });

        c6 = view.findViewById(R.id.calcSix);
        c6.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "6";
                equalsPressed = false;
            }
            else
                screen += "6";
        });

        c7 = view.findViewById(R.id.calcSeven);
        c7.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "7";
                equalsPressed = false;
            }
            else
                screen += "7";
        });

        c8 = view.findViewById(R.id.calcEight);
        c8.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "8";
                equalsPressed = false;
            }
            else
                screen += "8";
        });

        c9 = view.findViewById(R.id.calcNine);
        c9.setOnClickListener(v -> {
            if (equalsPressed) {
                screen = "9";
                equalsPressed = false;
            }
            else
                screen += "9";
        });*/

        return view;
    }

    public void getValues() {
        view = new MainActivity();
    }

}
