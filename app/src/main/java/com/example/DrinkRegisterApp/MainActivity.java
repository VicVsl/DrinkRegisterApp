package com.example.DrinkRegisterApp;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mDbHelper;
    private OptionsPopUpHelper opuHelper;
    private PinPopUpHelper ppuHelper;

    private LayoutInflater inflater;

    private boolean verified;
    private List<User> users;
    private User login;
    private List<Change> changes;

    private TextView loginLabel;
    private Button leftButton;
    private Button rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

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


        loginLabel = (TextView) findViewById(R.id.loginLabel);

        // Configure exit button
        leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(this::leftButtonHandler);

        // Configure options button
        rightButton = (Button) findViewById(R.id.rightButton);
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

    public void showConfirmation(View v) {
        View popupView = inflater.inflate(R.layout.confirm_changes, null);
        PopupWindow popupWindow = createPopup(popupView, 400, 500);

        TextView changesText = (TextView) popupView.findViewById(R.id.changesText);
        changesText.setText(printList(changes));
        changesText.setMovementMethod(new ScrollingMovementMethod());

        Button confirmButton = (Button) popupView.findViewById(R.id.confirmButton);
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

        TextView logText = (TextView) popupView.findViewById(R.id.logText);
        logText.setText(printList(mDbHelper.getLog()));
        logText.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showBalance(View v) {
        View popupView = inflater.inflate(R.layout.check_balance, null);
        PopupWindow popupWindow = createPopup(popupView, 700, 600);

        TextView balanceTotal = (TextView) popupView.findViewById(R.id.balanceTotal);
        int balance = login.getBalance();
        double inEuros = Math.round(balance * 0.7 * 100.0) / 100.0;
        String text = balance + " = " + inEuros + '€';
        balanceTotal.setText(text);

        TextView balanceHistory = (TextView) popupView.findViewById(R.id.balanceHistory);
        balanceHistory.setText(printList(mDbHelper.findLogByName(login.createShortName())));
        balanceHistory.setMovementMethod(new ScrollingMovementMethod());

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showCreateUser(View v) {
        View popupView = inflater.inflate(R.layout.create_user, null);
        PopupWindow popupWindow = createPopup(popupView, 750, 600);

        EditText firstNameInput = (EditText) popupView.findViewById(R.id.firstNameInput);
        EditText lastNameInput = (EditText) popupView.findViewById(R.id.lastNameInput);
        EditText pinCodeInput = (EditText) popupView.findViewById(R.id.pinCodeInput);
        RadioGroup groupSelect = (RadioGroup ) popupView.findViewById(R.id.groupSelect);

        Button confirmButton = (Button) popupView.findViewById(R.id.confirmationButton);
        confirmButton.setOnClickListener(view -> {
            //TODO: input validation
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            int pincode = Integer.parseInt(pinCodeInput.getText().toString());
            int buttonId = groupSelect.getCheckedRadioButtonId();
            RadioButton button = (RadioButton) popupView.findViewById(buttonId);
            String group = button.getText().toString();
            User newUser = new User(firstName, lastName, group, pincode);
            mDbHelper.insertUser(newUser);
            mDbHelper.insertLog(login.createShortName(), newUser.createShortName(), "creation", 0);
            finish();
            startActivity(getIntent());
        });

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

    public boolean verifyPinCode(String pincode) {
        if (Integer.parseInt(pincode) == login.getPinCode()) {
            verified = true;
            updateScreen();
            return true;
        }
        return false;
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

    //Can be deleted when the app is finished
    public void startDatabase() {
        mDbHelper.emptyDb();
        // Creates users
        User vic = new User(0,"Vic", "Vansteelant", "Verkenners", "admin", 10, 1111);
        User jannes = new User(0, "Jannes", "Dekeyzer", "Kapoenen", "mod", 5, 2222);
        User bavo = new User(0, "Bavo", "Dewaele", "Jins", "regular", 100, 3333);

        // Inserts the users in the database
        mDbHelper.insertUser(vic);
        mDbHelper.insertUser(jannes);
        for(int i = 0; i < 10; i++ ) {
            mDbHelper.insertUser(bavo);
        }
        finish();
        startActivity(getIntent());
    }

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