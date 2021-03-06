DROP TABLE if exists Manager;
DROP TABLE if exists Offers;
DROP TABLE if exists Provides;
DROP TABLE if exists PresidentialSuite;
DROP TABLE if exists RoomServiceStaff;
DROP TABLE if exists CateringServiceStaff;
DROP TABLE if exists FrontDeskStaff;
DROP TABLE if exists BillingInfo;
DROP TABLE if exists PaymentInfo_payment;
DROP TABLE if exists PaymentInfo_payer;
DROP TABLE if exists Customer;
DROP TABLE if exists Card_payment;
DROP TABLE if exists Card_details;
DROP TABLE if exists Services;
DROP TABLE if exists Staff;
DROP TABLE if exists Room;
DROP TABLE if exists Hotel;


/*DROP TABLE if exists Staff;
DROP TABLE if exists Room;
DROP TABLE if exists Hotel;*/

CREATE TABLE Hotel (
hotelID INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
address VARCHAR(150) NOT NULL,
phoneNumber BIGINT NOT NULL UNIQUE,
city VARCHAR(25) NOT NULL
);

Alter Table Hotel Auto_Increment=0001;

CREATE TABLE Room (
availability BOOLEAN NOT NULL DEFAULT 1,
category VARCHAR(50) NOT NULL,
occupancy INT NOT NULL,
rate DECIMAL NOT NULL,
hotelID INT NOT NULL,
roomNo INT NOT NULL,
CONSTRAINT room_pk PRIMARY KEY(roomNo, hotelID),
CONSTRAINT room_hotel_fk FOREIGN KEY(hotelID) REFERENCES Hotel(hotelID) ON DELETE CASCADE
);

CREATE TABLE Staff (
staffID INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
age INT NOT NULL,
jobTitle VARCHAR(30) NOT NULL,
dept VARCHAR(30) NOT NULL,
ph BIGINT NOT NULL UNIQUE,
hotelID INT NOT NULL,	
address VARCHAR(150) NOT NULL,
CONSTRAINT staff_hotel_fk FOREIGN KEY(hotelID) REFERENCES Hotel(hotelID) ON DELETE CASCADE
);

Alter Table Staff Auto_Increment=100;

CREATE TABLE Manager(
staffID INT NOT NULL PRIMARY KEY,
hotelID INT NOT NULL,
CONSTRAINT manager_hotel_fk FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE,
CONSTRAINT man_hotel_fk FOREIGN KEY(hotelID) REFERENCES Hotel(hotelID) ON DELETE CASCADE
);

CREATE TABLE RoomServiceStaff (
staffID INT PRIMARY KEY,
availability BOOLEAN NOT NULL DEFAULT 1,
CONSTRAINT room_staff_fk FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE
);


CREATE TABLE CateringServiceStaff (
staffID INT PRIMARY KEY,
availability BOOLEAN NOT NULL DEFAULT 1,
CONSTRAINT catering_staff_fk FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE

);


CREATE TABLE FrontDeskStaff (
staffID INT PRIMARY KEY,
CONSTRAINT staff_fk FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE
);


CREATE TABLE PresidentialSuite (
RoomServiceStaffID INT,
CateringServiceStaffID INT,
roomNo INT NOT NULL,
hotelID INT NOT NULL,
CONSTRAINT suite_pk PRIMARY KEY(roomNo, hotelID),
CONSTRAINT psuite_service_staff_fk FOREIGN KEY(CateringServiceStaffID) REFERENCES CateringServiceStaff(staffID) ON DELETE CASCADE,
CONSTRAINT psuite_catering_staff_fk FOREIGN KEY(RoomServiceStaffID) REFERENCES RoomServiceStaff(staffID) ON DELETE CASCADE,
CONSTRAINT hotelroom_fk FOREIGN KEY(roomNo, hotelID) REFERENCES Room(roomNo, hotelID) ON DELETE CASCADE
  
);

CREATE TABLE Customer(
customerID INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
dob DATE NOT NULL,
phone BIGINT NOT NULL,
email VARCHAR(30) NOT NULL
);

Alter Table Customer Auto_Increment=1001;

CREATE TABLE PaymentInfo_payer (
ssn INT PRIMARY KEY,
address VARCHAR(150) NOT NULL
);

CREATE TABLE PaymentInfo_payment (
paymentID INT PRIMARY KEY AUTO_INCREMENT,
ssn INT NOT NULL,
paymentType VARCHAR(150) NOT NULL,
CONSTRAINT PaymentInfo_payment_fk FOREIGN KEY(ssn) REFERENCES PaymentInfo_payer(ssn) ON DELETE CASCADE
);

