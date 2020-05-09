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
	public static final int MSG_TYPE_LEFT  = 0;
	public static final int MSG_TYPE_RIGHT = 1;

	private Context       context;
	private List<Message> messages;
	private FirebaseUser  firebaseUser;

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
			return new MessageAdapter.ViewHolder(view);
		}
		else
		{
			View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
			return new MessageAdapter.ViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position)
	{
		Message message = messages.get(position);

		holder.tvMessage.setText(message.getMessage());

		FirebaseUser     user      = FirebaseAuth.getInstance().getCurrentUser();
		FirebaseStorage  storage   = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
		StorageReference reference = null;
		reference = storage.getReference().child("ProfilePictures/" + message.getSender());
		if (reference != null)
		{
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
	}

	@Override
	public int getItemCount()
	{
		return messages.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public TextView  tvMessage;
		public ImageView profilePicture;

		public ViewHolder(View view)
		{
			super(view);

			tvMessage = view.findViewById(R.id.tvChat);
			profilePicture = view.findViewById(R.id.profilePictureMessages);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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