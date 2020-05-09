package com.ionescuradu.steglock;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
	public static final int           MSG_TYPE_LEFT  = 0;
	public static final int           MSG_TYPE_RIGHT = 1;
	private             Context       context;
	private             List<Message> messages;
	private             String        img;

	FirebaseUser firebaseUser;

	public MessageAdapter(Context context, List<Message> messages, String img)
	{
		this.img = img;
		this.context = context;
		this.messages = messages;
	}

	@NonNull
	@Override
	public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		if(viewType == MSG_TYPE_RIGHT)
		{
			View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
			return new MessageAdapter.ViewHolder(view);
		}
		View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
		return new MessageAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position)
	{




		User user = users.get(position);

		holder.nickname.setText(user.getNickname());

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
					holder.profilePicture.setImageBitmap(bitmap);
				}
			});
		}

		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, MessageActivity.class);
				intent.putExtra("userId", user.getId());
				context.startActivity(intent);
			}
		});
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
		if(messages.get(position).getSender().equals(firebaseUser.getUid()))
		{
			return MSG_TYPE_RIGHT;
		}
		else
		{
			return MSG_TYPE_LEFT;
		}
	}
}