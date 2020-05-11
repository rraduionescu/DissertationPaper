package com.ionescuradu.steglock.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
	private EditText etSearch;

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

		etSearch = view.findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				searchUser(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});

		return view;
	}

	private void searchUser(String s)
	{
		FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("nickname").startAt(s).endAt(s + "\uf8ff");
		query.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				users.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					User user = snapshot.getValue(User.class);

					assert firebaseUser != null;
					assert user != null;
					if(!user.getId().equals(firebaseUser.getUid()))
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

	private void readUsers()
	{
		FirebaseUser      firebaseUser      = FirebaseAuth.getInstance().getCurrentUser();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

		databaseReference.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				if(etSearch.getText().toString().equals(""))
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
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}
}