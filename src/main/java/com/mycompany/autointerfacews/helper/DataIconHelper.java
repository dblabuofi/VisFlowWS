/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class DataIconHelper {
        /*
                read file and store it to a file and return file name
        */
        static public String readFile(Node node, MyResource storedResource, MyResource resource) {
                String fileName = "";
                if (resource.getResourceType().equals("csv")) {
                        //csv file the first row is attrs 
                        //generate file name
//                        int lastDot = storedResource.getFileName().lastIndexOf(".");
//                        int lastDot = node.getId().lastIndexOf("-");
//                        fileName = node.getId().substring(lastDot + 1) + storedResource.getId();
                        //xml not allow start numbers
                        fileName = "res" + node.getId() + storedResource.getId();
//                        System.out.println(fileName.length());
//                        System.out.println(storedResource.getLocation() + storedResource.getFileName());
//                        System.out.println(fileName);
                        //get selected indexes
                        List<Integer> selectedAttrsIndex = new ArrayList<>();
                        for (MyAttribute attr : resource.getAttributes()) {
//                                System.out.println(attr);
                                selectedAttrsIndex.add(storedResource.getAttributes().indexOf(attr));
                        }
                        selectedAttrsIndex.stream().forEach(t->System.out.println(t));
                        int rs = MyFileReader.convertCSVtoXML(resource.getLocation() + storedResource.getFileName(), 
                                storedResource.getLocation() + fileName + ".xml", 
                                fileName, 
                                ",", 
                                selectedAttrsIndex);
                        System.out.println(rs);

                        fileName += ".xml";
                        return fileName;
                        
                }
                
                return fileName;
        }
}
