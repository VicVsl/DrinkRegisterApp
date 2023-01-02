package com.example.DrinkRegisterApp;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<User> users;
    private DatabaseHelper mDbHelper;
    private PinPopUpHelper ppuHelper;
    private OptionsPopUpHelper opuHelper;
    private User login;
    private boolean verified = false;
    private TextView loginLabel;
    private Button returnButton;
    private Button optionsButton;
    private List<Change> changes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new DatabaseHelper(this);
        ppuHelper = new PinPopUpHelper(this);
        opuHelper = new OptionsPopUpHelper(this);
        changes = new ArrayList<>();

        // Makes sure users isn't 0
        users = mDbHelper.getUsers();
        if (users.isEmpty()) {
            User admin = new User(0, "admin", "temp", "other", "admin", 0);
            users.add(admin);
            mDbHelper.insertUser(admin);
        }

        loginLabel = (TextView) findViewById(R.id.loginLabel);

        // Configure exit button
        returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(view -> {
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
        });

        // Configure options button
        optionsButton = (Button) findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(view -> {
            if (!verified) {
                showLog(view);
                return;
            }
            opuHelper.showOptions(view);
        });


        // Configures the list of buttons
        RecyclerView buttonList = findViewById(R.id.button_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        buttonList.setLayoutManager(layoutManager);
        ButtonListAdapter adapter = new ButtonListAdapter(this);
        buttonList.setAdapter(adapter);
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pin_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        ppuHelper.setupPinButtons(popupView, popupWindow);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void showLog(View v) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.log, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, 1200, 1300, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        TextView logText = (TextView) popupView.findViewById(R.id.logText);
        String text = mDbHelper.getLog().toString();
        logText.setText(text);
        logText.setMovementMethod(new ScrollingMovementMethod());

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showBalance(View v) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.check_balance, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, 900, 1000, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        TextView balanceTotal = (TextView) popupView.findViewById(R.id.balanceTotal);
        balanceTotal.setText(login.getBalance() + "€");

        TextView balanceHistory = (TextView) popupView.findViewById(R.id.balanceHistory);
        String text = mDbHelper.findLogByName(createShortName(login)).toString();
        balanceHistory.setText(text);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showConfirmation(View v) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.confirm_changes, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, 800, 1000, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        TextView changesText = (TextView) popupView.findViewById(R.id.changesText);
        changesText.setText(changes.toString());

        Button confirmButton = (Button) popupView.findViewById(R.id.confirmationButton);
        confirmButton.setOnClickListener(view -> {
            applyChanges();
            login = null;
            verified = false;
            popupWindow.dismiss();
            updateScreen();
        });

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void showCreateUser(View v) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.create_user, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, 1200, 1300, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        EditText firstNameInput = (EditText) popupView.findViewById(R.id.firstNameInput);
        EditText lastNameInput = (EditText) popupView.findViewById(R.id.lastNameInput);
        EditText pinCodeInput = (EditText) popupView.findViewById(R.id.pinCodeInput);
        RadioGroup groupSelect = (RadioGroup ) popupView.findViewById(R.id.groupSelect);

        Button confirmButton = (Button) popupView.findViewById(R.id.confirmationButton);
        confirmButton.setOnClickListener(view -> {
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            int pincode = Integer.parseInt(pinCodeInput.getText().toString());
            int buttonId = groupSelect.getCheckedRadioButtonId();
            RadioButton button = (RadioButton) popupView.findViewById(buttonId);
            String group = button.getText().toString();
            User newUser = new User(0, firstName, lastName, group, "regular", pincode);
            mDbHelper.insertUser(newUser);
            mDbHelper.insertLog(createShortName(login), createShortName(newUser), "creation", 0);
            finish();
            startActivity(getIntent());
        });

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void applyChanges() {
        for (int i = 0; i < changes.size(); i++) {
            Change change = changes.get(i);
            User user = mDbHelper.findUserByName(change.getFirstName(), change.getLastName());
            user.addBalance(0.7 * change.getAmount());
            mDbHelper.updateBalance(user);
            mDbHelper.insertLog(createShortName(login), createShortName(user), "addition", change.getAmount());
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

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void updateScreen() {
        if (verified) {
            loginLabel.setText(login.getFirstName() + " " + login.getLastName());
            returnButton.setText("LOG OUT");
            returnButton.setTextSize(18);
            optionsButton.setText("OPTIONS");
            optionsButton.setTextSize(18);
        } else {
            loginLabel.setText("Scouts Gits");
            returnButton.setText("EXIT");
            returnButton.setTextSize(25);
            optionsButton.setText("LOG");
            optionsButton.setTextSize(25);
        }
    }

    public String createShortName(User user) {
        return user.getFirstName() + " " + user.getLastName().charAt(0) + ".";
    }

    public void startDatabase() {
        mDbHelper.emptyDb();
        // Creates users
        User vic = new User(0,"Vic", "Vansteelant", "Verkenners", "admin", 1111);
        User jannes = new User(0, "Jannes", "Dekeyzer", "Kapoenen", "mod", 2222);
        User bavo = new User(0, "Bavo", "Dewaele", "Jins", "regular", 3333);

        // Inserts the users in the database
        mDbHelper.insertUser(vic);
        mDbHelper.insertUser(jannes);
        for(int i = 0; i < 5; i++ ) {
            mDbHelper.insertUser(bavo);
        }
        finish();
        startActivity(getIntent());
    }

    public List<User> getUsers() {
        return users;
    }

    public DatabaseHelper getMdbHelper() {
        return mDbHelper;
    }

    public User getLogin() {
        return login;
    }

    public void setLogin(User login) {
        this.login = login;
    }

    public boolean isVerified() {
        return verified;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}