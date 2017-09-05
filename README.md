# About

This is a program to store appointments.  You can add, remove, change, and search for appointments.  You can also add data that describe appointments.  For example, you can add a data field for the appointment's location, or one for its reason, etc.

Note: This is a prototype.  It was tested and cofirmed to run on Linux 4.10.

![Main screen](/images/demo/calendar_1.png)

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

```
    $ java -cp .:bin/:lib/mysql-connector-java-5.1.40-bin.jar:lib/javax.json-api-1.0.jar:lib/javax.json-1.0.4.jar CalendarLauncher
```

To compile the program run this in the terminal:

```
    $ javac -cp .:lib/javax.json-api-1.0.jar -d bin/ <srcfile>.java
```


# Demo

## Context menu

Left click on a cell to show the options available for it.  If the cell is empty, it can be locked.  Locked cells display 'XXXXXX' and cannot be modified.  If it's not empty, you can copy it and then paste it to an empty cell, and you can also delete it.

![Context menu](/images/demo/calendar_3.png)

## Customization

You can customize the contents of the calendar by pressing the 'Customize' button at the top.  This dialog allows you to show and hide days, show a range of times, and to change the colors of cells depending on their data value.  In this dialog, appointments made with people whose first name is Rebecca are displayed with a green background, and those with a first name of loldfsdf have a pink background.  You can use the dropdown menu to the left to choose other data for changing colors, for example, set the background to red if the preferred name is Becky.  However, there is no data field to define the preferred name; but you can easily create one (the next section's topic).

![Customize](/images/demo/calendar_5.png)

## Editing data

Double-click on an appointment, and you'll see the following dialog.  Here, you can edit the appointment's data.  If you want to add more data, for example, a field for the preferred name, then click on the add button, enter the new field, and it'll then show up as an editable piece of data (for all appointments, not just this one).  Now you can change the background of appointments with a preferred name of Becky to red, if you want.

![Customize](/images/demo/calendar_7.png) ![Customize](/images/demo/calendar_8.png) ![Customize](/images/demo/calendar_9.png)

If you click on the Hide button, then the selcted data will be hidden.  This is useful for hiding data that you don't care about.  If you find that you never use the 'Preferred name' field, then you can hide it, forget about it, and never be bothered by it again.  You can also do this by clicking on the Show button, which allows you to hide data, and show data that you might have hidden earilier.

![Customize](/images/demo/calendar_10.png)

## Editing appointments

To see all the appointments that you booked with this person, click on the Appointments tab.  This will show you all the appointments you made in the past and future.  You can remove or add appointments here.  If you want to add an appointment, then you'll be presented with a list of free dates and times.  Choose one, click on 'Add appointment', and you're done.  The appointment wont show up immediately in the list of appointments, but don't worry, it's been made.  You can close and reopen the dialog to see the change.

![Customize](/images/demo/calendar_11.png) ![Customize](/images/demo/calendar_12.png)

## Searching for appointments

On the main screen screen, click on 'Search', and you'll see the following dialog.  This allows you to search for any person who you've made an appointment with.  If you tick the 'Fuzzy search' checkbox, then you'll enable fuzzy search, for those times when you only fuzzily recall the person's name.

![Customize](/images/demo/calendar_6.png)

# Background

This is a program to store appointments for a clinic.  It started out as a proposal by an actual clinic, but the project never took off.  I still wanted to develop it and decided to see how far I could take the project while collaborating with the clinic during the month-long winter break.  This was first implemented as a web app, but I switched to JavaFX because it provided more tools for facilitating the development of the client's needs, and because it was an emerging framework I had never used before that could be helpful in the future.

Some notable things I wished to do differently include:
* Encrypting the database.
* Reading and setting database configurations from a file.
* Make the program more reactive.
* Search using any key, not just name.
* Install the program on the end-user's hardware.
* Talk more with the user about the database and legal issues.  I doubt the database could be deployed as is, even if encrypted.
