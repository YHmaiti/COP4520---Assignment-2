import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.*;


public class MinotaurCrystalVase {

    public static void main(String[] args) {
        
        // first we will prompt the user for the number of guests (threads) that are within the Minotaur's party 
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the guest number (threads) in the Minotaur's party -> ");
        int totalGuestNumber = sc.nextInt();

        while(totalGuestNumber < 1) {
            System.out.println("The party has less than one guest, are you sure you invited people? (try again) -> ");
            totalGuestNumber = sc.nextInt();
        }

        // now we prompt for the time the party will take
        System.out.println("What is the duration of the party? (in seconds) -> ");
        int durationOfParty = sc.nextInt();
        while (durationOfParty < 1) {
            System.out.println("The party has less than one second, are you sure you want to have a party? (try again) -> ");
            durationOfParty = sc.nextInt();
        }
    }

}