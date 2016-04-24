package com.example.bloder.socketchat.chat.management;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.bloder.socketchat.BuildConfig;
import com.example.bloder.socketchat.R;
import com.example.bloder.socketchat.message.Message;

import java.util.List;

/**
 * Created by bloder on 22/04/16.
 */
public class Chat {

    private List<Message> messageList;
    private RecyclerView messages;
    private Context context;

    public Chat(Context context, List<Message> messageList, RecyclerView messages) {
        this.context = context;
        this.messageList = messageList;
        this.messages = messages;
    }

    public void addLog(String message) {
        messageList.add(new Message(BuildConfig.TYPE_LOG, message, ""));
        messages.getAdapter().notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    public void addParticipantsLog(int numUsers) {
        addLog(context.getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
        scrollToBottom();
    }

    public void addMessage(String username, String message) {
        messageList.add(new Message(BuildConfig.TYPE_MESSAGE, message, username));
        messages.getAdapter().notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    public void addTyping(String username) {
        messageList.add(new Message(BuildConfig.TYPE_ACTION, "typing...", username));
        messages.getAdapter().notifyItemInserted(messageList.size()-1);
        scrollToBottom();
    }

    public void removeTyping(String username) {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message message = messageList.get(i);
            if (message.id == BuildConfig.TYPE_ACTION && message.messageUser.equals(username)) {
                messageList.remove(i);
                messages.getAdapter().notifyItemRemoved(i);
            }
        }
    }

    public void scrollToBottom() {
        messages.scrollToPosition(messages.getAdapter().getItemCount() - 1);
    }
}
