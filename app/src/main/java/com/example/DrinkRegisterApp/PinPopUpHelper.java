package com.example.DrinkRegisterApp;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PinPopUpHelper {

    private final MainActivity app;
    private String pinCode;
    private TextView pinCodeProgress;

    public PinPopUpHelper(MainActivity app) {
        this.app = app;
    }

    public void setupPinButtons(View v, PopupWindow window) {
        pinCode = "";

        TextView pinCodeHeader = (TextView) v.findViewById(R.id.pinCodeHeader);
        pinCodeProgress = (TextView) v.findViewById(R.id.pinCodeProgress);

        Button corButton = (Button) v.findViewById(R.id.buttonCOR);
        corButton.setOnClickListener(view -> {
            if (pinCode.isEmpty()) return;
            pinCode = pinCode.substring(0, pinCode.length()-1);
            setProgress();
        });

        Button enterButton = (Button) v.findViewById(R.id.buttonENTER);
        enterButton.setOnClickListener(view -> {
            if (!pinCode.isEmpty() && app.verifyPinCode(pinCode)) {
                window.dismiss();
            } else {
                pinCodeHeader.setText("Incorrect pincode!");
                pinCodeHeader.setTextColor(app.getResources().getColor(R.color.red));
                pinCode = "";
                setProgress();
            }
        });

        Button button1 = (Button) v.findViewById(R.id.button1);
        button1.setOnClickListener(view -> {
            pinCode += "1";
            setProgress();
        });

        Button button2 = (Button) v.findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            pinCode += "2";
            setProgress();
        });

        Button button3 = (Button) v.findViewById(R.id.button3);
        button3.setOnClickListener(view -> {
            pinCode += "3";
            setProgress();
        });

        Button button4 = (Button) v.findViewById(R.id.button4);
        button4.setOnClickListener(view -> {
            pinCode += "4";
            setProgress();
        });

        Button button5 = (Button) v.findViewById(R.id.button5);
        button5.setOnClickListener(view -> {
            pinCode += "5";
            setProgress();
        });

        Button button6 = (Button) v.findViewById(R.id.button6);
        button6.setOnClickListener(view -> {
            pinCode += "6";
            setProgress();
        });

        Button button7 = (Button) v.findViewById(R.id.button7);
        button7.setOnClickListener(view -> {
            pinCode += "7";
            setProgress();
        });

        Button button8 = (Button) v.findViewById(R.id.button8);
        button8.setOnClickListener(view -> {
            pinCode += "8";
            setProgress();
        });

        Button button9 = (Button) v.findViewById(R.id.button9);
        button9.setOnClickListener(view -> {
            pinCode += "9";
            setProgress();
        });

        Button button0 = (Button) v.findViewById(R.id.button0);
        button0.setOnClickListener(view -> {
            pinCode += "0";
            setProgress();
        });
    }

    public void setProgress() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < pinCode.length(); i++) {
            text.append('*');
        }
        pinCodeProgress.setText(text.toString());
    }
}
