package com.bookstore.controller;

import com.bookstore.Main;
import javafx.fxml.FXML;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void handleEnter() throws IOException {
        Main.setRoot("layout");
    }
}
