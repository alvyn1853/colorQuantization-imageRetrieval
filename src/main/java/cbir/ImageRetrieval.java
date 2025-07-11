/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cbir;

import colorStrings.ColorStrings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author alvyn
 */
public class ImageRetrieval {
    private ColorStrings query; //stores color strings of the query image
    private ColorStrings[] database; //stores color strings of the database images
    private List<IndexScore> results; //stores the similarity scores
    
    //constructor
    public ImageRetrieval(ColorStrings query, ColorStrings[] database){
        //set attributes
        this.database=database;
        this.query=query;
        this.results= new ArrayList<>();
    }
    
    //calculates the similarity and sorts it in descending order
    public void calculateSimilarity(){
        int score; //declare variable to store the score
        //for all images in database
        for(int i=0;i<this.database.length;i++){
            score=0;//reset score
            //compare all the characters in the strung
            for(int j=0;j<this.query.getColorStrings().length();j++){
                //if the character at the same position has the same value; add score
                if(this.query.getColorStrings().charAt(j)==this.database[i].getColorStrings().charAt(j)){
                    score++;
                }
            }
            //store results
            this.results.add(new IndexScore(this.database[i],score));
        }
        //sorts results
        Collections.sort(this.results);
    }
    
    //returns the results
    public List<IndexScore> getRes(){
        return this.results;
    }
}
