package dbms;
import java.sql.* ;  // for standard JDBC programs
import java.util.Scanner;

public class HotelUtility {
    private static final String NULL = null;
    public int getBillingID(Statement statement,String hotelID,String roomNo){
        Scanner scan = new Scanner(System.in);
        try{
            ResultSet result = statement.executeQuery("select bID from BillingInfo WHERE hotelID="+hotelID+" AND roomNo="+roomNo+" AND endTime IS NULL");
            if(result.next())
                return result.getInt("bID");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }
	public void maintainServiceRecord(Statement statement)
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your ID(Staff ID): ");
        String staffID = scan.nextLine();
        
        System.out.print("Enter Hotel ID: ");
        String hotelID = scan.nextLine();
        System.out.print("Enter Room number: ");
        String roomNo = scan.nextLine();
        String bID = Integer.toString(getBillingID(statement,hotelID,roomNo));
        if(bID=="-1")
        {
            System.out.println("Room is unoccupied!!");
            return;
        }

        System.out.println("************SERVICES OFFERED*************");
        ResultSet result;
        try{
            result = statement.executeQuery("select * from Services, Offers WHERE Offers.hotelID="+hotelID+" AND Offers.roomNo="+roomNo+" AND Offers.serviceID=Services.serviceID");
            ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%9s   %-25s",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
            while (result.next()) {
                System.out.format("%n%9s   %-25s",result.getString(1), result.getString(2));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.print("Enter Service ID: ");
        String serviceID = scan.nextLine();
        System.out.print("Enter Quantity: ");
        String qty = scan.nextLine();
        try
        {
            result = statement.executeQuery("select qty from Provides where staffID=" + staffID + " AND serviceID=" + serviceID + " AND bID=" + bID);
            if(result.next())
            {
                int newqty=result.getInt("qty")+Integer.parseInt(qty);
                statement.executeUpdate("UPDATE Provides SET qty="+ Integer.toString(newqty) + " WHERE staffID="+staffID+" AND serviceID="+ serviceID + " AND bID=" +bID );
            }
            else
            {
                statement.executeUpdate("INSERT INTO Provides(staffID, serviceID, bID, qty) VALUES (" + staffID +", " + serviceID +", "+bID+", "+ qty +")" );
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

    }

    public void maintainBillingAccount(Statement statement)
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter Hotel ID: ");
        String hotelID = scan.nextLine();
        System.out.print("Enter Room number: ");
        String roomNo = scan.nextLine();
        String bID = Integer.toString(getBillingID(statement,hotelID,roomNo));
        if(bID=="-1")
        {
            System.out.println("Room is unoccupied!!");
            return;
        }

        try
        {
            ResultSet result = statement.executeQuery("(SELECT \"Room Stay\" AS Item, (CEIL(TIMESTAMPDIFF("+
            "SECOND,BI.startTime,BI.endTime)/86400))*R.rate  as Amount FROM BillingInfo BI, Room R WHERE R.roomNo=BI.roomNo AND "+
            "R.hotelID=BI.hotelID AND BI.bID="+bID+") "+
            "UNION "+
            "(SELECT S.name AS Item, SUM(O.price*P.qty) AS Amount from Provides P, Services S, Offers O, BillingInfo BI2 WHERE "+
            "P.bID="+bID+" AND P.serviceID=S.serviceID AND BI2.bID=P.bID AND BI2.hotelID=O.hotelID AND BI2.roomNo=O.roomNo AND "+
            "P.serviceID=O.serviceID GROUP by P.serviceID) "+
            "UNION"+
            "(SELECT \"Total\", (CASE WHEN (SELECT COUNT(*) FROM BillingInfo, Card_payment WHERE BillingInfo.bID="+bID+" AND "+
            "BillingInfo.paymentID=Card_payment.paymentID AND Card_payment.type='hotel card')=0 THEN SUM(C.Amount) ELSE SUM(C.Amount)*0.95 END) "+
            "FROM "+
            "((SELECT \"Room Stay\" AS Item, (CEIL(TIMESTAMPDIFF(SECOND,BI3.startTime,BI3.endTime)/86400))*R.rate  as Amount "+
            "FROM BillingInfo BI3, Room R WHERE R.roomNo=BI3.roomNo AND R.hotelID=BI3.hotelID AND BI3.bID="+bID+") "+
            "UNION "+
            "(SELECT S.name AS Item, SUM(O.price*P.qty) AS Amount from Provides P, Services S, Offers O, BillingInfo BI4 "+
            "WHERE P.bID="+bID+" AND P.serviceID=S.serviceID AND BI4.bID=P.bID AND BI4.hotelID=O.hotelID AND BI4.roomNo=O.roomNo "+
            "AND P.serviceID=O.serviceID GROUP by P.serviceID))AS C)");

    	    ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%16s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
            while (result.next()) {
	           System.out.format("%-25s%16s%n",result.getString(1), result.getString(2));
            }
            result.previous();
            result = statement.executeQuery("UPDATE BillingInfo SET amount="+result.getString(2)+" WHERE bID="+bID);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

    }
    
    //Function to insert the hotel details
    public void enterHotelInfo(Statement statement) {
    	    Scanner scan = new Scanner(System.in);
        System.out.println("Enter name of the hotel");
        String hname = scan.nextLine();
        System.out.println("Enter address of the hotel");
        String haddress = scan.nextLine();
        System.out.println("Enter phone number of the hotel");
        int hnumber = scan.nextInt();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Hotel " +  "VALUES ('"+hname+"','"+haddress+"', '"+hnumber+"')");
            System.out.println("Hotel information has been entered");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //Function to update the hotel details    
    public void updateHotelInfo(Statement statement) {
    		Scanner scan = new Scanner(System.in);
    	    System.out.println("The hotel information");
    	    try {
    	    ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
    	    ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%16s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
	    while (result.next()) {
	        System.out.format("%-25s%16s%n",result.getString(1), result.getString(2));
        }
    	    }catch(SQLException e)
        {
            e.printStackTrace();
        }
    	    System.out.println("Enter the ID of the hotel which needs to be updated");
    	    int hid = scan.nextInt();
        System.out.println("Enter name of the hotel to update");
        String hname = scan.nextLine();
        System.out.println("Enter address of the hotel to update");
        String haddress = scan.nextLine();
        System.out.println("Enter phone number of the hotel update");
        int hnumber = scan.nextInt();
        try
        {
            ResultSet result1 = statement.executeQuery("UPDATE HOTEL SET name = '"+hname+"', address = '"+haddress+"', phoneNumber = '"+hnumber+"' where hotelID = "+hid+"");
            System.out.println("Hotel information has been updated");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        
    }
    
    //Function to delete the hotel details
    public void deleteHotel(Statement statement) throws SQLException {
    	    Scanner scan = new Scanner(System.in);
	    System.out.println("The hotel information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
	    //add printing statement
	    System.out.println("Enter the ID of the hotel which needs to be deleted");
	    int hid = scan.nextInt();
	    try
        {
            result = statement.executeQuery("DELETE FROM Hotel where hotelID = "+hid+"");
            System.out.println("Hotel information has been deleted");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //Function to insert the room details
    public void enterRoomInfo(Statement statement) {
      	Scanner scan = new Scanner(System.in);
        System.out.println("Enter the  hotel id");
        int hid = scan.nextInt();
        System.out.println("Enter the room number");
        int rno = scan.nextInt();
        System.out.println("Enter the category of the room");
        String rcategory = scan.nextLine();
        System.out.println("Enter the occupancy");
        int roccupancy = scan.nextInt();
        System.out.println("Enter the rate of the room");
        float rrate = scan.nextFloat();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Room " +  "VALUES ('"+hid+"','"+rno+"', '"+rcategory+"','"+roccupancy+"','"+rrate+"')");
            System.out.println("Room information has been entered");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //Function to update the room details
    public void updatedRoomInfo(Statement statement) throws SQLException {
    	    Scanner scan = new Scanner(System.in);
    	    System.out.println("The room information");
    	    ResultSet result = statement.executeQuery("SELECT * FROM Room");
    	    //add printing statement
    	    System.out.println("Enter the hotel id and the room number which need to be updated");
    	    int hid = scan.nextInt();
    	    int rnumber = scan.nextInt();
        System.out.println("Enter the category of the room to update");
        String rcategory = scan.nextLine();
        System.out.println("Enter the occupancy to update");
        int roccupancy = scan.nextInt();
        System.out.println("Enter the rate of the room to update");
        float rrate = scan.nextFloat();
        try
        {
            result = statement.executeQuery("UPDATE Room SET category = '"+rcategory+"', occupancy = '"+roccupancy+"', rate = '"+rrate+"' where hotelID = "+hid+" and roomNo = "+rnumber+"");
            System.out.println("Room information has been updated");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to delete the room details
    public void deleteRoom(Statement statement) throws SQLException {
       	Scanner scan = new Scanner(System.in);
	    System.out.println("The room information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Room");
	    //add printing statement
	    System.out.println("Enter the hotel id and the room number which need to be deleted");
	    int hid = scan.nextInt();
	    int rnumber = scan.nextInt();
	    try
        {
            result = statement.executeQuery("DELETE FROM Room where hotelID = "+hid+" and roomNo = "+rnumber+"");
            System.out.println("Room information has been deleted");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
  //Function to enter the staff details
    public void enterStaffInfo(Statement statement) {
      	Scanner scan = new Scanner(System.in);
        System.out.println("Enter the  name of the staff");
        String sname = scan.nextLine();
        System.out.println("Enter the age of the staff member");
        int sage = scan.nextInt();
        System.out.println("Enter the job title of the staff member");
        String sjobtitle = scan.nextLine();
        System.out.println("Enter the department of the staff");
        String sdept = scan.nextLine();
        System.out.println("Enter the phone number of the staff");
        int sphone = scan.nextInt();
        System.out.println("Enter the hotel id in which the staff works");
        int hid = scan.nextInt();        
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Staff " +  "VALUES ('"+sname+"','"+sage+"', '"+sjobtitle+"','"+sdept+"','"+sphone+"','"+hid+"')");
            System.out.println("Staff information has been entered");
            int idFetched = statement.executeUpdate("SELECT TOP 1 staffID FROM Table ORDER BY ID DESC");
            int availability = 1;
            if(sdept == "FrontDeskStaff") {
            	result = statement.executeQuery("INSERT INTO FrontDeskStaff " +  "VALUES ('"+idFetched+"')");
            }
            else if(sdept == "RoomServiceStaff") {
            	result = statement.executeQuery("INSERT INTO RoomServiceStaff " +  "VALUES ('"+idFetched+"','"+availability+"')");
            }
            else if(sdept == "CateringServiceStaff") {
            	result = statement.executeQuery("INSERT INTO CateringServiceStaff " +  "VALUES ('"+idFetched+"','"+availability+"')");
            }
            else if(sdept == "Manager") {
            	result = statement.executeQuery("INSERT INTO Manager " +  "VALUES ('"+idFetched+"','"+hid+"')");
            }
            else {
            	System.out.println("Enter valid department: FrontDeskStaff,RoomServiceStaff and CateringServiceStaff");
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        
    }
    //Function to update the staff details
    public void updateStaffInfo(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
      	System.out.println("The staff information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Staff");
	    //add printing statement
	    System.out.println("Enter the staff id which need to be updated");
	    int sid = scan.nextInt();
        System.out.println("Enter the  name of the staff to update");
        String sname = scan.nextLine();
        System.out.println("Enter the age of the staff member to update");
        int sage = scan.nextInt();
        System.out.println("Enter the job title of the staff member to update");
        String sjobtitle = scan.nextLine();
        System.out.println("Enter the department of the staff to update");
        String sdept = scan.nextLine();
        System.out.println("Enter the phone number of the staff to update");
        int sphone = scan.nextInt();
        System.out.println("Enter the hotel id in which the staff works to update");
        int hid = scan.nextInt();        
        try
        {
            result = statement.executeQuery("UPDATE Staff SET Name = '"+sname+"', age = '"+sage+"', jobTitle = '"+sjobtitle+"', dept = '"+sdept+"', ph = '"+sphone+"', hotelID = '"+hid+"' where staffID = "+sid+"");
            System.out.println("Staff information has been updated");
            System.out.println("Enter availability");
            int availability = scan.nextInt();
            if(sdept == "FrontDeskStaff") {
            //	ResultSet result = statement.executeUpdate("INSERT INTO FrontDeskStaff " +  "VALUES ('"+idFetched+"')");
            }
            else if(sdept == "RoomServiceStaff") {
            	result = statement.executeQuery("UPDATE RoomServiceStaff SET availability = '"+availability+"' where staffID = "+sid+"");
            }
            else if(sdept == "CateringServiceStaff") {
            	result = statement.executeQuery("UPDATE CateringServiceStaff SET availability = '"+availability+"' where staffID = "+sid+"");
            }
            else if(sdept == "Manager") {
            result = statement.executeQuery("UPDATE Manager SET hotelID = '"+hid+"' where staffID = "+sid+"");
            }
            else {
            	System.out.println("Enter valid department: Manager,FrontDeskStaff,RoomServiceStaff and CateringServiceStaff");
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
   //Function to delete the staff details
    public void deleteStaff(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
	    System.out.println("The Staff information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Staff");
	    //add printing statement
	    System.out.println("Enter the staff id which needs to be deleted");
	    int sid = scan.nextInt();
	    System.out.println("Enter the staff department to which the staff to be deleted belongs to");
	    String dept = scan.nextLine();  
	    try
        {
	    	    if(dept == "FrontDeskStaff") {
	    	    result = statement.executeQuery("DELETE FROM FrontDeskStaff where staffID = "+sid+"");
	    	    statement.executeUpdate("DELETE FROM Staff where staffID = "+sid+"");
	        System.out.println("Staff information has been deleted");
	    	    }
	    	    else if(dept == "RoomServiceStaff") {
		    	result = statement.executeQuery("DELETE FROM RoomServiceStaff where staffID = "+sid+"");
		    	statement.executeUpdate("DELETE FROM Staff where stafflID = "+sid+"");
		    System.out.println("Staff information has been deleted");
		    	}
	    	    else if(dept == "CateringServiceStaff") {
			result = statement.executeQuery("DELETE FROM CateringServiceStaff where staffID = "+sid+"");
			statement.executeUpdate("DELETE FROM Staff where staffID = "+sid+"");
			System.out.println("Staff information has been deleted");
			}
	    	    else if(dept == "Manager") {
	    		result = statement.executeQuery("DELETE FROM Manager where staffID = "+sid+"");
	    		statement.executeUpdate("DELETE FROM Staff where staffID = "+sid+"");
	    		System.out.println("Staff information has been deleted");
	    		}
	    	    else {
	        System.out.println("Enter valid department: Manager,FrontDeskStaff,RoomServiceStaff and CateringServiceStaff");
	        }
            
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to insert the customer details
    public void enterCustomerInfo(Statement statement) {
      	Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the customer");
        String cname = scan.nextLine();
        System.out.println("Enter the date of birth of the customer");
        String cdob = scan.nextLine();
        System.out.println("Enter the phone number of the customer");
        int cphone = scan.nextInt();
        System.out.println("Enter the email of the customer");
        String cemail = scan.nextLine();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Customer " +  "VALUES ('"+cname+"','"+cdob+"', '"+cphone+"','"+cemail+"')");
            System.out.println("Customer information has been entered");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to update the customer details
    public void updateCustomerInfo(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
	    System.out.println("The customer information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Customer");
	    //add printing statement
	    System.out.println("Enter the customer id to update");
        int cid = scan.nextInt();
	    System.out.println("Enter the name of the customer to update");
        String cname = scan.nextLine();
        System.out.println("Enter the date of birth of the customer to update");
        String cdob = scan.nextLine();
        System.out.println("Enter the phone number of the customer to update");
        int cphone = scan.nextInt();
        System.out.println("Enter the email of the customer to update");
        String cemail = scan.nextLine();
        try
        {
            result = statement.executeQuery("UPDATE Customer SET Name = '"+cname+"', Dob = '"+cdob+"', Phone = '"+cphone+"', Email = '"+cemail+"' where customerID = "+cid+"");
            System.out.println("Customer information has been updated");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to delete the customer details
    public void deleteCustomer(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
	    System.out.println("The customer information");
	    ResultSet result = statement.executeQuery("SELECT * FROM Customer");
	    //add printing statement
	    System.out.println("Enter the customer id which needs to be deleted");
	    int cid = scan.nextInt();
	    try
        {
            result = statement.executeQuery("DELETE FROM Customer where customerID = "+cid+"");
            System.out.println("Customer information has been deleted");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to check room availability by hotel and room type
    public void roomAvailabilityByHotelAndRoomtype(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
      	System.out.println("The hotel information");
      	ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
        //add printing statement
	    System.out.println("The hotel Id and room category");
	    int hid = scan.nextInt();
	    String rcategory = scan.nextLine();
	    int availability = 1;
	    result  = statement.executeQuery("SELECT roomNo FROM Room WHERE hotelID = "+hid+" AND category = "+rcategory+" AND availability="+availability+"");
	    ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%16s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
	    while (result.next()) {
	        System.out.format("%-25s%16s%n",result.getString(1), result.getString(2));
	    }
	    
    }
    //Function to check room availability by hotel and room number
    public void roomAvailabilityByHotelAndRoomno(Statement statement) throws SQLException {
    		Scanner scan = new Scanner(System.in);
    		System.out.println("The hotel information");
      	ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
        //add printing statement
	    System.out.println("The hotel Id and room number");
	    int hid = scan.nextInt();
	    int rno = scan.nextInt();
	    int availability = 1;
	    result = statement.executeQuery("SELECT (CASE WHEN availability = "+availability+" THEN 'Available' ELSE 'Not Available' END) AS 'Room Availability' FROM Room WHERE hotelID = "+hid+" AND roomNo = "+rno+"");
	    ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%16s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
	    while (result.next()) {
	        System.out.format("%-25s%16s%n",result.getString(1), result.getString(2));
	    }
    }
    //Function to assign rooms to customers accroding to the request and availability
    public void assignRoom(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
		System.out.println("The billing information");
  	    ResultSet result = statement.executeQuery("SELECT * FROM BillingInfo");
        //add printing statement
  	    System.out.println("The Customer information");
	   result = statement.executeQuery("SELECT * FROM Customer");
        //add printing statement
	    System.out.println("The Hotel information");
  	    result = statement.executeQuery("SELECT * FROM Hotel");
        //add printing statement
  	    System.out.println("The Staff information");
	    result = statement.executeQuery("SELECT * FROM Staff");
        //add printing statement
    	    System.out.println("Enter the guest count");
        int gcount = scan.nextInt();
        System.out.println("Enter the billing id");
        int bid = scan.nextInt();
        System.out.println("Enter the customer id");
        int cid = scan.nextInt();
        System.out.println("Enter the hotel id");
        int hid = scan.nextInt();
        System.out.println("Enter the staff id");
        int sid = scan.nextInt();
        System.out.println("Enter the room service staff id");
        int rsid = scan.nextInt();
        System.out.println("Enter the catering service staff id");
        int csid = scan.nextInt();
        System.out.println("Enter the room number");
        int rno = scan.nextInt();
        int notAvailable = 0;
        try
        {
            result = statement.executeQuery("INSERT INTO CheckInInfo " +  "VALUES ('NOW()','"+gcount+"', '"+bid+"','"+cid+"','"+hid+"','"+sid+"','"+rno+"')");
            result = statement.executeQuery("UPDATE Room SET availability = "+notAvailable+" WHERE hotelID = "+hid+" AND roomNo = "+rno+"");
            result = statement.executeQuery("UPDATE PresidentialSuite SET RoomServiceStaffID = "+rsid+",CateringServiceStaffID = "+csid+" WHERE hotelID = "+hid+" AND roomNo = "+rno+"");
            result = statement.executeQuery("UPDATE RoomServiceStaff SET availability = "+notAvailable+" WHERE staffID = "+rsid+"");
            result = statement.executeQuery("UPDATE CateringServiceStaff SET availability = "+notAvailable+" WHERE staffID = "+csid+"");
            System.out.println("Customer has been checked in");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Function to release rooms 
    public void releaseRoom(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
		System.out.println("The Hotel information");
  	    ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
        //add printing statement
  	    System.out.println("Enter the hotel id");
        int hid = scan.nextInt();
        System.out.println("Enter the staff id");
        int sid = scan.nextInt();
        System.out.println("Enter the room service staff id");
        int rsid = scan.nextInt();
        System.out.println("Enter the catering service staff id");
        int csid = scan.nextInt();
        System.out.println("Enter the room number");
        int rno = scan.nextInt();
        int available = 1;
        String nullValue = NULL;
        try
        {
            result = statement.executeQuery("UPDATE Room SET availability = "+available+" WHERE hotelID = "+hid+" AND roomNo = "+rno+"");
            result = statement.executeQuery("UPDATE PresidentialSuite SET RoomServiceStaffID = "+nullValue+" AND CateringServiceStaffID = "+nullValue+" WHERE hotelID = "+hid+" AND roomNo = "+rno+"");
            result = statement.executeQuery("UPDATE RoomServiceStaff SET availability = "+available+" WHERE staffID = "+rsid+"");
            result = statement.executeQuery("UPDATE CateringServiceStaff SET availability = "+available+" WHERE staffID = "+csid+"");
            System.out.println("Room has been released");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
}
