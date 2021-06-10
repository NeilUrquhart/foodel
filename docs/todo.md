# Todo 

This markdown file serves as a place to document bugs, problems and suggestions for Neil. 

## Bugs
- (known issue) Takes a long time to upload a problem file.
- Maps can overflow (spill off page)


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
- Minor CSS adjustments:
  - Change padding on page body so no gap
  - Maybe have the footer stick to the bottom of the window
