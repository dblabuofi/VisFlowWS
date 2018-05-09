/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.Library;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.autointerfacews.mydata.RegisterAttribute;
import static com.mycompany.autointerfacews.resources.AggregateResource.aggFileName;
import static com.mycompany.autointerfacews.resources.AggregateResource.aggFunName;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
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
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author jupiter
 */
@Path("resource")
public class ResourcesResources {

    Gson gson;

    @Inject
    public ResourcesResources(Gson gson) {
        this.gson = gson;
    }

    @GET
    @Path("preview")
    public Response getResoure(@QueryParam("fileURL") String fileURL) {
        System.out.println("here22");
        String imageDataString = null;
        byte[] imageData = null;
        try {
            System.out.println(fileURL);
            BufferedImage bufImgOne = null;

            File repositoryFile = new File(fileURL);
            String fileName = repositoryFile.getName();
            if (repositoryFile.length() <= 1024) {
                Response.ResponseBuilder response = Response.ok(repositoryFile, MediaType.APPLICATION_OCTET_STREAM);
                //                        response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                return response.build();
            }
        } catch (Exception e) {
        }

        return Response.status(201)
                .entity("file too large")
                .build();
    }

    @GET
    @Path("resource")
    public Response getResoures(@QueryParam("username") String username) {
        System.out.println("resource all");
        String resourceLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Resources/";
        List<MyResource> reserouces = new ArrayList<>();
        reserouces = MyFileReader.getResources(resourceLocation);
        System.out.println(username);
        if (username != null && !username.equals("null")) {
            System.out.println(username);
            String userLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/" + username + "/Resources/";
            reserouces.addAll(MyFileReader.getResources(userLocation));
        }
        
        reserouces.forEach(t -> {
            t.setText(t.getResourceName());
        });

        String res = gson.toJson(reserouces);

        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("functions")
    public Response getFunctions(@QueryParam("username") String username) {
        System.out.println("functions all");
//                 Type listType = new TypeToken<List<Library>>() {}.getType();
        List<Function> functions = new ArrayList<>();
        String functionLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Functions/";
        functions = MyFileReader.getFunctions(functionLocation);
        
        if (username != null && !username.equals("null")) {
            String userLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/" + username + "/Functions/";
            functions.addAll(MyFileReader.getFunctions(userLocation));
        }
        
        //we clean
        functions.forEach(
                t -> {
                    t.setText(t.getFunctionName());
                });
        String res = gson.toJson(functions);

        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("libraries")
    public Response getLibraries() {
//                 Type listType = new TypeToken<List<Library>>() {}.getType();
        List<Library> libraries = new ArrayList<>();
        List<Function> functions = new ArrayList<>();
        String libraryLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Libraries/";
        String functionLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Functions/";
        libraries = MyFileReader.getLibraries(libraryLocation);
        functions = MyFileReader.getFunctions(functionLocation);
        //we clean
        libraries.forEach(
                t -> {
                    t.setText(t.getLibraryName());
                    t.setChildren(new ArrayList<>());
                });
        for (Function f : functions) {
            f.setText(f.getFunctionName());
            Library l = libraries.stream().filter(g -> g.getId().equals(f.getLibraryId())).findFirst().get();
            l.getChildren().add(f);
        }

        String res = gson.toJson(libraries);

        return Response.status(200)
                .entity(res)
                .build();
    }

    Type listAttributeType = new TypeToken<List<RegisterAttribute>>() {
    }.getType();

    @POST
    @Path("uploadresource")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeResource(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("resourceType") String resourceType,
            @FormDataParam("organization") String organization,
            @FormDataParam("resourceName") String resourceName,
            @FormDataParam("description") String description,
            @FormDataParam("aggregateName") String aggregateName,
            @FormDataParam("URL") String URL,
            @FormDataParam("URLExample") String URLExample,
            @FormDataParam("suggestOutputFileName") String suggestOutputFileName,
            @FormDataParam("schema") String schema,
            @FormDataParam("sqltablescript") String sqltablescript,
            @FormDataParam("returnFileType") String urlReturnFileType,
            @FormDataParam("method") String method,
            @FormDataParam("methodReturnType") String methodReturnFileType,
            @FormDataParam("wrapper") String wrapper,
            @FormDataParam("separator") String separator,
            @FormDataParam("textTableWrapperHeaders") String textTableWrapperHeaders,
            @FormDataParam("resultContainsHeaderInfo") boolean resultContainsHeaderInfo,
            @FormDataParam("jsonToxmlWrapperAttributes") String jsonToxmlWrapperAttributes,
            @FormDataParam("myTableExactorHeaders") String myTableExactorHeaders,
            @FormDataParam("attributeList") String attributeList,
            @FormDataParam("username") String username
    ) {
        System.out.println(contentDispositionHeader.getFileName());
        System.out.println(resourceType);
        System.out.println(organization);
        System.out.println(schema);
        System.out.println(attributeList);
        System.out.println(urlReturnFileType);
        String dataLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        String resourceLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\";

        if (username == null || username.equals("null")) {
            resourceLocation += "Resources\\";
        } else {
            resourceLocation += username + "\\Resources\\";
        }

        //aggregate names
        List<String> aggregate = MyFileReader.readCSVContent(aggFileName).stream().map(t -> t.get(0)).collect(Collectors.toList());
        if (!aggregate.contains(aggregateName)) {
            if (aggregate.isEmpty()) {
                MyFileReader.appendToTheEnd(aggFileName, "\"" + aggregateName + "\"");
            } else {
                MyFileReader.appendToTheEnd(aggFileName, "\n\"" + aggregateName + "\"");
            }
        }

        //save data 
        if (!contentDispositionHeader.getFileName().isEmpty()) {
            MyFileReader.inputStreamToFile(fileInputStream, dataLocation + contentDispositionHeader.getFileName());
        }
        if (resourceType.toLowerCase().equals("csv")) {
            QueryTree schemaTree = gson.fromJson(schema, QueryTree.class);
            List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);
            urlReturnFileType = "CSV";
//            methodReturnFileType = "CSV";
            List<MyAttribute> headers = new ArrayList<>();
            headers = QueryTree.generateAttributeList(schemaTree);

            MyFileReader.saveResource(resourceType, organization, resourceName + ".csv",
                    description, URL, schemaTree, attributes,
                    methodReturnFileType, urlReturnFileType,
                    "", headers,
                    resourceLocation + resourceName + ".xml", dataLocation, aggregateName);
        } else if (resourceType.toLowerCase().equals("xml")) {

            QueryTree schemaTree = gson.fromJson(schema, QueryTree.class);
            List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);

            urlReturnFileType = "XML";
//            methodReturnFileType = "XML";

            List<MyAttribute> headers = new ArrayList<>();
            headers = QueryTree.generateAttributeList(schemaTree);

            MyFileReader.saveResource(resourceType, organization, resourceName + ".xml",
                    description, URL, schemaTree, attributes,
                    methodReturnFileType, urlReturnFileType,
                    "", headers,
                    resourceLocation + resourceName + ".xml", dataLocation, aggregateName);
        } else if (resourceType.toLowerCase().equals("http") || resourceType.toLowerCase().equals("rest")) {
            QueryTree schemaTree = gson.fromJson(schema, QueryTree.class);
            List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);

            urlReturnFileType = "XML";
//            methodReturnFileType = "XML";

            List<MyAttribute> headers = new ArrayList<>();
            headers = QueryTree.generateAttributeList(schemaTree);
            headers.add(new MyAttribute("", "", ""));
            headers.add(new MyAttribute("", "", ""));

            MyFileReader.saveResourceWeb(resourceType, organization, resourceName + ".xml",
                    description,
                    URL,
                    URLExample,
                    schemaTree, attributes,
                    methodReturnFileType, urlReturnFileType,
                    wrapper,
                    headers,
                    separator,
                    textTableWrapperHeaders,
                    resultContainsHeaderInfo,
                    jsonToxmlWrapperAttributes,
                    myTableExactorHeaders,
                    resourceLocation + resourceName + ".xml", dataLocation, aggregateName, suggestOutputFileName,
                    method);
        } else if (resourceType.toLowerCase().equals("sqltable")) {
            MyStatus status = new MyStatus();
            try {
                MySQLHelper.runScript(dataLocation, sqltablescript, status);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            sqltablescript
            QueryTree schemaTree = gson.fromJson(schema, QueryTree.class);
            List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);
            urlReturnFileType = "SQL";
            methodReturnFileType = "SQL";
            List<MyAttribute> headers = new ArrayList<>();
            headers = QueryTree.generateAttributeList(schemaTree);
            resourceType = "SQL";

            MyFileReader.saveResource(resourceType, organization, resourceName,
                    description, URL, schemaTree, attributes,
                    methodReturnFileType, urlReturnFileType,
                    "", headers,
                    resourceLocation + resourceName + ".xml", dataLocation, aggregateName);
            //download to localfile
            try {
                MySQLHelper.downLoadaTableToLocal(resourceName, dataLocation, resourceName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resourceType.toLowerCase().equals("other")) {
            QueryTree schemaTree = gson.fromJson(schema, QueryTree.class);
            List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);

            urlReturnFileType = "OTHER";
            methodReturnFileType = "OTHER";

            List<MyAttribute> headers = new ArrayList<>();
            headers = QueryTree.generateAttributeList(schemaTree);

            MyFileReader.saveResource(resourceType, organization, resourceName,
                    description, URL, schemaTree, attributes,
                    methodReturnFileType, urlReturnFileType,
                    "", headers,
                    resourceLocation + resourceName + ".xml", dataLocation, aggregateName);
        }

        return Response.status(200)
                .entity("Good")
                .build();
    }

