/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.mycompany.autointerfacews.bioflow.BioFlowIFStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyStatus;
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
public class IfData implements Callable<String> {

    Node cur;
    MyStatus status;
    BioFlowService bioFlowService;
    EXist eXist;

    public IfData(Node cur, MyStatus status, BioFlowService bioFlowService, EXist eXist) {
        this.cur = cur;
        this.status = status;
        this.bioFlowService = bioFlowService;
        this.eXist = eXist;
    }

    @Override
    public String call() throws CodeException, IOException {
        try {
            
            System.out.println("if data");
            List<String> outputs = new ArrayList<>();

            String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";

            if (cur.getActions() != null && cur.getActions().size() > 0) {
                for (Action action : cur.getActions()) {
                    System.out.println("generate inputs");
                    String fileName = action.getConditions().get(0).getResource();
                    String type = MyUtils.getFileType(fileName);

                    List<List<String>> inputs = new ArrayList<>();
                    if (type.equals("csv")) {
                        inputs = MyFileReader.readCSV(location + fileName);
                    } else if (type.equals("xml")) {
                        inputs = MyFileReader.readXML(location + fileName);
                    } else if (type.equals("sql")) {
                        MySQLHelper.downLoadaTableToLocal(fileName, location, fileName);
                        inputs = MyFileReader.readCSV(location + fileName);
                    }

                    //generate bioflow statement
                    BioFlowIFStatement ifScript = bioFlowService.generateBioFlowIFScript(cur, action);
                    System.out.println(ifScript);
                    System.out.println("run bioflow");
                    Boolean result = bioFlowService.executeBioFlowIFStatement(ifScript, inputs);
                    System.out.println("**********if output multi************");
                    System.out.println(result);
                    status.addMessage("IF condition result:" + result);
                    List<String> deleteNodes = new ArrayList<>();
                    //remove nodes
                    if (result) {
                        System.out.println("1");
                        System.out.println(cur.getActions());
                        System.out.println(cur.getActions().get(0));
                        System.out.println(cur.getActions().get(0).getFalseBranch());
                        deleteNodes.add(cur.getActions().get(0).getFalseBranch().getId());
                        System.out.println("2");
                    } else {
                        System.out.println("3");
                        deleteNodes.add(cur.getActions().get(0).getTrueBranch().getId());
                        System.out.println("4");
                    }
                    deleteNodes.forEach(t -> System.out.println(t));
                    ParseResources.parallelExecution.removeTasks(deleteNodes);
                    System.out.println("********remove nodes*************");
                    deleteNodes.forEach(t -> System.out.println(t));

                }
            }

            for (MyResource res : cur.getResourcesOut()) {
                outputs.add(location + res.getUrlReturnFileName());
            }
            cur.setOutputs(outputs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
