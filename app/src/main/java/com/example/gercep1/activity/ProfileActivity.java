package com.example.gercep1.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Objects;

import com.example.gercep1.R;
import com.example.gercep1.activity.SessionManager;
import com.example.gercep1.activity.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SessionManager session;
    String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);

        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM TB_USER WHERE username = '" + email + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            name = cursor.getString(2);
        }

        TextView lblName = findViewById(R.id.lblName);
        TextView lblEmail = findViewById(R.id.lblEmail);

        lblName.setText(name);
        lblEmail.setText(email);

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbProfile);
        toolbar.setTitle("Identitas Penyewa");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
