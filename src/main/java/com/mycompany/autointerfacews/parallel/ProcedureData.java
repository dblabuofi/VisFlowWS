/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.BufferedReader;
import java.io.File; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 *
 * @author mou1609
 */
public class ProcedureData implements Callable<String> {

    Node cur;
    Gson gson;
    HttpClient client;
    MyStatus status;

    public ProcedureData(Node cur, MyStatus status, Gson gson, HttpClient client) {
        this.cur = cur;
        this.gson = gson;
        this.status = status;
        this.client = client;
    }

    @Override
    public String call() throws CodeException, IOException {
        String res = "", line = "";
        try {
            System.out.println("procedure");
            status.addMessage("procedure");
            String location = cur.getResourcesIn().isEmpty() ? "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\" :cur.getResourcesIn().get(0).getLocation();
            Action action = cur.getActions().get(0);
            List<String> inputOlds = action.getInputReplace().stream().map(t -> t.getOldFile()).collect(Collectors.toList());
            List<String> inputNews = action.getInputReplace().stream().map(t -> t.getNewFile()).collect(Collectors.toList());
            List<String> outputOlds = action.getOutputReplace().stream().map(t -> t.getOldFile()).collect(Collectors.toList());
            List<String> outputNews = action.getOutputReplace().stream().map(t -> t.getNewFile()).collect(Collectors.toList());
            //copy to bak
            for (String f : inputOlds) {
                File file = new File(location + f);
                if (file.exists()) {
                    File newFile = new File(location + f + ".bak");
                    FileUtils.copyFile(file, newFile);
                }
            }
            for (String f : outputOlds) {
                File file = new File(location + f);
                if (file.exists()) {
                    File newFile = new File(location + f + ".bak");
                    FileUtils.copyFile(file, newFile);
                    System.out.println("delete" + f);
                    FileUtils.deleteQuietly(file);
                    System.out.println(file.exists());
                }
            }
            //we delete output news if we have before
            for (String f : outputNews) {
                File file = new File(location + f);
                file.deleteOnExit();
            }
            //copy to new file
            for (int i = 0; i < inputNews.size(); ++i) {
                String from = inputNews.get(i);
                String to = inputOlds.get(i);
                if (from.equals(to)) continue;
                File file = new File(location + from);
                File newFile = new File(location + to);
                FileUtils.copyFile(file, newFile);
            }

            String url = "/AutoInterfaceWS/parse/run/graph2";

            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            url = hostname.contains("CS-PREC3620HJ")
                    ? "http://localhost:8084/AutoInterfaceWS/parse/run/graph2" : "http://dblab2.nkn.uidaho.edu/AutoInterfaceWS/parse/run/graph2";

            System.out.println("url: " + url);
            status.addMessage("url:" + url);
            boolean flag = false;
            HttpPost post = new HttpPost(url);
            post.addHeader("content-type", "application/json");
            StringEntity params = new StringEntity(gson.toJson(action.getModule().getGraph()));
            post.setEntity(params);
            HttpResponse response = client.execute(post);
//            EntityUtils.consume(response.getEntity());
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                status.addMessage(line);
                res += line;
            }
            rd.close();

            boolean waitUntil = true;
            List<File> outOldFiles = new ArrayList<>();
            for (int i = 0; i < outputOlds.size(); ++i) {
                String from = outputOlds.get(i);
                File file = new File(location + from);
                outOldFiles.add(file);
            }
            while (waitUntil) {
                for (File f : outOldFiles) {
                    if (!f.exists()) {
                        waitUntil = true;
                        break;
                    } else {
                        waitUntil = false;
                    }
                }
                Thread.sleep(1000);
            }
            System.out.println("*****Wait until it finished!!*****");

            //copy from old file
            for (int i = 0; i < outputOlds.size(); ++i) {
                String from = outputOlds.get(i);
                String to = outputNews.get(i);
                if (from.equals(to)) continue;
                File file = new File(location + from);
                if (!file.exists()) {
                    file.createNewFile();
                }
                File newFile = new File(location + to);
                FileUtils.copyFile(file, newFile);
            }
            //copy from bak
            for (String f : inputOlds) {
                File file = new File(location + f + ".bak");
                if (file.exists()) {
                    File newFile = new File(location + f);
                    FileUtils.copyFile(file, newFile);
                    file.delete();
                }
            }
            for (String f : outputOlds) {
                File file = new File(location + f + ".bak");
                if (file.exists()) {
                    File newFile = new File(location + f);
                    FileUtils.copyFile(file, newFile);
                    file.delete();
                }
            }
            outputNews = outputNews.stream().map(t -> location + t).collect(Collectors.toList());
            cur.setOutputs(outputNews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
