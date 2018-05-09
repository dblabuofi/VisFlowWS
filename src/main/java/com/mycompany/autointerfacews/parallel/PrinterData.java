/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.gson.Gson;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mymessage.ReturnMessage;
import com.mycompany.autointerfacews.resources.ParseResources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author jupiter
 */
public class PrinterData implements Callable<String> {
    Node cur;
    Gson gson;

    public PrinterData(Node cur, Gson gson) {
        this.cur = cur;
        this.gson = gson;
    }
    
    @Override
    public String call() throws CodeException, IOException {
        System.out.println("printerData");
        List<String> outputs = new ArrayList<>();
        List<ReturnMessage> result = new ArrayList<>();

        //get print type
        for (MyResource resourcesIn : cur.getResourcesIn()) {
            result.add(new ReturnMessage("Printer", resourcesIn.getResourceType(), resourcesIn.getUrlReturnFileName(), cur.getId()));
        }
        cur.setOutputs(outputs);
        String res = gson.toJson(result);
        
        ParseResources.resultList.addAll(result);
        
        return res;
    }
}
