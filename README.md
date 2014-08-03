oneplusone
=============

Command-line program which generates 1+1 pairings within an organization that is split into teams.

<ul>
<li> <a href="#how-to-use">How to use</a>
  <ul>
  <li> <a href="#installation">Installation</a>
  <li> <a href="#generating-pairs">Generating Pairs</a>
  <li> <a href="#managing-the-database">Managing the database</a>
  </ul>
<li> <a href="#methodology">Methodology</a>
  <ul>
  <li> <a href="#pair-generation">Pair Generation</a>
  <li> <a href="#technologies">Technologies</a>
  <li> <a href="#files">Files</a>
  <li> <a href="#algorithm">Algorithm</a>
  <li> <a href="#testing">Testing</a>
  </ul>
</ul>

## How to Use

### Installation

Options:
<ul>
<li> Import project into Eclipse.
<li> Compile on your own.
<li> Download latest release <a href="https://github.com/cheniel/oneplusone/releases">HERE.</a> 
</ul>

Run from command line using:
> java -jar oneplusone.jar;*.jar;*.zip

### Generating pairs
Once you have run the program and set up at least one organization, generate pairs for an organization using:
> run     

The program will then prompt you for an organization name. Type in the name of the organization that you want pairs for. 

### Managing the database
Here is an overview of all of the instructions that you can run from the oneplusone command line (view anytime using "?"):
```
	---------------------------------------------------     
	EXECUTE       
		run	|	run pairings for this week.     
	VIEW      
		vo	|	view organization tree      
		lo	|	list organizations          
		vm	|	view members of organization      
	ADD     
		amt	|	add member-team     
		norg	|	add new organization      
		lorg	|	load organization from file     
	DELETE      
		rmt	|	remove member from team     
		rm	|	remove member from all teams        
		rt	|	remove team (keep members)      
		dorg	|	delete organization       
	OTHER       
		?	|	display commands        
		q	|	quit        
	---------------------------------------------------       
```

Most of these commands are straightforward and should allow you to do anything you need to manage an organization's teams and membership. Operations that make changes automatically save to the database.

#### Importing from file
oneplusone allows for a simple way to quickly load a database using a CSV-like file. The command for this is "lorg". Example files can be found in the <a href="https://github.com/cheniel/oneplusone/tree/master/orgs">"orgs"</a> folder of this repo. 

It is easy to create your own version of this file to quickly load a desired organization. Here is the format (this one is <a href="https://github.com/cheniel/oneplusone/blob/master/orgs/3-8">3-8</a>):
```
p1;t1   
p2;t1   
p3;t1,t3    
p4;t1,t3    
p5;t2,t3    
p6;t2,t3    
p7;t2     
p8;t2   
```
Each line corresponds to a different member of the organization. The text before the semi-colon is the member's name, and the text after the semi-colon lists the teams that member is in, separated by commas. The organization name is given by the filename.

To load this organization into the database, you first enter the command
> lorg      

And then the file name
> FILENAME

If you need to traverse directories to file, the path is ignored when producing the organization name. For example, the organization name if you enter a path like this:
> ./orgs/FILENAME

is FILENAME.

## Methodology
This section outlines the methodology used to generate pairs and specifics on the implementation.

### Pair Generation
oneplusone matchings are determined by viewing the problem as a weighted constraint satisfaction problem. A recursive backtracking function is used to determine the optimal matchings given the conditions and organization structure. Steps are taken to prune off paths which have already been deemed to be sub-optimal, and the best path based on a cost algorithm is the one generated from the backtracking.

With this algorithm, we can guarantee that pairings produced will make sure that each person has at least one matching per week, and the assignment produced is the optimal solution based on the costs outlined in <a href="#matching-priorities">matching priorities.</a>

#### Algorithm
Here is psuedocode. View the real thing <a href="https://github.com/cheniel/oneplusone/blob/master/src/oneplusone/WeightedCSP.java">HERE.</a>
```
int wBacktracking(indexOfMember, bestCostFoundSoFar, costSoFar)   {
  if assignment is complete 
    return costSoFar
  else
    current = getCurrentPersonToAssign(indexOfMember)
    teammates = getTeammatesSortedByCost(current) 
    for costs that make sense given costSoFar and bestCostFoundSoFar
      for teammates that cost that much to pair with
        assign teammate to current
        bestCostOfPickingThatTeammate = wBacktracking(indexOfMember + 1, bestCostFoundSoFar, costSoFar + costOfPairing)
        if (bestCostOfPickingThatTeammate < bestCostFoundSoFar) 
          store that teammate as the best match for current 
          update bestCostFoundSoFar 
        unassign teammate to current 
  return bestCostFoundSoFar
```

A result's cost is the summation of all of the costs of pairings based on the cost structure in the next section. The result with the lowest cost is generated.

To run this properly, two data structures must be created, and the top wBacktracking call must be made. This looks something like this:
```
assignment = new
bestPartner[] (indexOfPerson => set of partners) = new
wBacktracking(0, Integer.MAX_VALUE, 0)
```
Once this code is run, assignment will actually be empty, as wBacktracking removes all assignments that it makes. However, during wBacktracking's run it also kept track of the best partners for each person which creates the optimal assignment. To get this optimal assignment, we do this:
```
For all indices in bestPartner
	for all partners in bestPartner[i]
		assign partner to person at index
```
Now, assignment is the optimal assignment, and is returned by the called of wBacktracking.


#### Matching priorities
The backtracking algorithm implements a cost structure which determines which path is best. Teammates are given the highest cost that they fall under, unless it is 0, and potential teammates are evaluated from lowest to highest cost (lower cost is preferred). During each pairing, each person must select at least one teammate.

Here are the cost categories:
<ol>
<li> The person has already paired with these teammates. Cost of 0.
<li> Teammates who have not been paired up with yet. Cost of 1.
<li> Teammates who are already paired up with someone else. Cost of 2.
<li> Teammates who have been paired up with previous in this cycle (see next section on cycles). Cost of 3.
<li> The person paired up with these teammates last time. Cost of 4.
<li> The person paired up with these teammates last time, and they are already matched up with someone else for this cycle. Cost of 5.
</ol>
This is implemented in getSortedTeammates() of <a href="https://github.com/cheniel/oneplusone/blob/master/src/oneplusone/PairingAssignment.java">PairingAssignment.java</a>

#### The cycle
The cycle is a recordkeeping concept used to satisfy the constraint:
> Teammates should be paired with equal frequency: if A and B are paired together this week, A shouldn't be paired with B again until A has paired with every other person on the team.

This is implemented using two variables: the cycleCount in the Person class and the matched boolean in the Teammate class.

Here are the rules for the changes in these variables that allow this to happen:
<ol>
<li> The cycleCount for an individual person is incremented every time a teammate whose matched is false is paired with the person.
<li> Everytime a teammate is paired with a person, that teammates matched becomes true (after cycleCount is increased, if necessary).
<li> When the cycleCount is the same as the number of teammates that the person has, the cycleCount resets back to zero and all teammates matched values become false
<li> A teammate with a matched value of true is given a larger cost to pair with.
</ol>

Using these rules, a teammate who has been paired with already before other teammates have paired with the person is given a higher cost than the teammates who have not been paired with this person as much. The implementation of cost can be seen in <a href="https://github.com/cheniel/oneplusone/blob/master/src/oneplusone/Person.java">Person.java</a>

### Technologies
<ul>
<li> Java 1.7
<li> Eclipse Luna
<li> JUnit for unit testing
<li> db4o for the database
</ul>

### Files

### Algorithm

### Testing
