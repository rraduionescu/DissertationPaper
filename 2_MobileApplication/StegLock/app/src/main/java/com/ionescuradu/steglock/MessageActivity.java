package com.ionescuradu.steglock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity
{
	CircleImageView   profilePicture;
	TextView          nickname;
	FirebaseUser      firebaseUser;
	DatabaseReference databaseReference;
	ImageButton       bSend;
	EditText          etMessage;
	Intent            intent;
	MessageAdapter    messageAdapter;
	List<Message>     messages;
	RecyclerView      recyclerViewMessages;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

		Toolbar toolbar = findViewById(R.id.chatToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		recyclerViewMessages = findViewById(R.id.recyclerMessages);
		recyclerViewMessages.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
		linearLayoutManager.setStackFromEnd(true);
		recyclerViewMessages.setLayoutManager(linearLayoutManager);

		intent = getIntent();
		String userId = intent.getStringExtra("userId");
		profilePicture = findViewById(R.id.profilePictureChat);
		nickname = findViewById(R.id.nicknameChat);
		bSend = findViewById(R.id.bSend);
		etMessage = findViewById(R.id.etMessage);
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);


		bSend.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String message = etMessage.getText().toString();
				if (!message.equals(""))
				{
					sendMessage(firebaseUser.getUid(), userId, message);
				}
				else
				{
					Toast.makeText(MessageActivity.this, R.string.msgError, Toast.LENGTH_SHORT);
				}
				etMessage.setText("");
			}
		});

		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				User user = dataSnapshot.getValue(User.class);

				nickname.setText(user.getNickname());

				FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
				StorageReference reference = null;
				reference = storage.getReference().child("ProfilePictures/" + user.getId());
				if (reference != null)
				{
					reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
					{
						@Override
						public void onSuccess(byte[] bytes)
						{
							Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
							profilePicture.setImageBitmap(bitmap);
						}
					});
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}

	private void sendMessage(String sender, String receiver, String message)
	{
		DatabaseReference       reference = FirebaseDatabase.getInstance().getReference();
		HashMap<String, Object> hashmap   = new HashMap<>();
		hashmap.put("sender", sender);
		hashmap.put("receiver", receiver);
		hashmap.put("message", message);

		reference.child("Chats").push().setValue(hashmap);
	}

	private void readMesage(String myId, String userId, String message, String img)
	{
		messages = new ArrayList<>();
		databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				messages.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Message message1 = snapshot.getValue(Message.class);
					if(message1.getReceiver().equals(myId) && message1.getSender().equals(userId) || message1.getReceiver().equals(userId) && message1.getSender().equals(myId))
					{

					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		})
	}
}
