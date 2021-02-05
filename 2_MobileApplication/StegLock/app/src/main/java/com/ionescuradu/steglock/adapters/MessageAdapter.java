package com.ionescuradu.steglock.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.classes.Message;
import com.ionescuradu.steglock.dialogs.EditNicknameDialog;
import com.ionescuradu.steglock.dialogs.SecretMessageDialog;
import com.ionescuradu.steglock.steganography.StegoEngine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

//  Created by Ionescu Radu Stefan  //

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
	private static final int MSG_TYPE_LEFT  = 0;
	private static final int MSG_TYPE_RIGHT = 1;

	private Context       context;
	private List<Message> messages;

	public MessageAdapter(Context context, List<Message> messages)
	{
		this.context = context;
		this.messages = messages;
	}

	@NonNull
	@Override
	public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		if (viewType == MSG_TYPE_RIGHT)
		{
			View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
			return new ViewHolder(view);
		}
		else
		{
			View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
			return new ViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position)
	{
		Message message = messages.get(position);

		if (message.getMessage().length() > 35)
		{
			if (message.getMessage().substring(0, 5).compareToIgnoreCase("SentI") == 0)
			{
				String uid = message.getSender();
				holder.tvMessage.setVisibility(View.GONE);
				holder.ivChat.setVisibility(View.VISIBLE);
				FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
				StorageReference reference = storage.getReference().child(message.getMessage());
				reference.getBytes((int) (1024 * 1024 * 8)).addOnSuccessListener(new OnSuccessListener<byte[]>()
				{
					@RequiresApi(api = Build.VERSION_CODES.O)
					@Override
					public void onSuccess(byte[] bytes)
					{
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						holder.ivChat.setImageBitmap(bitmap);
						String message = StegoEngine.extract(holder.ivChat, uid);
						holder.ivChat.setOnLongClickListener(new View.OnLongClickListener()
						{
							@Override
							public boolean onLongClick(View v)
							{
								openDialog(message);
								return false;
							}
						});
					}
				});
			}
			else if (message.getMessage().substring(0, 5).compareTo("SentR") == 0)
			{
				final String fileName = context.getExternalCacheDir().getAbsolutePath() + "/" + message.toString();
				holder.tvMessage.setVisibility(View.GONE);
				holder.ivChat.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) holder.ivChat.getLayoutParams();
				ivParams.width = 150;
				ivParams.height = 150;
				holder.ivChat.setLayoutParams(ivParams);
				holder.ivChat.setImageDrawable(context.getDrawable(R.drawable.ic_play));
				FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
				StorageReference reference = storage.getReference().child(message.getMessage());
				reference.getBytes(1024 * 256).addOnSuccessListener(new OnSuccessListener<byte[]>()
				{
					@RequiresApi(api = Build.VERSION_CODES.O)
					@Override
					public void onSuccess(byte[] bytes)
					{
						try
						{
							/*byte[] length = new byte[4];
							length[0] = bytes[0];
							length[1] = bytes[1];
							length[2] = bytes[2];
							length[3] = bytes[3];
							ByteBuffer       wrapped      = ByteBuffer.wrap(length);
							int              num          = wrapped.getInt();
							byte[]           recording    = Arrays.copyOfRange(bytes, 4 + num, bytes.length);*/
							FileOutputStream outputStream = new FileOutputStream(new File(fileName));
							/*byte[]           cipher       = Arrays.copyOfRange(bytes, 4, 4 + num);
							String secret = StegoEngine.decrypt(new String(cipher), message.getSender());*/
							holder.ivChat.setOnLongClickListener(new View.OnLongClickListener()
							{
								@Override
								public boolean onLongClick(View v)
								{
									openDialog("test");
									return false;
								}
							});
							outputStream.write(bytes);
							//outputStream.write(recording);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				holder.ivChat.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						MediaPlayer player = new MediaPlayer();
						try
						{
							player.setDataSource(fileName);
							player.prepare();
							player.start();
						}
						catch (IOException e)
						{
							e.printStackTrace();
							Log.e("PLAYER", "prepare() failed");
						}
					}
				});
			}
		}
		else if (message.getMessage().length() > 4 && message.getMessage().length() < 80)
		{
			if (message.getMessage().substring(0, 4).compareTo("Sent") != 0)
			{
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.ivChat.setVisibility(View.GONE);
				holder.tvMessage.setText(message.getMessage());
			}
		}
		else
		{
			holder.tvMessage.setVisibility(View.VISIBLE);
			holder.ivChat.setVisibility(View.GONE);
			holder.tvMessage.setText(message.getMessage());
		}

		FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
		StorageReference reference = storage.getReference().child("ProfilePictures/" + message.getSender());
		reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
		{
			@Override
			public void onSuccess(byte[] bytes)
			{
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				holder.profilePicture.setImageBitmap(bitmap);
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return messages.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		TextView  tvMessage;
		ImageView ivChat;
		ImageView profilePicture;

		ViewHolder(View view)
		{
			super(view);

			tvMessage = view.findViewById(R.id.tvChat);
			ivChat = view.findViewById(R.id.ivChat);
			profilePicture = view.findViewById(R.id.profilePictureMessages);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		assert firebaseUser != null;
		if (messages.get(position).getSender().equals(firebaseUser.getUid()))
		{
			return MSG_TYPE_RIGHT;
		}
		else
		{
			return MSG_TYPE_LEFT;
		}
	}

	public void openDialog(String message)
	{
		SecretMessageDialog secretMessageDialog = new SecretMessageDialog(message, context);
		secretMessageDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog");
	}
}