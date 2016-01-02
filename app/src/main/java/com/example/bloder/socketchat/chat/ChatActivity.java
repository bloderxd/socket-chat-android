package com.example.bloder.socketchat.chat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bloder.socketchat.BuildConfig;
import com.example.bloder.socketchat.R;
import com.example.bloder.socketchat.message.Message;
import com.example.bloder.socketchat.message.MessageAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@EActivity(R.layout.chat_layout)
public class ChatActivity extends AppCompatActivity {

    private static final int TYPING_TIMER_LENGTH = 600;
    private List<Message> messageList = new ArrayList<>();
    private Boolean typing = false;
    private Handler typingHandler = new Handler();

    @ViewById protected RecyclerView messages;
    @ViewById protected EditText messageArea;

    @Extra protected String username;
    @Extra protected int numUsers;

    @AfterViews
    protected void afterViews(){
        setupSocket();
    }

    @Background
    protected void setupSocket() {
        configuringSocket();
        setupAdapter();
    }

    @UiThread
    protected void setupAdapter(){
        LinearLayoutManager messageLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messages.setLayoutManager(messageLayoutManager);
        messages.setAdapter(new MessageAdapter(messageList, getApplicationContext()));
    }

    @Click(R.id.send_message)
    protected void sendMessage(){
        attemptSend();
    }

    private Socket socket;{
        try{
            socket = IO.socket(BuildConfig.CHAT_SERVER_ADRESS);
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @TextChange(R.id.message_area)
    protected void userIsTyping(){
        if(username == null) return;
        if(!socket.connected()) return;

        if(!typing) {
            typing = true;
            socket.emit("typing");
        }

        typingHandler.removeCallbacks(onTypingTimeout);
        typingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("new message", onNewMessage);
        socket.off("user joined", onUserJoined);
        socket.off("user left", onUserLeft);
        socket.off("typing", onTyping);
        socket.off("stop typing", onStopTyping);
    }

    private void configuringSocket() {
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("new message", onNewMessage);
        socket.on("user joined", onUserJoined);
        socket.on("user left", onUserLeft);
        socket.on("typing", onTyping);
        socket.on("stop typing", onStopTyping);
        socket.connect();
    }

    private void addLog(String message) {
        messageList.add(new Message(BuildConfig.TYPE_LOG, message, ""));
        messages.getAdapter().notifyItemInserted(messageList.size()-1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
        scrollToBottom();
    }

    private void addMessage(final String username, final String message) {
        messageList.add(new Message(BuildConfig.TYPE_MESSAGE, message, username));
        messages.getAdapter().notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
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

    private void attemptSend() {
        if (null == username) return;
        if (!socket.connected()) return;

        typing = false;
        String message = messageArea.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            messageArea.requestFocus();
            return;
        }

        messageArea.setText("");
        addMessage(username, message);

        socket.emit("new message", message);
    }

    private void scrollToBottom(){
        messages.scrollToPosition(messages.getAdapter().getItemCount()-1);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    String username;
                    String message;

                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);
                    addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    String username;
                    int numUsers;

                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    String username;

                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }

                    addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    removeTyping(username);
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!typing) return;

            typing = false;
            socket.emit("stop typing");
        }
    };

}
