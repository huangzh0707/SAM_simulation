/*
 * Main.java
 *
 * Created on March 15, 2007, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package YankeeSim;
import javax.swing.*;
import java.io.*;
/**
 *
 * @author agupta
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Create an instance of Yankee Auction Simulation
        SimYankee xYankee = new SimYankee();
        
        xYankee.init();
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Simulation of Jump Bidding Strategy");
        
        frame.add("Center",xYankee);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
  /*      try {
        PrintWriter out1 = new PrintWriter(new FileWriter("E:\\JavaOutput\\Valuations.txt"));
        PrintWriter out2 = new PrintWriter(new FileWriter("E:\\JavaOutput\\Revenue.txt"));
        }
        catch (Exception e) {
            System.err.println("error writing to file");
        } */
        System.out.println("Done!");
    }
    
}
