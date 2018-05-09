/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import static com.mycompany.autointerfacews.helper.MyFileReader.getXMLFileURLs;
import com.mycompany.autointerfacews.wrapper.Module;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mou1609
 */
@Path("agg")
public class AggregateResource {

    final static String aggFileName = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Aggregate\\agg.csv";
    final static String aggFunName = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Aggregate\\aggFun.csv";

    Gson gson;

    @Inject
    public AggregateResource(Gson gson) {
        this.gson = gson;
    }

    @GET
    public Response getStatues() {
        return Response.status(200)
                .entity("ModuleResource good")
                .build();
    }

    @GET
    @Path("getAll")
    public Response getModules() {
        String res = "";
        List<List<String>> content = MyFileReader.readCSVContent(aggFileName);
        List<String> a = content.stream().map(t -> t.get(0)).collect(Collectors.toList());
        System.out.println(a);
        res = gson.toJson(a);
        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("fungetAll")
    public Response getFunModules() {
        String res = "";
        List<List<String>> content = MyFileReader.readCSVContent(aggFunName);
        List<String> a = content.stream().map(t -> t.get(0)).collect(Collectors.toList());
        System.out.println(a);
        res = gson.toJson(a);
        return Response.status(200)
                .entity(res)
                .build();
    }
    
}
