package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("InflateParams")
public class MainActivity extends AppCompatActivity {

    private CreateUserPopUpHelper cupuHelper;
    private EditUserPopUpHelper eupuHelper;
    private DatabaseHelper dbHelper;
    private OptionsPopUpHelper opuHelper;
    private PinPopUpHelper ppuHelper;

    private Activity activity;
    private int counter;
    private LayoutInflater inflater;
    private Timer timer;
    private TimerTask autoLogOut;

    private boolean verified;
    private boolean editMode;
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
        eupuHelper = new EditUserPopUpHelper(this);
        dbHelper = new DatabaseHelper(this);
        opuHelper = new OptionsPopUpHelper(this);
        ppuHelper = new PinPopUpHelper(this);

        activity = this;
        inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        timer = new Timer();
        setupAutoBackup();

        verified = false;
        changes = new ArrayList<>();

        // Makes sure users isn't 0
        users = dbHelper.getUsers();
        if (users.isEmpty()) {
            counter = 20;
            createAdmin();
        }
        Collections.sort(users, (u1, u2) -> u1.createShortName().compareTo(u2.createShortName()));

        loginLabel = findViewById(R.id.loginLabel);
        loginLabel.setOnClickListener(view -> {
            createAdmin();
        });

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
        dbHelper.close();
        super.onDestroy();
    }

    public void leftButtonHandler(View view) {
        if (!verified) {
            finish();
            System.exit(0);
        }
        if (changes.isEmpty() || editMode) {
            login = null;
            changes.clear();
            verified = false;
            editMode = false;
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
        PopupWindow popupWindow = createPopup(popupView, 400, 600);
        startAutoLogOutTimer(2, popupWindow);

        TextView changesText = popupView.findViewById(R.id.changesText);
        Collections.sort(changes, (c1, c2) -> c1.toString().compareTo(c2.toString()));
        changesText.setText(printList(changes));
        changesText.setMovementMethod(new ScrollingMovementMethod());

        Button confirmButton = popupView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            applyChanges();
            login = null;
            verified = false;
            updateScreen();
        });

        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            changes.clear();
            leftButtonHandler(null);
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showLog(View v) {
        View popupView = inflater.inflate(R.layout.log, null);
        PopupWindow popupWindow = createPopup(popupView, -2, -2);
        startAutoLogOutTimer(2, popupWindow);

        TextView logText = popupView.findViewById(R.id.logText);
        logText.setText(printList(dbHelper.getLog()));
        logText.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showBalance(View v) {
        View popupView = inflater.inflate(R.layout.check_balance, null);
        PopupWindow popupWindow = createPopup(popupView, 650, 600);
        startAutoLogOutTimer(3, popupWindow);

        TextView balanceTotal = popupView.findViewById(R.id.balanceTotal);
        int balance = login.getBalance();
        double inEuros = Math.round(balance * 0.7 * 100.0) / 100.0;
        String text = balance + " = " + inEuros + '€';
        balanceTotal.setText(text);

        TextView balanceHistory = popupView.findViewById(R.id.balanceHistory);
        balanceHistory.setText(printList(dbHelper.findLogByName(login.createShortName())));
        balanceHistory.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showAllBalances(View v) {
        dbHelper.exportDB("backup.csv");

        View popupView = inflater.inflate(R.layout.log, null);
        PopupWindow popupWindow = createPopup(popupView, -2, -2);
        startAutoLogOutTimer(5, popupWindow);

        TextView logText = popupView.findViewById(R.id.logText);
        logText.setText(printList(dbHelper.getBalances()));
        logText.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    @SuppressLint("SetTextI18n")
    public void showDatabase(View v) {
        dbHelper.exportDB("backup.csv");

        View popupView = inflater.inflate(R.layout.log, null);
        PopupWindow popupWindow = createPopup(popupView, -2, -2);
        startAutoLogOutTimer(5, popupWindow);

        TextView logText = popupView.findViewById(R.id.logText);
        logText.setText("U" + printList(dbHelper.getUsers()));
        logText.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showSnackbar(View v, String text) {
        Snackbar snackbar = Snackbar
                .make(v, text, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.dark_blue));
        snackbar.setTextColor(getResources().getColor(R.color.yellow));
        snackbar.show();
    }

    public void enableEditMode() {
        editMode = true;
        loginLabel.setText(R.string.edit_mode);
        loginLabel.setTextColor(getResources().getColor(R.color.red));
        leftButton.setText(R.string.exit);
        leftButton.setTextSize(30);
        rightButton.setVisibility(View.GONE);
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
        autoLogOut.cancel();
        for (int i = 0; i < changes.size(); i++) {
            Change change = changes.get(i);
            User user = dbHelper.findUserByName(change.getFirstName(), change.getLastName());
            user.updateBalance(change.getAmount());
            dbHelper.updateBalance(user);
            dbHelper.insertLog(login.createShortName(), user.createShortName(), "addition", change.getAmount());
        }
        users = dbHelper.getUsers();
        changes.clear();
    }

    public void updateScreen() {
        counter = 0;
        if (verified) {
            String fullName = login.getFirstName() + " " + login.getLastName();
            loginLabel.setText(fullName);
            leftButton.setText(R.string.log_out);
            leftButton.setTextSize(20);
            rightButton.setText(R.string.options_header);
            rightButton.setTextSize(20);
            startAutoLogOutTimer(2, null);
        } else {
            loginLabel.setText(R.string.app_organization);
            loginLabel.setTextColor(getResources().getColor(R.color.yellow));
            leftButton.setText(R.string.exit);
            leftButton.setTextSize(30);
            rightButton.setText(R.string.log);
            rightButton.setTextSize(30);
            rightButton.setVisibility(View.VISIBLE);
        }
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    @SuppressWarnings("deprecation")
    public void setupAutoBackup() {
        TimerTask createBackup = new TimerTask() {
            @Override
            public void run() {
                dbHelper.exportDB("backup.csv");
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 1900;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Date date = new Date(year, month, day, 23, 59, 59);
        timer.scheduleAtFixedRate(createBackup, date, 86400000);
    }

    public void startAutoLogOutTimer(long delay, PopupWindow popupWindow) {
        if (autoLogOut != null) autoLogOut.cancel();
        autoLogOut = new TimerTask() {
            @Override
            public void run() {
                if (popupWindow != null) activity.runOnUiThread(popupWindow::dismiss);
                applyChanges();
                login = null;
                verified = false;
                activity.runOnUiThread(() -> updateScreen());
            }
        };
        timer.schedule(autoLogOut, delay * 60000);
    }

    public void createAdmin() {
        counter++;
        if (counter < 20) return;
        counter = 0;
        User admin = new User(0,"Admin", "Admin", "other", "admin", 0, 0);
        users.add(admin);
        dbHelper.insertUser(admin);
    }

    public CreateUserPopUpHelper getCupuHelper() {return cupuHelper;}

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public EditUserPopUpHelper getEupuHelper() {return eupuHelper;}

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

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
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