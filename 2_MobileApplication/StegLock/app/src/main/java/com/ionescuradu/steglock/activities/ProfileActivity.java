package com.ionescuradu.steglock.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.classes.User;

//  Created by Ionescu Radu Stefan  //

public class ProfileActivity extends AppCompatActivity
{
	private DatabaseReference databaseReference;
	private FirebaseAuth      firebaseAuth;
	private FirebaseUser      firebaseUser;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		firebaseAuth = FirebaseAuth.getInstance();
		firebaseUser = firebaseAuth.getCurrentUser();
		databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
		String[] name = firebaseUser.getDisplayName().split(" ");
		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				User user = dataSnapshot.getValue(User.class);
				((EditText) findViewById(R.id.etNicknameProfile)).setText(user.getNickname());
				((EditText) findViewById(R.id.etFirstNameProfile)).setText(name[0]);
				((EditText) findViewById(R.id.etLastNameProfile)).setText(name[1]);
				((EditText) findViewById(R.id.etEmail)).setText(firebaseUser.getEmail());
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});

		FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
		StorageReference reference = null;
		if (firebaseUser != null)
		{
			reference = storage.getReference().child("ProfilePictures/" + firebaseUser.getUid());
		}
		if (reference != null)
		{
			reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
			{
				@Override
				public void onSuccess(byte[] bytes)
				{
					Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					((ImageView) findViewById(R.id.ivProfile)).setImageBitmap(bitmap);
				}
			});
		}
	}
}