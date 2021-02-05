package com.ionescuradu.steglock.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ionescuradu.steglock.R;

//  Created by Ionescu Radu Stefan  //

public class SecretMessageDialog extends AppCompatDialogFragment
{
	private EditText etDisplaySecretMessage;
	private String   message;
	private Context context;

	public SecretMessageDialog(String message, Context context)
	{
		super();
		this.message = message;
		this.context = context;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		AlertDialog.Builder builder        = new AlertDialog.Builder(getActivity());
		LayoutInflater      layoutInflater = getActivity().getLayoutInflater();
		View                view           = layoutInflater.inflate(R.layout.dialog_secret_message, null);
		etDisplaySecretMessage = view.findViewById(R.id.etDisplaySecretMessage);
		etDisplaySecretMessage.setText(message);

		builder.setView(view).setTitle("Secret message").setNegativeButton(R.string.bCancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dismiss();
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
			InputMethodManager imm  = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
