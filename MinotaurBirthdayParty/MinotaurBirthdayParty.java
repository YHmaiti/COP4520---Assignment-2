/*
 * @author: Yohan Hmaiti
 * @date: 02/05/2023
 * @brief: more documentation will be added soon
 */

import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

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

public class MinotaurBirthdayParty {

    private AtomicBoolean _isCupcakeThere = new AtomicBoolean(true);
    private AtomicBoolean _simulationOngoing = new AtomicBoolean(true);
    LockTuple _locks = new LockTuple(new ReentrantLock(), new ReentrantLock());
    private int processedSofar = 0;

   public static void main(String[] args) {

        MinotaurBirthdayParty currentParty = new MinotaurBirthdayParty();
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Minotaur Birthday Party. Please scpecify the number of guests(threads):");
        int numOfGuests = sc.nextInt();
        while (numOfGuests < 1) {
                System.out.println("Invalid number of guests, please chose at least one guest!!");
                numOfGuests = sc.nextInt();
        }
        sc.close();

        Thread[] guests = new Thread[numOfGuests];
        currentParty._isCupcakeThere.set(true);
        currentParty._simulationOngoing.set(true);
        currentParty._locks = new LockTuple(new ReentrantLock(), new ReentrantLock());
        currentParty.processedSofar = 0;

        int currentNumberOfGuests = 0;
        
        long startTime = System.currentTimeMillis();

        while (currentNumberOfGuests < numOfGuests) {
            
                guests[currentNumberOfGuests] = new Thread(new Simulation(numOfGuests, currentNumberOfGuests, currentParty._locks, currentParty._simulationOngoing, currentParty._isCupcakeThere, currentParty.processedSofar));
                guests[currentNumberOfGuests].start();
                currentNumberOfGuests++;
        }
       
        for (int i = 0; i < numOfGuests; i++) {
                try {
                        guests[i].join();
                } catch (InterruptedException e) {
                        System.out.println("Guest (thread) index " + i + " was interrupted, please try again!!");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                }
        }

        long endTime = System.currentTimeMillis();

        currentParty.printOutput(startTime, endTime, numOfGuests);

   }

   private void printOutput(long start, long end, int numOfGuests) {

        File outputFile = new File("MinotaurBirthdayParty_OutputResult.txt");
        try {
                FileWriter fw = new FileWriter(outputFile);
                fw.write("The total time it took for all guests: "+ numOfGuests+" to enter the labyrinth was: " + (end - start) + " milliseconds");
                fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred, while printing to the file, please try again!!");
        }

        System.out.println("The total time it took for all guests: "+ numOfGuests+" to enter the labyrinth was: " + (end - start) + " milliseconds");
        System.out.println("End of the party : D");

   }

}

class Simulation extends Thread {

    private int currentGuestIndex;
    private AtomicBoolean _isCupcakeThere;
    private AtomicBoolean _isPartyStillGoing;
    private LockTuple _locks;
    private int totalNumGuests;
    private int processedSofar;
    private int ateBefore = 0;

    Simulation(int totalExpectedGuests, int currentIndexOfGuest, LockTuple locks,AtomicBoolean statusOfParty, AtomicBoolean _isCupcakeThere, int processedSofar){
        this.currentGuestIndex = currentIndexOfGuest;
        this._isPartyStillGoing = statusOfParty;
        this._isCupcakeThere = _isCupcakeThere;
        this._locks = locks;
        this.totalNumGuests = totalExpectedGuests;
        this.processedSofar = processedSofar;
        this.ateBefore = 0;
    }

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

    private boolean isFirstGuestLeader() {
        return this.currentGuestIndex == 0;
    }

    private boolean wasCakeEaten() {
        return !this._isCupcakeThere.get();
    }

    private boolean isPartyStillGoing() {
        return this._isPartyStillGoing.get();
    }

    private boolean allGuestsWereProcessed() {
        return (this.processedSofar == this.totalNumGuests - 1) || (this.currentGuestIndex == this.totalNumGuests - 1);
    }

    private boolean hasEatenBefore() {
        return this.ateBefore == 1;
    }

    private void LabyrinthOutcome() {

        if (isFirstGuestLeader() && wasCakeEaten() && !allGuestsWereProcessed()) {
            _locks.getCupcakeAccessLock().lock();
            try {
                processedSofar++;
                // print the updated processedSofar
                System.out.println("total of the processed so far ------------------------>" + processedSofar);
                _isCupcakeThere.set(true);
                if(allGuestsWereProcessed()) {
                    _isPartyStillGoing.set(false);
                }
                System.out.println("Guest " + this.currentGuestIndex + " has reset the cake for others and is leaving the labyrinth");
            }finally {
                _locks.getCupcakeAccessLock().unlock();
            } 
                
        }
        
        else if(!isFirstGuestLeader() && wasCakeEaten() == false && hasEatenBefore() == false){
            _locks.getCupcakeAccessLock().lock();
            try {
                if(_isCupcakeThere.get()) {
                    _isCupcakeThere.set(false);
                    this.ateBefore = 1;
                    System.out.println("Guest " + this.currentGuestIndex + " has eaten the cake and is leaving the labyrinth");
                }
            }finally {
                _locks.getCupcakeAccessLock().unlock();
            }
        }
        
        
    }

}
