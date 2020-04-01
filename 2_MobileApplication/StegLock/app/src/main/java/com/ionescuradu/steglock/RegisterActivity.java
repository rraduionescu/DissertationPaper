package com.ionescuradu.steglock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ionescuradu.steglock.utilities.TextValidator;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//  Created by Ionescu Radu Stefan  //

public class RegisterActivity extends AppCompatActivity
{
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etR;
    private EditText etL;
    private ImageView ivProfile;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPasswordActivity);
        etConfirmPassword = findViewById(R.id.etPasswordConfirmation);
        etL = findViewById(R.id.etProfile2);
        etR = findViewById(R.id.etProfile);
        ivProfile = findViewById(R.id.ivProfile);
        firebaseAuth = FirebaseAuth.getInstance();

        etL.setClickable(false);
        etR.setClickable(false);
        if (Objects.requireNonNull(ivProfile.getDrawable().getConstantState()).equals(getResources().getDrawable(R.drawable.ic_launcher_foreground, null).getConstantState()))
        {
            etR.setError(getResources().getString(R.string.ppError));
            etR.requestFocus();
        }

        ivProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 2);
            }
        });

        etFirstName.addTextChangedListener(new TextValidator(etFirstName)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                Pattern p = Pattern.compile("[A-Z][a-z]{1,15}?");
                Matcher m = p.matcher(text);
                if (!m.matches())
                {
                    textView.setError(getResources().getString(R.string.fnError));
                }
            }
        });

        etLastName.addTextChangedListener(new TextValidator(etLastName)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                Pattern p = Pattern.compile("[A-Z][a-z]{1,15}?");
                Matcher m = p.matcher(text);
                if (!m.matches())
                {
                    textView.setError(getResources().getString(R.string.lnError));
                }
            }
        });

        etEmail.addTextChangedListener(new TextValidator(etEmail)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                Pattern p = Patterns.EMAIL_ADDRESS;
                Matcher m = p.matcher(text);
                if (!m.matches())
                {
                    textView.setError(getResources().getString(R.string.eError));
                }
            }
        });

        etPassword.addTextChangedListener(new TextValidator(etPassword)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                Pattern p = Pattern.compile("[a-zA-Z0-9]{10,15}");
                Matcher m = p.matcher(text);
                if (!m.matches())
                {
                    textView.setError(getResources().getString(R.string.psError));
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextValidator(etConfirmPassword)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                if (!text.equals(((EditText) findViewById(R.id.etPasswordActivity)).getText().toString()))
                {
                    textView.setError(getResources().getString(R.string.cpsError));
                }
            }
        });

        findViewById(R.id.bRegisterActivity).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (etFirstName.getError() != null ||
                        etLastName.getError() != null ||
                        etEmail.getError() != null ||
                        etPassword.getError() != null ||
                        etConfirmPassword.getError() != null ||
                        etR.getError() != null )
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.tRegFail), Toast.LENGTH_LONG).show();
                }
                else
                {
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    createAccount(email, password);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bitmap bmpImg;
        if (requestCode == 2 && resultCode == RESULT_OK)
        {
            Uri img = data.getData();
            try
            {
                bmpImg = MediaStore.Images.Media.getBitmap(getContentResolver(), img);
                int h = bmpImg.getHeight();
                int w = bmpImg.getWidth();
                float ratio = (float) h / w;
                if (ratio < 0.8 || ratio > 1.2)
                {
                    ((EditText) findViewById(R.id.etProfile)).setError(getResources().getString(R.string.ppError));
                    findViewById(R.id.etProfile).requestFocus();
                }
                else
                {
                    ivProfile.setImageBitmap(Bitmap.createScaledBitmap(bmpImg, 100, 100, false));
                    etR.setError(null);
                    etR.clearFocus();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void createAccount(String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://steglockmapp.appspot.com");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(String.valueOf(etFirstName.getText()) + " " + String.valueOf(etLastName.getText())).build();
                    firebaseUser.updateProfile(profileUpdates);
                    BitmapDrawable drawable = (BitmapDrawable) ivProfile.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] data = byteArrayOutputStream.toByteArray();
                    StorageReference storageReference = storage.getReference();
                    StorageReference profiles = storageReference.child("ProfilePictures/" + firebaseUser.getUid());
                    profiles.putBytes(data);
                    updateUI(firebaseUser);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.tRegFail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {
            Toast.makeText(getApplicationContext(), String.valueOf(((EditText) findViewById(R.id.etFirstName)).getText()) + " " + String.valueOf(((EditText) findViewById(R.id.etLastName)).getText()) + getResources().getString(R.string.tRegSuccess), Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }
}
