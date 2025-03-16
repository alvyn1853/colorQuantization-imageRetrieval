/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package octree;

/**
 *
 * @author alvyn
 */
public class Node {
    //color var
    Node parent;
    Node[] children=new Node[8];
    int level;
    Node(){
        
    }
}
