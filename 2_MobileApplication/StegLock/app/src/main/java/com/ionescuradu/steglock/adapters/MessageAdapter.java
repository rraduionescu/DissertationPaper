package com.ionescuradu.steglock.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.classes.Message;

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
				holder.tvMessage.setVisibility(View.GONE);
				holder.ivChat.setVisibility(View.VISIBLE);
				FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
				StorageReference reference = storage.getReference().child(message.getMessage());
				reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
				{
					@Override
					public void onSuccess(byte[] bytes)
					{
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						holder.ivChat.setImageBitmap(bitmap);
					}
				});
			}
			else if (message.getMessage().substring(0, 14).compareTo("SentRecordings/") == 0)
			{
				holder.tvMessage.setText("");
			}
		}
		else if (message.getMessage().length() > 4 && message.getMessage().length() < 35)
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
}