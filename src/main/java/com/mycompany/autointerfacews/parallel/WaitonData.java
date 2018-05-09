/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.mycompany.autointerfacews.dataIcon.AttrMatch;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author mou1609
 */
public class WaitonData implements Callable<String> {

    Node node;
    MyStatus status;
    
    public WaitonData(Node node, MyStatus status) {
        this.node = node;
        this.status = status;
    }

    @Override
    public String call() {
         System.out.println("read Data");
        List<String> resources = new ArrayList<>();
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        //only support from database right now, need working for user files
        for (MyResource resource : node.getResourcesOut()) {
            String fileUrl = location + resource.getUrlReturnFileName();
            resources.add(fileUrl);
        }
        node.setOutputs(resources);
        return "";
    }
}
