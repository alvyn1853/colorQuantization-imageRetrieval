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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author alvyn
 */
public class OctreeQuantization {
    private Node root;
    private List<Color>pallete;
    private List<Node> pruneList;
    private int k;//max number of unique colors
    private BufferedImage imgInput;
    private BufferedImage imgOutput;
    //constructor
    public OctreeQuantization(File file,int k) throws IOException{
        this.imgInput = ImageIO.read(file);//get image file
        int rgb,red,green,blue;//init var
        this.root= new Node(0);//init tree root
        this.pallete=new ArrayList<Color>();
        this.k=k;
        
        //list to be pruned (parent of lowest level)
        this.pruneList=new ArrayList<Node>();
        
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
        
        
        ///////////////////////////////////////////////////////////////////////////////////////testing
        this.testFunction("initPallete.png");
        /////////////////////////////////////////////////////////////////////////
        
        //sort the prune list based on level and pixel count in decsending order
        Collections.sort(this.pruneList);
        
        //quantisize color
        this.quantize();
        this.testFunction("resPallete.png");
        
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
        
        //compare images
        int rgb1,rgb2;
        int diffcount=0;
        for(int i=0;i<this.imgInput.getHeight();i++){
            for(int j=0;j<this.imgInput.getWidth();j++){
                //convert rgb from image to int
                rgb1 = this.imgInput.getRGB(j, i);
                rgb2 = this.imgOutput.getRGB(j, i);
                if(rgb1!=rgb2) diffcount++;
            }    
        }
        System.out.println("Total pixel difference: "+diffcount);
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
//                colorAmount++;
                this.pallete.add(new Color(curr.r,curr.g,curr.b));
                if(!this.pruneList.contains(curr.parent)){
                    this.pruneList.add(curr.parent);
                }
            }
            else{
                curr.pixelCount++;
            }
//            System.out.println();
//            System.out.println("RGB val::("+curr.r+","+curr.g+","+curr.b+")");
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
    }
    
    public void quantize(){
        List<Node> tempList=new ArrayList<Node>();
        while(this.pallete.size()>this.k){
            //select predecesor of to be pruned nodes
            Node n= this.pruneList.get(0);
            //sum r g b values
            int sumR=0;
            int sumG=0;
            int sumB=0;
            int sumDiv=0;
            Node child;
            int chlcount=0;
            for(int i=0;i<8;i++){
                if(n.children[i]!=null) chlcount++;
            }
            for(int i=0;i<8;i++){
                child=n.children[i];
                //add rgb value to sum
                if(child!=null){
                    sumR+=child.r;
                    sumG+=child.g;
                    sumB+=child.b;
                    sumDiv++;
                    //delete connection to parent
                    child.parent=null;
                    //remove color of child from pallete
                    boolean del=this.pallete.remove(new Color(child.r,child.g,child.b));
                }
                //delete child connections
                n.children[i]=null;
            }
            
            //set color and leaf status
            n.r=sumR/sumDiv;
            n.g=sumG/sumDiv;
            n.b=sumB/sumDiv;
            n.leaf=true;
            this.pallete.add(new Color(n.r,n.g,n.b));
            //add parent of n to new list for next set of prune candidates
            if(!tempList.contains(n.parent)){
                tempList.add(n.parent);
            }
            
            //remove this node from the list
            this.pruneList.remove(0);
            //if the list is empty, replace with the templist with the upper level nodes
            if(this.pruneList.isEmpty()){
                this.pruneList=new ArrayList<Node>(tempList);
                Collections.sort(this.pruneList);
                tempList.clear();
            }
        }
    }
    
    
    private int[] reconstruct(Node curr,ColorPixel clr,int lvl){
        //leaf node
        if(curr.leaf==true){
            int[] rgb=new int[3];
            //get color and leaf 
            rgb[0]=curr.r;
            rgb[1]=curr.g;
            rgb[2]=curr.b;
            return rgb;
        }
     
        //normal nodes
        else{
            return reconstruct(curr.children[clr.getRoute()[lvl]],clr,lvl+1);
        }
    }
    
    
    public void testFunction(String palletename) throws IOException{
        System.out.println(this.pallete.size());//amt of unique colors
        //output pallete
        BufferedImage imgpal=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
        this.pallete.sort((c1, c2) -> {
            double lum1 = 0.299 * c1.getRed() + 0.587 * c1.getGreen() + 0.114 * c1.getBlue();
            double lum2 = 0.299 * c2.getRed() + 0.587 * c2.getGreen() + 0.114 * c2.getBlue();
            return Double.compare(lum1, lum2);
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
//        File f = new File("output.png");
        File f = new File(palletename);

        ImageIO.write(imgpal, "PNG", f);
        //unique check
        Map<Color, Integer> colorCount = new HashMap<>();
        for (Color c : this.pallete) {
            colorCount.put(c, colorCount.getOrDefault(c, 0) + 1);
        }

        for (Map.Entry<Color, Integer> entry : colorCount.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("Duplicate Color: " + entry.getKey() + " appears " + entry.getValue() + " times.");
            }
        }
    }
}
