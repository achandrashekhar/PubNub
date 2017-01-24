# PubNub
When I first read about PubNub and saw some tutorials, the Java in me screamed “Observer Pattern!”. The Real Time examples given on the website were pretty amazing! I decided to solve a problem that I had faced just last month. My sister and I booked a ticket for my Mother on a reliable site but never got a confirmation email! We immediately called the airlines, but in vain, they said we should have gotten the email and that their system hadn’t been updated yet! There was a 1000 bucks at stake, but thankfully, we received a confirmation email three days later! I have done something similar. When the Customer places an order, an iPhone in my example, this gets published to a channel, and there are two listeners – An email notifier and a Database Updater that will do their jobs in REAL TIME! The Pub/Sub would look something like this:



![Alt text](/HTML_PAGES/PubNub - Page 1.jpeg?raw=true "Optional Title")



Jar Dependencies:
pubnub-gson-4.3.0-all.jar
slf4j-api-1.6.1.jar
sl4f-simple-1.6.1.jar
jetty-all-9.3.9.v20160517-uber.jar
velocity-1.7-dep.jar
mysql-connector-java-5.1.40-bin.jar
javax.mail-1.5.6.jar
json-simple-1.1.1.jar

Database Dependencies
Dabble with these two files only if you want to check the Database update subscriber. You will need MySQL set up on your local machine for this. These files will be visible on the dataBaseUpdate branch
File 1: The database.properties file will need credentials for MySQL 
Place this file in the project (on the top level, not inside src)

File2: DatabaseSetUp.sql is a script file has a couple DML commands to set up the Database and the tables. When you are logged in to MySQL you will be able to see something like this :
mysql>
Type the following to run the script:
mysql>source [entire path of this file];
This should have set up two tables: login_users and orders
Try these commands:
mysql>use Project;
mysql>show tables;


Running the application: 
The master branch has no Database dependencies at all and runs as a Real Time email notifier. You will need the files in src as well as HTML_PAGES. To run the master brunch application, first run EmailNotifier.java and then run ApplicationServer.java Now on the browser type in localhost:8080/login


Use the following default credentials to login :

Username: testuser
Password: Testpwd1*

You will be able to see a button that says Place order, click on it, enter all the details and click place order, you will immediately receive an email on the emailId you provided! (Type in your id to verify results immediately)

The DataBaseUpdate branch requires MySQL and I have mentioned the instructions above. Ensure that the two tables have been set up properly.
To run this, first run EmailNotifier.java and then run DatabaseUpdate.java and finally run ApplicationServer.java
Type in localhost:8080/register
Register here. The password must start with a Capital letter followed by small letters and a number and a special character e.g. : Testpwd1*
Login with the credentials you provided and place the order, now two things should have happened: you should have gotten an email and the database – orders table should have been updated (in REAL TIME)
Try this MySQL command : SELECT * FROM orders;

I’ve used Bootstrap for the front end and Java Servlets for the back end.
On a side note, The PubNub API is very well written. It's concise and clear and took me just a few examples to get a hang of it. Kudos to the PubNub team for writing such easy to understand, yet powerful code!

