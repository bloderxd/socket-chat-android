package com.example.bloder.socketchat.chat.management;

/**
 * Created by bloder on 22/04/16.
 */
public interface ChatEvents {

    void addLog(String message);
    void addParticipantsLog(int numUsers);
    void addMessage(final String username, final String message);
    void addTyping(String username);
    void removeTyping(String username);
    void scrollToBottom();
}
