# About

This is a program to store appointments.  You can add, remove, change, and search for appointments.  You can also add data that describe appointments.  For example, you can add a data field for the appointment's location, or one for its reason, etc.

Note: This is a prototype.  It was tested and cofirmed to run on Linux 4.10.

The program in its current form supports:
* Insertion, deletion, and modification of appointments.
* Create new appointments by copy-and-pasting a cell.
* Navigate to any week of any month and year.
* Disable any kind of modification on user-defined days and times to prevent assigning appointments (holidays, non-workdays, meeting hours, etc.).
* Customize the appearence of the calendar: show only a subset of days or times, and give appointments colors to help differentiate among appointment types.
* Search for people using either exact or fuzzy search.
* Retrieve a list of all appointments--past and future--of any person.
* Retrieve any subset of data about any person.
* Adding any number of data fields to the data associated with a person (if you think adding a field for birthplace was important, you could do so).

These features make the calendar easy to use and customize.  The customizations allow users to work with the data they most frequently use, and to ignore data that they rarely use, saving time and simplifying their experience.

To use this program, initialize a MySQL database with the schema defined in calendar.sql.  Next, start the MySQL server; the default connection details are stored and can be changed in the private constructor of the CalendarModel class.

Once the database is online, start the program by running this command in the terminal:
    $ java -cp .:bin/:lib/mysql-connector-java-5.1.40-bin.jar:lib/javax.json-api-1.0.jar:lib/javax.json-1.0.4.jar CalendarLauncher

To compile the program run this in the terminal:
    $ javac -cp .:lib/javax.json-api-1.0.jar -d bin/ <srcfile>.java


# Demo


# Background

This is a program to store appointments for a clinic.  It started out as a proposal by an actual clinic, but the project never took off.  I still wanted to develop it and decided to see how far I could take the project while collaborating with the clinic during the month-long winter break.  This was first implemented as a web app, but I switched to JavaFX because it provided more tools for facilitating the development of the client's needs, and because it was an emerging framework I had never used before that could be helpful in the future.

Some notable things I wished to do differently include:
* Encrypting the database.
* Reading and setting database configurations from a file.
* Make the program more reactive.
* Search using any key, not just name.
* Install the program on the end-user's hardware.
* Talk more with the user about the database and legal issues.  I doubt the database could be deployed as is, even if encrypted.