    @POST
    @Path("uploadfunction")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeFunction(
            @FormDataParam("functionFile") InputStream fileInputStream,
            @FormDataParam("functionFile") FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("functionName") String functionName,
            @FormDataParam("functionType") String functionType,
            @FormDataParam("organization") String organization,
            @FormDataParam("description") String description,
            @FormDataParam("aggregateName") String aggregateName,
            @FormDataParam("attributeList") String attributeList,
            @FormDataParam("username") String username
    ) {
        System.out.println(contentDispositionHeader.getFileName());
        System.out.println(functionName);
        System.out.println(functionType);
        System.out.println(description);
        System.out.println(attributeList);
        String dataLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";

        String functionLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\";

        if (username == null || username.equals("null")) {
            functionLocation += "Functions\\";
        } else {
            functionLocation += username + "\\Functions\\";
        }

        //aggregate names
        List<String> aggregate = MyFileReader.readCSVContent(aggFunName).stream().map(t -> t.get(0)).collect(Collectors.toList());
        if (!aggregate.contains(aggregateName)) {
            if (aggregate.isEmpty()) {
                MyFileReader.appendToTheEnd(aggFunName, "\"" + aggregateName + "\"");
            } else {
                MyFileReader.appendToTheEnd(aggFunName, "\n\"" + aggregateName + "\"");
            }
        }

        if (!contentDispositionHeader.getFileName().isEmpty()) {
            MyFileReader.inputStreamToFile(fileInputStream, dataLocation + contentDispositionHeader.getFileName());
        }
        List<RegisterAttribute> attributes = gson.fromJson(attributeList, listAttributeType);

        MyFileReader.saveFunction(functionName, functionType, organization, description, attributes,
                functionLocation + functionName + ".xml", dataLocation, aggregateName);

        return Response.status(200)
                .entity("Good")
                .build();

    }

    @POST
    @Path("uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeFile(
            @FormDataParam("file") FormDataBodyPart body
    ) {
//        System.out.println(contentDispositionHeader.getFileName());
        String dataLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";

        for (BodyPart part : body.getParent().getBodyParts()) {
            InputStream is = part.getEntityAs(InputStream.class);
            ContentDisposition meta = part.getContentDisposition();
            System.out.println(meta.getFileName());
            MyFileReader.inputStreamToFile(is, dataLocation + meta.getFileName());
        }

        return Response.status(200)
                .entity("Good")
                .build();

    }

}
