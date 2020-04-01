package com.ionescuradu.steglock;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//  Created by Ionescu Radu Stefan  //

public class ProfileActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

		if (firebaseUser != null)
		{
			((EditText) findViewById(R.id.etName)).setText(firebaseUser.getDisplayName());
			((EditText) findViewById(R.id.etEmail)).setText(firebaseUser.getEmail());
		}

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