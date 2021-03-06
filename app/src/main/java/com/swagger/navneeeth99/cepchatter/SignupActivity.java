package com.swagger.navneeeth99.cepchatter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SignupActivity extends ActionBarActivity {
    public static final int GET_FROM_GALLERY = 555;
    public byte[] image = null;
    public ParseFile file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ImageButton mAddProfPicButton = (ImageButton)findViewById(R.id.addProfPicButton);
        mAddProfPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        final RelativeLayout mSignupLoadingOverlay = (RelativeLayout)findViewById(R.id.signup_progress);

        Button mSignUpButton = (Button)findViewById(R.id.signupBT);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText)findViewById(R.id.newidnameET)).getText().toString() == null){
                    ((EditText)findViewById(R.id.newidnameET)).setError("Name field cannot be empty!");
                } else if (((EditText) findViewById(R.id.newpasswordET)).getText().toString() == null){
                    ((EditText) findViewById(R.id.newpasswordET)).setError("Password field cannot be empty!");
                } else if (((EditText) findViewById(R.id.newEmailET)).getText().toString() == null){
                    ((EditText) findViewById(R.id.newEmailET)).setError("Email field cannot be empty!");
                } else if (!((EditText) findViewById(R.id.newpasswordET)).getText().toString().equals(((EditText) findViewById(R.id.confirmpasswordET)).getText().toString())){
                    ((EditText) findViewById(R.id.confirmpasswordET)).setError("Passwords do not match!");
                }  else if (file == null) {
                    Toast.makeText(SignupActivity.this, "Please set a profile picture!", Toast.LENGTH_LONG).show();
                } else {
                    mSignupLoadingOverlay.setVisibility(View.VISIBLE);
                    ParseUser user = new ParseUser();
                    user.setUsername(((EditText) findViewById(R.id.newidnameET)).getText().toString());
                    user.setPassword(((EditText) findViewById(R.id.newpasswordET)).getText().toString());
                    user.setEmail(((EditText) findViewById(R.id.newEmailET)).getText().toString());
                    ArrayList mFriendsList = new ArrayList();
                    user.put("friends", mFriendsList);
                    user.put("photo",file);
                    user.put("status", "");

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                mSignupLoadingOverlay.setVisibility(View.GONE);
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                Toast.makeText(SignupActivity.this, "Successfully signed up!", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                mSignupLoadingOverlay.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, "Error: "+ e.toString(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = stream.toByteArray();
                file = new ParseFile("profile_picture.jpeg", image);
                file.saveInBackground();
//                ParseUser.getCurrentUser().put("photo", file);
//                ParseUser.getCurrentUser().saveInBackground();
                ((ImageButton)findViewById(R.id.addProfPicButton)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }




        }
    }
}
