/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colorStrings;

import java.awt.image.BufferedImage;

/**
 *
 * @author alvyn
 */
public class ColorStrings {
    private BufferedImage image;
    private String filepath;
    private String cs;
    public ColorStrings(BufferedImage img, String f){
        this.image=img;
        this.filepath=f;
        stringify();
    }
    private void stringify(){
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
                res+= rules(r,g,b);
            }    
        }
        this.cs=res;
    }
    
    /* Rules:
    1. If R > G > B, then map the pixel with the character ‘R’;
    2. If R > B > G, then map the pixel with the character ‘S’;
    3. If G > R > B, then map the pixel with the character ‘G’;
    4. If G >= B >= R, then map the pixel with the character ‘H’;
    5. If B >= R >= G, then map the pixel with the character ‘B’;
    6. If B >= G >= R, then map the pixel with the character ‘C’;
    7. Else, then map the pixel with the character ‘N’; 
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
    public String getColorStrings(){
        return this.cs;
    }
    
    public String getPath(){
        return this.filepath;
    }
}
