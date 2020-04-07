package com.agh.wiet.mobilki.chat;

public class ChatMessage {

    private final String nick;
    private final String message;

    public ChatMessage(String nick, String message) {
        this.nick = nick;
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public String getMessage() {
        return message;
    }
}
