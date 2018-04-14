package dbms;

import java.sql.*; // for standard JDBC programs
import java.util.Scanner;

public class WolfInn {

	// Update user info
	private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/piyer3"; // Using
																		// SERVICE_NAME

	// Update your user and password info here!

	private static final String user = "piyer3";
	private static final String password = "Pavi_DBMS540";

	public static void main(String[] args) {
		try {
			// Loading the driver. This creates an instance of the driver
			// and calls the registerDriver method to make MySql(MariaDB) Thin
			// available to clients.
			Class.forName("org.mariadb.jdbc.Driver");
			Connection connection = null;
			Statement statement = null;

			try {
				// Get a connection instance from the first driver in the
				// DriverManager list that recognizes the URL jdbcURL
				connection = DriverManager.getConnection(jdbcURL, user,
						password);

				// Create a statement instance that will be sending
				// your SQL statements to the DBMS
				statement = connection.createStatement();

				// use statement to write create/update statements
				// statement.executeUpdate("CREATE TABLE CATS (CNAME VARCHAR(20), "
				// +
				// "TYPE VARCHAR(30), AGE INTEGER, WEIGHT FLOAT, SEX CHAR(1))");
				mainMenu(statement, connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void generateReport(Statement statement, Connection connection) {
		// TODO Auto-generated method stub

		String nextChoice;
		do{
    		System.out.println("1 - Report occupancy by hotel");
    		System.out.println("2 - Report occupancy by room type");
    		System.out.println("3 - Report occupancy by date range");
    		System.out.println("4 - Report occupancy by date City");
    		System.out.println("5 - Report of staff information group by role");
    		System.out.println("6 - Information on all staff members serving the customer during the stay");
    		System.out.println("7 - Revenue earned by a hotel during a given date range");
//    		System.out.println("8 - Return to main menu");
    		HotelUtility obj = new HotelUtility();
    		Scanner scan = new Scanner(System.in);
    		int choice = scan.nextInt();
    		switch (choice) {
    		case 1:
    			obj.reportOccupancyByHotel(statement);
    			break;
    
    		case 2:
    			obj.reportOccupancyByRoomType(statement);
    			break;
    
    		case 3:
    			obj.reportOccupancyByDateRange(statement);
    			break;
    
    		case 4:
    			obj.reportOccupancyByCity(statement);
    			break;
    
    		case 5:
    			obj.reportStaffInfoGroupedByRole(statement);
    			break;
    
    		case 6:
    			obj.reportStaffServingCustomerDuringStay(statement);
    			break;
    
    		case 7:
    			obj.reportRevenueEarnedInDateRange(statement);
    			break;
    		default:
    			System.out.println("Not a valid choice");
    			break;
    		}
    		
    		System.out.println("Do you want to continue?(Y/N)");
    		Scanner sc = new Scanner(System.in);
    		nextChoice = scan.nextLine();
    		while(!(nextChoice.equals("Y")||nextChoice.equals("y")||nextChoice.equals("N")||nextChoice.equals("n"))){
    			System.out.println("Enter a valid choice");
    			nextChoice = scan.nextLine();
    		}
    	}while(nextChoice.equals("Y")||nextChoice.equals("y"));
	}

	private static void closeConnection(Statement statement, Connection connection) {
		try {
			close(statement);
			close(connection);
			System.out.println("Closing all connections and exiting from the system...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void infoProcessing( Statement statement, Connection connection) throws SQLException {
		// handle 15 cases
		HotelUtility obj = new HotelUtility(connection);
		Scanner scan = new Scanner(System.in);
		String nextChoice;
        do{
            System.out.println("1 - Enter information about Hotel");
            System.out.println("2 - Update Hotel information");
            System.out.println("3 - Delete Hotel");
            System.out.println("4 - Enter information about Rooms");
            System.out.println("5 - Update Room information");
            System.out.println("6 - Delete Room");
            System.out.println("7 - Enter information about Staff");
            System.out.println("8 - Update Staff information");
            System.out.println("9 - Delete Staff");
            System.out.println("10 - Enter information about Customer");
            System.out.println("11 - Update Customer information");
            System.out.println("12 - Delete Customer");
            System.out.println("13 - Check Availability of room by Hotel and Room Type");
            System.out.println("14 - Check Availability of room by Hotel and Room Number");
            System.out.println("15 - Assign room to customer");
            System.out.println("16 - Release room");
            
            int choice = scan.nextInt();
            
            switch(choice) {
            		case 1 :	obj.enterHotelInfo(statement);
            					break;
            		case 2 :obj.updateHotelInfo(statement);
            		            break;
            		case 3 :obj.deleteHotel(statement);
            		            break;
            		case 4 :obj.enterRoomInfo(statement);
            		            break;
            		case 5 :obj.updatedRoomInfo(statement);
            		            break;
            		case 6 :obj.deleteRoom(statement);
            		            break;
            		case 7 :obj.enterStaffInfo(statement);
            		            break;
            		case 8 :obj.updateStaffInfo(statement);
            		            break;
            		case 9 :obj.deleteStaff(statement);
            		            break;
            		case 10:obj.enterCustomerInfo(statement);
            		            break;
            		case 11:obj.updateCustomerInfo(statement);
            		            break;
            		case 12:obj.deleteCustomer(statement);
            		            break;
            		case 13:obj.roomAvailabilityByHotelAndRoomtype(statement);
            		            break;
            		case 14:obj.roomAvailabilityByHotelAndRoomno(statement);
    	            			break;            
            		case 15:obj.assignRoom(statement);
            		            break;
            		case 16:obj.releaseRoom(statement);
            		            break;
            		default : System.out.println("Please select a valid task");
            					break;
            }
            
            System.out.println("Do you want to continue?(Y/N)");
    		
    		nextChoice = scan.nextLine();
    		while(!(nextChoice.equals("Y")||nextChoice.equals("y")||nextChoice.equals("N")||nextChoice.equals("n"))){
    			System.out.println("Enter a valid choice");
    			nextChoice = scan.nextLine();
    		}
   		}while(nextChoice.equals("Y")||nextChoice.equals("y"));
	}
	/*private static void infoProcessing() {
		// handle 15 cases
		Scanner scan = new Scanner(System.in);

		System.out.println("1 - Enter information about Hotel");
		System.out.println("2 - Update Hotel information");
		System.out.println("3 - Delete Hotel");
		System.out.println("4 - Enter information about Rooms");
		System.out.println("5 - Update Room information");
		System.out.println("6 - Delete Room");
		System.out.println("7 - Enter information about Staff");
		System.out.println("8 - Update Staff information");
		System.out.println("9 - Delete Staff");
		System.out.println("10 - Enter information about Customer");
		System.out.println("11 - Update Customer information");
		System.out.println("12 - Delete Customer");
		System.out.println("13 - Check Availability of room");
		System.out.println("14 - Assign room to customer");
		System.out.println("15 - Release room");

		int choice = scan.nextInt();

		switch (choice) {
		case 1: // add respective function calls
			break;
		default:
			System.out.println("Please select a valid task");
			break;
		}
		scan.close();
	}*/

	static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Throwable whatever) {
			}
		}
	}

	static void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (Throwable whatever) {
			}
		}
	}

	static void close(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (Throwable whatever) {
			}
		}
	}

	private static void mainMenu(Statement statement, Connection connection) {
		try {

			Scanner scan = new Scanner(System.in);
			while(true){
    			System.out.println("Please select the Task or Operation you would like to perform: ");
    			System.out.println("-------------------------\n");
    			System.out.println("1 - Information Processing");
    			System.out.println("2 - Maintaining Service Records");
    			System.out.println("3 - Maintaining billing accounts");
    			System.out.println("4 - Reports");
    			System.out.println("5 - Close application");
    
    			int choice = scan.nextInt();
    			HotelUtility obj = new HotelUtility();
    			switch (choice) {
    			case 1:
    				infoProcessing(statement, connection);
    				break;
    			case 2:
    				obj.maintainServiceRecord(statement);
    				break;
    			case 3:
    				obj.maintainBillingAccount(statement);
    				break;
    			case 4:
    				generateReport(statement, connection);
    				break;
    			case 5: closeConnection(statement, connection);
				scan.close();
				System.exit(0);
    			default:
    				System.out.println("Incorrect value entered. Closing application...");
    			}
			}
			

		}

		catch (Throwable oops) {
			oops.printStackTrace();
		}
	}
}


