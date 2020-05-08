package com.ionescuradu.steglock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ChatTabActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_tab);

		TabLayout tabLayout = findViewById(R.id.tabLayout);
		ViewPager viewPager = findViewById(R.id.viewPager);

		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
		viewPagerAdapter.addFragment(new UsersFragment(), "Users");

		viewPager.setAdapter(viewPagerAdapter);

		tabLayout.setupWithViewPager(viewPager);
	}

	class ViewPagerAdapter extends FragmentPagerAdapter
	{
		private ArrayList<Fragment> fragments;
		private ArrayList<String>   titles;

		ViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
			this.fragments = new ArrayList<>();
			this.titles = new ArrayList<>();
		}

		@NonNull
		@Override
		public Fragment getItem(int position)
		{
			return fragments.get(position);
		}

		@Override
		public int getCount()
		{
			return fragments.size();
		}

		public void addFragment(Fragment fragment, String title)
		{
			fragments.add(fragment);
			titles.add(title);
		}

		@Nullable
		@Override
		public CharSequence getPageTitle(int position)
		{
			return titles.get(position);
		}
	}
}
