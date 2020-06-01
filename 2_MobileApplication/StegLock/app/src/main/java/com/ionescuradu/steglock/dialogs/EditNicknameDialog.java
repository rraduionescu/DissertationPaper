package com.ionescuradu.steglock.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ionescuradu.steglock.R;
import com.ionescuradu.steglock.utilities.TextValidator;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//  Created by Ionescu Radu Stefan  //

public class EditNicknameDialog extends AppCompatDialogFragment
{
	private EditText etNickname;

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		AlertDialog.Builder builder        = new AlertDialog.Builder(getActivity());
		LayoutInflater      layoutInflater = getActivity().getLayoutInflater();
		View                view           = layoutInflater.inflate(R.layout.dialog_edit_nickname, null);
		etNickname = view.findViewById(R.id.etEditNickname);
		etNickname.setError(getResources().getString(R.string.nnError));

		builder.setView(view).setTitle("Edit nickname").setPositiveButton(R.string.bEditNickname, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				FirebaseUser            firebaseUser      = FirebaseAuth.getInstance().getCurrentUser();
				DatabaseReference       databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid() + "/nickname");
				if(etNickname.getError() == null)
				{
					databaseReference.setValue(etNickname.getText().toString());

				}
			}
		}).setNegativeButton(R.string.bCancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dismiss();
			}
		});

		etNickname.setHint(R.string.etEditNickname);
		etNickname.addTextChangedListener(new TextValidator(etNickname)
		{
			@Override
			public void validate(TextView textView, String text)
			{
				Pattern pattern = Pattern.compile("[a-z0-9._]{1,15}?");
				Matcher matcher = pattern.matcher(text);
				if (!matcher.matches())
				{
					textView.setError(getResources().getString(R.string.nnError));
				}
			}
		});

		return builder.create();
	}

	@Override
	public void onDismiss(@NonNull DialogInterface dialog)
	{
		super.onDismiss(dialog);

		try
		{

			View               view = getActivity().getCurrentFocus();
			InputMethodManager imm  = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}