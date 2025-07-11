package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        WindowUtils.openWindow("/fxml/MainView.fxml", "Octree Color Quantization CBIR");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
