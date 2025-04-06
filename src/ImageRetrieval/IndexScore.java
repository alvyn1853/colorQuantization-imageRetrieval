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
    private int score;
    public IndexScore(ColorStrings cs,int x){
        this.image=cs;
        this.score=x;
    }
    @Override
    public int compareTo(IndexScore i1) {
        //reverse order, for descending sort
        //compare level first
        return Integer.compare( i1.score,this.score);
    }
}
