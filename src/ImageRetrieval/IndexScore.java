/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ImageRetrieval;

import colorStrings.ColorStrings;

/**
 *
 * @author alvyn
 */
public class IndexScore implements Comparable<IndexScore>{
    private ColorStrings image;
    private double score;
    public IndexScore(ColorStrings cs,int x){
        this.image=cs;
        this.score=(x*1.0)/(32*32);
    }
    @Override
    public int compareTo(IndexScore i1) {
        //reverse order, for descending sort
        //compare level first
        return Double.compare( i1.score,this.score);
    }
    public ColorStrings getImgRep(){
        return this.image;
    }
    public double getScore(){
        return this.score;
    }
}
