package org.example.oris_shakurova.view;

import org.example.oris_shakurova.service.CommandProcessor;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class ChatBotView extends BaseView {
    private AnchorPane root;
    private TextArea conversation;
    private TextArea input;

    @Override
    public Parent getView() {
        if (root == null) createView();
        return root;
    }

    public void append(String message) {
        if (message != null) {
            conversation.appendText(message + System.lineSeparator());
        }
    }

    private void createView() {
        root = new AnchorPane();

        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);
        AnchorPane.setTopAnchor(conversation, 10.0);
        AnchorPane.setBottomAnchor(conversation, 70.0);
        AnchorPane.setLeftAnchor(conversation, 10.0);
        AnchorPane.setRightAnchor(conversation, 10.0);

        input = new TextArea();
        input.setMaxHeight(50);
        AnchorPane.setBottomAnchor(input, 10.0);
        AnchorPane.setLeftAnchor(input, 10.0);
        AnchorPane.setRightAnchor(input, 10.0);

        input.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                String userMsg = input.getText().trim();
                if (!userMsg.isEmpty()) {
                    append("Вы: " + userMsg);
                    String reply = CommandProcessor.process(userMsg);

                    if ("QUIT_SIGNAL".equals(reply)) {
                        conversation.clear();
                        input.clear();
                        append("Привет! Введите команду или 'list' для справки.");
                    } else {
                        append("Бот: " + reply);
                        input.clear();
                    }
                    event.consume();
                }
            }
        });

        root.getChildren().addAll(conversation, input);
        append("Привет! Введите команду или 'list' для справки.");
    }
}