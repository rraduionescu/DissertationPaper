package com.ionescuradu.steglock.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.ionescuradu.steglock.dialogs.EditNicknameDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//  Created by Ionescu Radu Stefan  //

public class ProfileActivity extends AppCompatActivity
{
	private FirebaseUser firebaseUser;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseUser = firebaseAuth.getCurrentUser();
		AppCompatImageButton bEditProfilePicture       = findViewById(R.id.bEditProfilePicture);
		AppCompatImageButton bEditNickname             = findViewById(R.id.bEditNickname);
		DatabaseReference    firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
		String[]             userFullName              = firebaseUser.getDisplayName().split(" ");

		firebaseDatabaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				User user = dataSnapshot.getValue(User.class);
				((EditText) findViewById(R.id.etNicknameProfile)).setText(user.getNickname());
				((EditText) findViewById(R.id.etFirstNameProfile)).setText(userFullName[0]);
				((EditText) findViewById(R.id.etLastNameProfile)).setText(userFullName[1]);
				((EditText) findViewById(R.id.etEmail)).setText(firebaseUser.getEmail());
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
				Log.e("DATABASE ERROR: ", databaseError.getMessage());
			}
		});

		bEditProfilePicture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 2);
			}
		});

		bEditNickname.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openDialog();
			}
		});

		StorageReference storageReference = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com").getReference().child("ProfilePictures/" + firebaseUser.getUid());
		storageReference.getBytes(1024 * 1024 * 2).addOnSuccessListener(new OnSuccessListener<byte[]>()
		{
			@Override
			public void onSuccess(byte[] bytes)
			{
				Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				((ImageView) findViewById(R.id.ivProfile)).setImageBitmap(bitmapImage);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		Bitmap bitmapImage;
		Uri    imageURI = data.getData();

		if (requestCode == 2 && resultCode == RESULT_OK)
		{
			StorageReference storageReference = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com").getReference().child("ProfilePictures/" + firebaseUser.getUid());
			storageReference.delete();
			try
			{
				bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byte[] bitmapData = byteArrayOutputStream.toByteArray();
				((ImageView) findViewById(R.id.ivProfile)).setImageBitmap(bitmapImage);
				storageReference.putBytes(bitmapData);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void openDialog()
	{
		EditNicknameDialog editNicknameDialog = new EditNicknameDialog();
		editNicknameDialog.show(getSupportFragmentManager(), "dialog");
	}
}