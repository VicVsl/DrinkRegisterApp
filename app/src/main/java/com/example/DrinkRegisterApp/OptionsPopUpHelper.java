package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

@SuppressLint("InflateParams")
public class OptionsPopUpHelper {

    private final MainActivity app;
    private int counter;

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
        app.startAutoLogOutTimer(3, popupWindow);

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
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);
        app.startAutoLogOutTimer(3, popupWindow);

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

        Button editUserButton = popupView.findViewById(R.id.editUserButton);
        editUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.enableEditMode();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showAdminOptions(View v) {
        View popupView = app.getInflater().inflate(R.layout.mod_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);
        app.startAutoLogOutTimer(3, popupWindow);

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

        Button editUserButton = popupView.findViewById(R.id.editUserButton);
        editUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.enableEditMode();
        });

        TextView optionsHeader = popupView.findViewById(R.id.optionsTitle);
        optionsHeader.setOnClickListener(view -> {
            counter++;
            if (counter == 5) {
                counter = 0;
                popupWindow.dismiss();
                showAdminOptions2(view);
            }
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showAdminOptions2(View v) {
        View popupView = app.getInflater().inflate(R.layout.admin_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);
        app.startAutoLogOutTimer(2, popupWindow);

        Button getBalancesButton = popupView.findViewById(R.id.getBalancesButton);
        getBalancesButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showAllBalances(view);
        });

        Button resetBalancesButton = popupView.findViewById(R.id.resetBalancesButton);
        resetBalancesButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            List<User> users = app.getDbHelper().getUsers();
            for (User user : users) {
                int balance = user.getBalance();
                user.updateBalance(-balance);
                app.getDbHelper().updateBalance(user);
                app.getDbHelper().insertLog(app.getLogin().createShortName(), user.createShortName(), "deletion", balance);
            }
            app.leftButtonHandler(view);
        });

        Button printDatabaseButton = popupView.findViewById(R.id.printDatabaseButton);
        printDatabaseButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showDatabase(view);
        });

        Button emptyDatabaseButton = popupView.findViewById(R.id.emptyDatabaseButton);
        emptyDatabaseButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getDbHelper().emptyDb();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
