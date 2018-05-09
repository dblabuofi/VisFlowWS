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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mou1609
 */
@Path("module")
public class ModuleResource {
    final static String ModuleLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Module\\";
    
    Gson gson;
    @Inject
    public ModuleResource(Gson gson) {
        this.gson = gson;
    }

    @GET
    public Response getStatues() {
        return Response.status(200)
                .entity("ModuleResource good")
                .build();
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addModule(String g) throws CodeException, IOException {
        System.out.println(g);
        Module m = gson.fromJson(g, Module.class);
        MyFileReader.writeFile(ModuleLocation + m.getFileName(), g);
        
        return Response.status(200)
                .entity("Module added")
                .build();
    }
    @GET
    @Path("getModules")
    public Response getSelectedModules(@QueryParam("module") String module) {
        File file = new File(ModuleLocation);
        List<Module> ModuleList = new ArrayList<>();
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                ModuleList.add(gson.fromJson(MyFileReader.readFileAll(f.getAbsolutePath()), Module.class));
            }
        }
        
        List<Module> filteredModule = ModuleList.stream().filter(t->
                t.getFileName().toLowerCase().contains(module.toLowerCase()) || t.getDescription().toLowerCase().contains(module.toLowerCase())
        ).collect(Collectors.toList());
        
        String res = gson.toJson(filteredModule);
        
        return Response.status(200)
                .entity(res)
                .build();
    }
    @GET
    @Path("getAll")
    public Response getModules() {
        File file = new File(ModuleLocation);
        List<Module> ModuleList = new ArrayList<>();
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                ModuleList.add(gson.fromJson(MyFileReader.readFileAll(f.getAbsolutePath()), Module.class));
            }
        }
//        Type listModuleType = new TypeToken<List<Module>>() {}.getType();
        String res = gson.toJson(ModuleList);
        
        return Response.status(200)
                .entity(res)
                .build();
    }

    
    
}
