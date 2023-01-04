package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class OptionsPopUpHelper {

    private final MainActivity app;

    public OptionsPopUpHelper(MainActivity app) {
        this.app = app;
    }


    public void showOptions(View v) {
        String rank = app.getLogin().getRank();
        switch (rank) {
            case "admin":
                showAdminOptions(v);
                break;
            case "mod":
                showModOptions(v);
                break;
        }
    }

    @SuppressLint("InflateParams")
    public void showAdminOptions(View v) {
        View popupView = app.getInflater().inflate(R.layout.admin_options, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);

        Button checkBalanceButton = popupView.findViewById(R.id.checkBalanceButton);
        checkBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showBalance(view);
        });

        Button createUserButton = popupView.findViewById(R.id.createUserButton);
        createUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.getCupuHelper().showCreateUser(view);
        });

        Button emptyDatabaseButton = popupView.findViewById(R.id.emptyDatabaseButton);
        emptyDatabaseButton.setOnClickListener(view -> {
            app.getMdbHelper().emptyDb();
            app.finish();
            app.startActivity(app.getIntent());
        });

        Button setupDatabaseButton = popupView.findViewById(R.id.setupDatabaseButton);
        setupDatabaseButton.setOnClickListener(view -> app.getMdbHelper().startDatabase());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showModOptions(View v) {
        showAdminOptions(v);
    }
}
