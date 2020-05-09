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
import com.ionescuradu.steglock.classes.User;
import com.ionescuradu.steglock.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;

//  Created by Ionescu Radu Stefan  //

public class UsersFragment extends Fragment
{
	private RecyclerView recyclerView;
	private UserAdapter  userAdapter;
	private List<User>   users;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_users, container, false);

		recyclerView = view.findViewById(R.id.recyclerViewUsers);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		users = new ArrayList<>();

		readUsers();

		return view;
	}

	private void readUsers()
	{
		FirebaseUser      firebaseUser      = FirebaseAuth.getInstance().getCurrentUser();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				users.clear();
				for (DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					User user = snapshot.getValue(User.class);
					if (!user.getId().equals(firebaseUser.getUid()))
					{
						users.add(user);
					}
				}

				userAdapter = new UserAdapter(getContext(), users);
				recyclerView.setAdapter(userAdapter);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}
}