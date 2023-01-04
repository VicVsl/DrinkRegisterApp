package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("InflateParams")
public class MainActivity extends AppCompatActivity {

    private CreateUserPopUpHelper cupuHelper;
    private DatabaseHelper mDbHelper;
    private OptionsPopUpHelper opuHelper;
    private PinPopUpHelper ppuHelper;

    private LayoutInflater inflater;

    private boolean verified;
    private User login;
    private List<User> users;
    private List<Change> changes;

    private TextView loginLabel;
    private Button leftButton;
    private Button rightButton;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        cupuHelper = new CreateUserPopUpHelper(this);
        mDbHelper = new DatabaseHelper(this);
        opuHelper = new OptionsPopUpHelper(this);
        ppuHelper = new PinPopUpHelper(this);

        inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        verified = false;
        changes = new ArrayList<>();

        // Makes sure users isn't 0
        users = mDbHelper.getUsers();
        if (users.isEmpty()) {
            User admin = new User(0,"admin", "temp", "other", "admin", 100, 0);
            users.add(admin);
            mDbHelper.insertUser(admin);
        }
        Collections.sort(users, (u1, u2) -> u1.createShortName().compareTo(u2.createShortName()));


        loginLabel = findViewById(R.id.loginLabel);

        // Configure exit button
        leftButton = findViewById(R.id.leftButton);
        leftButton.setOnClickListener(this::leftButtonHandler);

        // Configure options button
        rightButton = findViewById(R.id.rightButton);
        rightButton.setOnClickListener(this::rightButtonHandler);


        // Configures the list of buttons
        RecyclerView buttonList = findViewById(R.id.buttonList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        buttonList.setLayoutManager(layoutManager);
        ButtonListAdapter adapter = new ButtonListAdapter(this);
        buttonList.setAdapter(adapter);

        updateScreen();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void leftButtonHandler(View view) {
        if (!verified) {
            finish();
            System.exit(0);
        }
        if (changes.isEmpty()) {
            login = null;
            verified = false;
            updateScreen();
            return;
        }
        showConfirmation(view);
    }

    public void rightButtonHandler(View view) {
        if (!verified) {
            showLog(view);
            return;
        }
        opuHelper.showOptions(view);
    }

    @SuppressWarnings("all")
    public void showConfirmation(View v) {
        View popupView = inflater.inflate(R.layout.confirm_changes, null);
        PopupWindow popupWindow = createPopup(popupView, 400, 500);

        TextView changesText = popupView.findViewById(R.id.changesText);
        Collections.sort(changes, (c1, c2) -> c1.toString().compareTo(c2.toString()));
        changesText.setText(printList(changes));
        changesText.setMovementMethod(new ScrollingMovementMethod());

        Button confirmButton = popupView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            applyChanges();
            login = null;
            verified = false;
            popupWindow.dismiss();
            updateScreen();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showLog(View v) {
        View popupView = inflater.inflate(R.layout.log, null);
        PopupWindow popupWindow = createPopup(popupView, -2, -2);

        TextView logText = popupView.findViewById(R.id.logText);
        logText.setText(printList(mDbHelper.getLog()));
        logText.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showBalance(View v) {
        View popupView = inflater.inflate(R.layout.check_balance, null);
        PopupWindow popupWindow = createPopup(popupView, 700, 600);

        TextView balanceTotal = popupView.findViewById(R.id.balanceTotal);
        int balance = login.getBalance();
        double inEuros = Math.round(balance * 0.7 * 100.0) / 100.0;
        String text = balance + " = " + inEuros + '€';
        balanceTotal.setText(text);

        TextView balanceHistory = popupView.findViewById(R.id.balanceHistory);
        balanceHistory.setText(printList(mDbHelper.findLogByName(login.createShortName())));
        balanceHistory.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    @SuppressWarnings("rawtypes")
    public String printList(List log) {
        String text = log.toString();
        if (text.length() < 3) return "";
        return text.substring(2, text.length() - 1);
    }

    public PopupWindow createPopup(View popupView, int width, int height) {
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        return popupWindow;
    }

    public void applyChanges() {
        for (int i = 0; i < changes.size(); i++) {
            Change change = changes.get(i);
            User user = mDbHelper.findUserByName(change.getFirstName(), change.getLastName());
            user.addBalance(change.getAmount());
            mDbHelper.updateBalance(user);
            mDbHelper.insertLog(login.createShortName(), user.createShortName(), "addition", change.getAmount());
        }
        changes.clear();
    }

    public void updateScreen() {
        if (verified) {
            String fullName = login.getFirstName() + " " + login.getLastName();
            loginLabel.setText(fullName);
            leftButton.setText(R.string.log_out);
            leftButton.setTextSize(20);
            rightButton.setText(R.string.options_header);
            rightButton.setTextSize(20);
        } else {
            loginLabel.setText(R.string.app_organization);
            leftButton.setText(R.string.exit);
            leftButton.setTextSize(30);
            rightButton.setText(R.string.log);
            rightButton.setTextSize(30);
        }
    }

    public CreateUserPopUpHelper getCupuHelper() {return cupuHelper;}

    public DatabaseHelper getMdbHelper() {
        return mDbHelper;
    }

    public PinPopUpHelper getPpuHelper() {
        return ppuHelper;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getLogin() {
        return login;
    }

    public void setLogin(User login) {
        this.login = login;
    }

    public List<Change> getChanges() {
        return changes;
    }
}