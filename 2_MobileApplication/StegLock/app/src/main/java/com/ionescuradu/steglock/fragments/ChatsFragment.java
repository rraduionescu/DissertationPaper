package com.ionescuradu.steglock.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.adapters.UserAdapter;
import com.ionescuradu.steglock.classes.Message;
import com.ionescuradu.steglock.classes.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//  Created by Ionescu Radu Stefan  //

public class ChatsFragment extends Fragment
{
	private RecyclerView      recyclerViewChats;
	private UserAdapter       userAdapter;
	private List<User>        users;
	private List<String>      usersList;
	private FirebaseUser      firebaseUser;
	private DatabaseReference databaseReference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_chats, container, false);

		recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
		recyclerViewChats.setHasFixedSize(true);
		recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		usersList = new ArrayList<>();
		databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				usersList.clear();

				for (DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Message message = snapshot.getValue(Message.class);

					if (message.getSender().equals(firebaseUser.getUid()))
					{
						usersList.add(message.getReceiver());
					}
					if (message.getReceiver().equals(firebaseUser.getUid()))
					{
						usersList.add(message.getSender());
					}
				}

				Set<String> set = new HashSet<>(usersList);
				usersList.clear();
				usersList.addAll(set);
				readMessages();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});

		return view;
	}

	private void readMessages()
	{
		users = new ArrayList<>();
		databaseReference = FirebaseDatabase.getInstance().getReference("Users");
		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				users.clear();

				for (DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					User user = snapshot.getValue(User.class);

					for (String id : usersList)
					{
						if (user.getId().equals(id))
						{
							if (users.size() != 0)
							{

								for (User listUser : users)
								{
									if (!user.getId().equals(listUser.getId()))
									{
										users.add(user);
									}
								}
							}
							else
							{
								users.add(user);
							}
						}
					}
				}

				userAdapter = new UserAdapter(getContext(), users);
				recyclerViewChats.setAdapter(userAdapter);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}
}