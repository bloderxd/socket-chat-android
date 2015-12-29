package com.example.bloder.socketchat.message;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bloder.socketchat.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

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
        usernameContent.setTextColor(randomColorGenerator());
        messageContent.setText(message);
    }

    private int randomColorGenerator(){
        Random number = new Random();
        return Color.argb(255, number.nextInt(256), number.nextInt(256), number.nextInt(256));
    }
}
