import  java.sql.*;

public class informationProcessing {

    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/avarote";

    public static void main(String[] args) {
        try {
Class.forName("org.mariadb.jdbc.Driver");
    String user = "avarote";
    String passwd = "200203589";

            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {
                    conn = DriverManager.getConnection(jdbcURL, user, passwd);
// Create a statement object that will be sending your
// 		// SQL statements to the DBMS
                    stmt = conn.createStatement();
                    stmt.executeUpdate("INSERT INTO Hotel " +  "VALUES ('Hilton','1230 Champion Ct', 1232376543)");
                    stmt.executeUpdate("UPDATE HOTEL SET name = 'Hilton', address = '1240 Champion Ct', phoneNumber = 3456372345 where hotelID = 6");
                    stmt.executeUpdate("DELETE FROM Hotel where hotelID = 6");
                    
                    stmt.executeUpdate("INSERT INTO Room " +  "VALUES (1, 301, 'Economy', 4, 60.49)");
                    stmt.executeUpdate("UPDATE Room SET category = 'Executive Suite', occupancy = 2, rate = 200 where hotelID = 1 and roomNo = 301");
                    stmt.executeUpdate("DELETE FROM Room where hotelID = 1 and roomNo = 301");
                    
                    stmt.executeUpdate("INSERT INTO Staff " +  "VALUES ('Robin', 35, 'FrontDesk', 'FrontDeskStaff', 9822211145, 1)");
                    stmt.executeUpdate("INSERT INTO FrontDeskStaff " +  "VALUES (17)");
                    stmt.executeUpdate("UPDATE Staff SET name = 'Robin', age = 36, jobTitle = 'FrontDesk', dept = 'FrontDeskStaff',ph = 9822211146, hotelID = 2 where staffID = 17");
                    stmt.executeUpdate("DELETE FROM FrontDeskStaff where staffID = 17");
                    stmt.executeUpdate("DELETE FROM Staff where staffID = 17");
                } finally {
                close(rs);
                close(stmt);
                }
          }
    }
}
