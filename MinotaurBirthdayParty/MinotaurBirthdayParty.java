/*
 * @author: Yohan Hmaiti
 * @date: 02/05/2023
 * @brief: This program simulates a labyrinth based birthday party that is for a Minotaur and some guests.
 *         The guests access the labyrinth in a random order, however, the guests should report to the minotaur when 
 *         every guest present went through it. The minotaur placed a cake at the end of the labyrinth near the exit and 
 *         refilling the cake is possible whenever requested. The minotaur can pick any guests to go into the labyrinth, even 
 *         pick the same guest several times. The guests need to plan a strategy to inform the minotaur that all the guests
 *         entred the labyrinth at least 1 time. We know that a birthday cake is present at the exit of the labyrinth at the start of the 
 *         simulation.   
 * 
 * @MyStrategy: I use a random number generator to chose a random guest that will act as a decider, meaning that they will be the only guest that will 
 *             request a cupcake from the Minotaur whenever they see that it is not there, they will also keep track of the count of the number of guests that 
 *             went into the labyrinth and let the Minotaur know at the end that everyone went through the labyrinth at least 1 time. I was inspired to follow this strategy
 *             based on the prisoners examples we discussed in class. 
 *             Now, if a guest goes in and they see the cake, and they did not eat before, they can eat it once. No guest 
 *             will eat more than once, thus, if they already ate, next time they go through and they find a cake, they will not touch it. If a guest that is not the chosen decider,
 *             goes in and doesn't find a cake they will simply leave without requesting a cupcake. If the decider goes in and doesn't find a cake, they are the only guest that can request one, and increment the 
 *             count of the guests at each time they request a cake. When the decider reaches a count of n - 1 that means that everyone went through the labyrinth at least 1 time.
 * 
 * @note: testing and additional clarifications are in the ReadMe file as needed.
 * 
 * @note: since he decider guest is the first to go in, they will eat the cake if it's there, and then follow the same rule as other guests, if you ate once, don't eat again.
 * 
 */

// pre-processor directives
import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * @brief: this class presents a set of lock operations to the labyrinth and cupcake locks as needed
 */
class LockTuple {

    private Lock _cupcakeAccessLock;
    private Lock _LabyrinthAccessLock;

    LockTuple(Lock cupcakeAccessLock, Lock LabyrinthAccessLock) {
        this._cupcakeAccessLock = cupcakeAccessLock;
        this._LabyrinthAccessLock = LabyrinthAccessLock;
    }

    public Lock getCupcakeAccessLock() {
        return _cupcakeAccessLock;
    }

    public Lock getLabyrinthAccessLock() {
        return _LabyrinthAccessLock;
    }

    public void setCupcakeAccessLock(Lock cupcakeAccessLock) {
        this._cupcakeAccessLock = cupcakeAccessLock;
    }

    public void setLabyrinthAccessLock(Lock LabyrinthAccessLock) {
        this._LabyrinthAccessLock = LabyrinthAccessLock;
    }

}

/*
 * @brief: this class has the driver function and also handles spawning the threads and joining them.
 *         It also hanles the time tracking and reporting the output along with any declarations needed
 *         in additon, to upper level simulation management such as picking random guests (threads) to go 
 *         into the labyrinth.
 */
public class MinotaurBirthdayParty {

    // declare atomic booleans to handle the simulation continuation or stoppage
    // and also the status of the cupcake
    public AtomicBoolean _isCupcakeThere = new AtomicBoolean(true);
    public AtomicBoolean _simulationOngoing = new AtomicBoolean(true);

    // declare the locks needed for both the labyrinth and the cupcake
    LockTuple _locks = new LockTuple(new ReentrantLock(), new ReentrantLock());

    // keep track of the processed guests
    public int processedSofar = 0;

