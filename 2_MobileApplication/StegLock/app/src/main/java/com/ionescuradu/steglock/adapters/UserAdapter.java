package com.ionescuradu.steglock.adapters;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.activities.MessageActivity;
import com.ionescuradu.steglock.classes.User;

import java.util.List;

//  Created by Ionescu Radu Stefan  //

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
	private Context    context;
	private List<User> users;

	public UserAdapter(Context context, List<User> users)
	{
		this.context = context;
		this.users = users;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
		return new UserAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
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
		return users.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public TextView  nickname;
		public ImageView profilePicture;

		public ViewHolder(View view)
		{
			super(view);

			nickname = view.findViewById(R.id.nicknameChat);
			profilePicture = view.findViewById(R.id.profilePicture);
		}
	}
}