CREATE TABLE BillingInfo (
bID INT PRIMARY KEY AUTO_INCREMENT,
amount DECIMAL(10,2) NOT NULL,
paymentID INT NOT NULL,
startTime TIMESTAMP NOT NULL,
endTime TIMESTAMP,
guestCount INT NOT NULL,
customerID INT NOT NULL,
hotelID INT NOT NULL,
staffID INT NOT NULL,
roomNo INT NOT NULL,
CONSTRAINT BillingInfo_fk FOREIGN KEY(paymentID) REFERENCES PaymentInfo_payment(paymentID) ON DELETE CASCADE,
CONSTRAINT BillingInfo_fk2 FOREIGN KEY(customerID) REFERENCES Customer(customerID) ON DELETE CASCADE,
CONSTRAINT BillingInfo_fk3 FOREIGN KEY(hotelID, roomNo) REFERENCES Room(hotelID, roomNo) ON DELETE CASCADE,
CONSTRAINT BillingInfo_fk4 FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE
);

CREATE TABLE Card_details (
cardNo VARCHAR(20),
name VARCHAR(150) NOT NULL,
type VARCHAR(50) NOT NULL,
expiryDate DATE NOT NULL,
cvv INT,
CONSTRAINT Card_details_pk PRIMARY KEY(cardNo)
);

CREATE TABLE Card_payment (
paymentID INT PRIMARY KEY,
type VARCHAR(50) NOT NULL,
cardNo VARCHAR(20) NOT NULL,
CONSTRAINT Card_details_fk FOREIGN KEY(CardNo) REFERENCES Card_details(cardNo) ON DELETE CASCADE
);

CREATE TABLE Services(
serviceID INT PRIMARY KEY,
name VARCHAR(50) NOT NULL
);


CREATE TABLE Offers(
serviceID INT NOT NULL,
price INT NOT NULL,
hotelID INT NOT NULL,
roomNo INT NOT NULL,
CONSTRAINT offers_pk PRIMARY KEY(hotelID,roomNo,serviceID),
CONSTRAINT offers_fk1 FOREIGN KEY(hotelID,roomNo) REFERENCES Room(hotelID, roomNo) ON DELETE CASCADE,
CONSTRAINT offers_fk2 FOREIGN KEY(serviceID) REFERENCES Services(serviceID) ON DELETE CASCADE
);


CREATE TABLE Provides(
qty INT NOT NULL,
staffID INT NOT NULL,
serviceID INT NOT NULL,
bID INT NOT NULL,
CONSTRAINT provides_pk PRIMARY KEY(staffID, serviceID, bID),
CONSTRAINT provides_fk1 FOREIGN KEY(staffID) REFERENCES Staff(staffID) ON DELETE CASCADE,
CONSTRAINT provides_fk2 FOREIGN KEY(serviceID) REFERENCES Services(serviceID) ON DELETE CASCADE,
CONSTRAINT provides_fk3 FOREIGN KEY(bID) REFERENCES BillingInfo(bID) ON DELETE CASCADE
);

INSERT INTO Hotel(name, address, phoneNumber, city) VALUES ( 'Hotel A', '21 ABC St , Raleigh NC 27', 919, 'Raleigh');			/* Manager 100 */
INSERT INTO Hotel(name, address, phoneNumber, city) VALUES ( 'Hotel B', '25 XYZ St , Rochester NY 54', 718, 'Rochester');		/* Manager 101 */
INSERT INTO Hotel(name, address, phoneNumber, city) VALUES ( 'Hotel C', '29 PQR St , Greensboro NC 27', 984, 'Greensboro');	/* Manager 102 */
INSERT INTO Hotel(name, address, phoneNumber, city) VALUES ( 'Hotel D', '28 GHW St , Raleigh NC 32', 920, 'Raleigh');			/* Manager 105 */

INSERT INTO Room(hotelID,roomNo, category, occupancy, rate) VALUES (0001, 01, 'Economy', 1, 100);
INSERT INTO Room(hotelID,roomNo, category, occupancy, rate) VALUES (0001, 02, 'Deluxe', 2, 200);
INSERT INTO Room(hotelID,roomNo, category, occupancy, rate) VALUES (0002, 03, 'Economy', 1, 100);
INSERT INTO Room(hotelID,roomNo, category, occupancy, rate, availability) VALUES (0003, 02, 'Executive', 3, 1000, 0);
INSERT INTO Room(hotelID,roomNo, category, occupancy, rate) VALUES (0004, 01, 'Presidential', 4, 5000);
INSERT INTO Room(hotelID,roomNo, category, occupancy, rate) VALUES (0001, 05, 'Deluxe', 2, 200);

