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
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import ocqComponents.ColorPixel;
import ocqComponents.Node;

/**
 *
 * @author alvyn
 */
public class OctreeQuantizationIncremental {
    private Node root;//stores root node
    private List<Color>pallete; //stores pallate
    private List<Color> oldPallete;//stores initial pallete
    private List<Node> pruneList;//stores to be pruned nodes
    private int k;//max number of unique colors
    private BufferedImage imgInput;//inpur image
    private BufferedImage imgOutput;//output image
    private String filepath;//filepath of image
    //constructor
    public OctreeQuantizationIncremental(File f,int k) throws IOException{
        //set attributes
        this.filepath=f.getPath();
        this.imgInput = ImageIO.read(f);//get image file
        int rgb,red,green,blue;//init var
        this.root= new Node(0);//init tree root
        this.pallete=new ArrayList<>();
        this.oldPallete=new ArrayList<>();
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
                
                //also add to old pallete
                Color oldColor=new Color(red,green,blue);
                
                //adds to color intial pallete
                if(!this.oldPallete.contains(oldColor)){
                    this.oldPallete.add(oldColor);
                }
            }    
        }
        //other commands is run by the controller
    }
    
    //builds tree
    private void build(Node curr,ColorPixel clr,int lvl){
        //if at leaf node level 8
        if(lvl==8){
            //if newly created node
            if(curr.leaf==false){
                //set color and leaf; also add pixelcount
                curr.r=clr.getR();
                curr.g=clr.getG();
                curr.b=clr.getB();
                curr.leaf=true;
                curr.pixelCount++;
                //add to pallete
                this.pallete.add(new Color(curr.r,curr.g,curr.b));
                //add to prunelist
                if(!this.pruneList.contains(curr.parent)){
                    this.pruneList.add(curr.parent);
                }
            }
            //otherwise just add rgb values and pixelcount
            else{
                curr.r+=clr.getR();
                curr.g+=clr.getG();
                curr.b+=clr.getB();
                curr.pixelCount++;
            }
        }
        //if at leaf node not level 8
        else if(curr.leaf==true){
            //remove old color
            this.pallete.remove(new Color(curr.r/curr.pixelCount,curr.g/curr.pixelCount,curr.b/curr.pixelCount));
            //add valuse for new color
            curr.r+=clr.getR();
            curr.g+=clr.getG();
            curr.b+=clr.getB();
            curr.pixelCount++;
            //add to pallete
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
        //checks if value k is fulfilled, if not quantize the tree
        if(this.pallete.size()>this.k){
            this.quantize();
        }
    }
    
    //quantize the tree
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
        //if leaf node
        if(curr.leaf==true){
            int[] rgb=new int[3];//int array to store rgb val
            //get color from averaging
            rgb[0]=curr.r/curr.pixelCount;
            rgb[1]=curr.g/curr.pixelCount;
            rgb[2]=curr.b/curr.pixelCount;
            return rgb;//returns rgb values of color
        }
     
        //normal nodes
        else{
            //recursive method, traverse until leaf
            return reconstruct(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
        }
    }
    
    //get buffered image output
    public BufferedImage getQuantizedImage(){
        return this.imgOutput;
    }
    
    //get bufferedImage input
    public BufferedImage getImage(){
        return this.imgInput;
    }
    
    //get filepath
    public String getPath(){
        return this.filepath;
    }
    
    //prints color pallete
    public int printPallete(String palletename) throws IOException{
        //output pallete
        BufferedImage imgpal=new BufferedImage(this.imgInput.getWidth(),this.imgInput.getHeight(),BufferedImage.TYPE_INT_RGB);
        this.pallete.sort((c1, c2) -> {
            float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
            float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
            return Float.compare(hsb1[0], hsb2[0]); // Compare hue (index 0)
        });
        int x=0;int y=0;
        for(Color color: this.pallete){
            imgpal.setRGB(x, y, color.getRGB());
            x++;
            if(x==this.imgInput.getWidth()){
                x=0;
                y++;
            }
        }

        File f = new File(palletename);

        ImageIO.write(imgpal, "PNG", f);
        
        return this.pallete.size();//amt of unique colors
    }
    
    //prints initial color pallete
    public int printOldPallete(String palletename) throws IOException{
        //output pallete
        BufferedImage imgpal=new BufferedImage(this.imgInput.getWidth(),this.imgInput.getHeight(),BufferedImage.TYPE_INT_RGB);
        this.oldPallete.sort((c1, c2) -> {
            float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
            float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
            return Float.compare(hsb1[0], hsb2[0]); // Compare hue (index 0)
        });
        int x=0;int y=0;
        for(Color color: this.oldPallete){
            imgpal.setRGB(x, y, color.getRGB());
            x++;
            if(x==this.imgInput.getWidth()){
                x=0;
                y++;
            }
        }

        File f = new File(palletename);

        ImageIO.write(imgpal, "PNG", f);
        
        return this.oldPallete.size();//amt of unique colors
    }
    
    //creates an image using the reduced color pallete in imgOutput
    public void processOutputImg(){
        //initialize variables
        int rgb,red,green,blue;
        //reconstruct image
        this.imgOutput=new BufferedImage(this.imgInput.getWidth(),this.imgInput.getHeight(),BufferedImage.TYPE_INT_RGB);
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
    }
    
    //prints a file of output image
    public void printCQImg(String filepath) throws IOException{
        File f = new File(filepath);//prepares file obj
        ImageIO.write(this.imgOutput, "PNG", f);//write to file
    }
}
