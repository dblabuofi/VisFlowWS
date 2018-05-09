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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jupiter
 */
public class ReadData implements Callable<String> {

    Node node;
    List<AttrMatch> globalmatch;

    public ReadData(Node node, List<AttrMatch> globalmatch) {
        this.node = node;
        this.globalmatch = globalmatch;
    }

    @Override
    public String call() {
        try {
            System.out.println("read Data");
            System.out.println(node);
            List<String> resources = new ArrayList<>();
            String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
            //only support from database right now, need working for user files
            for (MyResource resource : node.getResources()) {
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                resources.add(fileUrl);
            }
            node.setInputs(resources);
            node.setOutputs(resources);
            
            if (globalmatch == null) return "";
            //we store the data to bak after it's done, we copy it back
            for (MyResource resource : node.getResources()) {
                String f = resource.getUrlReturnFileName();
                int i = 0;
                for (; i < globalmatch.size(); ++i) {
                    if (globalmatch.get(i).getResourceName().equals(f)) {
                        break;
                    }
                }
                if (i != globalmatch.size()) {
                    File file = new File(location + f);
                    if (file.exists()) {
                        System.out.println("copyed file" + f + ".bak");
                        File newFile = new File(location + f + ".bak");
                        FileUtils.copyFile(file, newFile);
                        //we replace the header
                        List<List<String>> contents = MyFileReader.readCSV(file.getAbsolutePath());
                        List<String> header = contents.get(0);
                        for (int j = 0; j < header.size(); ++j) {
                            List<MapAttr> matched = globalmatch.get(i).getMapped();
                            for (int k = 0; k < matched.size(); ++k) {
                                if (matched.get(k).getOldAttr().equals(header.get(j))) {
                                    header.set(j, matched.get(k).getNewAttr());
                                }
                            }
                        }
                        contents.set(0, header);
                        MyFileReader.writeFile(file.getAbsolutePath(), contents, ",");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
