package org.example.oris_shakurova;

import org.example.oris_shakurova.view.BaseView;
import org.example.oris_shakurova.view.ChatBotView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatBotApplication extends Application {
    private ChatBotView chatView;
    private BorderPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Чат-бот (локальный)");
        stage.setOnCloseRequest(e -> System.exit(0));
        BaseView.setChatApplication(this);

        chatView = new ChatBotView();
        root = new BorderPane();
        root.setCenter(chatView.getView());

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();

    }

    public void appendMessage(String message) {
        chatView.append(message);
    }
}
