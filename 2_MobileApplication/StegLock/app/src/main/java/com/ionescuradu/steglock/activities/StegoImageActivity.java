package com.ionescuradu.steglock.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.steganography.StegoEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.HashMap;

//  Created by Ionescu Radu Stefan  //

public class StegoImageActivity extends AppCompatActivity
{
	private FirebaseUser firebaseUser;
	private Timestamp    timestamp;
	private String       userId;
	private byte[]       bitmapData;
	private byte[]       cipherText;
	private byte[]       embeddedBitmapData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stego_image);

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		ImageView ivCoverImage   = findViewById(R.id.ivCoverImage);
		EditText etSecretMessage = findViewById(R.id.etSecretMessage);
		Button   bSendStegoImage = findViewById(R.id.bSendStegoImage);
		Intent   intent          = getIntent();
		userId = intent.getStringExtra("userId");
		Uri    imageUri      = Uri.parse(intent.getStringExtra("imageURI"));

		try
		{
			Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
			if(bitmapImage.getHeight() > 1500 || bitmapImage.getWidth() > 1500)
			{
				ivCoverImage.setImageBitmap(Bitmap.createScaledBitmap(bitmapImage, (int)(bitmapImage.getWidth()*0.3), (int)(bitmapImage.getHeight()*0.3), false));
			}
			else
			{
				ivCoverImage.setImageBitmap(bitmapImage);
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			bitmapData = byteArrayOutputStream.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		bSendStegoImage.setOnClickListener(new View.OnClickListener()
		{
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onClick(View v)
			{
				timestamp = new Timestamp(System.currentTimeMillis());
				String secretMessage = etSecretMessage.getText().toString();

				cipherText = StegoEngine.encrypt(secretMessage, firebaseUser.getUid()).getBytes();
				embeddedBitmapData = StegoEngine.embed(cipherText, ivCoverImage);

				StorageReference storageReference = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com").getReference();
				StorageReference sentImages       = storageReference.child("SentImages/" + firebaseUser.getUid() + timestamp);
				sentImages.putBytes(embeddedBitmapData);

				String message = "SentImages/" + firebaseUser.getUid() + timestamp;
				sendMessage(firebaseUser.getUid(), userId, message);

				Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
				intent.putExtra("userId", userId);
				try
				{
					Thread.sleep(2500);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				startActivity(intent);
			}
		});
	}

	private void sendMessage(String sender, String receiver, String message)
	{
		DatabaseReference       reference = FirebaseDatabase.getInstance().getReference();
		HashMap<String, Object> hashMap   = new HashMap<>();
		hashMap.put("sender", sender);
		hashMap.put("receiver", receiver);
		hashMap.put("message", message);

		reference.child("Chats").push().setValue(hashMap);
	}
}