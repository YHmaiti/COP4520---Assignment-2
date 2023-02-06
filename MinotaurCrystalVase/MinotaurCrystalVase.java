import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.*;


public class MinotaurCrystalVase {

    private int totalNumberOfGuests;

    MinotaurCrystalVase(){
        this.totalNumberOfGuests = 0;
    }

    // we create a function that will create the thread of each guest 
    private void generateGuests(int numberOfGuests) {
        
    }
    public static void main(String[] args) {
        
        // first we will prompt the user for the number of guests (threads) that are within the Minotaur's party 
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the guest number (threads) in the Minotaur's party -> ");
        int totalGuestNumber = sc.nextInt();

        while(totalGuestNumber < 1) {
            System.out.println("The party has less than one guest, are you sure you invited people? (try again) -> ");
            totalGuestNumber = sc.nextInt();
        }

        sc.close();




    }

}