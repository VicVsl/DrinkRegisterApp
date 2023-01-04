package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CreateUserPopUpHelper {

    private final MainActivity app;

    public CreateUserPopUpHelper(MainActivity app) {
        this.app = app;
    }

    @SuppressLint("InflateParams")
    public void showCreateUser(View v) {
        View popupView = app.getInflater().inflate(R.layout.create_user, null);
        PopupWindow popupWindow = app.createPopup(popupView, 750, 600);

        Button confirmButton = popupView.findViewById(R.id.confirmationButton);
        confirmButton.setOnClickListener(view -> validateUser(popupView));

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void validateUser(View v) {
        EditText firstNameInput = v.findViewById(R.id.firstNameInput);
        String firstName = firstNameInput.getText().toString();
        if (firstName.isEmpty()) {
            firstNameInput.setError(app.getResources().getString(R.string.empty_field));
        } else if (!Character.isUpperCase(firstName.charAt(0))) {
            firstNameInput.setError(app.getResources().getString(R.string.name_uppercase));
        }

        EditText lastNameInput = v.findViewById(R.id.lastNameInput);
        String lastName = lastNameInput.getText().toString();
        if (lastName.isEmpty()) {
            lastNameInput.setError(app.getResources().getString(R.string.empty_field));
        } else if (!Character.isUpperCase(lastName.charAt(0))) {
            lastNameInput.setError(app.getResources().getString(R.string.name_uppercase));
        }

        EditText pinCodeInput = v.findViewById(R.id.pinCodeInput);
        int pincode = 0;
        String pincodeText = pinCodeInput.getText().toString();
        if (pincodeText.isEmpty()) {
            pinCodeInput.setError(app.getResources().getString(R.string.empty_field));
        } else if (pincodeText.length() < 2 || pincodeText.length() > 6) {
            pinCodeInput.setError(app.getResources().getString(R.string.pincode_length));
        } else try {
            pincode = Integer.parseInt(pincodeText);
        } catch (Exception e) {
            pinCodeInput.setError(app.getResources().getString(R.string.invalid_number));
        }
        if (!(firstNameInput.getError() == null) || !(lastNameInput.getError() == null) || !(pinCodeInput.getError() == null))
            return;

        RadioGroup groupSelect = v.findViewById(R.id.groupSelect);
        RadioButton button = v.findViewById(groupSelect.getCheckedRadioButtonId());
        String group = button.getText().toString();
        if (group.equals("Oud-leiding")) group = "Ex-leaders";
        if (group.equals("Andere")) group = "Other";

        User newUser = new User(firstName, lastName, group, pincode);
        app.getMdbHelper().insertUser(newUser);
        app.getMdbHelper().insertLog(app.getLogin().createShortName(), newUser.createShortName(), "creation", 0);
        app.finish();
        app.startActivity(app.getIntent());
    }
}
