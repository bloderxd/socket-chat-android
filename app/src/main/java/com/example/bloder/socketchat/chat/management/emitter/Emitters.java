package com.example.bloder.socketchat.chat.management.emitter;

import android.app.Activity;
import android.widget.Toast;

import com.example.bloder.socketchat.R;
import com.example.bloder.socketchat.chat.management.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by bloder on 24/04/16.
 */
public class Emitters {

    private Activity context;
    private Chat chat;

    public Emitters(Activity context, Chat chat) {
        this.context = context;
        this.chat = chat;
    }

    public Emitter.Listener onConnectError() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context , R.string.error_connect, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
    }

    public Emitter.Listener onNewMessage() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
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

                        chat.removeTyping(username);
                        chat.addMessage(username, message);
                    }
                });
            }
        };
    }

    public Emitter.Listener onUserJoined() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
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

                        chat.addLog(context.getResources().getString(R.string.message_user_joined, username));
                        chat.addParticipantsLog(numUsers);
                    }
                });
            }
        };
    }

    public Emitter.Listener onUserLeft() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
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

                        chat.addLog(context.getResources().getString(R.string.message_user_left, username));
                        chat.addParticipantsLog(numUsers);
                        chat.removeTyping(username);
                    }
                });
            }
        };
    }

    public Emitter.Listener onTyping() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];

                        String username;

                        try {
                            username = data.getString("username");
                        } catch (JSONException e) {
                            return;
                        }

                        chat.addTyping(username);
                    }
                });
            }
        };
    }

    public Emitter.Listener onStopTyping() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        try {
                            username = data.getString("username");
                        } catch (JSONException e) {
                            return;
                        }
                        chat.removeTyping(username);
                    }
                });
            }
        };
    }

}
