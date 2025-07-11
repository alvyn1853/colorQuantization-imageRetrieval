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
public class OctreeQuantization {
    private Node root;//stores root
    private List<Color>pallete;//stores current pallete
    private List<Node> pruneList;//stores nodes to be pruned
    private int k;//max number of unique colors
    private BufferedImage imgInput;//stors input image
    private BufferedImage imgOutput;//stors output image
    private String filepath;//stores filepath
    //constructor
    public OctreeQuantization(File f,int k) throws IOException{
        this.filepath=f.getPath();//set path
        this.imgInput = ImageIO.read(f);//get image file
        int rgb,red,green,blue;//init var
        this.root= new Node(0);//init tree root
        this.pallete=new ArrayList<>();
        this.k=k;//set k value
        
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
        
        //sort the prune list based on level and pixel count in decsending order
        Collections.sort(this.pruneList);
        //other commands is run by the controller
    }
    
    //builds the tree
    private void build(Node curr,ColorPixel clr,int lvl){
        //leaf node
        if(lvl==8){
            //if newly created node
            if(curr.leaf==false){
                //set color and leaf; add pixel count
                curr.r=clr.getR();
                curr.g=clr.getG();
                curr.b=clr.getB();
                curr.leaf=true;
                curr.pixelCount++;
                //add to pallete
                this.pallete.add(new Color(curr.r,curr.g,curr.b));
            }
            //else just add values and pixelcount
            else{
                curr.r+=clr.getR();
                curr.g+=clr.getG();
                curr.b+=clr.getB();
                curr.pixelCount++;
            }
        }
        //in normal nodes
        else{
            curr.pixelCount++;
            //if curr node's child is empty, add
            if(curr.children[clr.getRoute()[lvl]]==null){
                Node n=new Node(lvl+1);
                n.parent=curr;
                //if level 7 add to prunelist
                if(n.getLevel()==7){
                    this.pruneList.add(n);
                }
                curr.children[clr.getRoute()[lvl]]=n;
                build(n,clr,lvl+1);
            }
            //else just move
            else{
                build(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
            }
        }
    }
    
    //quantization process
    public void quantize(){
        List<Node> tempList=new ArrayList<>();
        while(this.pallete.size()>this.k){
            //select predecesor of to be pruned nodes
            Node n= this.pruneList.get(0);
            //sum r g b values to parent
            Node child;
            //iterate thorugh all children
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
            if(!tempList.contains(n.parent)){
                tempList.add(n.parent);
            }
            
            //remove this node from the list
            this.pruneList.remove(0);
            //if the list is empty, replace with the templist with the upper level nodes
            if(this.pruneList.isEmpty()){
                this.pruneList=new ArrayList<>(tempList);
                Collections.sort(this.pruneList);
                tempList.clear();
            }
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
    
    //returns a buffered image of the output
    public BufferedImage getQuantizedImage(){
        return this.imgOutput;
    }
    
    //returns a buffered image of the input
    public BufferedImage getImage(){
        return this.imgInput;
    }
    
    //get the input filepath
    public String getPath(){
        return this.filepath;
    }
    
    //prints current pallete in this.pallete
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
