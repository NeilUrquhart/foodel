# Todo 

This markdown file serves as a place to document bugs, problems and suggestions
for Neil. 

## Bugs
- ~~(known issue) Takes a long time to upload a problem file.  ~~ Fixed
- ~~ Maps can overflow (spill off page) ~~ Fixed
- ~~Solver throws an error if CSV file is malformed e.g:  - Fixed
  - ~~An empty CSV file 
  - Date/time information is missing 
- If you visit "/upload" directly from the browser (i.e. GET request) a 500
  error occurs
- Can sometimes take a while to redirect once job is done (even when the job is
  already done)
- Cannot find cause of issue:
  - Sorry, I cannot find that job. - root cause undefined
  - ![image](https://user-images.githubusercontent.com/15977217/121962214-59147700-cd60-11eb-84aa-c417bdd608a1.png) - (when ran with 2 bags it is fine), although it runs in Eclipse)
  - situation: Clone first address and make it once with 1 bag and the second time the address appears with 3 bags - but different customer name (would be benefitial if program could combine the same addresses, but still keep a memory of the customer)
      * observation: 
      * when an address non repeated is run with 3 bags it runs fine
      * if a repeated address has 1 bag and other one has one is fine (works with 2 as well, but 3 it crashes)
      * noticed that last address was missing a value - tested if it works with 2 and 1 bags and it does but it crashed when 3 bags 
  - file causing issue - https://livenapierac-my.sharepoint.com/:x:/g/personal/40313507_live_napier_ac_uk/ER-F_bXhvQVOtROGiB0iMP8B1rucTh2vAytGNcQEPkckgg?e=4yJg0W - copied and pasted same data from table with addresses into another spreadsheet and it works
- Treating 0 bags as a delivery
  - ![image](https://user-images.githubusercontent.com/15977217/121968874-04c2c480-cd6b-11eb-9ccb-7176e0cdd3a7.png)
  - Eventhough 219 Colinton Road Edinburgh has 0 bags it is still treated as a address, may be benefitial to not consider it as one 
- Maybe an issue with bags and capacity - does not consider bags just addresses 
  - ran experiment with adress having 4 bags and it did not change the output when an address has 10 bags or 1 
  - there is no difference it time either between these experiments (if car has a capacity of 4 bags per vehicle it would need 1 run just to the address and multiple to the one with 10 bags)
  - Extended: if we input the same addresses so they repeat though out the spreadsheet it considers them as additions and makes more runs but not if they are inputted in the same column as more than 1 bag per address
- Inputting for example an Bulgarian location should be producing an error message for example (highly unlikely that such an address will be inputed) but it is good to be considered for out of UK distances where it is quite making the impossible run: 
 - ![image](https://user-images.githubusercontent.com/15977217/121967579-8cf39a80-cd68-11eb-945b-c1651ebc3fd4.png) ![image](https://user-images.githubusercontent.com/15977217/121967624-9d0b7a00-cd68-11eb-8005-2071f2e81748.png)
 - tested with a location in Glasgow and works quite well
 
## Problems
- If map data missing, the page endlessly refreshes after a job has been
  submitted. No message / indicator on web interface if map data missing.


## Suggestions
- We could maybe convert the project to a Maven project so dependencies are
  fetched when needed as opposed to being bundled with the repository.
  Dependencies would be listed in a pom.xml file. This is currently how the
  Pimps project repository is set up:
  [https://github.com/PIMPS-Napier/PIMPS-Server/blob/development/pom.xml](https://github.com/PIMPS-Napier/PIMPS-Server/blob/development/pom.xml).
- Write some code to fetch the map data from the web so the user doesn't need to
  download it themselves. Thus avoids bundling map data with the repository and
  also means the user doesn't need to download it and place it in the data
  folder themselves.
- Set up git repo to ignore java and eclipse generated files. An example is
  here:
  [https://github.com/github/gitignore/blob/master/Global/Eclipse.gitignore](https://github.com/github/gitignore/blob/master/Global/Eclipse.gitignore).
  Kind of feeds into point suggestion 1 so as few things as necessary are stored
  in the repository.
- May also be helpful to ignore other non-essential files e.g: .DS_Store files
- Minor CSS adjustments:
  - Change padding on page body so no gap
  - Maybe have the footer stick to the bottom of the window so there's no whitespace at the bottom
- Providing guidance on how to launch the program as a programmer would be useful as well as it depends how the program is loaded as well: (if not fixed at least detailed)
   - when launching Ecliplse as a workspace I had to choose the folder the project was in so it recognises the optimiser and solver otherwise it would recognise more folder and when building the path it creates issues as it cannot find the optimizer (or anything within the project)
   - an extra step would be to add the optimizer to the build path to solver (right click on solver (named "server") -> build parth -> configure build path -> Projects -> Select optimizer (named "Foodel")
- Staring point of program should be described that it is /server/src/edu/napier/foodel/server/Server.java (that is where the main is so Sever needs to be run) 
- when a job is finished a submit another job can be added so the user does not have to go back to the initial page but rather press a button 
- a button to dowload all GPX Files could be added 
- for more than one bag per address a note can be added on the Output where the runs are produced 
- back button can be added for ease of use 
- dowload/copy all URLs for Foodel Status can be added - where it displays all the solved jobs 
  - it can display which jobs have failed so the user can compare what are the changes between the excel files (if not uploaded files at least the names the time that it has been run in case user can reverse changes if it is the same file used)  
- both maps can be combined into one with runs being in different color (may be benefitial to user for comparison) 
  - when hoovering over a run the other runs can become blurrer so the hoovered one is more visible
- add more space between these two for nicer view
  - ![image](https://user-images.githubusercontent.com/15977217/121970739-1b6b1a80-cd6f-11eb-81a0-f24eabbe2be4.png)
- extra button can be added to where the jobs are added in case a user is confused whether to click on the DropBox
  -  ![image](https://user-images.githubusercontent.com/15977217/121970883-6a18b480-cd6f-11eb-922c-9246a52afe30.png)
  - can add the feature to add multiple jobs (via the drop box or button) that stack in a queue
- compatible files for drop box (loaded job's file) can be added if there are more than the .CSV file
  - accepted formats for drop box can be added / restriction to files added 
  - ![image](https://user-images.githubusercontent.com/15977217/121971108-e4493900-cd6f-11eb-8fb1-33da4985dbde.png)
  - currently any file format can be added
- an icon indicating that there is more until end of page as right now it is not very clear so user can think that data is missing 
       - ![image](https://user-images.githubusercontent.com/15977217/121971357-75b8ab00-cd70-11eb-8996-bb164ffe10d6.png)
       - ![image](https://user-images.githubusercontent.com/15977217/121971369-7cdfb900-cd70-11eb-907c-b448220b2004.png)
- when page is "Upload Project" (uploading project), could make the page (**or just "Sovle my problem" button**) greyer in order to indicate that page is not accessible while uploading  
  - **in fact the button should be made inaccessible while it it uploading a problem as if it is clicked a few times it breaks** 
    - ![image](https://user-images.githubusercontent.com/15977217/121971754-62f2a600-cd71-11eb-91ef-3021c0d7143f.png)
    - user may think that clicking the button multiple times will speed the process or that page has frozen as there is no indication and clearly button is working while loading a problem which produces more links that seem solved but never actually shown is server that it is complete
      - further more every click of the button uploads the same job and they are ran on the solver 
      - ![image](https://user-images.githubusercontent.com/15977217/121972005-fb892600-cd71-11eb-8833-5e8b0172055f.png)
      - jobs indeed go in the quueue as all are run one after the other which can create an issue for the program 
      - ![image](https://user-images.githubusercontent.com/15977217/121972044-13f94080-cd72-11eb-9b90-afec4357f6d2.png)