    // driver method
    public static void main(String[] args) {

        // create an instance of the current class
        MinotaurBirthdayParty currentParty = new MinotaurBirthdayParty();

        // gather the count of the guests expected 
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Minotaur Birthday Party. Please scpecify the number of guests(threads):");
        int numOfGuests = sc.nextInt();
        while (numOfGuests < 1) {
            System.out.println("Invalid number of guests, please chose at least one guest!!");
            numOfGuests = sc.nextInt();
        }
        sc.close();

        // declare an array of guests (threads)
        Thread[] guests = new Thread[numOfGuests];

        // since on the start it was mentioned that there will be cake since the start, we set the cupcake status to present (true)
        // we also set the simulation status to true (it started)
        // we also create the locks we need and set the count of the processed threads (guests) to 0
        currentParty._isCupcakeThere.set(true);
        currentParty._simulationOngoing.set(true);
        currentParty._locks = new LockTuple(new ReentrantLock(), new ReentrantLock());
        currentParty.processedSofar = 0;

        // we pick a random thread to be the one that will handle the cake requests and also the counting of the guests
        Random rand = new Random();
        int decisionGuest = rand.nextInt(numOfGuests);
        
        // catch any out of bound index
        while (decisionGuest < 0 || decisionGuest >= numOfGuests) {
                decisionGuest = rand.nextInt(numOfGuests);
        }
        // inform who is the decision making guest
        System.out.println("The decision guest is: " + decisionGuest);

        // counter for the guests in the looping process, and also a counter for the enteries, these are helpers for debugging/loop counters also.
        int currentNumberOfGuests = 0;
        int currentEnteriesToLabyrinth = 0;
        
        // log the start time
        long startTime = System.currentTimeMillis();

        // as long as the simulation is not stopped based on the conditions and strategy we discussed, we continue to spaw threads
        // randomness is also applied here, as based on the prompt the Minotaur can pick any guest to go into the labyrinth, even pick the same guest 
        // again and even more than once. So we randomize to simulate that
        while (currentParty._simulationOngoing.get() == true) {
            currentNumberOfGuests = rand.nextInt(numOfGuests);
            System.out.println("Guest -> " + currentNumberOfGuests + " is entering the labyrinth");   
            guests[currentNumberOfGuests] = new Thread(new Simulation(currentParty, decisionGuest, numOfGuests, currentNumberOfGuests, currentParty._locks, currentParty._simulationOngoing, currentParty._isCupcakeThere, currentParty.processedSofar));
            guests[currentNumberOfGuests].start();
            currentEnteriesToLabyrinth++;
        }
       
        // after the simulation has ended we simply join the threads
        for (int i = 0; i < numOfGuests; i++) {
            try {
                guests[i].join();
            } catch (InterruptedException e) {
                System.out.println("Guest (thread) index " + i + " was interrupted, please try again!!");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        // log the end time
        long endTime = System.currentTimeMillis();

        // print the output and stats
        currentParty.printOutput(startTime, endTime, numOfGuests, decisionGuest, currentEnteriesToLabyrinth);

   }

   /*
    * @brief: this method prints the stats and output to both the console and to a file.
    * @results: we print the thread that made the decition making (it's index).
    *           we print the time the total simulation too.
    *           we print also the general total number of enteries that were made to the labyrinth.
    */
   private void printOutput(long start, long end, int numOfGuests, int decisionGuest, int totalEntries) {

        File outputFile = new File("MinotaurBirthdayParty_OutputResult.txt");
        try {
                FileWriter fw = new FileWriter(outputFile);
                fw.write("Thread index of -> " + decisionGuest + " reported that the party has ended and that all visitors entred the labyrinth at least once!!\n");
                fw.write("The total time it took for all guests: "+ numOfGuests+" to enter the labyrinth was: " + (end - start) + " milliseconds");
                fw.write("The total number of entries to the labyrinth was: " + totalEntries + "\n");
                fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred, while printing to the file, please try again!!");
        }
        System.out.println("Thread index of -> " + decisionGuest + " reported that the party has ended and that all visitors entred the labyrinth at least once!!");
        System.out.println("The total time it took for all guests: "+ numOfGuests+" to enter the labyrinth was: " + (end - start) + " milliseconds");
        System.out.println("The total number of entries to the labyrinth was: " + totalEntries);
        System.out.println("End of the party : D");

   }

}

/*
 * @brief: this class handles the simulation mechanism at the thread level (guest level) for the Minotaur Birthday Party.
 *         this class extends the thread class to use thread based methods.
 */
class Simulation extends Thread {

    // all the variables needed to perform the simulation while assessing changes and conditions of the current 
    // thread (guest) and the ongoing party.
    private int currentGuestIndex;
    private AtomicBoolean _isCupcakeThere;
    private AtomicBoolean _isPartyStillGoing;
    private LockTuple _locks;
    private int totalNumGuests;
    private int processedSofar;
    private int ateBefore = 0;
    private int indexOfDecisionGuest;
    MinotaurBirthdayParty currentParty;

    /*
     * @brief: constructor for the simulation class.
     */
    Simulation(MinotaurBirthdayParty Object, int decisionGuestIndex, int totalExpectedGuests, int currentIndexOfGuest, LockTuple locks,AtomicBoolean statusOfParty, AtomicBoolean _isCupcakeThere, int processedSofar){
        this.currentGuestIndex = currentIndexOfGuest;
        this._isPartyStillGoing = statusOfParty;
        this._isCupcakeThere = _isCupcakeThere;
        this._locks = locks;
        this.totalNumGuests = totalExpectedGuests;
        this.processedSofar = processedSofar;
        this.ateBefore = 0;
        this.indexOfDecisionGuest = decisionGuestIndex;
        this.currentParty = Object;
    }

    /*
     * @brief: logic of the simulation of the party. As long as the party is still going, we lock the labyrith and then we execute the remaining parts of the logic
     *         based on the presented strategy. At the end we unlock the labyrinth.
     */
    @Override
    public void run() {
        while (isPartyStillGoing()) {
            _locks.getLabyrinthAccessLock().lock();
            try {
                LabyrinthOutcome();
            }finally {
                _locks.getLabyrinthAccessLock().unlock();
            }
        }
    }

    // check if the current guest is the decision maker guest (thread)
    private boolean isDecisionMaker() {
        return this.currentGuestIndex == this.indexOfDecisionGuest;
    }

    // check the status of the cake if it was eaten or no
    private boolean wasCakeEaten() {
        return !this._isCupcakeThere.get();
    }

    // check if the party/simulation is still going
    private boolean isPartyStillGoing() {
        return this._isPartyStillGoing.get();
    }

    // check if all the guests were processed and everyone went into the labyrinth
    private boolean allGuestsWereProcessed() {
        return (this.processedSofar == this.totalNumGuests - 1) || (this.currentGuestIndex == this.totalNumGuests - 1);
    }

    // check if the current guest has eaten before
    private boolean hasEatenBefore() {
        return this.ateBefore == 1;
    }

    /*
     * @brief: the rest of the required logic for my strategy is implemented here.
     *        The decision maker is the only one that can request a new cupcake if it is not there.
     *        Guests only eat once only.
     *        Guests that are not the decision maker cannot request cakes if they don't find a cake.
     *        If a guest goes in and they are not the decision maker, and they don't find a cake, they simply leave.
     *        If all guests went in, we stop the simulation.
     *        If a guest finds the cake and they did not eat before, they eat it!
     * 
     * @note: we use locks as needed here, and also helper methods.
     */
    private void LabyrinthOutcome() {

        if (isDecisionMaker() && wasCakeEaten() && !allGuestsWereProcessed()) {
            _locks.getCupcakeAccessLock().lock();
            try {
                processedSofar++;
                // Use this as needed to test:
                // System.out.println("total of the processed so far ------------------------>" + processedSofar);
                _isCupcakeThere.set(true);
                if(allGuestsWereProcessed()) {
                    _isPartyStillGoing.set(false);
                    currentParty._simulationOngoing.set(false);
                }
                // use this if needed to test:
                //System.out.println("Guest " + this.currentGuestIndex + " has reset the cake for others and is leaving the labyrinth");
            }finally {
                _locks.getCupcakeAccessLock().unlock();
            } 
                
        }
        
        else if(!isDecisionMaker() && wasCakeEaten() == false && hasEatenBefore() == false){
            _locks.getCupcakeAccessLock().lock();
            try {
                if(_isCupcakeThere.get()) {
                    _isCupcakeThere.set(false);
                    this.ateBefore = 1;
                    // use this as needed to test: 
                    // System.out.println("Guest " + this.currentGuestIndex + " has eaten the cake and is leaving the labyrinth for now");
                }
            }finally {
                _locks.getCupcakeAccessLock().unlock();
            }
        }
    }
}
