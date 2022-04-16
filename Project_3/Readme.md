
/**************************************** Compile ****************************************/
TO compile the project. All java & image files should be in same folder
Run : javac *.java
/**************************************** Compile ****************************************/

Two Main files:
1. start_nasa.java ==> This file takes one argument 0 or 1. 0 means nasa is not sender; 1 means nasa is sender.
                        After this is initilised program will prompt to enter command in integer range 1-255
                        Here each command integer signifies what rover should do ?
                        Eg : 1 ==> Move forward
                             2 ==> Fly back to earth etc
                        For now I have defined only 2 commands so input expected is 1 or 2
                        User can define further commands based on needs
                       
2. start_rover.java ==> This file takes two argument rover_id which should be unique and 
                        0 0r 1. 0 means nasa is not sender; 1 means nasa is sender.
                        After this is initilised program will prompt to enter path of image to send

/**************************************** RUN ****************************************/
Communication 1:
    From Rover to NASA:
        1. java start_nasa 0
        2. java start_rover <rover_id> 1

Communication 2:
    From NASA to Rover:
        1. java start_rover <rover_id> 0
        2. java start_nasa 1
        Here to Rover wo whom command needs to send should be online before sending
/**************************************** RUN ****************************************/

Note :  1. This project will work for one sender and one receiver at a time. 
        2. Multiple sender cannot send at a time.
        3. In order to send again project needs to be run again.
        4. Ouptput image will be in same folder as other files with name as output_<source_rover>.jpg
        5. NASA runs on fixed IP : 10.0.1.0
        6. Rover id should be entered in between 2 - 255 as 1 is already taken by NASA