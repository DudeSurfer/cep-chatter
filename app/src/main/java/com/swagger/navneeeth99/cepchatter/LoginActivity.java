package com.swagger.navneeeth99.cepchatter;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mLoginButton = (Button)findViewById(R.id.loginBT);
        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null){
            ParseUser.logOut();
        }

        final RelativeLayout mLoginLoadingOverlay = (RelativeLayout)findViewById(R.id.login_progress);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginLoadingOverlay.setVisibility(View.VISIBLE);
                String usrName = ((EditText)findViewById(R.id.idnameET)).getText().toString();
                String password = ((EditText)findViewById(R.id.passwordET)).getText().toString();

                ParseUser.logInInBackground(usrName, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            mLoginLoadingOverlay.setVisibility(View.GONE);
                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            mLoginLoadingOverlay.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        Button mSignUpButton = (Button) findViewById(R.id.newSignupBT);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        Button mResetPasswordButton = (Button)findViewById(R.id.resetPasswordButton);

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment mResetPasswordDialogFrag = new ResetPasswordDialogFragment();
                mResetPasswordDialogFrag.show(getFragmentManager(), "Reset Password");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
