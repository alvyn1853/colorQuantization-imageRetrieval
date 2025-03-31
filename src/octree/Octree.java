/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package octree;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author alvyn
 */
public class Octree {
    private Node root;
    private int colorAmount=0;
    private List<Color>pallete;
    private List<Node> nodelvl1=new ArrayList<Node>();
    private List<Node> nodelvl2=new ArrayList<Node>();
    private List<Node> nodelvl3=new ArrayList<Node>();
    private List<Node> nodelvl4=new ArrayList<Node>();
    private List<Node> nodelvl5=new ArrayList<Node>();
    private List<Node> nodelvl6=new ArrayList<Node>();
    private List<Node> nodelvl7=new ArrayList<Node>();
    private List<Node> nodelvl8=new ArrayList<Node>();
    
    public Octree(File file) throws IOException{
        BufferedImage image = ImageIO.read(file);
        int rgb,red,green,blue;
        this.root= new Node(0);
        this.pallete=new ArrayList<Color>();
        for(int i=0;i<image.getHeight();i++){
            for(int j=0;j<image.getWidth();j++){
                rgb = image.getRGB(j, i);
                red= (rgb & 0x00ff0000) >> 16;
                green= (rgb & 0x0000ff00) >> 8;
                blue= rgb & 0x000000ff;
                ColorPixel clrp= new ColorPixel(red,green,blue);
//                int[] clroute=clrp.getRoute();
                build(clrp);
//                System.out.print("x:"+j+" y:"+i+"=> ");
//                for(int k=0;k<clroute.length;k++){
//                    System.out.print(clroute[k]+" ");
//                }
//                System.out.println();
                
            }    
        }
        System.out.println(this.colorAmount);
        
        
    }
    private void build(ColorPixel clr){
        Node curr=this.root;
        int[] clroute=clr.getRoute();
        for(int i=0;i<clroute.length;i++){
            if(curr.children[clroute[i]]==null){
                curr.children[clroute[i]]=new Node(curr.level+1);
                curr.children[clroute[i]].parent=curr;
            }
            curr=curr.children[clroute[i]];
        }
        if(curr.hasColor==false){
            curr.r=clr.getR();
            curr.g=clr.getG();
            curr.b=clr.getB();
            curr.hasColor=true;
            colorAmount++;
            this.pallete.add(new Color(curr.r,curr.g,curr.b));
//            System.out.println("RGB val::("+curr.r+","+curr.g+","+curr.b+")");
        }
    }
}
