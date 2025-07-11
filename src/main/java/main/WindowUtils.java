/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author alvyn
 */
public class WindowUtils {
    public static void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static <T> T openWindow(String fxmlPath, String title,int confirm) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            return loader.getController();  // This returns the controller instance
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

