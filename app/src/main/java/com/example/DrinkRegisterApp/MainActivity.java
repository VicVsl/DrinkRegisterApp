package com.example.DrinkRegisterApp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<User> users;
    private DatabaseHelper mDbHelper;
    private PinPopUpHelper ppuHelper;
    private User login;
    private boolean verified = false;
    private TextView loginLabel;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new DatabaseHelper(this);
        ppuHelper = new PinPopUpHelper(this);

        // Makes sure users isn't 0
        users = mDbHelper.getUsers();
        if (users.isEmpty()) {
            users.add(new User(0, "temp", "test", "other", 0));
        }

        loginLabel = (TextView) findViewById(R.id.loginLabel);

        // Configure exit button
        returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(view -> {
            if (!verified) {
                finish();
                System.exit(0);
            }
            loginLabel.setText("Scouts Gits");
            verified = false;
            login = null;
            returnButton.setText("EXIT");
            returnButton.setTextSize(25);
        });

        // Configure log button
        Button logButton = (Button) findViewById(R.id.logButton);
        logButton.setOnClickListener(this::showLog);


        // Configures the list of buttons
        RecyclerView buttonList = findViewById(R.id.button_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        buttonList.setLayoutManager(layoutManager);
        ButtonListAdapter adapter = new ButtonListAdapter(this);
        buttonList.setAdapter(adapter);

        // Setup the database if it's empty
        if (mDbHelper.getUsers().isEmpty()) {
            startDatabase();
            finish();
            startActivity(getIntent());
        }
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pin_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

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
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        TextView logText = (TextView) popupView.findViewById(R.id.testText);
        String text = mDbHelper.getUsers().toString();
        logText.setText(text);
        logText.setTextColor(getResources().getColor(R.color.yellow));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public boolean verifyPinCode(String pincode) {
        if (Integer.parseInt(pincode) == login.getPinCode()) {
            verified = true;
            loginLabel.setText(login.getFirstName() + " " + login.getLastName());
            returnButton.setText("LOG OUT");
            returnButton.setTextSize(18);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void startDatabase() {
        // Creates users
        User vic = new User(0,"Vic", "Vansteelant", "Verkenners", 1111);
        User jannes = new User(0, "Jannes", "Dekeyzer", "Kapoenen", 2222);
        User bavo = new User(0, "Bavo", "Dewaele", "Jins", 3333);

        // Inserts the users in the database
        mDbHelper.insertUser(vic);
        mDbHelper.insertUser(jannes);
        for(int i = 0; i < 30; i++ ) {
            mDbHelper.insertUser(bavo);
        }
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
}