package com.ionescuradu.steglock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.sql.Timestamp;
import java.util.HashMap;

//  Created by Ionescu Radu Stefan  //

public class StegoImageActivity extends AppCompatActivity
{
	private FirebaseUser firebaseUser;
	private ImageView    ivCoverImage;
	private EditText     etSecretMessage;
	private Button       bSendStegoImage;
	private Intent       intent;
	private String       timestamp;
	private String       secretMessage;
	private String       userId;
	private Bitmap       bitmapImage;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stego_image);

		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		ivCoverImage = findViewById(R.id.ivCoverImage);
		etSecretMessage = findViewById(R.id.etSecretMessage);
		bSendStegoImage = findViewById(R.id.bSendStegoImage);
		intent = getIntent();
		timestamp = intent.getStringExtra("timestamp");
		userId = intent.getStringExtra("userId");
		secretMessage = etSecretMessage.getText().toString();

		FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
		StorageReference reference = storage.getReference().child("SentImages/" + firebaseUser.getUid() + timestamp);
		reference.getBytes(1024 * 1024 * 10).addOnSuccessListener(new OnSuccessListener<byte[]>()
		{
			@Override
			public void onSuccess(byte[] bytes)
			{
				bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				ivCoverImage.setImageBitmap(bitmapImage);
			}
		});

		bSendStegoImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TO-DO : Steganographic process
				// Encrypt secretMessage String
				// Embed secretMessage cipher into bitmapImage
				// Delete original image, upload stego image

				String message = "SentImages/" + firebaseUser.getUid() + timestamp;
				sendMessage(firebaseUser.getUid(), userId, message);

				Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
				intent.putExtra("userId", userId);
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