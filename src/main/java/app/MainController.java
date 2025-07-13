package app;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
    //window 1: Start menu
    //variables to store values
    private int optionOCQ=0;
    private File queryFile;
    private int clrNum;
    private boolean optionResize=true;
    //fmxl skeleton
    
    @FXML
    private Button chooseFileButton;
    
    @FXML
    private Button btnSet;
    
    @FXML
    private CheckBox resizeCheck;
    
    @FXML
    private ToggleGroup ocqbtn;
    
    @FXML
    private Label chosenFileLabel;

    @FXML
    private StackPane pane;

    @FXML
    private Button startButton;

    @FXML
    private Label title;
    
    @FXML
    private RadioButton rbtn0;

    @FXML
    private RadioButton rbtn1;

    @FXML
    private RadioButton rbtn2;

    @FXML
    private void handleChooseFile(ActionEvent event) {
        //filechooser init
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        //selected file
        File selectedFile = fileChooser.showOpenDialog(stage);
        //check selected file
        if (selectedFile != null) {
//            System.out.println("File selected: " + selectedFile.getAbsolutePath());
            chosenFileLabel.setText(selectedFile.getAbsolutePath());
            queryFile=selectedFile;
        } else {
//            System.out.println("File selection cancelled.");
            chosenFileLabel.setText("File selection cancelled.");
        }
    }
    
    //handle radiobtn
    @FXML
    private void handleOctreeChoice0(ActionEvent event){
        optionOCQ=0;
//        System.out.println("Radio Button OCQ");
    }
    @FXML
    private void handleOctreeChoice1(ActionEvent event){
        optionOCQ=1;
//        System.out.println("Radio Button OCQI");
    }
    @FXML
    private void handleOctreeChoice2(ActionEvent event){
        optionOCQ=2;
//        System.out.println("Radio Button MOCQ");
    }
    
    //handle check
    @FXML
    private void handleCheckIR() {
        if (resizeCheck.isSelected()) {
//            statusLabel.setText("Feature is ON");
            optionResize=true;
        } else {
//            statusLabel.setText("Feature is OFF");
            optionResize=false;
        }
    }
    
    //handle input color number
    @FXML
    private TextField numberField;

    @FXML
    private void initialize() {
        numberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    @FXML
    private void handleNumberInput(ActionEvent event) {
        try {
            int value = Integer.parseInt(numberField.getText());
//            System.out.println("Entered number: " + value);
            clrNum=value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number input");
        }
    }
    
    //go to ocq view
    @FXML
    private void handleMoveToOCQ(ActionEvent event) throws IOException {
        if(this.clrNum!=0){
            OCQController controller = WindowUtils.openWindow("/fxml/OCQView.fxml", "Octree Color Quantization",0);
            if (controller != null) {
                controller.setup(clrNum, queryFile, optionOCQ, optionResize);
            }
            Stage stage = (Stage) this.startButton.getScene().getWindow();
            stage.close();
        }
    }
}