create database Project;
use Project;
CREATE TABLE login_users(userid INTEGER AUTO_INCREMENT PRIMARY KEY, username VARCHAR(32), password CHAR(64) NOT NULL, usersalt CHAR(32));
CREATE TABLE orders(orderId INTEGER AUTO_INCREMENT PRIMARY KEY, custName VARCHAR(50),emailId VARCHAR(50),address VARCHAR(250),quantity INTEGER(10));