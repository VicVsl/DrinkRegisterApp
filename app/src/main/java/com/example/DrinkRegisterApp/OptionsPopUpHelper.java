package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

@SuppressLint("InflateParams")
public class OptionsPopUpHelper {

    private final MainActivity app;

    public OptionsPopUpHelper(MainActivity app) {
        this.app = app;
    }


    public void showOptions(View v) {
        String rank = app.getLogin().getRank();
        switch (rank) {
            case "regular":
                showRegularOptions(v);
                break;
            case "mod":
                showModOptions(v);
                break;
            case "admin":
                showAdminOptions(v);
                break;
        }
    }

    public void showRegularOptions(View v) {
        View popupView = app.getInflater().inflate(R.layout.regular_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 400);

        Button checkBalanceButton = popupView.findViewById(R.id.checkBalanceButton);
        checkBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showBalance(view);
        });

        Button changePincodeButton = popupView.findViewById(R.id.changePincodeButton);
        changePincodeButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getPpuHelper().setUpdate(true);
            app.getPpuHelper().showPincode(view);
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showModOptions(View v) {
        View popupView = app.getInflater().inflate(R.layout.mod_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 520);

        Button checkBalanceButton = popupView.findViewById(R.id.checkBalanceButton);
        checkBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showBalance(view);
        });

        Button changePincodeButton = popupView.findViewById(R.id.changePincodeButton);
        changePincodeButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getPpuHelper().setUpdate(true);
            app.getPpuHelper().showPincode(view);
        });

        Button createUserButton = popupView.findViewById(R.id.createUserButton);
        createUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getCupuHelper().showCreateUser(view);
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showAdminOptions(View v) {
        View popupView = app.getInflater().inflate(R.layout.admin_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);

        Button checkBalanceButton = popupView.findViewById(R.id.checkBalanceButton);
        checkBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showBalance(view);
        });

        Button changePincodeButton = popupView.findViewById(R.id.changePincodeButton);
        changePincodeButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getPpuHelper().setUpdate(true);
            app.getPpuHelper().showPincode(view);
        });

        Button createUserButton = popupView.findViewById(R.id.createUserButton);
        createUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getCupuHelper().showCreateUser(view);
        });

        Button setupDatabaseButton = popupView.findViewById(R.id.setupDatabaseButton);
        setupDatabaseButton.setOnClickListener(view -> app.getMdbHelper().startDatabase());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
