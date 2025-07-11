/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ocqComponents;


/**
 *
 * @author alvyn
 */
public class Node implements Comparable<Node> {
    public boolean leaf=false;//leaf status
    public int r,g,b;//stores rgb value
    public int pixelCount=0;//stores the amount of pixels this node represents
    public Node parent;//stores parent of this node
    public Node[] children=new Node[8]; //stores children of this node
    private int level;  //stores the level of this node
    
    //constructor
    public Node(int level){
        //set attributes
        this.level=level;
        this.r=0;
        this.g=0;
        this.b=0;
    }
    
    //returns level
    public int getLevel(){
        return this.level;
    }
    
    //override for sorting purposes
    @Override
    public int compareTo(Node n1) {
        //reverse order, for descending sort
        //compare level first
        if(this.level!=n1.level){
            return Integer.compare(n1.level,this.level);
        }
        //then compare pixelcount
        return Integer.compare( n1.pixelCount,this.pixelCount);
    }
}
