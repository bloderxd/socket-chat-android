package com.example.bloder.socketchat.chat.management;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import com.example.bloder.socketchat.message.Message;

import java.util.List;

/**
 * Created by bloder on 22/04/16.
 */
public class Chat implements ChatEvents {

    private List<Message> messageList;
    private RecyclerView messages;

    public Chat(List<Message> messageList, RecyclerView messages) {
        this.messageList = messageList;
        this.messages = messages;
    }

    @UiThread
    @Override
    public void addLog(String message) {

    }

    @Override
    public void addParticipantsLog(int numUsers) {

    }

    @Override
    public void addMessage(String username, String message) {

    }

    @Override
    public void addTyping(String username) {

    }

    @Override
    public void removeTyping(String username) {

    }

    @Override
    public void attemptSend() {

    }
}
