package com.example.DrinkRegisterApp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.view.Gravity;
import android.view.LayoutInflater;
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


    public void showAdminOptions(View v) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                app.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.admin_options, null);

        // create the popup window
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, 800, 1200, focusable);

        Button checkBalanceButton = (Button) popupView.findViewById(R.id.checkBalanceButton);
        checkBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.showBalance(view);
        });

        Button createUserButton = (Button) popupView.findViewById(R.id.buttonCreateUser);
        createUserButton.setOnClickListener(view -> {
            //TODO
        });

        Button emptyDatabaseButton = (Button) popupView.findViewById(R.id.buttonEmptyDatabase);
        emptyDatabaseButton.setOnClickListener(view -> {
            app.getMdbHelper().emptyDb();
            app.finish();
            app.startActivity(app.getIntent());
        });

        Button setupDatabaseButton = (Button) popupView.findViewById(R.id.confirmationButton);
        setupDatabaseButton.setOnClickListener(view -> {
            app.startDatabase();
        });

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showModOptions(View v) {
        showAdminOptions(v);
    }
}
