/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import cbir.ImageRetrieval;
import cbir.IndexScore;
import colorStrings.ColorStrings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author alvyn
 */
public class CBIRController {
    ColorStrings cscQuery;
    File imageQuery;
    
     @FXML
    private Button btnBack;

    @FXML
    private ImageView imgQuery;

    @FXML
    private TextArea listAll;

    @FXML
    private ListView<ImageView> listImgRel;


    //setup the class attributes
    public void setup(File i, ColorStrings cs) throws IOException{
        this.imageQuery=i;
        this.cscQuery=cs;
        checkAndInit();
    }
    
    //list files from database (sumber dari: https://www.baeldung.com/java-list-directory-files )
    public ArrayList<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return (ArrayList<String>) stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .collect(Collectors.toList());
        }
    }
    
    
    @FXML
    private void initialize() {
        //initializze view
    }
    
    private void checkAndInit() throws IOException {
        if(this.cscQuery!=null && this.imageQuery!=null){
            //get all color strings from database

            InputStream listStream = getClass().getClassLoader().getResourceAsStream("imgdatabase/list.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(listStream));
            ColorStrings[] compareImg=new ColorStrings[500];
            String fileName;
            for(int i=0;(fileName = reader.readLine()) != null;i++){
                //using image rep txt version
                compareImg[i]=new ColorStrings("imgdatabase/"+fileName);
            }
            
            //set query image
            this.imgQuery.setImage(new Image(this.imageQuery.toURI().toString(),128,128,false,false));
            
            //image retrieval run
            ImageRetrieval ir=new ImageRetrieval(this.cscQuery,compareImg);
            ir.calculateSimilarity();
            List<IndexScore> res=ir.getRes();
            
            //create imageview for top 20 most relevant
            ArrayList<ImageView> relImg20= new ArrayList<>();
            for(int i=0;i<20;i++){
                if(i>=res.size()) break;
//                relImg20.add(i, new ImageView(new Image(new File(res.get(i).getImgRep().getPath()).toURI().toString(),128,128,false,false)));
                String path = res.get(i).getImgRep().getPath(); // e.g., "imgdatabase/myimg.png"
                URL imageUrl = getClass().getClassLoader().getResource(path);
                if (imageUrl != null) {
                    Image img = new Image(imageUrl.toExternalForm(), 128, 128, false, false);
                    relImg20.add(i, new ImageView(img));
                } else {
                    System.err.println("Image not found: " + path);
                }
            }
            
            ObservableList<ImageView> contentImgRel = FXCollections.observableArrayList(relImg20);
            this.listImgRel.setItems(contentImgRel);
            
            //list all simmilarity and filepaths
            String allRes="";
            for(int i=0;i<res.size();i++){
                allRes+=(i+1)+") Sim: "+res.get(i).getScore()+" || Path: "+res.get(i).getImgRep().getPath()+"\n";
            }
            this.listAll.setText(allRes);
            
            //write log for experimaent
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("Explog.txt"))){
                for(int i=0;i<20;i++){
                    writer.write((i+1)+". "+Paths.get(res.get(i).getImgRep().getPath()).getFileName()+"\n");
                }
            }
            
            //write log
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("CBIRlog.txt"))){
                for(int i=0;i<res.size();i++){
                    writer.write(res.get(i).getImgRep().getPath()+"\n");
                }
            }
            
        }
        else{//initialization failed
            System.out.print("failed to receive input");
        }
    }
    
    //go back to main view
    @FXML
    private void handleMoveToMain(ActionEvent event) throws IOException {
        WindowUtils.openWindow("/fxml/MainView.fxml", "Octree Color Quantization CBIR");
        Stage stage = (Stage) this.btnBack.getScene().getWindow();
        stage.close();
    }
}
