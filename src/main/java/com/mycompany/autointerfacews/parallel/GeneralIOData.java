/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.gson.Gson;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;

/**
 *
 * @author mou1609
 */
public class GeneralIOData implements Callable<String> {
    Node cur;
    MyStatus status;
    
    public GeneralIOData(Node cur, MyStatus status) {
        this.cur = cur;
        this.status = status;
    }
    
    @Override
    public String call() throws CodeException, IOException {
         try {
            String location = cur.getResourcesIn().get(0).getLocation();
            List<String> outputs = cur.getResourcesOut().stream().map(t -> location + t.getUrlReturnFileName()).collect(Collectors.toList());
            List<File> outFiles = new ArrayList<>();
            for (String f : outputs) {
                File file = new File(f);
                outFiles.add(file);
                if (file.exists()) {
                    FileUtils.deleteQuietly(file);
                }
            }
            status.addMessage("General IO need add Message");

            boolean waitOn = true;
            while (waitOn) {
                for (File file : outFiles) {
                    if (!file.exists()) {
                        waitOn = true;
                        break;
                    } else {
                        waitOn = false;
                    }
                }
                Thread.sleep(1000);
            }
            cur.setOutputs(outputs);
        } catch (Exception e) {
            e.printStackTrace();
        }
         return "";
    }
    
}
