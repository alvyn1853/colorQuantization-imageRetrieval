/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cbir;

import colorStrings.ColorStrings;

/**
 *
 * @author alvyn
 */
public class IndexScore implements Comparable<IndexScore>{
    private ColorStrings image; //stores color stings
    private double score;//stores the similarity score of the image towards query
    //constructor
    public IndexScore(ColorStrings cs,int x){
        //set attributes
        this.image=cs;
        this.score=(x*1.0)/(32*32);
    }
    //override compareTo for sorting purposes
    @Override
    public int compareTo(IndexScore i1) {
        //reverse order, for descending sort
        //compare level first
        return Double.compare( i1.score,this.score);
    }
    
    //returns color strings of an image
    public ColorStrings getImgRep(){
        return this.image;
    }
    
    //returns the similarity score
    public double getScore(){
        return this.score;
    }
}
