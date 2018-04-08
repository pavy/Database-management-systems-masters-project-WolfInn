package dbms;
import java.sql.* ;
import java.math.*;  // for standard JDBC programs
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
        BigInteger hnumber = scan.nextBigInteger();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Hotel(name,address,phoneNumber)" +  "VALUES ('"+hname+"','"+haddress+"', '"+hnumber+"')");
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
        System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
	    while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4));
        }
    	    }catch(SQLException e)
        {
            e.printStackTrace();
        }
    	    System.out.println("Enter the ID of the hotel which needs to be updated");
    	    int hid = scan.nextInt();
          scan.nextLine();
        System.out.println("Enter name of the hotel to update");
        String hname = scan.nextLine();
        System.out.println("Enter address of the hotel to update");
        String haddress = scan.nextLine();
        System.out.println("Enter phone number of the hotel update");
        BigInteger hnumber = scan.nextBigInteger();
        try
        {
            ResultSet result1 = statement.executeQuery("UPDATE Hotel SET name = '"+hname+"', address = '"+haddress+"', phoneNumber = '"+hnumber+"' where hotelID = "+hid+"");
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
	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
            ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4));
        }
            }catch(SQLException e)
        {
            e.printStackTrace();
        }

	    System.out.println("Enter the ID of the hotel which needs to be deleted");
	    int hid = scan.nextInt();
	    try
        {
            ResultSet result1 = statement.executeQuery("DELETE FROM Hotel where hotelID = "+hid+"");
            System.out.println("Hotel information has been deleted");
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //Function to insert the room details
    //foreign key case is to be implemented 
    public void enterRoomInfo(Statement statement) {
      	Scanner scan = new Scanner(System.in);
        System.out.println("Enter the  hotel id");
        int hid = scan.nextInt();
        System.out.println("Enter the room number");
        int rno = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the category of the room");
        String rcategory = scan.nextLine();
        System.out.println("Enter the occupancy");
        int roccupancy = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the rate of the room");
        float rrate = scan.nextFloat();
        System.out.println("Enter the availability of the room 1 if yes");
        int available = scan.nextInt();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Room(HotelID,roomNo,category,occupancy,rate,availability)" +  "VALUES ('"+hid+"','"+rno+"', '"+rcategory+"','"+roccupancy+"','"+rrate+"','"+available+"')");
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
    	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Room");
	    ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5),rsMetaData.getColumnName(6));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4),result.getString(5),result.getString(6));
            }
            }catch(SQLException e)
            {
              e.printStackTrace();
            }

    	    System.out.println("Enter the hotel id and the room number which need to be updated");
    	    int hid = scan.nextInt();
    	    int rnumber = scan.nextInt();
            scan.nextLine(); 
        System.out.println("Enter the category of the room to update");
        String rcategory = scan.nextLine();
        System.out.println("Enter the occupancy to update");
        int roccupancy = scan.nextInt();
        System.out.println("Enter the rate of the room to update");
        float rrate = scan.nextFloat();
        try
        {
            ResultSet result1 = statement.executeQuery("UPDATE Room SET category = '"+rcategory+"', occupancy = '"+roccupancy+"', rate = '"+rrate+"' where hotelID = "+hid+" and roomNo = "+rnumber+"");
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
	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Room");
            ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5),rsMetaData.getColumnName(6));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4),result.getString(5),result.getString(6));
            }
            }catch(SQLException e)
            {
              e.printStackTrace();
            }
	    System.out.println("Enter the hotel id and the room number which need to be deleted");
	    int hid = scan.nextInt();
	    int rnumber = scan.nextInt();
	    try
        {
            ResultSet result1 = statement.executeQuery("DELETE FROM Room where hotelID = "+hid+" and roomNo = "+rnumber+"");
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
        scan.nextLine();
        System.out.println("Enter the job title of the staff member");
        String sjobtitle = scan.nextLine();
        System.out.println("Enter the department of the staff");
        String sdept = scan.nextLine();
        System.out.println("Enter the phone number of the staff");
        BigInteger sphone = scan.nextBigInteger();
        scan.nextLine();
        System.out.println("Enter the hotel id in which the staff works");
        int hid = scan.nextInt();        
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Staff(name,age,jobTitle,dept,ph,hotelID)" +  "VALUES ('"+sname+"','"+sage+"', '"+sjobtitle+"','"+sdept+"','"+sphone+"','"+hid+"')");
            System.out.println("Staff information has been entered");
            int idFetched = statement.executeUpdate("SELECT staffID FROM Staff ORDER BY staffID desc LIMIT 1");
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
	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Staff");
            ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5),rsMetaData.getColumnName(6),rsMetaData.getColumnName(7));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4),result.getString(5),result.getString(6),result.getString(7));
            }
            }catch(SQLException e)
            {
              e.printStackTrace();
            }
	    System.out.println("Enter the staff id which need to be updated");
	    int sid = scan.nextInt();
            scan.nextLine();
        System.out.println("Enter the  name of the staff to update");
        String sname = scan.nextLine();
        System.out.println("Enter the age of the staff member to update");
        int sage = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the job title of the staff member to update");
        String sjobtitle = scan.nextLine();
        System.out.println("Enter the department of the staff to update");
        String sdept = scan.nextLine();
        System.out.println("Enter the phone number of the staff to update");
        BigInteger sphone = scan.nextBigInteger();
        scan.nextLine();
        System.out.println("Enter the hotel id in which the staff works to update");
        int hid = scan.nextInt();        
        ResultSet result1;
        try
        {
            result1 = statement.executeQuery("UPDATE Staff SET Name = '"+sname+"', age = '"+sage+"', jobTitle = '"+sjobtitle+"', dept = '"+sdept+"', ph = '"+sphone+"', hotelID = '"+hid+"' where staffID = "+sid+"");
            System.out.println("Staff information has been updated");
            System.out.println("Enter availability");
            int availability = scan.nextInt();
            if(sdept.equals("FrontDeskStaff")) {
            //	ResultSet result = statement.executeUpdate("INSERT INTO FrontDeskStaff " +  "VALUES ('"+idFetched+"')");
            }
            else if(sdept.equals("RoomServiceStaff")){
            	result1 = statement.executeQuery("UPDATE RoomServiceStaff SET availability = '"+availability+"' where staffID = "+sid+"");
            }
            else if(sdept.equals("CateringServiceStaff")) {
            	result1 = statement.executeQuery("UPDATE CateringServiceStaff SET availability = '"+availability+"' where staffID = "+sid+"");
            }
            else if(sdept.equals("Manager")) {
                result1 = statement.executeQuery("UPDATE Manager SET hotelID = '"+hid+"' where staffID = "+sid+"");
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
	    try{
	    ResultSet result = statement.executeQuery("SELECT * FROM Staff");
            ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5),rsMetaData.getColumnName(6),rsMetaData.getColumnName(7));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4),result.getString(5),result.getString(6),result.getString(7));
            }
            }catch(SQLException e)
            {
              e.printStackTrace();
            }

	    System.out.println("Enter the staff id which needs to be deleted");
	    int sid = scan.nextInt();
            scan.nextLine();
	    System.out.println("Enter the staff department to which the staff to be deleted belongs to");
	    String dept = scan.nextLine();
            ResultSet result1;  
	    try
             {
                    System.out.println("Hello1");
	    	    if(dept.equals("FrontDeskStaff")){
	    	   // result1 = statement.executeQuery("DELETE FROM FrontDeskStaff where staffID = "+sid+"");
	    	    statement.executeUpdate("DELETE FROM Staff where staffID = "+sid+"");
	        System.out.println("Staff information has been deleted");
	    	    }
	    	    else if(dept.equals("RoomServiceStaff")) {
		    	result1 = statement.executeQuery("DELETE FROM RoomServiceStaff where staffID = "+sid+"");
		    	statement.executeUpdate("DELETE FROM Staff where stafflID = "+sid+"");
		    System.out.println("Staff information has been deleted");
		    	}
	    	    else if(dept.equals("CateringServiceStaff")){
			result1 = statement.executeQuery("DELETE FROM CateringServiceStaff where staffID = "+sid+"");
			statement.executeUpdate("DELETE FROM Staff where staffID = "+sid+"");
			System.out.println("Staff information has been deleted");
			}
	    	    else if(dept.equals("Manager")) {
	    		result1 = statement.executeQuery("DELETE FROM Manager where staffID = "+sid+"");
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
        BigInteger cphone = scan.nextBigInteger();
        scan.nextLine();
        System.out.println("Enter the email of the customer");
        String cemail = scan.nextLine();
        try
        {
            ResultSet result = statement.executeQuery("INSERT INTO Customer(name,dob,phone,email)" +  "VALUES ('"+cname+"','"+cdob+"', '"+cphone+"','"+cemail+"')");
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
	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Customer");
            ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
        }
            }catch(SQLException e)
        {
            e.printStackTrace();
        }
	    System.out.println("Enter the customer id to update");
        int cid = scan.nextInt();
            scan.nextLine();
	    System.out.println("Enter the name of the customer to update");
        String cname = scan.nextLine();
        System.out.println("Enter the date of birth of the customer to update");
        String cdob = scan.nextLine();
        System.out.println("Enter the phone number of the customer to update");
        BigInteger cphone = scan.nextBigInteger();
        scan.nextLine();
        System.out.println("Enter the email of the customer to update");
        String cemail = scan.nextLine();
        try
        {
           ResultSet result1 = statement.executeQuery("UPDATE Customer SET Name = '"+cname+"', Dob = '"+cdob+"', Phone = '"+cphone+"', Email = '"+cemail+"' where customerID = "+cid+"");
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
	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Customer");
            ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
        }
            }catch(SQLException e)
        {
            e.printStackTrace();
        }
	    System.out.println("Enter the customer id which needs to be deleted");
	    int cid = scan.nextInt();
	    try
        {
            ResultSet result1 = statement.executeQuery("DELETE FROM Customer where customerID = "+cid+"");
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
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
            ResultSetMetaData rsMetaData = result.getMetaData();
        System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4));
        }
            }catch(SQLException e)
        {
            e.printStackTrace();
        }
      	ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
        //add printing statement
	    System.out.println("The hotel Id and room category");
	    int hid = scan.nextInt();
            scan.nextLine();
	    String rcategory = scan.nextLine();
	    int availability = 1;
	    ResultSet result1  = statement.executeQuery("SELECT roomNo FROM Room WHERE hotelID = '"+hid+"' AND category = '"+rcategory+"' AND availability='"+availability+"'");
	    ResultSetMetaData rsMetaData = result1.getMetaData();
        System.out.format("%n%-25s%n%n",rsMetaData.getColumnName(1));
	    while (result1.next()) {
	        System.out.format("%-25s%n",result1.getString(1));
	    }
	    
    }
    //Function to check room availability by hotel and room number
    public void roomAvailabilityByHotelAndRoomno(Statement statement) throws SQLException {
    		Scanner scan = new Scanner(System.in);
    		System.out.println("The hotel information");
      	    try {
            ResultSet result = statement.executeQuery("SELECT * FROM Hotel");
            ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
            while (result.next()) {
                System.out.format("%-25s%25s%25s%25s%n",result.getString(1), result.getString(2), result.getString(3), result.getString(4));
            }
            }catch(SQLException e)
            {
            e.printStackTrace();
            } 
	    System.out.println("The hotel Id and room number");
	    int hid = scan.nextInt();
            scan.nextLine(); 
	    int rno = scan.nextInt();
	    int availability = 1;
	    ResultSet result1 = statement.executeQuery("SELECT (CASE WHEN availability = '"+availability+"' THEN 'Available' ELSE 'Not Available' END) AS 'Room Availability' FROM Room WHERE hotelID = '"+hid+"' AND roomNo = '"+rno+"'");
	   // ResultSetMetaData rsMetaData = result1.getMetaData();
           // System.out.format("%n%-25s%n%n",rsMetaData.getColumnName(1));
	    while (result1.next()) {
	        System.out.format("%-25s%n",result1.getString(1));
	    }
    }
    //Function to assign rooms to customers accroding to the request and availability
    //Correct last two functionalities
    public void assignRoom(Statement statement) throws SQLException {
      	Scanner scan = new Scanner(System.in);
	System.out.println("The billing information");
            try {
            ResultSet resultBilling = statement.executeQuery("SELECT * FROM BillingInfo");
            ResultSetMetaData rsMetaData = resultBilling.getMetaData();
            System.out.format("%n%-25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3));
            while (resultBilling.next()) {
                System.out.format("%-25s%25s%25s%n",resultBilling.getString(1), resultBilling.getString(2), resultBilling.getString(3));
            }
            }catch(SQLException e)
            {
            e.printStackTrace();
            }
  	    System.out.println("The Customer information");
	    try {
            ResultSet resultCust = statement.executeQuery("SELECT * FROM Customer");
            ResultSetMetaData rsMetaData = resultCust.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5));
            while (resultCust.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%n",resultCust.getString(1), resultCust.getString(2), resultCust.getString(3), resultCust.getString(4), resultCust.getString(5));
            }
            }catch(SQLException e)
            {
            e.printStackTrace();
            }
	    System.out.println("The Hotel information");
  	    try {
            ResultSet resultHotel = statement.executeQuery("SELECT * FROM Hotel");
            ResultSetMetaData rsMetaData = resultHotel.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
            while (resultHotel.next()) {
                System.out.format("%-25s%25s%25s%25s%n",resultHotel.getString(1), resultHotel.getString(2), resultHotel.getString(3), resultHotel.getString(4));
            }
            }catch(SQLException e)
            {
            e.printStackTrace();
            }
  	    System.out.println("The Staff information");
	    try{
            ResultSet resultStaff = statement.executeQuery("SELECT * FROM Staff");
            ResultSetMetaData rsMetaData = resultStaff.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4),rsMetaData.getColumnName(5),rsMetaData.getColumnName(6),rsMetaData.getColumnName(7));
            while (resultStaff.next()) {
                System.out.format("%-25s%25s%25s%25s%25s%25s%25s%n",resultStaff.getString(1), resultStaff.getString(2), resultStaff.getString(3), resultStaff.getString(4),resultStaff.getString(5),resultStaff.getString(6),resultStaff.getString(7));
            }
            }catch(SQLException e)
            {
              e.printStackTrace();
            }
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
        ResultSet result; 
        try
        {
            result = statement.executeQuery("INSERT INTO CheckInInfo(startTime,guestCount,bID,customerID,hotelID,staffID,roomNo)" +  "VALUES ('NOW()','"+gcount+"', '"+bid+"','"+cid+"','"+hid+"','"+sid+"','"+rno+"')");
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
  	    try {
            ResultSet resultHotel = statement.executeQuery("SELECT * FROM Hotel");
            ResultSetMetaData rsMetaData = resultHotel.getMetaData();
            System.out.format("%n%-25s%25s%25s%25s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2),rsMetaData.getColumnName(3),rsMetaData.getColumnName(4));
            while (resultHotel.next()) {
                System.out.format("%-25s%25s%25s%25s%n",resultHotel.getString(1), resultHotel.getString(2), resultHotel.getString(3), resultHotel.getString(4));
            }
            }catch(SQLException e)
            {
            e.printStackTrace();
            }
  	    System.out.println("Enter the hotel id");
        int hid = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the staff id");
        int sid = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the room service staff id");
        int rsid = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the catering service staff id");
        int csid = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the room number");
        int rno = scan.nextInt();
        scan.nextLine();
        int available = 1;
        String nullValue = NULL;
        ResultSet result;
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
//include synchronization.
//discuss the feature of frontDesk staff updation
