/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.mycompany.autointerfacews.dao.MySQLCon;
import com.mycompany.autointerfacews.mydata.AdminReturn;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author mou1609
 */
@Path("admin")
public class RigisterResource {
    MySQLCon mySQLCon;
    Gson gson;

    @Inject
    public RigisterResource(MySQLCon mySQLCon, Gson gson) {
        this.mySQLCon = mySQLCon;
        this.gson = gson;
    }

    @GET
    public Response status() {
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("register")
    public Response register(
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("first") String first,
            @QueryParam("last") String last,
            @QueryParam("des") String des,
            @QueryParam("organization") String organization
    ) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(organization);
        int userId = mySQLCon.getUserId(username);
        if (userId != -1) {//we have before
            return Response.status(200)
                    .entity("Duplicated Value")
                    .build();
        }
        mySQLCon.addUser(username, password, first, last, des, organization);
        String locationRes = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/" + username;
        try {
            new File(locationRes + "/Resources/").mkdirs();
            new File(locationRes + "/Functions/").mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("login")
    public Response login(
            @QueryParam("username") String username,
            @QueryParam("password") String password
    ) {
        System.out.println(username);
        System.out.println(password);
        String pass = mySQLCon.getUserPassword(username);
        System.out.println(pass);
        if (pass == null) {
            return Response.status(200)
                .entity("you dont have permission yet.")
                .build();
        } else if (!pass.equals(password)) {//we have before
            return Response.status(200)
                    .entity("password not match")
                    .build();
        }
        return Response.status(200)
                .entity("done")
                .build();
    }
    
    @GET
    @Path("users")
    public Response getUsers() {
        String res = "";
        List<List<String>> content = mySQLCon.getUsers();
        AdminReturn a = new AdminReturn(content);
        System.out.println(a);
        res = gson.toJson(a);
        System.out.println(res);
        return Response.status(200)
                .entity(res)
                .build();
    }
    
    @GET
    @Path("grand")
    public Response grandAccess(@QueryParam("id") String id) {
        String res = "";
        mySQLCon.grandAccess(id);
        
        return Response.status(200)
                .entity("good")
                .build();
    }
    
}
