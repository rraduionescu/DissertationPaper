package com.ionescuradu.steglock.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

//  Created by Ionescu Radu Stefan  //

public class StegoRecordingActivity extends AppCompatActivity
{
	private static final String                            LOG_TAG                         = "AudioRecording";
	private static final int                               REQUEST_RECORD_AUDIO_PERMISSION = 200;
	private static       String                            fileName                        = null;
	private              MediaRecorder                     recorder                        = null;
	private              StegoRecordingActivity.PlayButton playButton                      = null;
	private              MediaPlayer                       player                          = null;
	private              boolean                           permissionToRecordAccepted      = false;
	private              String[]                          permissions                     = {Manifest.permission.RECORD_AUDIO};
	private              FirebaseUser                      firebaseUser;
	private              String                            userId;
	private              String                            timestamp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stego_recording);

		fileName = getExternalCacheDir().getAbsolutePath();
		fileName += "/recording.3gp";
		ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
		LinearLayout llRecord = findViewById(R.id.llRecord);
		Button       bSend    = findViewById(R.id.bSendStegoRecording);
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		userId = getIntent().getStringExtra("userId");
		timestamp = getIntent().getStringExtra("timestamp");


		bSend.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TO-DO : Steganographic process
				// Encrypt secretMessage String
				// Embed secretMessage cipher into recording file

				try
				{
					StorageReference storageReference = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com").getReference();
					StorageReference sentRecordings   = storageReference.child("SentRecordings/" + firebaseUser.getUid() + timestamp);

					FileInputStream       fis = new FileInputStream(new File(fileName));
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[]                b   = new byte[2048];

					for (int readNum; (readNum = fis.read(b)) != -1; )
					{
						bos.write(b, 0, readNum);
					}

					byte[] bytes = bos.toByteArray();
					sentRecordings.putBytes(bytes);
					Thread.sleep(2500);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				String message = "SentRecordings/" + firebaseUser.getUid() + timestamp;
				sendMessage(firebaseUser.getUid(), userId, message);

				Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		RecordButton              recordButton = new RecordButton(this);
		LinearLayout.LayoutParams rParams      = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		rParams.leftMargin = 8;
		rParams.rightMargin = 8;
		llRecord.addView(recordButton, rParams);

		playButton = new StegoRecordingActivity.PlayButton(this);
		LinearLayout.LayoutParams pParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		pParams.leftMargin = 8;
		pParams.rightMargin = 8;
		llRecord.addView(playButton, pParams);

		ViewGroup.LayoutParams recordParams = recordButton.getLayoutParams();
		recordParams.width = 150;
		recordButton.setLayoutParams(recordParams);
		recordButton.setBackground(getDrawable(R.drawable.bshape));

		ViewGroup.LayoutParams playParams = playButton.getLayoutParams();
		playParams.width = 150;
		playButton.setLayoutParams(playParams);
		playButton.setBackground(getDrawable(R.drawable.bshape));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode)
		{
			case REQUEST_RECORD_AUDIO_PERMISSION:
				permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				break;
		}
		if (!permissionToRecordAccepted)
		{
			finish();
		}

	}

	private void onRecord(boolean start)
	{
		if (start)
		{
			startRecording();
		}
		else
		{
			stopRecording();
		}
	}

	private void onPlay(boolean start)
	{
		if (start)
		{
			startPlaying();
		}
		else
		{
			stopPlaying();
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();
		if (recorder != null)
		{
			recorder.release();
			recorder = null;
		}

		if (player != null)
		{
			player.release();
			player = null;
			playButton.setText(R.string.bStartPlay);
		}
	}

	private void startPlaying()
	{
		player = new MediaPlayer();
		try
		{
			player.setDataSource(fileName);
			player.prepare();
			player.start();
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying()
	{
		player.release();
		player = null;
	}

	private void startRecording()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setOutputFile(fileName);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try
		{
			recorder.prepare();
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "prepare() failed");
		}

		recorder.start();
	}

	private void stopRecording()
	{
		recorder.stop();
		recorder.release();
		recorder = null;
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

	class RecordButton extends androidx.appcompat.widget.AppCompatButton
	{
		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener()
		{
			public void onClick(View v)
			{
				onRecord(mStartRecording);
				if (mStartRecording)
				{
					setText(R.string.bStopRecord);
				}
				else
				{
					setText(R.string.bStartRecord);
				}
				mStartRecording = !mStartRecording;
			}
		};

		public RecordButton(Context ctx)
		{
			super(ctx);
			setText(R.string.bStartRecord);
			setOnClickListener(clicker);
		}
	}

	class PlayButton extends androidx.appcompat.widget.AppCompatButton
	{
		boolean mStartPlaying = true;

		OnClickListener clicker = new OnClickListener()
		{
			public void onClick(View v)
			{
				onPlay(mStartPlaying);
				if (mStartPlaying)
				{
					setText(R.string.bStopPlay);
				}
				else
				{
					setText(R.string.bStartPlay);
				}
				mStartPlaying = !mStartPlaying;
			}
		};

		public PlayButton(Context ctx)
		{
			super(ctx);
			setText(R.string.bStartPlay);
			setOnClickListener(clicker);
		}
	}
}