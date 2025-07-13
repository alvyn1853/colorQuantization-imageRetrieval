/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import colorStrings.ColorStrings;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import octree.ModifiedOctreeQuantization;
import octree.OctreeQuantization;
import octree.OctreeQuantizationIncremental;

/**
 *
 * @author alvyn
 */
public class OCQController {
    //window 2 cloro quantization
    private int target;
    private File query,queryOriginal;
    private Integer type;
    private boolean useForIR;
    private ColorStrings cs;
    
    //setup values to display
    public void setup(int i, File f, int t, boolean b) throws IOException{
        this.target=i;
        this.queryOriginal=f;
        this.type=t;
        this.useForIR=b;
        checkAndInit();
    }
    
    //javafx skeleton
    @FXML
    private TextArea colorStringViewer;
    
    @FXML
    private ImageView imgIn;

    @FXML
    private ImageView imgOut;

    @FXML
    private Label lblWarnaAsli;

    @FXML
    private Label lblWarnaHasil;

    @FXML
    private Label lblWarnaTarget;

    @FXML
    private Label lblWktKuantisasi;

    @FXML
    private Label lblWktString;

    @FXML
    private ImageView palleteIn;

    @FXML
    private ImageView palleteOut;
    
     @FXML
    private Button btnBack;

    @FXML
    private Button btnIR;
    
    private void checkAndInit() throws IOException {
//        System.out.println("checkAndInit called");
//        System.out.println("target=" + target);
//        System.out.println("queryOriginal=" + queryOriginal);
//        System.out.println("type=" + type);
        if (this.target != 0 && this.queryOriginal != null && this.type != null) {
//            System.out.println("All inputs are valid, initializing...");
            int clrIn,clrOut;//amount of colors in the pallete
            String filepathOfQuery=this.queryOriginal.getPath();

            //check if the image will be resized for IR or not
            if(this.useForIR==false){
                this.query=this.queryOriginal;
                this.btnIR.setDisable(true);
            }
            else{//create resized image 32*32 for IR
                //image resize
                BufferedImage origin= ImageIO.read(this.queryOriginal);
                if(origin.getWidth()==32&&origin.getHeight()==32){
                    this.query=this.queryOriginal;
                }
                else{
                    BufferedImage resize=new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = resize.createGraphics();
                    graphics2D.drawImage(origin, 0, 0, 32, 32, null);
                    graphics2D.dispose();
                    //create file
                    File resizedImgFile = new File("queryResize.png");
                    ImageIO.write(resize, "png", resizedImgFile);
                    this.query=resizedImgFile;
                }
            }

            //start measuring time
            long startTimeOct = System.nanoTime();
            long endTimeOct;
            //use quantization based on choice
            if(this.type==0){
                /*
                    normal octree
                */
                OctreeQuantization oct = new OctreeQuantization(this.query,this.target);
                //print pallete
                clrIn=oct.printPallete("palleteIn.png");
                //quantisize color
                oct.quantize();
                clrOut=oct.printPallete("palleteOut.png");
                //reconctruct image
                oct.processOutputImg();
                endTimeOct = System.nanoTime();//process finishes
                //print output
                oct.printCQImg("output.png");
            }
            else if(this.type==1){
                /*
                    incremental octree
                */
                OctreeQuantizationIncremental oct = new OctreeQuantizationIncremental(this.query,this.target);
                //pallete
                clrIn=oct.printOldPallete("palleteIn.png");
                clrOut=oct.printPallete("palleteOut.png");
                //reconstruct image
                oct.processOutputImg();
                endTimeOct = System.nanoTime();//process finishes
                //print image
                oct.printCQImg("output.png");
            }
            else{
                /*
                    modified octree
                */
                ModifiedOctreeQuantization oct = new ModifiedOctreeQuantization(this.query,this.target);
                //set color
                oct.setColorPallete();
                //palletr
                clrIn=oct.printOldPallete("palleteIn.png");
                //quantisize color
                oct.quantizeM();
                //pallete
                clrOut=oct.printPallete("palleteOut.png");
                //reconstruct image
                oct.processOutputImg();
                endTimeOct = System.nanoTime();//process finishes
                //printfile
                oct.printCQImg("output.png");
            }
            //octree color quantization durtaion in ms
            long durationOct = (endTimeOct - startTimeOct)/1000000;

            //put images into the stage
            this.imgIn.setImage(new Image(this.query.toURI().toString(),this.imgIn.getFitWidth(),this.imgIn.getFitHeight(),false,false));
            this.imgOut.setImage(new Image(new File("output.png").toURI().toString(),this.imgOut.getFitWidth(),this.imgOut.getFitHeight(),false,false));
            this.palleteIn.setImage(new Image(new File("palleteIn.png").toURI().toString(),this.palleteIn.getFitWidth(),this.palleteIn.getFitHeight(),false,false));
            this.palleteOut.setImage(new Image(new File("palleteOut.png").toURI().toString(),this.palleteOut.getFitWidth(),this.palleteOut.getFitHeight(),false,false));

            //color strings
            long startTimeCsc = System.nanoTime();//start time csc
            
            //membuat colorstrings menggunakan hasil kuantisasi, filepath menunjuk image asli 
            this.cs= new ColorStrings("output.png",filepathOfQuery);

            this.cs.stringify();//create the strings
            long endTimeCsc = System.nanoTime();//end time csc
            long durationCsc = (endTimeCsc - startTimeCsc)/1000000;//duration in ms

            //set textarea
            this.colorStringViewer.setText(this.cs.getColorStrings());
            //set labels
            this.lblWarnaAsli.setText("Jumlah Warna Asli: "+clrIn);
            this.lblWarnaTarget.setText("Jumlah Warna Target: "+this.target);
            this.lblWarnaHasil.setText("Jumlah Warna Hasil: "+clrOut);
            this.lblWktKuantisasi.setText("Waktu Kuantisasi: "+durationOct+" ms");
            this.lblWktString.setText("Waktu String Coding: "+durationCsc+" ms");
        }
        else{
            System.out.println("Initialization Failed.");
        }
        
    }
    
    @FXML
    private void initialize(){
        //initializze view
    }
    
    //go to ocq view
    @FXML
    private void handleMoveToIR(ActionEvent event) throws IOException {
        CBIRController controller = WindowUtils.openWindow("/fxml/CBIRView.fxml", "Content Based Image Retrieval",0);
        if (controller != null) {
            controller.setup(this.queryOriginal,this.cs);
        }
        Stage stage = (Stage) this.btnIR.getScene().getWindow();
        stage.close();
    }
    
    //go back to main view
    @FXML
    private void handleMoveToMain(ActionEvent event) throws IOException {
        WindowUtils.openWindow("/fxml/MainView.fxml", "Octree Color Quantization CBIR");
        Stage stage = (Stage) this.btnBack.getScene().getWindow();
        stage.close();
    }
    
}
