/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ImageRetrieval;

import colorStrings.ColorStrings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author alvyn
 */
public class ImageRetrieval {
    private ColorStrings query;
    private ColorStrings[] database;
    private List<IndexScore> results;
    public ImageRetrieval(ColorStrings query, ColorStrings[] database){
        this.database=database;
        this.query=query;
        this.results= new ArrayList<>();
        this.calculateSimilarity();
    }
    public void calculateSimilarity(){
        int score;
        for(int i=0;i<this.database.length;i++){
            score=0;
            for(int j=0;j<this.query.getColorStrings().length();j++){
                if(this.query.getColorStrings().charAt(j)==this.database[i].getColorStrings().charAt(j)){
                    score++;
                }
            }
            this.results.add(new IndexScore(this.database[i],score));
        }
        Collections.sort(this.results);
    }
    
    public List<IndexScore> getRes(){
        return this.results;
    }
}
