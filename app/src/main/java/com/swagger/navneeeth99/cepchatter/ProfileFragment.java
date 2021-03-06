package com.swagger.navneeeth99.cepchatter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ProfileFragment extends Fragment {
    public static final int GET_FROM_GALLERY = 555;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public View rootView = null;
    public byte[] image = null;

    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageButton mChangeProfPicButton = (ImageButton)rootView.findViewById(R.id.changeProfPicButton);
        mChangeProfPicButton.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams mParams;
                mParams = (LinearLayout.LayoutParams) mChangeProfPicButton.getLayoutParams();
                mParams.height = mChangeProfPicButton.getWidth();
                mChangeProfPicButton.setLayoutParams(mParams);
                mChangeProfPicButton.postInvalidate();
            }
        });

        final RelativeLayout mLoadingOverlay = (RelativeLayout)rootView.findViewById(R.id.profile_progress);
        mLoadingOverlay.setVisibility(View.VISIBLE);

        final TextView mUsernameTV = (TextView)rootView.findViewById(R.id.profileUsernameTV);
        final TextView mStatusTV = (TextView)rootView.findViewById(R.id.profileStatusTV);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                ParseFile fileObject = (ParseFile)parseUser.get("photo");
                if (fileObject == null){
                    ((ImageButton) rootView.findViewById(R.id.changeProfPicButton)).setImageResource(R.drawable.emptydp);
                } else {
                    fileObject.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ((ImageButton) rootView.findViewById(R.id.changeProfPicButton)).setImageBitmap(bmp);
                        }
                    });
                }
                mUsernameTV.setText(parseUser.getUsername());
                if (parseUser.get("status") == null){
                    mStatusTV.setText("~ NO STATUS ~");
                } else if (parseUser.get("status").equals("")){
                    mStatusTV.setText("~ NO STATUS ~");
                } else {
                    mStatusTV.setText(parseUser.get("status").toString());
                }
                mLoadingOverlay.setVisibility(View.GONE);
            }
        });

        mChangeProfPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        mStatusTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusSetterDialogFrag statusDF = new StatusSetterDialogFrag();
                statusDF.show(getActivity().getFragmentManager(), "changingStatus");
            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = stream.toByteArray();
                ParseFile file;
                file = new ParseFile("profile_picture.jpeg", image);

                file.saveInBackground();
                ParseUser.getCurrentUser().put("photo", file);
                ParseUser.getCurrentUser().saveInBackground();
                ((ImageButton)rootView.findViewById(R.id.changeProfPicButton)).setImageBitmap(bitmap);
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
