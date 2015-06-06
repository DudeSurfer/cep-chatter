package com.swagger.navneeeth99.cepchatter;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
* Created by navneeeth99 on 17/5/15.
*/
public class MessagesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ParseQueryAdapter<PMessage> mUnreadMessagesAdapter = null;
    public static ParseQueryAdapter<PMessage> mReadMessagesAdapter = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MessagesFragment newInstance(int sectionNumber) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        TextView mReadEmpty = (TextView)rootView.findViewById(R.id.readEmptyTV);
        final ListView mReadListView = (ListView)rootView.findViewById(R.id.readLV);
        mReadListView.setEmptyView(mReadEmpty);
        mReadMessagesAdapter = new CustomReadMessageAdapter(getActivity());
        mReadListView.setAdapter(mReadMessagesAdapter);
        mReadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "You have no friends to send a message to :(", Toast.LENGTH_LONG).show();
                PMessage mSelectedMessage = (PMessage)mReadListView.getItemAtPosition(position);
                Log.d("test", mSelectedMessage.getObjectId());
                Log.d("test", mSelectedMessage.get("read").toString());
                ReadMessageDialogFrag mFullMsgDialogFrag = new ReadMessageDialogFrag();
                Bundle args = new Bundle();
                args.putString("sender", mSelectedMessage.getmSender());
                args.putString("title", mSelectedMessage.getmTitle());
                args.putString("msg", mSelectedMessage.getmContent());
                args.putString("id", mSelectedMessage.getObjectId());
                mFullMsgDialogFrag.setArguments(args);
                mFullMsgDialogFrag.show(getActivity().getFragmentManager(), "Show full message");
            }
        });

        TextView mInboxEmpty = (TextView)rootView.findViewById(R.id.inboxEmptyTV);
        final ListView mInboxListView = (ListView)rootView.findViewById(R.id.incomingLV);
        mInboxListView.setEmptyView(mInboxEmpty);
        mUnreadMessagesAdapter = new CustomUnreadMessageAdapter(getActivity());
        mInboxListView.setAdapter(mUnreadMessagesAdapter);
        mInboxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "You have no friends to send a message to :(", Toast.LENGTH_LONG).show();
                PMessage mSelectedMessage = (PMessage)mInboxListView.getItemAtPosition(position);
                Log.d("test", mSelectedMessage.getObjectId());
                Log.d("test", mSelectedMessage.get("read").toString());
                ReadMessageDialogFrag mFullMsgDialogFrag = new ReadMessageDialogFrag();
                Bundle args = new Bundle();
                args.putString("sender", mSelectedMessage.getmSender());
                args.putString("title", mSelectedMessage.getmTitle());
                args.putString("msg", mSelectedMessage.getmContent());
                args.putString("id", mSelectedMessage.getObjectId());
                mFullMsgDialogFrag.setArguments(args);
                mFullMsgDialogFrag.show(getActivity().getFragmentManager(), "Show full message");
            }
        });

        Button mMessageSender = (Button)rootView.findViewById(R.id.sendMessageBT);
        mMessageSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mListSize = ParseUser.getCurrentUser().getList("friends").size();
                Log.d("test", "number of friends: " + mListSize);
                if (mListSize == 0){
                    Toast.makeText(getActivity(), "You have no friends to send a message to :(", Toast.LENGTH_LONG).show();
                } else {
                    DialogFragment mSendMsgDialogFrag = new SendMsgDialogFrag();
                    mSendMsgDialogFrag.show(getActivity().getFragmentManager(), "sendMsg");
                }
            }
        });

        return rootView;
    }

    public static class CustomUnreadMessageAdapter extends ParseQueryAdapter<PMessage> {

        public CustomUnreadMessageAdapter(Context context) {
            super(context, new ParseQueryAdapter.QueryFactory<PMessage>() {
                public ParseQuery<PMessage> create() {
                    ParseQuery<PMessage> query = new ParseQuery<>("PMessage");
                    if (ParseUser.getCurrentUser() != null) {
                        query.whereEqualTo("to", ParseUser.getCurrentUser().getUsername());
                        query.whereEqualTo("read", false);
                    }
                    return query;
                }
            });
        }

        // Customize the layout by overriding getItemView
        @Override
        public View getItemView(PMessage object, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.list_custommessage, null);
            }

            super.getItemView(object, v, parent);

            // Add the title view
            TextView titleTextView = (TextView)v.findViewById(R.id.msgTitleTV);
            titleTextView.setText(object.getString("title"));
            titleTextView.setTextColor(Color.RED);

            // Add a brief of the content
            TextView contentTextView = (TextView)v.findViewById(R.id.msgContentBriefTV);
            contentTextView.setText(object.getString("content"));
            return v;
        }

    }

    public static class CustomReadMessageAdapter extends ParseQueryAdapter<PMessage> {

        public CustomReadMessageAdapter(Context context) {
            super(context, new ParseQueryAdapter.QueryFactory<PMessage>() {
                public ParseQuery<PMessage> create() {
                    ParseQuery<PMessage> query = new ParseQuery<>("PMessage");
                    if (ParseUser.getCurrentUser() != null) {
                        query.whereEqualTo("to", ParseUser.getCurrentUser().getUsername());
                        query.whereEqualTo("read", true);
                    }
                    return query;
                }
            });
        }

        // Customize the layout by overriding getItemView
        @Override
        public View getItemView(PMessage object, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.list_custommessage, null);
            }

            super.getItemView(object, v, parent);

            // Add the title view
            TextView titleTextView = (TextView)v.findViewById(R.id.msgTitleTV);
            titleTextView.setText(object.getString("title"));

            // Add a brief of the content
            TextView contentTextView = (TextView)v.findViewById(R.id.msgContentBriefTV);
            contentTextView.setText(object.getString("content"));
            return v;
        }

    }
}
