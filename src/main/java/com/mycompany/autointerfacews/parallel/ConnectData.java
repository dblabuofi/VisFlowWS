/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.mycompany.autointerfacews.dataIcon.AttrMatch;
import com.mycompany.autointerfacews.dataIcon.MapAttr;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author mou1609
 */
public class ConnectData implements Callable<String> {

    Node node;

    MyStatus status;

    public ConnectData(Node cur, MyStatus status) {
        this.node = cur;
        this.status = status;
    }

    @Override
    public String call() {
        try {
            System.out.println("read Data");
            System.out.println(node);
            List<String> resources = new ArrayList<>();
            String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
            //only support from database right now, need working for user files
            for (MyResource resource : node.getResourcesIn()) {
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                resources.add(fileUrl);
            }
            node.setOutputs(resources);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
