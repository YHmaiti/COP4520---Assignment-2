# COP4520 - Assignment 2 

# To Compile and Run Problem 1 
``` 
javac MinotaurBirthdayParty.java
java MinotaurBirthdayParty 
``` 
# To Compile and Run Problem 2 
``` 
javac MinotaurCrystalVase.java
java MinotaurCrystalVase 
``` 

# Problem 1 - Minotaur Birthday Party:

# Proof of Correctness and Efficiency:

I use a random number generator to chose a random guest that will act as a decider, meaning that they will be the only guest that will 
request a cupcake from the Minotaur whenever they see that it is not there, they will also keep track of the count of the number of guests that 
went into the labyrinth and let the Minotaur know at the end that everyone went through the labyrinth at least 1 time. I was inspired to follow this strategy
based on the prisoners examples we discussed in class. 

Now, if a guest goes in and they see the cake, and they did not eat before, they can eat it once. No guest 
will eat more than once, thus, if they already ate, next time they go through and they find a cake, they will not touch it. If a guest that is not the chosen decider,
goes in and doesn't find a cake they will simply leave without requesting a cupcake. If the decider goes in and doesn't find a cake, they are the only guest that can request one, and increment the 
count of the guests at each time they request a cake, as that means someone for sure ate it before the decider came in. 

When the decider reaches a count of n - 1 that means that everyone went through the labyrinth at least 1 time. 

In this problem the labyrinth was represented with a lock since only one guest will go in at a time, and the cake was implemented as an atomic boolean too. When running the program 
The number of guests will be taken through input. 

This strategy I used is correct and efficient, it is correct based on the explantion given and through the code also (documented too with comments), and the efficiency can be shown through 
the experimental evaluation part where when I even had 500 guests my solution still performed under 3s while the total enteries to the labyrinth was 75888352. 

# Remark: 
Decision maker is not the same at each time you run the program.
Threads (guests) can enter the labyrinth more than once, so the number of enteries you have at each run of the program can differ, but still the program will complete 
under an efficient and optimized execution time. 

# Experimental Evaluation and Efficiency: 
I varied the number of guests that can go in, I tested with 100 up to 500, and I report the results here: 
Note: the decision guest is chosen at the start, and I report the time taken, number of enteries to the labyrinth (shows that indead there is randomness and threads that went in more than once).
 
100 
The decision guest is: 91
Thread index of -> 91 reported that the party has ended and that all visitors entred the labyrinth at least once!!
The total time it took for all guests: 100 to enter the labyrinth was: 175 milliseconds
The total number of entries to the labyrinth was: 1765764 
 
200 
The decision guest is: 152
Thread index of -> 152 reported that the party has ended and that all visitors entred the labyrinth at least once!!
The total time it took for all guests: 200 to enter the labyrinth was: 465 milliseconds
The total number of entries to the labyrinth was: 9427200 
 
300 
The decision guest is: 2
Thread index of -> 2 reported that the party has ended and that all visitors entred the labyrinth at least once!!
The total time it took for all guests: 300 to enter the labyrinth was: 910 milliseconds
The total number of entries to the labyrinth was: 24578365 
 
500 
The decision guest is: 394
Thread index of -> 394 reported that the party has ended and that all visitors entred the labyrinth at least once!!
The total time it took for all guests: 500 to enter the labyrinth was: 2760 milliseconds
The total number of entries to the labyrinth was: 75888352 
 

# Problem 2 - Minotaur Crystal Vase: 

# Proof of Correctness and Efficiency: 
Strategy from the prompt: 
            * The Minotaur’s second strategy allowed the guests to place a sign on the door 
            * indicating when the showroom is available. The sign would read AVAILABLE or 
            * BUSY. Every guest is responsible to set the sign to BUSY when entering the 
            * showroom and back to AVAILABLE upon exit. That way guests would not bother trying 
            * to go to the showroom if it is not available. 

I chose strategy number two, which consists in the guest that goes into the showroom marks its status as Busy and when they leave they 
make it available. Only one guest at a time inside, when inside the thread (guest) watches the vase for 10ms. The simulation runs as long as all guests 
did not see the vase at least once. A guest can go in more than once if they want, so randomness is expected. Yet the program stops once every guest visited the 
showroom at least once. 

I use an atomic boolean for the available or busy status and I also lock the room when someone goes in and I put the thread to sleep for 10ms to simulate viewing the vase.

The choice behind this strategy is as follows: 
# Advantages: 
Guests do not need to wait in a queue doing nothing, especially when N is high. 
Guests do not need to check the order continuously, they only need to do it periodically and that is reflected by a sign of either "Available" or "Busy".
This strategy saves time and memory, since it only requires the use of a lock for the room and upadting its status compared to using a parallel queue or so. 


# Some disadvantages:
There isn't really an order that the guests would know unlike when a queue is used. 
A guest can potentially wait for a bit before getting in, especially when N is very high.


# Why are strat 1 and 3 worse: 
For strategy 3, the guests can go the queue and then stay there for a long while without doing other things/tasks, which can be a huge time waste for them.
For strategy 1, there will be overcrowding as all guests will be around the showroom along with a possibility that the treads (guests) will remain there inactive 
while a person is inside the showroom. 
For strategy 3, following a queue system regardless of the data structure used in that case will most likely cause issues with memory compared to the strategy two that uses a lock simply.
For strategy 1, it has a high potential of starvation, even if it is maybe one of the fastest solutions available. 


Efficiency can also be seen through the experimental evaluation, as even for 500 threads, we got a total of 1857 visits to the vase
and a runtime of 28891ms, but since the sleep time is 10ms -> 28891 - (10 * 1857) = 10321ms to run all 500 threads with possibily of the guest to go in more than once. Other inputs were also given to proof efficiency please refer to the evaluation section. 

# Remark: 
The choice of the sleep time was made after referring to the prompt and to the professor. 
The choice to end the party after at least all guests went in at least once was also approved by the professor. 

# Experimental Evaluation: 

# Remark: 
Thread sleep time when inside the showroom: 10ms !!!!!!!!!!
Also, keep in mind that a thread can enter more than once if they want... entery time was reported too !!!!!!!!!!!!

100 
The Minotaur's Vase party took -> 3881 milliseconds and the number of total accesses to the showRoom (vase location) was -> 249.
The total number of processed threads was -> 100. 
 
200 
The Minotaur's Vase party took -> 18455 milliseconds and the number of total accesses to the showRoom (vase location) was -> 1185.
The total number of processed threads was -> 200. 
 
300 
The Minotaur's Vase party took -> 26851 milliseconds and the number of total accesses to the showRoom (vase location) was -> 1724.
The total number of processed threads was -> 300. 
 
500 
The Minotaur's Vase party took -> 28891 milliseconds and the number of total accesses to the showRoom (vase location) was -> 1857.
The total number of processed threads was -> 500. 

