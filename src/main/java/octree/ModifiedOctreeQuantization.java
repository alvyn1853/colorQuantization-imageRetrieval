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
public class ModifiedOctreeQuantization {
    private Node root; //stores root node
    private List<Color>pallete; //stores resulting color pallete
    private List<Color> oldPallete;//stores initial color pallete
    //contains node of pruneList<level> used to combine all children to this level
    private List<Node> pruneList0;
    private List<Node> pruneList1;
    private List<Node> pruneList2;
    private List<Node> pruneList3;
    private int k;//max number of unique colors
    private BufferedImage imgInput; //stores input image
    private BufferedImage imgOutput; //stores output image
    private String filepath; //stores image filepath
    //constructor
    public ModifiedOctreeQuantization(File f,int k) throws IOException{
        this.filepath=f.getPath();//set file
        this.imgInput = ImageIO.read(f);//get image file
        int rgb,red,green,blue;//init var
        this.root= new Node(0);//init tree root
        this.pallete=new ArrayList<>();//color pallete
        this.oldPallete=new ArrayList<>();
        this.k=k;//number of target number
        
        //list to be pruned stores per level to count nodes
        this.pruneList0=new ArrayList<>();
        this.pruneList1=new ArrayList<>();
        this.pruneList2=new ArrayList<>();
        this.pruneList3=new ArrayList<>();
        
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
                if(!this.oldPallete.contains(oldColor)){
                    this.oldPallete.add(oldColor);
                }
            }    
        }
        
        //other commands is run by the controller
    }
    
    //builds the tree
    private void build(Node curr,ColorPixel clr,int lvl){
        //if at leaf node
        if(lvl==4){
            //if newly created node
            if(curr.leaf==false){
                //set color and leaf status; also adds pixelcount value
                curr.r=clr.getR();
                curr.g=clr.getG();
                curr.b=clr.getB();
                curr.leaf=true;
                curr.pixelCount++;
            }
            else{
                //add to the rgb value and pixel count
                curr.r+=clr.getR();
                curr.g+=clr.getG();
                curr.b+=clr.getB();
                curr.pixelCount++;
            }
        }
        //if at normal nodes
        else{
            curr.pixelCount++;//add pixel count as it passes through nodes
            //if curr node's child is empty, add node
            if(curr.children[clr.getRoute()[lvl]]==null){
                Node n=new Node(lvl+1);
                n.parent=curr;
                
                //stores to prune list based on node level
                if(n.getLevel()==0){
                    this.pruneList0.add(n);
                }
                else if(n.getLevel()==1){
                    this.pruneList1.add(n);
                }
                else if(n.getLevel()==2){
                    this.pruneList2.add(n);
                }
                else if(n.getLevel()==3){
                    this.pruneList3.add(n);
                }
                //move to child in the route and continue building
                curr.children[clr.getRoute()[lvl]]=n;
                build(n,clr,lvl+1);
            }
            //else just move to child in the route and continue building
            else{
                build(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
            }
        }
    }
    
    //run modified quantization
    public void quantizeM(){
        //if node count in level 1>K
        if(this.pruneList1.size()>this.k){
            //prune lists
            this.quantizeEntireList(pruneList3);
            this.quantizeEntireList(pruneList2);
            this.quantizeEntireList(pruneList1);
            //prune normally
            this.quantize(pruneList0); //quantizes nodes at level 1 
        }
        //if node count in level 2>K
        else if(this.pruneList2.size()>this.k){
            //prune lists
            this.quantizeEntireList(pruneList3);
            this.quantizeEntireList(pruneList2);
            //prune normally
            this.quantize(pruneList1); //quantizes nodes at level 2 
        }
        //if node count in level 3>K
        else if(this.pruneList3.size()>this.k){
            //prune list
            this.quantizeEntireList(pruneList3);
            //prune normally
            this.quantize(pruneList2); //quantizes nodes at level 3 
        }
        //if node count in level 4>K
        else{
            //prune normally
            this.quantize(pruneList3);
        }
    }
    
    //quantisizes the entire list of a prunelist
    private void quantizeEntireList(List<Node> pruneList){
        //sort the prune list based on level and pixel count in decsending order
        Collections.sort(pruneList);
        while(!pruneList.isEmpty()){
            //select predecesor of to be pruned nodes
            Node n= pruneList.get(0);
            //sum r g b values
            Node child;
            
            //iterate through all children
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
            //add new color to pallete
            this.pallete.add(new Color(n.r/n.pixelCount,n.g/n.pixelCount,n.b/n.pixelCount));
            
            //remove this node from the list
            pruneList.remove(0);
        }
    }
    
    //quantize until condition k is met
    private void quantize(List<Node> pruneList){
        //sort the prune list based on level and pixel count in decsending order
        Collections.sort(pruneList);
        while(this.pallete.size()>this.k){
            //select predecesor of to be pruned nodes
            Node n= pruneList.get(0);
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
            //add new color to pallete
            this.pallete.add(new Color(n.r/n.pixelCount,n.g/n.pixelCount,n.b/n.pixelCount));
            
            //remove this node from the list
            pruneList.remove(0);
        }
    }
    
    //sets the color pallete after building mocq tree
    public void setColorPallete(){
        for(int i=0;i<this.pruneList3.size();i++){
            //select predecesor of leaf nodes
            Node n= this.pruneList3.get(i);
            Node child;//node to store child
            
            //iterate through all children
            for(int j=0;j<8;j++){
                child=n.children[j];
                
                //add rgb value to sum
                if(child!=null){
                    //divide r g b values based on pixel count
                    int rVal=child.r/child.pixelCount;
                    int gVal=child.g/child.pixelCount;
                    int bVal=child.b/child.pixelCount;
                    //add to the pallete
                    this.pallete.add(new Color(rVal,gVal,bVal));
                }
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
    
    //get output image
    public BufferedImage getQuantizedImage(){
        return this.imgOutput;
    }
    
    //get input image
    public BufferedImage getImage(){
        return this.imgInput;
    }
    
    //get filepath of input
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
