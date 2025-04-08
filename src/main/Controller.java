/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import ImageRetrieval.ImageRetrieval;
import ImageRetrieval.IndexScore;
import colorStrings.ColorStrings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import modifiedoctree.ModifiedOctreeQuantization;
import octree.OctreeQuantization;
import octreeIncremental.OctreeQuantizationI;

/**
 *
 * @author alvyn
 */
public class Controller {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        OctreeQuantization oct = new OctreeQuantization("test.jpg",254);
//        OctreeQuantizationI oct = new OctreeQuantizationI("test.jpg",16);
//        ModifiedOctreeQuantization oct = new ModifiedOctreeQuantization("test.jpg",16);

        ColorStrings csOld = new ColorStrings(oct.getImage(),oct.getPath());
        System.out.println(csOld.getColorStrings());
        ColorStrings cs = new ColorStrings(oct.getQuantizedImage(),oct.getPath());
        System.out.println(cs.getColorStrings());
        
        ColorStrings[] compareImg=new ColorStrings[4];
        
        
        BufferedImage imgBunga = ImageIO.read(new File("imgdatabase/bunga.jpg"));//get image file
        BufferedImage imgBurung = ImageIO.read(new File("imgdatabase/burung.png"));//get image file
        BufferedImage imgMobil = ImageIO.read(new File("imgdatabase/mobil.png"));//get image file
        BufferedImage imgFrog = ImageIO.read(new File("imgdatabase/frog.png"));//get image file
        
        compareImg[0]=new ColorStrings(imgBunga,"imgdatabase/bunga.jpg" );
        compareImg[1]=new ColorStrings(imgBurung,"imgdatabase/burung.png" );
        compareImg[2]=new ColorStrings(imgMobil,"imgdatabase/mobil.png" );
        compareImg[3]=new ColorStrings(imgFrog,"imgdatabase/frog.png" );

        
        ImageRetrieval ir=new ImageRetrieval(cs,compareImg);
        List<IndexScore> res=ir.getRes();
        for(int i=0;i<4;i++){
            System.out.println("Score: "+res.get(i).getScore()+" "+res.get(i).getImgRep().getPath());
        }
    }
    
}
