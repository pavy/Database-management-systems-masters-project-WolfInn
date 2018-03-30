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
            } finally {
                close(rs);
                close(stmt);
                }
          }
    }
}
