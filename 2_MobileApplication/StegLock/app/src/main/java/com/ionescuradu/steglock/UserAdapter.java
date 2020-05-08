package com.ionescuradu.steglock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{

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
