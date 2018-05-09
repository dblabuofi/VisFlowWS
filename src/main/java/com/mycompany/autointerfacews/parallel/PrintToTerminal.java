/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.gson.Gson;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.mymessage.ReturnMessage;
import com.mycompany.autointerfacews.resources.ParseResources;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author jupiter
 */
public class PrintToTerminal implements Callable<String> {

    Node cur;
    MyStatus status;
    Gson gson;
    WebResourceImageDownloader webResourceImageDownloader;

    public PrintToTerminal(Node cur, MyStatus status, Gson gson, WebResourceImageDownloader webResourceImageDownloader) {
        this.cur = cur;
        this.status = status;
        this.gson = gson;
        this.webResourceImageDownloader = webResourceImageDownloader;
    }

    @Override
    public String call() throws CodeException, IOException {
        System.out.println("printToTerminal");
        List<String> outputs = new ArrayList<>();
        List<ReturnMessage> result = new ArrayList<>();
        String location = cur.getResourcesIn().get(0).getLocation();
        //get print type
        for (Action action : cur.getActions()) {
            if (action.getPrintType().equals("graph")) {
                List<List<String>> content = MyUtils.getFileContents(cur.getResourcesIn().get(0).getLocation() + action.getOutputFileNames().get(0));
                List<String> imageFiles = new ArrayList<>();
                //rules        
                if (content.get(0).get(0).contains("hsa")) {
                    imageFiles = webResourceImageDownloader.downloadKEGGPathwayImage(content, location, status);
                }
                ReturnMessage m = new ReturnMessage("Terminal", action.getPrintType(), action.getResourceName(), cur.getId());
                List<List<String>> returnTable = new ArrayList<>();
                returnTable.add(imageFiles);
                returnTable = MyUtils.transpose(returnTable);
                m.setTableContent(returnTable);
                result.add(m);
            } else if (action.getPrintType().equals("file")) {
                result.add(new ReturnMessage("Terminal", action.getPrintType(), action.getOutputFileNames().get(0), cur.getId()));
            } else if (action.getPrintType().equals("table")) {
                if (location == null) {
                    location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                }
                List<List<String>> content = MyUtils.getFile(location + action.getOutputFileNames().get(0));
                ReturnMessage m = new ReturnMessage("Terminal", action.getPrintType(), action.getOutputFileNames().get(0), content, cur.getId(), action.getSubmit(), action.getNumOfWins(), location, action.getColFuns());
                result.add(m);
            } else if (action.getPrintType().equals("barchart")) {

            } else if (action.getPrintType().equals("piechart")) {

            } else if (action.getPrintType().equals("linechart")) {

            }
        }
        cur.setOutputs(outputs);
        String res = gson.toJson(result);
        System.out.println("******Terminal result*******");
        System.out.println(result);
        ParseResources.result = res;

        ParseResources.resultList.addAll(result);

        return res;
    }

}
