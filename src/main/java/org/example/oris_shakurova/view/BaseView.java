package org.example.oris_shakurova.view;

import org.example.oris_shakurova.ChatBotApplication;
import javafx.scene.Parent;

public abstract class BaseView {
    private static ChatBotApplication chatApplication;

    public static void setChatApplication(ChatBotApplication app) {
        BaseView.chatApplication = app;
    }

    public static ChatBotApplication getChatApplication() {
        if (chatApplication == null) {
            throw new IllegalStateException("ChatApplication not initialized");
        }
        return chatApplication;
    }

    public abstract Parent getView();
}
