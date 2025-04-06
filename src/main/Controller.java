/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import colorStrings.ColorStrings;
import java.io.File;
import java.io.IOException;
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
//        OctreeQuantization oct = new OctreeQuantization("test.jpg",16);
//        ModifiedOctreeQuantization oct = new ModifiedOctreeQuantization("test.jpg",16);
        OctreeQuantizationI oct = new OctreeQuantizationI("test.jpg",16);
        ColorStrings csOld = new ColorStrings(oct.getImage(),oct.getPath());
        System.out.println(csOld.getColorStrings());
        ColorStrings cs = new ColorStrings(oct.getQuantizedImage(),oct.getPath());
        System.out.println(cs.getColorStrings());
    }
    
}
