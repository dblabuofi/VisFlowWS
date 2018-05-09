/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mou1609
 */

@Path("printerRules")
public class PrinterRulesResources {
        WebResourceImageDownloader webResourceImageDownloader;

        @Inject 
        public PrinterRulesResources(WebResourceImageDownloader webResourceImageDownloader) {
                this.webResourceImageDownloader = webResourceImageDownloader;
        }
          
          
          
        
        @POST
        @Path("KEGGPathway")
//        @Consumes(MediaType.APPLICATION_JSON)
        public Response getKEGG(String g) throws Exception {
                System.out.println("getKEGG");
                System.out.println(g);
                
                g = g.substring(1, g.length() - 1);
                List<String> fileNames = Arrays.asList(g.split(","));
                fileNames = fileNames.stream().map( t -> t.replace("\"", "")).collect(Collectors.toList());
                String location = fileNames.get(fileNames.size() - 1);
                fileNames.remove(fileNames.size() - 1);
                webResourceImageDownloader.downloadKEGGPathwayImageByName(fileNames, location);
                
                return Response.status(200)
                        .entity("good GOGGDGSDG*************")
                        .build();
        }
        
        
        
        
        
        
        
        
        
}
