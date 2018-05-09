/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dao;

import com.mycompany.autointerfacews.dataIcon.MyResource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author jupiter
 */
public class MySQLCon {

    Connection connection;

    public MySQLCon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }
        try {
            //sessionVariables=wait_timeout=600
            connection = DriverManager
                    //                    .getConnection("jdbc:mysql://localhost:3306/flowqdb?relaxAutoCommit=true&sessionVariables=wait_timeout=2147483&autoReconnect=true", "root", "root");
                    //                    .getConnection("jdbc:mysql://localhost:3306/flowqdb?relaxAutoCommit=true&sessionVariables=wait_timeout=2147483&autoReconnect=true", "root", "hasan!xin");
                    .getConnection("jdbc:mysql://localhost:3306/flowqdb?relaxAutoCommit=true&sessionVariables=wait_timeout=2147483&autoReconnect=true", "root", "hasan!xin");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        if (connection == null) {
            System.out.println("Failed to make connection!");
        }
    }

    public Connection getConn() {
        return connection;
    }

    public void addUser(String username, String password,
            String first,
            String last,
            String des,
            String organization) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into registeredUsersVisFlow values (default, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, organization);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, first);
            preparedStatement.setString(5, last);
            preparedStatement.setString(6, des);
            preparedStatement.setBoolean(7, false);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserId(String username) {
        int res = -1;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select id from registeredUsersVisFlow where username='" + username + "'");
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public String getUserPassword(String username) {
        String res = null;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select pass from registeredUsersVisFlow where username='" + username + "' and approved = true");
            while (rs.next()) {
                res = rs.getString("pass");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public List<List<String>> getUsers() {
        List<List<String>> res = new ArrayList<>();
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select * from registeredUsersVisFlow");
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("orginization"));
                row.add(rs.getString("firstName"));
                row.add(rs.getString("lastName"));
                row.add(rs.getString("designation"));
                row.add(rs.getBoolean("approved") ? "true" : "false");
                res.add(row);
            }
        } catch (Exception e) {
        }
        return res;
    }

    public void grandAccess(String id) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("update registeredusersvisflow set approved = true where id = ?");
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
