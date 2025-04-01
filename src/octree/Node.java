/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package octree;


/**
 *
 * @author alvyn
 */
public class Node implements Comparable<Node> {
    boolean leaf=false;
    public int r,g,b;
    int pixelCount=0;
    Node parent;
    Node[] children=new Node[8];
    private int level; 
    public Node(int level){
        this.level=level;
    }
    
    public int getLevel(){
        return this.level;
    }
    
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
