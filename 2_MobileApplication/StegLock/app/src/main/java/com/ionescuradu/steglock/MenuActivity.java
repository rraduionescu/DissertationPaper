package com.ionescuradu.steglock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

//  Created by Ionescu Radu Stefan  //

public class MenuActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		findViewById(R.id.bChatMenu).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), ChatTabActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.bProfile).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.bAbout).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.bLogout).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Toast.makeText(MenuActivity.this, getResources().getString(R.string.tLogout), Toast.LENGTH_SHORT).show();
				Intent       intent       = new Intent(MenuActivity.this, MainActivity.class);
				FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
				firebaseAuth.signOut();
				LoginManager.getInstance().logOut();
				GoogleSignInOptions gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getResources().getString(R.string.oauth)).requestEmail().build();
				GoogleSignIn.getClient(getApplicationContext(), gOptions).signOut();
				startActivity(intent);
			}
		});
	}
}