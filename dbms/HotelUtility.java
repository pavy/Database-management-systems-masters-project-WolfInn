package dbms;
import java.sql.* ;  // for standard JDBC programs
import java.util.Scanner;

public class HotelUtility {
    public void maintainServiceRecord(Statement statement)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your ID(Staff ID): ");
        String staffID = scan.nextLine();
        System.out.println("Enter Service ID: ");
        String serviceID = scan.nextLine();
        System.out.println("Enter Billing ID: ");
        String bID = scan.nextLine();
        System.out.println("Enter Quantity: ");
        String qty = scan.nextLine();
        try
        {
            ResultSet result = statement.executeQuery("select qty from Provides where staffID=" + staffID + " AND serviceID=" + serviceID + " AND bID=" + bID);
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
        System.out.println("Enter billing ID: ");
        String bID = scan.nextLine();

        try
        {
            ResultSet result = statement.executeQuery("(SELECT \"Room Stay\" AS Item, (CEIL(TIMESTAMPDIFF("+ 
            "SECOND,CI.startTime,CI.endTime)/86400))*R.rate  as Amount FROM CheckInInfo CI, Room R WHERE R.roomNo=CI.roomNo AND "+
            "R.hotelID=CI.hotelID AND CI.bID="+bID+")"+ 
            "UNION "+
            "(SELECT S.name AS Item, SUM(O.price*P.qty) AS Amount from Provides P, Services S, Offers O, CheckInInfo C WHERE "+
            "P.serviceID=S.serviceID AND C.bID=P.bID AND C.hotelID=O.hotelID AND C.roomNo=O.roomNo AND P.serviceID=O.serviceID AND P.bID="+bID+
            " GROUP by P.serviceID)"+
            "UNION"+
            "(SELECT \"Total\", (CASE WHEN (SELECT COUNT(*) FROM BillingInfo, Card_payment WHERE BillingInfo.bID="+bID+" AND "+
            "BillingInfo.paymentID=Card_payment.paymentID AND Card_payment.type='hotel card')=0 THEN SUM(C.Amount) ELSE SUM(C.Amount)*0.95 END) "+
            "FROM "+
            "((SELECT \"Room Stay\" AS Item, (CEIL(TIMESTAMPDIFF(SECOND,CI.startTime,CI.endTime)/86400))*R.rate  as Amount FROM CheckInInfo CI, "+
            "Room R WHERE R.roomNo=CI.roomNo AND R.hotelID=CI.hotelID AND CI.bID="+bID+") "+
            "UNION "+
            "(SELECT S.name AS Item, SUM(O.price*P.qty) AS Amount from Provides P, Services S, Offers O, CheckInInfo C WHERE "+
            "P.serviceID=S.serviceID AND C.bID=P.bID AND C.hotelID=O.hotelID AND C.roomNo=O.roomNo AND P.serviceID=O.serviceID AND P.bID="+bID+
            " GROUP by P.serviceID))AS C)");

	    ResultSetMetaData rsMetaData = result.getMetaData();
            System.out.format("%n%-25s%16s%n%n",rsMetaData.getColumnName(1),rsMetaData.getColumnName(2));
	    while (result.next()) {
	        System.out.format("%-25s%16s%n",result.getString(1), result.getString(2));
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

    }
}
