package dbms;
import java.sql.* ;  // for standard JDBC programs
import java.util.Scanner;

public class WolfInn {
	
	// Update user info 
	private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/$USER$"; // Using SERVICE_NAME

	// Update your user and password info here!

	private static final String user = "$USER$";
	private static final String password = "$PASSWORD$";

	public static void main(String[] args) {
		try {
			// Loading the driver. This creates an instance of the driver
			// and calls the registerDriver method to make MySql(MariaDB) Thin available to clients.
			Class.forName("org.mariadb.jdbc.Driver");

			Connection connection = null;
            Statement statement = null;
            ResultSet result = null;

            try {
            // Get a connection instance from the first driver in the
            // DriverManager list that recognizes the URL jdbcURL
            connection = DriverManager.getConnection(jdbcURL, user, password);

            // Create a statement instance that will be sending
            // your SQL statements to the DBMS
            statement = connection.createStatement();

            // use statement to write create/update statements
            //statement.executeUpdate("CREATE TABLE CATS (CNAME VARCHAR(20), " +
            //"TYPE VARCHAR(30), AGE INTEGER, WEIGHT FLOAT, SEX CHAR(1))");
            Scanner scan = new Scanner(System.in);
    		
    			System.out.println("Please select the Task or Operation you would like to perform: ");
    			System.out.println("-------------------------\n");
            System.out.println("1 - Information Processing");
            System.out.println("2 - Maintaining Service Records");
            System.out.println("3 - Maintaining billing accounts");
            System.out.println("4 - Reports");

            int choice = scan.nextInt();
            
            switch(choice) {
            		case 1 :	infoProcessing();
            					break;
            		case 2 : maintainServiceRecord();
            					break;
            		case 3 : maintainBillingAccount();
    				break;
            		case 4 : generateReport();
    				break;
            		default : System.out.println("Please select a valid task or operation");
    				break;
            }
            scan.close();
            
            } finally {
                close(result);
                close(statement);
                close(connection);
            }
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	}

	private static void generateReport() {
		// TODO Auto-generated method stub
		
	}

	private static void maintainBillingAccount() {
		// TODO Auto-generated method stub
		
	}

	private static void maintainServiceRecord() {
		// function call for maintainServiceRecord
		
	}

	private static void infoProcessing() {
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
        
        switch(choice) {
        		case 1 :	// add respective function calls
        					break;
        		default : System.out.println("Please select a valid task");
        					break;
        }
        scan.close();
	}
	
	static void close(Connection connection) {
        if(connection != null) {
            try {
            connection.close();
            } catch(Throwable whatever) {}
        }
    }
    static void close(Statement statement) {
        if(statement != null) {
            try {
            statement.close();
            } catch(Throwable whatever) {}
        }
    }
    static void close(ResultSet result) {
        if(result != null) {
            try {
            result.close();
            } catch(Throwable whatever) {}
        }
    }
}

