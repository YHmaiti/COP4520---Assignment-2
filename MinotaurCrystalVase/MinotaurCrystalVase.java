/*
 * @author: Yohan Hmaiti
 * @date: 02/9/2023
 * 
 * @strategy: For this problem I have used the second strategy that consists on the following:
            * The Minotaur’s second strategy allowed the guests to place a sign on the door
            * indicating when the showroom is available. The sign would read AVAILABLE or
            * BUSY. Every guest is responsible to set the sign to BUSY when entering the
            * showroom and back to AVAILABLE upon exit. That way guests would not bother trying
            * to go to the showroom if it is not available.
 *
 * @note: discussion and more explanation is on the ReadMe file
 * @note: also in the process a guest can visit several times, but no need to a manually made randomization, we let the master thread do it
 *        when running child threads.
 */

// pre-processor directives
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


/*
 * @brief: This class handles lock operations as needed by either the simulation or the MinotaurCrystalVase class.
 *         The main lock here that is used is the one to lock the showroom or unlock it.
 */
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

/*
 * @brief: class that handles the main logic and mechanism of the simulation of the party and the visits to the showroom.
 *         This class extends the thread class to allow for the use of thread related operations and methods as needed.
 */
class Simulation extends Thread{

    // variables that are needed for the simulation and also for the functioning of the thread based on the strategy implemented
    private int _guestIndex;
    private MinotaurCrystalVase _minotaurParty;
    private int _cntOfVisits;
    public AtomicBoolean _hasWatchedCrystalVase;
    public AtomicBoolean _AvailableRoom;


    // constructor
    Simulation(int _guestIndex, MinotaurCrystalVase _minotaurParty, int totalVisitsSoFar){
        this._guestIndex = _guestIndex;
        this._minotaurParty = _minotaurParty;
        this._cntOfVisits = totalVisitsSoFar;
        this._hasWatchedCrystalVase = new AtomicBoolean(false);
        this._AvailableRoom = new AtomicBoolean(true);
    }

    // method that handles the main logic of the simulation for the thread
    @Override
    public void run() {

        // as long as not every guest in the party has seen the crystal vase we still keep the showcasing ongoing
        while(_minotaurParty.checkIfEveryoneSawTheCrystalVase() == false) {
            
            // check if the room is available
            if(_AvailableRoom.get() == true) {

                // print the current guest that is going to see the crystal
                // use this for testing if needed:
                /* System.out.println("Guest " + _guestIndex + " is going to see the crystal now."); */
                
                // set the room to unavailable
                _AvailableRoom.set(false);
                
                // use this for testing if needed:
                // System.out.println("The room sign says \"BUSY\" now.");
                
                // lock the room till the current guest that is seeing the vase is done
                // we also increment the count of visits and the total visits to the showroom
                _minotaurParty.showRoomLock.lock_showRoomLock();
                _cntOfVisits++;
                _minotaurParty.total_Visits++;

                // we make the thread sleep for 0.010 seconds as requested by the professor when asked
                // to simulate time spent watching the vase by the current guest
                try{
                    Thread.sleep(10); // for testing feel free to change this as needed but this accounts for the runtime of course!!!
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // after the guest is done we simply unlock the show room and make it available for a new guest to come
                _minotaurParty.showRoomLock.unlock_showRoomLock();
                _AvailableRoom.set(true);

                // use this for testing if needed:
                // System.out.println("The room sign says \"Available\" now.");
                
                // set the current guest to a guest that has seen the vase
                _hasWatchedCrystalVase.set(true);

            }else{
                // if not available just move on
                continue;
            }
            
            // if everyone saw the vase we can stop the simulation
            if (_minotaurParty.checkIfEveryoneSawTheCrystalVase() == true) {
                break;
            }
        }
    }
}

/*
 * @brief: this class contains the driver method and also handles the thread spawning and joining along with additional simulation logic as needed.
 */
public class MinotaurCrystalVase extends Thread {

    // create an instance of the locks class
    // and declare additional variables to hold the number of visits, guests, and a list of guests
    public Locks showRoomLock;
    public int numberofGuests_threads;
    public int total_Visits;
    public static ArrayList<Simulation> visitorGuests;

    // driver method
    public static void main(String[] args) throws IOException, InterruptedException{
        
        // get the number of guests
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the guest number (threads) in the Minotaur's party -> ");
        int totalGuestNumber = sc.nextInt();

        // check if the number of guests is valid
        while(totalGuestNumber < 1) {
            System.out.println("The party has less than one guest, are you sure you invited people? (try again) -> ");
            totalGuestNumber = sc.nextInt();
        }

        // create an instance of the current class and assign the total number of guests, assign the lock, and initialize the list of guests (threads)
        MinotaurCrystalVase newPartyWithVase = new MinotaurCrystalVase();
        newPartyWithVase.showRoomLock = new Locks(new ReentrantLock());
        newPartyWithVase.numberofGuests_threads = totalGuestNumber;
        visitorGuests = new ArrayList<Simulation>();
        newPartyWithVase.total_Visits = 0;
        
        sc.close();

        // time helper variables and helper counter
        long startTime;
        long endTime;
        int counter = 0;
        
        // as long as all threads were not spawned and not created we continue looping
        // and creating them through the declared array list of threads/guests
        while (counter < totalGuestNumber) {
            try {
                visitorGuests.add((new Simulation(counter + 1, newPartyWithVase, newPartyWithVase.total_Visits)));
                counter++;
            } catch (Exception e) {
                System.out.println("An error happened while creating the threads, please try again.");
                e.printStackTrace();
            }
        }

        // log the start time
        startTime = System.currentTimeMillis();

        // start the threads and the simulation
        for(int i = 0; i < totalGuestNumber; i++) {
            try {
                visitorGuests.get(i).start();
            } catch (Exception e) {
                System.out.println("An error happened while starting the threads!!! please try again.");
                e.printStackTrace();
            }
        }

        // join the threads
        for(int i = 0; i < totalGuestNumber; i++) {
            try {
                visitorGuests.get(i).join();
            } catch (Exception e) {
                System.out.println("An error happened while joining the threads!!! please try again.");
                e.printStackTrace();
            }
        }

        // log the end time
        endTime = System.currentTimeMillis();

        // generate an output that has the duration of the simulation, and the total guest number along with the total amount of visits that were made
        String MessageOutput = "The Minotaur's Vase party took -> " + (endTime - startTime) + " milliseconds and the number of total accesses to the showRoom (vase location) was -> " + newPartyWithVase.total_Visits + ".\n";
        MessageOutput += "The total number of processed threads was -> " + totalGuestNumber + ".\n";
        print(MessageOutput);

    }

    // method that handles printing the output/stats to a file
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

    // a method that simply checks if all the guests have seen the crystal vase
    public boolean checkIfEveryoneSawTheCrystalVase() {
        boolean result = true;
        for (Simulation guest : visitorGuests) {
            if (guest._hasWatchedCrystalVase.get() == false) {
                result = false;
                break;
            }
        }
        return result;
    }

}