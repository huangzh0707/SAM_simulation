/*
 * SimYankee.java
 *
 * Created on March 15, 2007, 10:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package YankeeSim;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.math.*;
import java.util.*;
import java.io.*;
import javax.swing.JFileChooser;

/**
 *
 * @author agupta
 */
public class SimYankee extends Applet implements ActionListener, TextListener {
    Label L[] = new Label[10];
    TextField t[] = new TextField[10];
    TextArea results;
    int increment;
    int replications;
    int val_lb, val_ub;
    int startBid;
    int units;
    int budget;
    int numBidders = 100;
    int bidOrder[];
    double bidValue[];
    String file1, file2;
    PrintWriter o1, o2;
    JFileChooser fc = new JFileChooser();
    File dirName;
    
    public void init() {
        setSize(600,600);

            setLayout(new BorderLayout(5,15));

            Panel p = new Panel();

            p.setLayout(new GridLayout(10,2,10,10));
            
            L[0] = new Label("Enter the number of anticipated bidders (Default = 100):", Label.RIGHT);
            L[1] = new Label("Enter the Lower Bound for Bidder Valuation (Min = 0):", Label.RIGHT);
            L[2] = new Label("Enter the Upper Bound for Bidder Valuation (Max = 1000):", Label.RIGHT);
            L[3] = new Label("Enter the number of repetitions (Default=10)", Label.RIGHT);
            L[4] = new Label("Enter the Starting Bid for Auction (Default = 1):", Label.RIGHT);
            L[5] = new Label("Enter the Bid Increment (Deafult = 1):", Label.RIGHT);
            L[6] = new Label("Enter the Number of Units for Sale (Default = 5):", Label.RIGHT);
            L[7] = new Label("Enter the value of budget constraints (Default = 100):", Label.RIGHT);
            L[8] = new Label("Enter the File Name to Output the Valuations:", Label.RIGHT);
            L[9] = new Label("Enter the File Name to Output the Results_Jump:",Label.RIGHT);
             
            t[0] = new TextField("100");
            t[1] = new TextField("0");
            t[2] = new TextField("1000");
            t[3] = new TextField("10");
            t[4] = new TextField("1");
            t[5] = new TextField("1");
            t[6] = new TextField("5");
            t[7] = new TextField("100");
            t[8] = new TextField("Valuations.txt");
            t[9] = new TextField("Results_Jump.txt");

            for (int i=0; i<10; i++) {
                t[i].addTextListener(this);
                p.add(L[i]);
                p.add(t[i]);
            }
            Button run = new Button("Simulate");
            run.addActionListener(this);

            results = new TextArea(11,50);
            results.setEditable(false);

            add(p, BorderLayout.CENTER);
            add(run, BorderLayout.SOUTH);
            add(results, BorderLayout.EAST);
    }
    /** Creates a new instance of SimYankee */
    public SimYankee() {
    }
    public void simulate () {
        //variables to run Simulation
                int i, j, mark;
                int Rep;
                int currentBid;
                int numBid = 0;
                Random nextBidder;
                Random bType;
                Random valuation;
                long seeds[] = new long[numBidders];
                double maxValue[] = new double[numBidders];
                int bidderType[] = new int[numBidders]; // 0-participator; 1-SAM bidder
                double frac;
                
                // random number seed
                for (i=0; i < numBidders; i++)
                    seeds[i] = 1234567 + i*1000;
                //generate valuations
                valuation = new Random(seeds[0]);
                for (i=0; i < numBidders; i++) {
                      maxValue[i] = val_lb + (val_ub - val_lb)*valuation.nextDouble();
                    //maxValue[i] = (val_ub + val_lb)/2 + (val_ub - val_lb)*valuation.nextGaussian()/3.5;  //to generate Normal Vals
                    bidValue[i] = 0;
                    //results.append("" + i + "\t" + maxValue[i] + "\n");
                }


                bType = new Random(seeds[1]);
                int test_id;
                test_id = bType.nextInt(numBidders); // select one bidder to compare between Participatory and SAM
                results.append("" + "observed bidder" + "\t" + test_id + "\n");
                valPrint(1, maxValue,test_id); // print valuations

                for (Rep=0; Rep < replications; Rep++) {

                     nextBidder = new Random(seeds[Rep+2]);//

                    for (i=0; i<numBidders; i++) {
                        if (i == test_id) //
                            bidderType[i] = 1; // SAM bidder
                        else
                            bidderType[i] = 0; // participator
                    }

                    for (i=0; i<numBidders; i++)
                        bidOrder[i] = i;

                    mark = numBidders;
                    currentBid = startBid;
                    int count=0;

                    while (mark > 0) {
                        // out.println("mark = " + mark + "; numBid = " + numBid + "; currentBid = " + currentBid +"<br>");
                        int bidder, tmp, tmpVal;
                        bidder = nextBidder.nextInt(mark);
                        //out.println("Bidder = " + bidder + "; Bid Order=" + bidOrder[bidder] + "; Value= " + maxValue[bidOrder[bidder]] + "<br>");
                        if (bidderType[bidOrder[bidder]] == 1) {
                        //if (bidOrder[bidder] == test_id) {
                            if(budget >= maxValue[bidOrder[test_id]]){
                                if (maxValue[bidOrder[bidder]] >= currentBid) {
                                    bidValue[bidOrder[bidder]] = currentBid; //Participator
                        /*if (bidderType[bidOrder[bidder]] == 0)
                            bidValue[bidOrder[bidder]] = currentBid; //Participator
                        else {
                            tmpVal = (int)maxValue[bidOrder[bidder]];
                            bidValue[bidOrder[bidder]] = startBid + ((tmpVal-startBid)/increment)*increment; // evaluator, ?? Why not direct tmpVal

                 //       out.println("Max Val = $" + maxValue[bidOrder[bidder]] + "; Bid = $" + bidValue[bidOrder[bidder]]);
                           }*/
                                    // out.println("Bibber Type = " + bidderType[bidOrder[bidder]] + "; Bid="+ bidValue[bidOrder[bidder]] + "; Current Bid=" + currentBid +"<br>");
                                    results.append("" + bidOrder[bidder] + "\t" + bidValue[bidOrder[bidder]] + "\n");

                                    //
                                    if (numBid >= units) {
                                        tmp = bidOrder[numBidders - units];
                                        bidOrder[numBidders - units] = bidOrder[bidder];
                                        bidOrder[bidder] = tmp;
                                        numBid++;
                                        sortBidder(units);
                                        currentBid = (int) bidValue[bidOrder[numBidders - units]] + increment;
                                    } else {
                                        count++;
                                        mark--;
                                        numBid++;
                                        if (mark == (numBidders - numBid)) { //no bidder with less value than currentbid found
                                            tmp = bidOrder[numBidders - numBid];
                                            bidOrder[numBidders - numBid] = bidOrder[bidder];
                                            bidOrder[bidder] = tmp;
                                        } else { //if some bidders have been excluded
                          /*  mark--;
                            numBid++; */ //edited out 11/21/04
                                            tmp = bidOrder[mark];
                                            for (j = mark; j < (numBidders - numBid); j++)
                                                bidOrder[j] = bidOrder[j + 1];
                                            bidOrder[numBidders - numBid] = bidOrder[bidder];
                                            bidOrder[bidder] = tmp;
                                        }
                                        sortBidder(numBidders - mark);
                                    }

                                }  //end if the bidder can bid
                                else { // bidder has low value
                                    //out.println("Bidder = " + bidOrder[bidder] + "; Current Bid=" + currentBid + "; Value= " + maxValue[bidOrder[bidder]] + "<br>");
                                    mark--;
                                    tmp = bidOrder[mark];
                                    bidOrder[mark] = bidOrder[bidder];
                                    bidOrder[bidder] = tmp;
                                    //sortBidder(numBidders-mark);
                                }

                            }
                        else{
                                //if (budget - currentBid <= increment) {
                                if (budget >= currentBid) {
                                    if(budget - currentBid <= increment)
                                    {
                                        bidValue[bidOrder[bidder]] = budget; //Always Jump
                                        /*int x = 1; //position at x in winning list when bid Current bid or B-k
                                        int y = 1; // position at y in winning list when bid Budget
                                        int m = 0; // how many bidders who can bid level >= current bid or B-k
                                        int n = 0; // how many bidders who can bid at level >= B
                                        int w = 0; // how many people whose valuations are <= B
                                        int z = 0; // how many people whose valuation are <= B+k

                                        for (i=0; i<numBidders; i++){
                                            if (bidValue[i]>=budget){x = x+1;}
                                            else {x = x;}

                                            if (bidValue[i]>=currentBid){y = y+1;}
                                            else {y = y;}

                                            if (maxValue[i]>=currentBid){m = m+1;}
                                            else {m = m;}

                                            if (maxValue[i]>=budget){n = n+1;}
                                            else {n = n;}

                                            if (maxValue[i]<=budget){w = w+1;}
                                            else {w = w;}

                                            if (maxValue[i]<=(budget + increment)){z = z+1;}
                                            else {z = z;}

                                        }

                                        double FB, FBk;
                                        FB = w/numBidders; // uniform distribution
                                        FBk = z/numBidders; // uniform distribution

                                        double pRebidBk, pRebidBP, pRebidBS;

                                        /// to calculate the probability of rebid at current bid or B-k level
                                        pRebidBk = combination(m,(x-1))*sumrebid(0,(x-1),FB)*sumrebid((units-x+1),(m-x+1),FB);

                                        /// to calculate the probability of rebid at B in participatory
                                        pRebidBP = combination(n,(units+y-x))*sumrebid(0,(units+y-x),FBk)*sumrebid((x-y),(n-units+x-y),FBk);

                                        /// to calculate the probability of rebid at B in SAM
                                        pRebidBS = combination(n,(y-1))*sumrebid(0,(y-1),FBk)*sumrebid((units-y+1),(n-y+1),FBk);


                                        double thresh1, thresh2;
                                        thresh1 = (1-pRebidBk)/((1-pRebidBS)-pRebidBk*(1-pRebidBP));
                                        thresh2 = increment*thresh1/(1-thresh1);

                                        if((maxValue[bidOrder[bidder]]-budget) >= thresh2){
                                            bidValue[bidOrder[bidder]]= budget;
                                        }
                                        else{
                                            bidValue[bidOrder[bidder]]= currentBid;
                                        }*/

                                    }
                                    else{
                                        bidValue[bidOrder[bidder]] = currentBid; //Participator
                                    }

                        /*if (bidderType[bidOrder[bidder]] == 0)
                            bidValue[bidOrder[bidder]] = currentBid; //Participator
                        else {
                            tmpVal = (int)maxValue[bidOrder[bidder]];
                            bidValue[bidOrder[bidder]] = startBid + ((tmpVal-startBid)/increment)*increment; // evaluator, ?? Why not direct tmpVal

                 //       out.println("Max Val = $" + maxValue[bidOrder[bidder]] + "; Bid = $" + bidValue[bidOrder[bidder]]);
                           }*/
                                    // out.println("Bibber Type = " + bidderType[bidOrder[bidder]] + "; Bid="+ bidValue[bidOrder[bidder]] + "; Current Bid=" + currentBid +"<br>");
                                    results.append("" + bidOrder[bidder] + "\t" + bidValue[bidOrder[bidder]] + "\n");

                                    //
                                    if (numBid >= units) {
                                        tmp = bidOrder[numBidders - units];
                                        bidOrder[numBidders - units] = bidOrder[bidder];
                                        bidOrder[bidder] = tmp;
                                        numBid++;
                                        sortBidder(units);
                                        currentBid = (int) bidValue[bidOrder[numBidders - units]] + increment;
                                    } else {
                                        count++;
                                        mark--;
                                        numBid++;
                                        if (mark == (numBidders - numBid)) { //no bidder with less value than currentbid found
                                            tmp = bidOrder[numBidders - numBid];
                                            bidOrder[numBidders - numBid] = bidOrder[bidder];
                                            bidOrder[bidder] = tmp;
                                        } else { //if some bidders have been excluded
                          /*  mark--;
                            numBid++; */ //edited out 11/21/04
                                            tmp = bidOrder[mark];
                                            for (j = mark; j < (numBidders - numBid); j++)
                                                bidOrder[j] = bidOrder[j + 1];
                                            bidOrder[numBidders - numBid] = bidOrder[bidder];
                                            bidOrder[bidder] = tmp;
                                        }
                                        sortBidder(numBidders - mark);
                                    }

                                    //?????

                                }  //end if the bidder can bid
                                else { // bidder has low value
                                    //out.println("Bidder = " + bidOrder[bidder] + "; Current Bid=" + currentBid + "; Value= " + maxValue[bidOrder[bidder]] + "<br>");
                                    mark--;
                                    tmp = bidOrder[mark];
                                    bidOrder[mark] = bidOrder[bidder];
                                    bidOrder[bidder] = tmp;
                                    //sortBidder(numBidders-mark);
                                }

                                /* if((budget-currentBid)<=increment){
                                    // Jump
                                }
                                else{
                                    //Participatory
                                }*/
                            }



                        }

                        else {
                        if (maxValue[bidOrder[bidder]] >= currentBid) {
                            bidValue[bidOrder[bidder]] = currentBid; //Participator
                        /*if (bidderType[bidOrder[bidder]] == 0)
                            bidValue[bidOrder[bidder]] = currentBid; //Participator
                        else {
                            tmpVal = (int)maxValue[bidOrder[bidder]];
                            bidValue[bidOrder[bidder]] = startBid + ((tmpVal-startBid)/increment)*increment; // evaluator, ?? Why not direct tmpVal

                 //       out.println("Max Val = $" + maxValue[bidOrder[bidder]] + "; Bid = $" + bidValue[bidOrder[bidder]]);
                           }*/
                            // out.println("Bibber Type = " + bidderType[bidOrder[bidder]] + "; Bid="+ bidValue[bidOrder[bidder]] + "; Current Bid=" + currentBid +"<br>");
                            results.append("" + bidOrder[bidder] + "\t" + bidValue[bidOrder[bidder]] + "\n");

                            //
                            if (numBid >= units) {
                                tmp = bidOrder[numBidders - units];
                                bidOrder[numBidders - units] = bidOrder[bidder];
                                bidOrder[bidder] = tmp;
                                numBid++;
                                sortBidder(units);
                                currentBid = (int) bidValue[bidOrder[numBidders - units]] + increment;
                            } else {
                                count++;
                                mark--;
                                numBid++;
                                if (mark == (numBidders - numBid)) { //no bidder with less value than currentbid found
                                    tmp = bidOrder[numBidders - numBid];
                                    bidOrder[numBidders - numBid] = bidOrder[bidder];
                                    bidOrder[bidder] = tmp;
                                } else { //if some bidders have been excluded
                          /*  mark--;
                            numBid++; */ //edited out 11/21/04
                                    tmp = bidOrder[mark];
                                    for (j = mark; j < (numBidders - numBid); j++)
                                        bidOrder[j] = bidOrder[j + 1];
                                    bidOrder[numBidders - numBid] = bidOrder[bidder];
                                    bidOrder[bidder] = tmp;
                                }
                                sortBidder(numBidders - mark);
                            }

                            //?????

                        }  //end if the bidder can bid
                        else { // bidder has low value
                            //out.println("Bidder = " + bidOrder[bidder] + "; Current Bid=" + currentBid + "; Value= " + maxValue[bidOrder[bidder]] + "<br>");
                            mark--;
                            tmp = bidOrder[mark];
                            bidOrder[mark] = bidOrder[bidder];
                            bidOrder[bidder] = tmp;
                            //sortBidder(numBidders-mark);
                        }
                    }
                }  //end while
                resPrint(Rep, maxValue);
                sortBidder(numBidders);
               } //end bRep
                  resPrint(-1,maxValue);
                   // end vRep
               /* for (i=99; i >= 0; i--)
                    out.println("Bid order = " + bidOrder[i] + "; Value = " + maxValue[bidOrder[i]] +"<br>"); */
                results.append("\nDone Simulating");
    }
    public void actionPerformed(ActionEvent e) {
    results.setText("");

    numBidders = Integer.parseInt(t[0].getText());
    val_lb = Integer.parseInt(t[1].getText());
    val_ub = Integer.parseInt(t[2].getText());
    replications = Integer.parseInt(t[3].getText());
    startBid = Integer.parseInt(t[4].getText());
    increment = Integer.parseInt(t[5].getText());
    units = Integer.parseInt(t[6].getText());;
    budget = Integer.parseInt(t[7].getText());;
    file1 = t[8].getText();
    file2 = t[9].getText();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int rVal = fc.showOpenDialog(this);
    dirName = fc.getSelectedFile();
    results.setText("Setting Output Directory to: " + dirName.getAbsolutePath() + ".\n");
    bidOrder = new int[numBidders];
    bidValue = new double[numBidders];
       
    setPrinting();
    simulate();
    closePrint();
    }// end actionPer
    
