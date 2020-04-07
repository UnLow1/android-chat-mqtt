package com.agh.wiet.mobilki.chat;

class ChatMessage {

    private final String nick;
    private final String message;

    ChatMessage(String nick, String message) {
        this.nick = nick;
        this.message = message;
    }

    String getNick() {
        return nick;
    }

    String getMessage() {
        return message;
    }
}
