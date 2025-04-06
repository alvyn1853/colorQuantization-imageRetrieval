/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testcode;

/**
 *
 * @author alvyn
 */
public class octreeTest {
    
}
/*
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
    */