    public void textValueChanged(TextEvent e) {
        
    }

    public static int factorial(int n) {
        int sum = 1;
        while( n > 0 ) {
            sum = sum * n--;
        }
        return sum;
    }

    public static int combination(int m, int n) {
        return m <= n ? factorial(n) / (factorial(m) * factorial((n - m))) : 0;
    }

    public static double sumrebid(int start, int end, double F) {
        double sum = 0;
        for(int s = start; s<=end; s++ ) {
            sum = sum + combination(end, s)*power((1-F),s)*power(F,(end-s));
        }
        return sum;
    }

    public static double power(double m, int n) {
        double power = 1;
        for(int j=0; j< n; j++){
            power = m*power;
        }
        return power;
    }

    protected void sortBidder(int units) {
        int i;
        int tmp;
        for (i=(numBidders-units); i < (numBidders-1); i++) {
            if (bidValue[bidOrder[i]] > bidValue[bidOrder[i+1]]){
                tmp = bidOrder[i+1];
                bidOrder[i+1] = bidOrder[i];
                bidOrder[i] = tmp;
            }
        }
    }
    
    public void valPrint(int iteration, double[] value, int test_id) {
        o1.println("Random selected bidder, NO." + test_id + "\n");
        o1.println("Valuations of all bidders: " + "\n");
        o1.println("Bidder id"+ "\t" + "Valuation"  + "\n");
        for (int i = 0; i < numBidders; i++){
            o1.println("\t"+ i + "\t" + value[i] + "\n");
        }
        o1.println("");
    }  //end printing bidder values for a given iteration
    
