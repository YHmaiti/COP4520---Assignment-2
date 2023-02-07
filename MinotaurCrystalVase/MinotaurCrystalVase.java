import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;


// create a class for the locks that might be needed
class Locks{

    private Lock showRoomLock;

    Locks(Lock showRoomLock){
        this.showRoomLock = showRoomLock;
    }

    public Lock getShowRoomLock(){
        return showRoomLock;
    }

    public void setShowRoomLock(Lock showRoomLock){
        this.showRoomLock = showRoomLock;
    }

    public void lock_showRoomLock(){
        showRoomLock.lock();
    }

    public void unlock_showRoomLock(){
        showRoomLock.unlock();
    }

}

class Simulation extends Thread{

    private int _guestIndex;
    private MinotaurCrystalVase _minotaurParty;
    private int _cntOfVisits;
    public AtomicBoolean _hasWatchedCrystalVase;
    public AtomicBoolean _AvailableRoom;


    Simulation(int _guestIndex, MinotaurCrystalVase _minotaurParty, int totalVisitsSoFar){
        this._guestIndex = _guestIndex;
        this._minotaurParty = _minotaurParty;
        this._cntOfVisits = totalVisitsSoFar;
        this._hasWatchedCrystalVase = new AtomicBoolean(false);
        this._AvailableRoom = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        while(_minotaurParty.checkIfEveryoneSawTheCrystalVase() == false) {
            
            if(_AvailableRoom.get() == true) {
                System.out.println("Guest " + _guestIndex + " is going to see the crystal now.");
                // write this to a file too 
                try {
                    FileWriter myWriter = new FileWriter("output_listOfAllAccesses.txt", true);
                    myWriter.write("Guest " + _guestIndex + " is going to see the crystal now.\n");
                    myWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                _AvailableRoom.set(false);
               /*  System.out.println("The room sign says \"BUSY\" now."); */
                _minotaurParty.showRoomLock.lock_showRoomLock();
                _cntOfVisits++;
                _minotaurParty.total_Visits++;
                try{
                    // we simulate the visiting process by making the current thread sleep for 2s
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _minotaurParty.showRoomLock.unlock_showRoomLock();
                _AvailableRoom.set(true);
                /* System.out.println("The room sign says \"Available\" now."); */
                _hasWatchedCrystalVase.set(true);
            }else{
                continue;
            }
            
            if (_minotaurParty.checkIfEveryoneSawTheCrystalVase() == true) {
                break;
            }
        }
    }

}
public class MinotaurCrystalVase extends Thread {

    // create an instance of the locks class
    public Locks showRoomLock;
    public int numberofGuests_threads;
    public int total_Visits;
    public static ArrayList<Simulation> visitorGuests;
    // create a concurrent queue 
    public static void main(String[] args) throws IOException, InterruptedException{
        
        // first we will prompt the user for the number of guests (threads) that are within the Minotaur's party 
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the guest number (threads) in the Minotaur's party -> ");
        int totalGuestNumber = sc.nextInt();

        // check if its an integer and at least one gues is in the party 
        while(totalGuestNumber < 1) {
            System.out.println("The party has less than one guest, are you sure you invited people? (try again) -> ");
            totalGuestNumber = sc.nextInt();
        }

        MinotaurCrystalVase newPartyWithVase = new MinotaurCrystalVase();
        newPartyWithVase.showRoomLock = new Locks(new ReentrantLock());
        newPartyWithVase.numberofGuests_threads = totalGuestNumber;
        visitorGuests = new ArrayList<Simulation>();
        newPartyWithVase.total_Visits = 0;
        
        sc.close();

        
        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();
        int counter = 0;
        while (counter < totalGuestNumber) {
            try {
                // chose a random thread index from 0 to totalGuestNumber - 1
                int i = new Random().nextInt(totalGuestNumber);
                visitorGuests.add((new Simulation(i, newPartyWithVase, newPartyWithVase.total_Visits)));
                counter++;
            } catch (Exception e) {
                System.out.println("An error happened while creating the threads, please try again.");
                e.printStackTrace();
            }
        }

        for(int i = 0; i < totalGuestNumber; i++) {
            try {
                visitorGuests.get(i).start();
            } catch (Exception e) {
                System.out.println("An error happened while starting the threads, please try again.");
                e.printStackTrace();
            }
        }

        for(int i = 0; i < totalGuestNumber; i++) {
            try {
                visitorGuests.get(i).join();
            } catch (Exception e) {
                System.out.println("An error happened while joining the threads, please try again.");
                e.printStackTrace();
            }
        }

        endTime = System.currentTimeMillis();
        String MessageOutput = "The Minotaur's Vase party took -> " + (endTime - startTime) + " milliseconds and the number of total accesses to the showRoom (vase location) was -> " + newPartyWithVase.total_Visits + ".\n";
        // add the total number of processed threads to the output message
        MessageOutput += "The total number of processed threads was -> " + totalGuestNumber + ".\n";

        print(MessageOutput);
    }

    // we set a standard output method to print all the details to a file and to the console 
    public static void print(String output) {
        System.out.println(output);
        try (FileWriter fw = new FileWriter("MinotaurCrystalVase_resultOutput.txt")) {
            fw.write(output);
            fw.close();
        } catch (IOException e) {
            System.out.println("An error happened while printing to the file, please try again.");
            e.printStackTrace();
        }
        
    }

    public boolean checkIfEveryoneSawTheCrystalVase() {
        boolean result = true;
        // use a Lenq or Lambda
        for (Simulation guest : visitorGuests) {
            if (guest._hasWatchedCrystalVase.get() == false) {
                result = false;
                break;
            }
        }
        return result;
    }

}