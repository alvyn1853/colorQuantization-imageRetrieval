/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import java.io.File;
import java.io.IOException;
import octree.OctreeQuantization;

/**
 *
 * @author alvyn
 */
public class Controller {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File file = new File("test.jpg");
//        BufferedImage image = ImageIO.read(file);
//        int rgb = image.getRGB(0, 0);
//        int red= (rgb & 0x00ff0000) >> 16;
//        int green= (rgb & 0x0000ff00) >> 8;
//        int blue= rgb & 0x000000ff;
//        ColorPixel clrp= new ColorPixel(90,113,157);
////        int[] clroute=clrp.getRoute();
////        for(int i=0;i<8;i++){
////            System.out.print(clroute[i]+" ");
////        }
//        int[] test=new int[3];
        OctreeQuantization oct = new OctreeQuantization(file,16);
        
    }
    
}
