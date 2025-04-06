/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package octreeIncremental;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author alvyn
 */
public class OctreeQuantizationI {
    private Node root;
    private List<Color>pallete;
    private List<Node> pruneList;
    private int k;//max number of unique colors
    private BufferedImage imgInput;
    private BufferedImage imgOutput;
    private String filepath;
    //constructor
    public OctreeQuantizationI(String filepath,int k) throws IOException{
        File file = new File(filepath);
        this.filepath=filepath;
        this.imgInput = ImageIO.read(file);//get image file
        int rgb,red,green,blue;//init var
        this.root= new Node(0);//init tree root
        this.pallete=new ArrayList<>();
        this.k=k;
        
        //list to be pruned (parent of lowest level)
        this.pruneList=new ArrayList<>();
        
        //loop to add all pixel colors from image to tree
        for(int i=0;i<this.imgInput.getHeight();i++){
            for(int j=0;j<this.imgInput.getWidth();j++){
                //convert rgb from image to int
                rgb = this.imgInput.getRGB(j, i);
                red= (rgb & 0x00ff0000) >> 16;
                green= (rgb & 0x0000ff00) >> 8;
                blue= rgb & 0x000000ff;
                //build the tree
                ColorPixel clrp= new ColorPixel(red,green,blue);
                build(this.root,clrp,0);
            }    
        }
        
        
        this.printPallete("palleteIncremental.png");
        
        //reconstruct image
        this.imgOutput=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
        //loop to get colors
        for(int i=0;i<this.imgInput.getHeight();i++){
            for(int j=0;j<this.imgInput.getWidth();j++){
                //convert rgb from image to int
                rgb = this.imgInput.getRGB(j, i);
                red= (rgb & 0x00ff0000) >> 16;
                green= (rgb & 0x0000ff00) >> 8;
                blue= rgb & 0x000000ff;
                //build the tree
                ColorPixel clrp= new ColorPixel(red,green,blue);
                int[] newClr=reconstruct(this.root,clrp,0);
                this.imgOutput.setRGB(j, i, new Color(newClr[0],newClr[1],newClr[2]).getRGB());
            }    
        }
        File f = new File("output.png");
        ImageIO.write(this.imgOutput, "PNG", f);
    }
    private void build(Node curr,ColorPixel clr,int lvl){
        //leaf node
        if(lvl==8){
            //if newly created node
            if(curr.leaf==false){
                //set color and leaf 
                curr.r=clr.getR();
                curr.g=clr.getG();
                curr.b=clr.getB();
                curr.leaf=true;
                curr.pixelCount++;
                this.pallete.add(new Color(curr.r,curr.g,curr.b));
                if(!this.pruneList.contains(curr.parent)){
                    this.pruneList.add(curr.parent);
                }
            }
            else{
                curr.r+=clr.getR();
                curr.g+=clr.getG();
                curr.b+=clr.getB();
                curr.pixelCount++;
            }
        }
        else if(curr.leaf==true){
            this.pallete.remove(new Color(curr.r/curr.pixelCount,curr.g/curr.pixelCount,curr.b/curr.pixelCount));
            curr.r+=clr.getR();
            curr.g+=clr.getG();
            curr.b+=clr.getB();
            curr.pixelCount++;
            this.pallete.add(new Color(curr.r/curr.pixelCount,curr.g/curr.pixelCount,curr.b/curr.pixelCount));
        }
        //normal nodes
        else{
            curr.pixelCount++;
            //if curr node's child is empty, add
            if(curr.children[clr.getRoute()[lvl]]==null){
                Node n=new Node(lvl+1);
                n.parent=curr;
                curr.children[clr.getRoute()[lvl]]=n;
                build(n,clr,lvl+1);
            }
            //else just move
            else{
                build(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
            }
        }
        if(this.pallete.size()>this.k){
            this.quantize();
        }
    }
    
    public void quantize(){
        while(this.pallete.size()>this.k){
            //sort the prune list based on level and pixel count in decsending order
            Collections.sort(this.pruneList);
            //select predecesor of to be pruned nodes
            Node n= this.pruneList.get(0);
            //sum r g b values
            Node child;

            for(int i=0;i<8;i++){
                child=n.children[i];
                //add rgb value to sum
                if(child!=null){
                    n.r+=child.r;
                    n.g+=child.g;
                    n.b+=child.b;
                    //delete connection to parent
                    child.parent=null;
                    //remove color of child from pallete
                    this.pallete.remove(new Color(child.r/child.pixelCount,child.g/child.pixelCount,child.b/child.pixelCount));
                }
                //delete child connections
                n.children[i]=null;
            }
            
            //set color and leaf status
            n.leaf=true;
            this.pallete.add(new Color(n.r/n.pixelCount,n.g/n.pixelCount,n.b/n.pixelCount));
            //add parent of n to new list for next set of prune candidates
            if(!this.pruneList.contains(n.parent)){
                this.pruneList.add(n.parent);
            }
            
            //remove this node from the list
            this.pruneList.remove(0);
        }
    }
    
    
    
    //method to recreate an image with reduced color pallete
    private int[] reconstruct(Node curr,ColorPixel clr,int lvl){
        //leaf node
        if(curr.leaf==true){
            int[] rgb=new int[3];//int array to store rgb val
            //get color
            rgb[0]=curr.r/curr.pixelCount;
            rgb[1]=curr.g/curr.pixelCount;
            rgb[2]=curr.b/curr.pixelCount;
            return rgb;
        }
     
        //normal nodes
        else{
            //recursive method
            return reconstruct(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
        }
    }
    
    public BufferedImage getQuantizedImage(){
        return this.imgOutput;
    }
    
    public BufferedImage getImage(){
        return this.imgInput;
    }
    
    public String getPath(){
        return this.filepath;
    }
    
    public void printPallete(String palletename) throws IOException{
        System.out.println("Color pallete has "+this.pallete.size()+" colors");//amt of unique colors
        //output pallete
        BufferedImage imgpal=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
        this.pallete.sort((c1, c2) -> {
            float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
            float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
            return Float.compare(hsb1[0], hsb2[0]); // Compare hue (index 0)
        });
        int x=0;int y=0;
        for(Color color: this.pallete){
            imgpal.setRGB(x, y, color.getRGB());
            x++;
            if(x==32){
                x=0;
                y++;
            }
        }

        File f = new File(palletename);

        ImageIO.write(imgpal, "PNG", f);
    }
}
