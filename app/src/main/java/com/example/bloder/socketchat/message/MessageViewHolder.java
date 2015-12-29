package com.example.bloder.socketchat.message;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bloder.socketchat.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by bloder on 28/12/15.
 */
@EViewGroup(R.layout.layout_message)
public class MessageViewHolder extends LinearLayout {

    @ViewById protected TextView usernameContent;
    @ViewById protected TextView messageContent;

    public MessageViewHolder(Context context) {
        super(context);
    }

    public void bind(String username, String message) {
        usernameContent.setText(username);
        messageContent.setText(message);
    }
}
