/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.mycompany.autointerfacews.mydata.MatchAttributeInput;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MySchema;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.autointerfacews.smtch.SMatch;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author mou1609
 */
@Path("queryTree")
public class QueryTreeResources {

    Gson gson;
    SMatch smatch;

    @Inject
    public QueryTreeResources(Gson gson, SMatch smatch) {
        this.gson = gson;
        this.smatch = smatch;
    }

//        @GET
//        @Path("schema")
//        public Response getResouceSchema(
//                @QueryParam("location") String location,
//                @QueryParam("fileName") String fileName
//        ) {
//                QueryTree tree = QueryTree.generateSchemaFromFile(location, fileName);
//                
//                String res = gson.toJson(tree);
//        
//                return Response.status(200).entity(res).build();
//        }        
    @GET
    @Path("schema")
    public Response getResouceSchema(
            @QueryParam("location") String location,
            @QueryParam("fileName") String fileName,
            @QueryParam("index") String index
    ) {
        QueryTree tree = QueryTree.generateSchemaFromFile(location, fileName);
        String res = gson.toJson(tree);
        if (index != null) {
            res += "#" + index;
        }
        System.out.println(res);
        return Response.status(200).entity(res).build();
    }

    @POST
    @Path("matchAttribute")
    public Response getMatchAttribute(
            String inputString
    ) {

        System.out.println(inputString);

        MatchAttributeInput input = gson.fromJson(inputString, MatchAttributeInput.class);
        QueryTree schema = input.getSchema();
        MyAttribute attribute = input.getAttribute();

//                System.out.println(attribute.getLabel());
        String matchedAttribute = smatch.getMatchAttribute(schema, attribute);
        System.out.println("*******************");
        System.out.println(matchedAttribute);

        return Response.status(200).entity(matchedAttribute).build();
    }

}
