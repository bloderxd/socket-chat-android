package com.example.bloder.socketchat.message;

/**
 * Created by bloder on 27/12/15.
 */
public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    public final int id;
    public final String message;
    public final String messageUser;

    public Message(int id, String message, String messageUser) {
        this.id = id;
        this.message = message;
        this.messageUser = messageUser;
    }
}
