package org.eniware.central.common.dao.jdbc;

import java.sql.*;

public class JDBCExample {
private static Statement sql;

public static void main(String[] args)  throws ClassNotFoundException, SQLException{

System.out.println("——– PostgreSQL " + "JDBC Connection Testing ————");

try {

Class.forName("org.postgresql.Driver");

} catch (ClassNotFoundException e) {

System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
e.printStackTrace();
return;

}

System.out.println("PostgreSQL JDBC Driver Registered!");

Connection connection = null;

try {

connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/eniware", "postgres",
"Eniware8");

} catch (SQLException e) {

System.out.println("Connection Failed! Check output console");
e.printStackTrace();
return;

}

sql = connection.createStatement();

if (connection != null) {

sqlStatement("INSERT INTO TESTTABLE (product_id,product_name,) VALUES(1,  ‘product1’)");
System.out.println("Successfully added");

} else {

System.out.println("Failed to make connection!");

}

}

private static void sqlStatement(String string) {
	// TODO Auto-generated method stub
	
}

}