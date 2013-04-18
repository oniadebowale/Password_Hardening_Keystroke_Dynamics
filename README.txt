README FILE

Password Hardening Based on KeyStroke Dynamics

This is a mock login system in which password hardening is implememted as another security
measure to protect against counterfeits. Specifically, this program measures the user's
keystroke dynamics and uses the data to determine if the timing of how the password is 
entered in the future matches the timing of how the password was entered in the past.

To execute the program, simply run Main.java. Then, simply follow the instructions given
to you. You will be prompted to type your password a certain number of times to collect
biometric data regarding your keystroke. Once the data collection is complete, you are
free to pretend "logging in" by entering your password as see if the system accepts or 
rejects you. The range of acceptable timings for the elapsed timings between each 
character in your password will be displayed, as well the keystroke timing from your last
input. You are able to repeatedly attempt to "login" as many times as you like to test the 
system.

*Note: This project is still far from complete. It functions well, but over time I will continue 
to improve the program to account for various user errors, as well as to make it more efficient and accurate. 