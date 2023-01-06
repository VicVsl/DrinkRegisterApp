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

@SuppressLint("InflateParams")
public class EditUserPopUpHelper {

    private final MainActivity app;
    private User user;

    public EditUserPopUpHelper(MainActivity app) {
        this.app = app;
    }

    public void showEditMenu(View v, User user) {
        this.user = app.getDbHelper().findUserByName(user.getFirstName(), user.getLastName());

        View popupView = app.getInflater().inflate(R.layout.edit_menu, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 650);
        app.startAutoLogOutTimer(3, popupWindow);

        Button editBalanceButton = popupView.findViewById(R.id.editBalanceButton);
        editBalanceButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            showEditBalanceMenu(view);
        });

        Button editPincodeButton = popupView.findViewById(R.id.editPincodeButton);
        editPincodeButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            app.setLogin(user);
            app.getPpuHelper().setUpdate(true);
            app.getPpuHelper().showPincode(view);
        });

        Button editGroupButton = popupView.findViewById(R.id.editGroupButton);
        editGroupButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            showEditGroupMenu(view);
        });

        Button deleteUserButton = popupView.findViewById(R.id.deleteUserButton);
        deleteUserButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            showDeleteUserMenu(view);
        });

        if (app.getLogin().getRank().equals("admin")) {
            TextView editMenuTitle = popupView.findViewById(R.id.editMenuTitle);
            editMenuTitle.setOnClickListener(view -> {
                popupWindow.dismiss();
                showEditRankMenu(view);
            });
        }

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showEditBalanceMenu(View v) {
        View popupView = app.getInflater().inflate(R.layout.edit_balance_menu, null);
        PopupWindow popupWindow = app.createPopup(popupView, 400, 400);
        app.startAutoLogOutTimer(3, popupWindow);

        Button confirmationButton = popupView.findViewById(R.id.confirmationButton);
        confirmationButton.setOnClickListener(view -> {
            EditText balanceInput = popupView.findViewById(R.id.balanceInput);
            int amount;
            String balanceText = balanceInput.getText().toString();
            if (balanceText.isEmpty()) {
                balanceInput.setError(app.getResources().getString(R.string.empty_field));
                return;
            } else try {
                amount = Integer.parseInt(balanceText);
            } catch (Exception e) {
                balanceInput.setError(app.getResources().getString(R.string.invalid_number));
                return;
            }
            user.updateBalance(-amount);
            app.getDbHelper().updateBalance(user);
            app.getDbHelper().insertLog(app.getLogin().createShortName(), user.createShortName(), "deletion", amount);
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showEditGroupMenu(View v) {
        View popupView = app.getInflater().inflate(R.layout.edit_group_menu, null);
        PopupWindow popupWindow = app.createPopup(popupView, 400, 600);
        app.startAutoLogOutTimer(3, popupWindow);

        Button confirmationButton = popupView.findViewById(R.id.confirmationButton);
        confirmationButton.setOnClickListener(view -> {
            RadioGroup groupSelect = popupView.findViewById(R.id.groupSelect);
            RadioButton button = popupView.findViewById(groupSelect.getCheckedRadioButtonId());
            String group = button.getText().toString();
            if (group.equals(app.getResources().getString(R.string.group6))) group = "Ex-leaders";
            if (group.equals(app.getResources().getString(R.string.group7))) group = "Other";

            app.getDbHelper().updateGroup(user.getId(), group);
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    @SuppressLint("SetTextI18n")
    public void showDeleteUserMenu(View v) {
        View popupView = app.getInflater().inflate(R.layout.delete_user_confirmation, null);
        PopupWindow popupWindow = app.createPopup(popupView, 500, 300);
        app.startAutoLogOutTimer(3, popupWindow);

        TextView deleteUserText = popupView.findViewById(R.id.deleteUserText);
        String text1 = app.getResources().getString(R.string.delete_user_text1);
        String text2 = app.getResources().getString(R.string.delete_user_text2);
        deleteUserText.setText(text1 + " " + user.createShortName() + " " + text2);

        Button confirmationButton = popupView.findViewById(R.id.confirmationButton);
        confirmationButton.setOnClickListener(view -> {
            app.getDbHelper().deleteUser(user.getId());
            popupWindow.dismiss();
            app.finish();
            app.startActivity(app.getIntent());
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showEditRankMenu(View v) {
        View popupView = app.getInflater().inflate(R.layout.edit_rank_menu, null);
        PopupWindow popupWindow = app.createPopup(popupView, 400, 450);
        app.startAutoLogOutTimer(3, popupWindow);

        Button confirmationButton = popupView.findViewById(R.id.confirmationButton);
        confirmationButton.setOnClickListener(view -> {
            RadioGroup rankSelect = popupView.findViewById(R.id.rankSelect);
            RadioButton button = popupView.findViewById(rankSelect.getCheckedRadioButtonId());
            String rank = button.getText().toString();

            app.getDbHelper().updateRank(user.getId(), rank);
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
