/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colorStrings;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

/**
 *
 * @author alvyn
 */
public class ColorStrings {
    private BufferedImage image; //stores image
    private String filepath; //stores the filepath of the image 
    private String cs; //stores the color string
    
    //constructor v1 (accepts image as inpur)
    public ColorStrings(String file, String f) throws IOException{
        //set attributes
        this.image=ImageIO.read(new File(file));
        this.filepath=f;
    }
    
    //constructor v2 (accepts txt as input)
    public ColorStrings(String file) throws IOException{
        //set attributes
//        BufferedReader reader = new BufferedReader(new FileReader(file));
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.filepath = reader.readLine();
        this.cs=reader.readLine();
        reader.close();
    }
    
    //creates txt file that can be used by constructor v2
    public void createTxt(String filePath) throws IOException{
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writer.write(this.filepath);
            writer.newLine();
            writer.write(this.cs);
        }
    }
    
    //converts image to color strings
    public void stringify(){
        //loop to add all pixel colors from image to tree
        int rgb,r,g,b;
        String res="";
        for(int i=0;i<this.image.getHeight();i++){
            for(int j=0;j<this.image.getWidth();j++){
                //convert rgb from image to int
                rgb = this.image.getRGB(j, i);
                r= (rgb & 0x00ff0000) >> 16;
                g= (rgb & 0x0000ff00) >> 8;
                b= rgb & 0x000000ff;
                //compare values to the rules to get values
                res+= rules(r,g,b);
            }    
        }
        this.cs=res;//store results
    }
    
    /* Rules:
    1. If R > G > B, then map the pixel with the character 'R';
    2. If R > B > G, then map the pixel with the character 'S';
    3. If G > R > B, then map the pixel with the character 'G';
    4. If G >= B >= R, then map the pixel with the character 'H';
    5. If B >= R >= G, then map the pixel with the character 'B';
    6. If B >= G >= R, then map the pixel with the character 'C';
    7. Else, then map the pixel with the character 'N'; 
    */
    private String rules(int r, int g,int b){
        if(r>g && g>b) return "R";          //1
        else if(r>b && b>g) return "S";     //2
        else if(g>r && r>b) return "G";     //3
        else if(g>=b && b>=r) return "H";   //4
        else if(b>=r && r>=g) return "B";   //5
        else if(b>=g && g>=r) return "C";   //6
        else return "N";                    //7
    }
    
    //returns color strings
    public String getColorStrings(){
        return this.cs;
    }
    
    //returns file path of image
    public String getPath(){
        return this.filepath;
    }
}
