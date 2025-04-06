/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package octreeIncremental;


/**
 *
 * @author alvyn
 */
public class ColorPixel {
    private int r,g,b;
    private int[][] rgbBits =new int[3][8];
    private int[] nodeRoute =new int[8];
    public ColorPixel(int r, int g, int b){
        //set color attribute
        this.r=r;
        this.g=g;
        this.b=b;
        //rgb binary string
        String rbit= String.format("%8s", Integer.toBinaryString(this.r)).replace(' ', '0');
        String gbit= String.format("%8s", Integer.toBinaryString(this.g)).replace(' ', '0');
        String bbit= String.format("%8s", Integer.toBinaryString(this.b)).replace(' ', '0');
        //insert bits to array
        for(int i=0;i<8;i++){
            rgbBits[0][i]=rbit.charAt(i)-'0';
        }
        for(int i=0;i<8;i++){
            rgbBits[1][i]=gbit.charAt(i)-'0';
        }
        for(int i=0;i<8;i++){
            rgbBits[2][i]=bbit.charAt(i)-'0';
        }
        /*create insertion route from 3 bits from i rgbBits
        X O O
        X O O
        X O O   X is the selected numbers for bits
        */
        String octChild;
        int pos;
        for(int i=0;i<8;i++){
            octChild=""+rgbBits[0][i]+rgbBits[1][i]+rgbBits[2][i];
            pos=Integer.parseInt(octChild,2);
            this.nodeRoute[i]=pos;
        }
    }
    //getter
    public int getR(){
        return this.r;
    }
    public int getG(){
        return this.g;
    }
    public int getB(){
        return this.b;
    }
    public int[][] getRGBBit(){
        return this.rgbBits;
    }
    public int[] getRoute(){
        return this.nodeRoute;
    }
}
