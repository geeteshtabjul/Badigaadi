package com.android.badigaadi.activity;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.badigaadi.activity.LoginActivity;

import java.util.HashMap;

import com.android.badigaadi.R;
import com.android.badigaadi.helper.SQLiteHandler;
import com.android.badigaadi.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtName;
	private TextView contact;
	private Button btnLogout;
	FloatingActionButton fab;
	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		contact = (TextView) findViewById(R.id.user_entered_contact);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String contact_no = user.get("contact_no");

		// Displaying the user details on the screen
		txtName.setText(name);
		contact.setText(contact_no);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, TruckMainActivity.class));
			}
		});

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
