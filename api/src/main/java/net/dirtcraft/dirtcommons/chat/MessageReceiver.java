package net.dirtcraft.dirtcommons.chat;

public interface MessageReceiver<T> {
    void sendChatMessage(T message);
    void sendFormattedChatMessage(String message);
    void sendPlainChatMessage(String message);

    void sendNotification(T message);
    void sendFormattedNotification(String message);
    void sendPlainNotification(String message);




}
