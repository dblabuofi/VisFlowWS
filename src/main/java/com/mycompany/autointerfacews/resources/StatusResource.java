/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author jupiter
 */
@Path("status")
public class StatusResource {

    static Queue<MyStatus> status = new ConcurrentLinkedQueue<>();
    Type listlistType = new TypeToken<Queue<MyStatus>>(){}.getType();
    Gson gson;

    @Inject
    public StatusResource(Gson gson) {
        this.gson = gson;
    }

    @GET
    public Response status() {

        String res = gson.toJson(status, listlistType);

        return Response.status(200)
                .entity(res)
                .build();
    }

}
