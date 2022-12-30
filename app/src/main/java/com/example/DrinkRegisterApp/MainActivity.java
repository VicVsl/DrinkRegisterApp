package com.example.DrinkRegisterApp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<User> users;
    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new DatabaseHelper(this);

        // Makes sure users isn't 0
        users = mDbHelper.getUsers();
        if (users.isEmpty()) {
            users.add(new User(0, "temp", "test", "other", 0));
        }

        // Configure exit button
        Button exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });

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
        for(int i = 0; i < 20; i++ ) {
            mDbHelper.insertUser(bavo);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public DatabaseHelper getMdbHelper() {
        return mDbHelper;
    }
}