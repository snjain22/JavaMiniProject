package com.pwdmgr.passwordmanagerv1;

import java.sql.*;

public class SQLLinker {
    Connection connection = null;
    public Connection connectsql(){
        String url = "jdbc:mysql://localhost:3306/PasswordManager";
        String user = "root";
        String password = "Sam@SQL23";

        // Initialize the connection
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            // Create the database connection
            connection = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = connection.prepareStatement("SELECT*FROM Password;");
            ps.executeQuery();

            return connection;
        }
        catch (SQLException e)
        {
            System.err.println("Connection to the database failed. Error: " + e.getMessage());
            return null;
        }

        catch (ClassNotFoundException e)
        {
            System.err.println("MySQL JDBC driver not found. Make sure you have it in your classpath.");
            return null;
        }

    }

    public void closesql(){
        if (connection != null)
        {
            try{
            connection.close();
            System.out.println("Connection closed.");
            }
            catch (SQLException e) {}
        }
        else {
            System.out.println("SQL Database not connected");
        }
        }

//    public static void main(String[] args) {
//        myjdbc A = new myjdbc();
//        A.connectsql();
//        A.closesql();
//
//    }
    }