INSERT INTO Customer(name, dob, phone, email) VALUES ('David', '1980-01-30', 123, 'david@gmail.com');
INSERT INTO Customer(name, dob, phone, email) VALUES ('Sarah', '1971-01-30', 456, 'sarah@gmail.com');
INSERT INTO Customer(name, dob, phone, email) VALUES ('Joseph', '1987-01-30', 789, 'joseph@gmail.com');
INSERT INTO Customer(name, dob, phone, email) VALUES ('Lucy', '1985-01-30', 213, 'lucy@gmail.com');

INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Mary', 40, 'Manager', 'Management', 654, 0001, '90 ABC St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('John', 45, 'Manager', 'Management', 564, 0002, '798 XYZ St , Rochester NY 54');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Carol', 55, 'Manager', 'Management', 566, 0003, '351 MH St , Greensboro NC 27');	
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Emma', 55, 'Front Desk Staff', 'Management', 546, 0001, '49 ABC St , Raleigh NC 27');	
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Ava', 55, 'Catering Staff', 'Catering', 7771, 0001, '425 RG St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Peter', 52, 'Manager', 'Management', 724, 0004, '475 RG St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Olivia', 27, 'Front Desk Staff', 'Management', 799, 0004, '325 PD St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Jane', 27, 'Front Desk Staff', 'Management', 798, 0002, '329 PD St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Raj', 27, 'Front Desk Staff', 'Management', 711, 0003, '125 PD St , Raleigh NC 27');
/* Services offered to be included in other table - clarify front desk staff not available for checkin in Hotel 0002 and 0003*/

INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Karl', 25, 'Catering Staff', 'Catering', 111, 0002, '41 CD St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Seth', 30, 'Catering Staff', 'Catering', 222, 0003, '95 ZY St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Monice', 32, 'Catering Staff', 'Catering', 333, 0004, '25 PD St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Rachel', 45, 'Room Service Staff', 'Room Service', 444, 0001, '411 ABC St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Joey', 50, 'Room Service Staff', 'Room Service', 555, 0002, '411 XYZ St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Phoebe', 37, 'Room Service Staff', 'Room Service', 777, 0003, '411 ANC St , Raleigh NC 27');
INSERT INTO Staff(name, age, jobTitle, dept, ph, hotelID, address) VALUES ('Chandler', 51, 'Room Service Staff', 'Room Service', 666, 0004, '411 YA St , Raleigh NC 27');



INSERT INTO Services(serviceID, name) VALUES (1, 'Phone Bills');
INSERT INTO Services(serviceID, name) VALUES (2, 'Dry cleaning');
INSERT INTO Services(serviceID, name) VALUES (3, 'Gyms');
INSERT INTO Services(serviceID, name) VALUES (4, 'Room Service');
INSERT INTO Services(serviceID, name) VALUES (5, 'Special Request');
INSERT INTO Services(serviceID, name) VALUES (6, 'Catering');

INSERT INTO Manager(staffID, hotelID) VALUES (100, 0001);
INSERT INTO Manager(staffID, hotelID) VALUES (101, 0002);
INSERT INTO Manager(staffID, hotelID) VALUES (102, 0003);
INSERT INTO Manager(staffID, hotelID) VALUES (105, 0004);

/* Room Service Staff does not exist 			INSERT INTO RoomServiceStaff(staffID) VALUES (7); */


INSERT INTO CateringServiceStaff(staffID) VALUES (104);
INSERT INTO CateringServiceStaff(staffID) VALUES (109);
INSERT INTO CateringServiceStaff(staffID) VALUES (110);
INSERT INTO CateringServiceStaff(staffID) VALUES (111);

INSERT INTO RoomServiceStaff(staffID) VALUES (112);
INSERT INTO RoomServiceStaff(staffID) VALUES (113);
INSERT INTO RoomServiceStaff(staffID) VALUES (114);
INSERT INTO RoomServiceStaff(staffID) VALUES (115);

INSERT INTO FrontDeskStaff(staffID) VALUES (106);
INSERT INTO FrontDeskStaff(staffID) VALUES (103);
INSERT INTO FrontDeskStaff(staffID) VALUES (107);
INSERT INTO FrontDeskStaff(staffID) VALUES (108);
/* No customer for Presidential Suite 			INSERT INTO PresidentialSuite(RoomServiceStaffID,CateringServiceStaffID,roomNo,hotelID) VALUES(7,10,401,1); */