    public void resPrint(int iteration, double[] value) {
        if (iteration > -1) {

            double surplus = 0;
            o2.println("Rep" + iteration + "\n");
            o2.println("Winner id" + "\t" + "Valuation" + "\t" + "Bid" + "\t" + "Surplus" + "\n");
            for (int i=0; i < units; i++) {
                surplus = value[bidOrder[numBidders-1-i]]-bidValue[bidOrder[numBidders-1-i]];
                o2.println("" + bidOrder[numBidders-1-i] + "\t" +value[bidOrder[numBidders-1-i]] + "\t"+ bidValue[bidOrder[numBidders-1-i]] + "\t" + surplus + "\n");
            }
            o2.println();

        }
        else {
            o2.println("");
            o2.println("Done");
            o2.println("");
        }
        
    }
    
    public void closePrint () {
       o1.close();
       o2.close();
    }
    
    public void setPrinting () {
        String f1, f2;
       
       f1 = dirName.getAbsolutePath()+ "\\" + file1;
       f2 = dirName.getAbsolutePath()+ "\\" + file2;
        
        try {
        o1 = new PrintWriter(new FileWriter(f1));
        o2 = new PrintWriter(new FileWriter(f2));
        }
        catch (Exception e) {
            System.err.println("error writing to file");
        }
    }
    
}
