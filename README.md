# Mobile Systems Security : Exercise 2
Exercise done for the Mobile systems security subject, UAM Poznan.

"Extend exercise 1, so that the program, instead of asking for a password, allows the user to log in using his fingerprint.
You can use libraries available for your chosen programming environment."

In order to increase the security, the message is encoded using AES-128. The password used in this process is obtained from the
fingerprint that the user enters, so that if someone could skip to the message screen without entering his fingerprint, the
message would not be decoded.