INSERT INTO PresidentialSuite(hotelID, roomNo) VALUES (4, 1);

INSERT INTO PaymentInfo_payer(ssn, address) VALUES (5939846, '980 TRT St , Raleigh NC');
INSERT INTO PaymentInfo_payment(paymentID, ssn, paymentType) VALUES (1, 5939846, 'card');
INSERT INTO Card_details(cardNo, name, type, expiryDate, cvv) VALUES ('1052', 'David', 'VISA', '2022-01-15', 212);
INSERT INTO Card_payment(paymentID, type, cardNo) VALUES (1, 'VISA', '1052');


INSERT INTO PaymentInfo_payer(ssn, address) VALUES (7778352, '7720 MHT St , Greensboro NC');
INSERT INTO PaymentInfo_payment(paymentID, ssn, paymentType) VALUES (2, 7778352, 'card');
INSERT INTO Card_details(cardNo, name, type, expiryDate, cvv) VALUES ('3020', 'Sarah', 'hotel card', '2022-01-01', 012);
INSERT INTO Card_payment(paymentID, type, cardNo) VALUES (2, 'hotel card', '3020');


INSERT INTO PaymentInfo_payer(ssn, address) VALUES (8589430, '231 DRY St , Rochester NY 78');
INSERT INTO PaymentInfo_payment(paymentID, ssn, paymentType) VALUES (3, 8589430, 'card');
INSERT INTO Card_details(cardNo, name, type, expiryDate, cvv) VALUES ('2497', 'Joseph', 'VISA', '2022-01-01', 512);
INSERT INTO Card_payment(paymentID, type, cardNo) VALUES (3, 'VISA', '2497');


INSERT INTO PaymentInfo_payer(ssn, address) VALUES (4409328, '24 BST Dr , Dallas TX 14');
INSERT INTO PaymentInfo_payment(paymentID, ssn, paymentType) VALUES (4, 4409328, 'cash');

INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 1, 05);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 1, 05);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 1, 05);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 1, 05);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 1, 05);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 1, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 1, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 1, 05);

INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 2, 03);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 2, 03);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 2, 03);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 2, 03);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 2, 03);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 2, 03);

INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 3, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 3, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 3, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 3, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 3, 02);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 3, 02);

INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (1, 5, 4, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (2, 16, 4, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (3, 15, 4, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (4, 10, 4, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (5, 20, 4, 01);
INSERT INTO Offers(serviceID, price, hotelID, roomNo) VALUES (6, 0, 4, 01);


INSERT INTO BillingInfo(customerID, hotelID, roomNo, guestCount, startTime, endTime, paymentID, staffID, amount) VALUES 
(1001, 0001, 01, 1, '2017-05-10 15:17:00', '2017-05-13 10:22:00', 1, 103, 331.00);
INSERT INTO BillingInfo(customerID, hotelID, roomNo, guestCount, startTime, endTime, paymentID, staffID, amount) VALUES 
(1002, 0001, 02, 2, '2017-05-10 16:11:00', '2017-05-13 09:27:00', 2, 103, 584.25);
INSERT INTO BillingInfo(customerID, hotelID, roomNo, guestCount, startTime, endTime, paymentID, staffID, amount) VALUES 
(1003, 0002, 03, 1, '2016-05-10 15:45:00', '2016-05-14 11:10:00', 3, 107, 410.00);
INSERT INTO BillingInfo(customerID, hotelID, roomNo, guestCount, startTime, endTime, paymentID, staffID, amount) VALUES 
(1004, 0003, 02, 2, '2018-05-10 14:30:00', '2018-05-12 10:00:00', 4, 108, 10030.00);


/* Billing info - custID, ssn, billadd, paymentmethod, cardno*/

/* Add Entries into Provides table - after confirmation */
INSERT INTO Provides(qty, staffID, serviceID, bID) VALUES (1, 103, 2, 1);
INSERT INTO Provides(qty, staffID, serviceID, bID) VALUES (1, 103, 3, 1);
INSERT INTO Provides(qty, staffID, serviceID, bID) VALUES (1, 103, 3, 2);
INSERT INTO Provides(qty, staffID, serviceID, bID) VALUES (1, 113, 4, 3);
INSERT INTO Provides(qty, staffID, serviceID, bID) VALUES (1, 108, 1, 4);




















