package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateUserPopUpHelper {

    private final MainActivity app;

    public CreateUserPopUpHelper(MainActivity app) {
        this.app = app;
    }

    @SuppressLint("InflateParams")
    public void showCreateUser(View v) {
        View popupView = app.getInflater().inflate(R.layout.create_user, null);
        PopupWindow popupWindow = app.createPopup(popupView, 750, 600);
        app.startAutoLogOutTimer(5, popupWindow);

        Button confirmButton = popupView.findViewById(R.id.confirmationButton);
        confirmButton.setOnClickListener(view -> validateUser(popupView));

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void validateUser(View v) {
        EditText firstNameInput = v.findViewById(R.id.firstNameInput);
        TextView firstNameError = v.findViewById(R.id.firstNameError);
        firstNameError.setText(null);
        String firstName = firstNameInput.getText().toString();
        if (firstName.isEmpty()) firstNameError.setText(app.getResources().getString(R.string.empty_field));
        else if (!Character.isUpperCase(firstName.charAt(0))) firstNameError.setText(app.getResources().getString(R.string.name_uppercase));

        EditText lastNameInput = v.findViewById(R.id.lastNameInput);
        TextView lastNameError = v.findViewById(R.id.lastNameError);
        lastNameError.setText(null);
        String lastName = lastNameInput.getText().toString();
        if (lastName.isEmpty()) lastNameError.setText(app.getResources().getString(R.string.empty_field));
        else if (!Character.isUpperCase(lastName.charAt(0))) lastNameError.setText(app.getResources().getString(R.string.name_uppercase));

        EditText pinCodeInput = v.findViewById(R.id.pinCodeInput);
        TextView pinCodeError = v.findViewById(R.id.pinCodeError);
        pinCodeError.setText(null);
        int pincode = 0;
        String pincodeText = pinCodeInput.getText().toString();
        if (pincodeText.isEmpty()) pinCodeError.setText(app.getResources().getString(R.string.empty_field));
        else if (pincodeText.length() < 2 || pincodeText.length() > 6)
            pinCodeError.setText(app.getResources().getString(R.string.pincode_length));
        else try {
            pincode = Integer.parseInt(pincodeText);
        } catch (Exception e) {
            pinCodeError.setText(app.getResources().getString(R.string.invalid_number));
        }
        if (!(firstNameError.getText().equals("")) || !(lastNameError.getText().equals("")) || !(pinCodeError.getText().equals("")))
            return;

        if (app.getDbHelper().findUserByName(firstName, lastName) != null) {
            firstNameError.setText(app.getResources().getString(R.string.user_exists));
            return;
        }

        RadioGroup groupSelect = v.findViewById(R.id.groupSelect);
        RadioButton button = v.findViewById(groupSelect.getCheckedRadioButtonId());
        String group = button.getText().toString();
        if (group.equals(app.getResources().getString(R.string.group6))) group = "Ex-leaders";
        if (group.equals(app.getResources().getString(R.string.group7))) group = "Other";

        User newUser = new User(firstName, lastName, group, pincode);
        app.getDbHelper().insertUser(newUser);
        app.getDbHelper().insertLog(app.getLogin().createShortName(), newUser.createShortName(), "creation", 0);
        app.finish();
        app.startActivity(app.getIntent());
    }
}
