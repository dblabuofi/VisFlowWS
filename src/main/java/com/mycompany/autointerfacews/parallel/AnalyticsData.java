/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.common.io.Files;
import com.mycompany.autointerfacews.bioflow.BioFLowFunctionStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowCodeStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowExtractStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.dataIcon.SelectAttr;
import com.mycompany.autointerfacews.dataIcon.UpdateAttr;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;

/**
 *
 * @author jupiter
 */
public class AnalyticsData implements Callable<String> {

    Node cur;
    MyStatus status;
    BioFlowService bioFlowService;
    EXist eXist;
    InputGenerator inputGenerator;

    public AnalyticsData(Node cur, MyStatus status, BioFlowService bioFlowService, EXist eXist, InputGenerator inputGenerator) {
        this.cur = cur;
        this.status = status;
        this.bioFlowService = bioFlowService;
        this.eXist = eXist;
        this.inputGenerator = inputGenerator;
    }
    
    @Override
    public String call() throws CodeException {
        System.out.println("analytics");
        status.addMessage("Analytics");
        List<String> outputs = new ArrayList<>();
        if (cur.getActions() != null && cur.getActions().size() > 0) {
            for (Action action : cur.getActions()) {
                if (action.getAct().equals("Resource")) {//need access remote web resources
//                    if (cur.getId().equals("978100ed-c923-4706-8b33-a1e9ff4f9242")
//                            || cur.getId().equals("7d52b97b-6f30-4234-8a82-93e7f2408d38")
//                            || cur.getId().equals("7d52b97b-6f30-4234-8a82-93e7f2408d38")
//                            || cur.getId().equals("f5ed9b04-5e34-4176-8a2d-18ccb1b136d1")
//                            || cur.getId().equals("2da6797c-2f59-438b-aebf-54163566bcfd")
//                            || cur.getId().equals("e39dd2f5-98af-4fd3-8806-b7f64f876c24")
//                            //                          ||  cur.getId().equals("5573cf7f-3ed9-4539-8d02-d48b010df954") 
//                            //                            || cur.getId().equals("3504ace7-c9e7-40c1-82f7-3c763c6b30b0")
//                            || cur.getId().equals("c1b9a719-ca3a-457c-9e5a-bfa34c770a0d")
//                            || cur.getId().equals("35954ad3-4271-4641-80b7-19cc58975a4f")) {
//                        System.out.println("testing only!!");
//                        if (cur.getId().equals("7d52b97b-6f30-4234-8a82-93e7f2408d38")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gsspLabel.xml");
//                        }
//                        if (cur.getId().equals("5573cf7f-3ed9-4539-8d02-d48b010df954")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gsspCoord.xml");
//                        }
//                        if (cur.getId().equals("3504ace7-c9e7-40c1-82f7-3c763c6b30b0")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\oldCoordinats.csv");
//                        }
//                        if (cur.getId().equals("35954ad3-4271-4641-80b7-19cc58975a4f")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\fossilRecords.csv");
//                        }
//                        if (cur.getId().equals("c1b9a719-ca3a-457c-9e5a-bfa34c770a0d")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gsspCoord.csv");
//                        }
//                        if (cur.getId().equals("978100ed-c923-4706-8b33-a1e9ff4f9242")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\orthologygen.csv");
//                        }
//                        if (cur.getId().equals("f5ed9b04-5e34-4176-8a2d-18ccb1b136d1")
//                                || cur.getId().equals("e39dd2f5-98af-4fd3-8806-b7f64f876c24")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\orthologygen.csv");
//                        }
//                        if (cur.getId().equals("2da6797c-2f59-438b-aebf-54163566bcfd")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\orthology.csv");
//                        }
//                        cur.setOutputs(outputs);
//                        break;
//                    }
                    status.addMessage("Access Resource");
                    //generate input file
                    status.addMessage("generate inputs");
                    System.out.println("generate inputs");
                    String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";

                    for (MyResource resource : cur.getResourcesIn()) {
                        if (resource.getResourceType().toLowerCase().equals("sql")) {
                            String rootName = resource.getUrlReturnFileName();
                            MySQLHelper.downLoadaTableToLocal(rootName, location, rootName + ".csv");
                            MyFileReader.convertCSVtoXML(location + rootName + ".csv", location + rootName + ".xml");
                        }
                    }

                    List<List<String>> scriptInputs = inputGenerator.generateInput(cur, action, status, location);
                    //generate Bioflow statement
                    status.addMessage("generate bioflow");
                    System.out.println("generate bioflow");
                    BioFlowExtractStatement extractScript = bioFlowService.generateBioFlowExtractScript(cur, action);
                    System.out.println(extractScript);
                    //run Bioflow statement
                    status.addMessage("run bioflow");
                    System.out.println("run bioflow");
                    //dirty for getMethodReturnFileSchema
                    String outputFileURL;
//                    if (action.getTargetResource().getWrapper() == null || action.getTargetResource().getWrapper().getAttrs() == null) {
                    if (action.getTargetResource().getWrapper() == null) {
                        outputFileURL = bioFlowService.executeBioFlowExtractStatement(extractScript, scriptInputs,
                                action.getTargetResource().getLocation(),
                                action.getOutputFileNames().get(0),
                                status,
                                action.getTargetResource().getMethodReturnFileSchema(),
                                new ArrayList<>(), null);
                    } else {
                        outputFileURL = bioFlowService.executeBioFlowExtractStatement(extractScript, scriptInputs,
                                action.getTargetResource().getLocation(),
                                action.getOutputFileNames().get(0),
                                status,
                                action.getTargetResource().getMethodReturnFileSchema(),
                                action.getTargetResource().getWrapper().getAttrs(), action.getTargetResource().getWrapper());
                    }
                    //write to file
                    outputs.add(outputFileURL);
                    //update data output
                    cur.setOutputs(outputs);
                } else if (action.getAct().equals("Code")) {
                    status.addMessage("Run Code");
                    String location = cur.getResourcesIn().get(0).getLocation();
                    String codeName = action.getCodeName();

                    status.addMessage("generate inputs");
                    if (action.getCodeType().equals("bash")) {
                        MyFileReader.generateFile(location, codeName, action.getVal().replaceAll("\r\n", "\n"));
                    } else if (action.getCodeType().equals("python")) {
                        MyFileReader.generateFile(location, action.getCodeName(), action.getVal().replaceAll("\r\n", "\n"));
                    } else if (action.getCodeType().equals("r")) {
                        MyFileReader.generateFile(location, action.getCodeName(), action.getVal().replaceAll("\r\n", "\n"));
                    } else if (action.getCodeType().equals("xquery")) {//xquery to xml file
                        //put file to eXist database
                        for (String fileName : action.getInputFileNames()) {
                            eXist.uploadFileToExist(location, fileName);
                        }
                    } else if (action.getCodeType().equals("sql")) {
                        //put file to eXist database
                        for (String fileName : action.getInputFileNames()) {
                            MySQLHelper.uploadFileToSQL(location, fileName, MyUtils.getFileName(fileName), status);
                        }
                    }

                    status.addMessage("generate bioflow code");
                    //generate bioflow statement
                    BioFlowCodeStatement codeScript = bioFlowService.generateBioFlowCodeScript(cur, action);
                    System.out.println(codeScript);
                    status.addMessage("run bioflow");
                    System.out.println("run bioflow");
                    bioFlowService.executeBioFlowCodeStatement(codeScript, status);
                    //after that we deal with it 
                    if (action.getCodeType().equals("sql")) {
                        //copy files back
                        for (int i = 0; i < action.getOutputFileTypes().size(); ++i) {
                            String type = action.getOutputFileTypes().get(i).toLowerCase();
                            String returnFileName = action.getOutputFileNames().get(i);
                            String rootName = MyUtils.getFileName(returnFileName);
                            System.out.println("**SDFSD");
                            System.out.println(rootName);
                            if (type.equals("csv")) {
                                MySQLHelper.downLoadaTableToLocal(rootName, location, returnFileName);
                            } else if (type.equals("xml")) {

                            }
                        }
                    }

                    //output files
                    for (String name : action.getOutputFileNames()) {
                        outputs.add(location + name);
                    }

                } else if (action.getAct().equals("Function")) {
                    status.addMessage("Run Function");
                    System.out.println("generate inputs");
                    String location = null;
                    if (cur.getResourcesIn().isEmpty()) {
                        location = cur.getLibrariesIn().get(0).getLocation();
                    } else {
                        location = cur.getResourcesIn().get(0).getLocation();
                    }
                    List<List<String>> scriptInputs = inputGenerator.generateInput(cur, action, status, location);

                    status.addMessage("Run Local Function");
                    //generate bioflow statement
                    BioFLowFunctionStatement functionScript = bioFlowService.generateBioFlowFunctionScript(cur, action);
                    status.addMessage("Run BioFlow");
                    System.out.println(functionScript);
                    System.out.println("run bioflow");
                    bioFlowService.executeBioFlowFunctionStatement(functionScript, scriptInputs, location, action.getOutputFileNames(), status);

                    //output files
                    for (String name : action.getOutputFileNames()) {
                        outputs.add(location + name);
                    }

                } else if (action.getAct().equals("ExtractFunction")) {
                    String location = cur.getResourcesIn().get(0).getLocation();
                    String fileName = action.getTargetResource().getUrlReturnFileName();
                    String outputFileName = action.getOutputFileNames().get(0);
                    List<SelectAttr> attrs = action.getSelectAttrs();
                    List<UpdateAttr> newAttrs = action.getNewAttrs();
                    if (MyUtils.getFileType(fileName).toLowerCase().equals("csv")) {
                        List<String> headers = MyFileReader.readCSVHead(location + fileName);
                        List<List<String>> contents = MyFileReader.readCSVContent(location + fileName);
                        Map<SelectAttr, Integer> map = new HashMap<>();
                        Map<String, Integer> headToInt = new HashMap<>();
                        for (int i = 0; i < headers.size(); ++i) {
                            for (SelectAttr attr : attrs) {
                                if (headers.get(i).equals(attr.getAttribute())) {
                                    map.put(attr, i);
                                }
                            }
                            headToInt.put(headers.get(i), i);
                        }
                        List<List<String>> res = new ArrayList<>();
                        for (int i = 0; i <= contents.size(); ++i) {
                            res.add(new ArrayList<>());
                        }
                        //add all to row
                        for (Map.Entry<SelectAttr, Integer> entry : map.entrySet()) {
                            res.get(0).add(entry.getKey().getName());
                            for (int i = 0; i < contents.size(); ++i) {
                                res.get(i + 1).add(contents.get(i).get(entry.getValue()));
                            }
                        }
                        System.out.println(res);
                        //we assume haha 
                        int cnt = 0;
                        for (List<String> val : res) {
                            cnt = Math.max(cnt, val.size());
                        }
                        Iterator<List<String>> it = res.iterator();
                        while (it.hasNext()) {
                            List<String> now = it.next();
                            if (now.size() != cnt) {
                                it.remove();
                            }
                        }
                        if (action.getAfterAction().getAction().equals("removeDuplicate")) {
                            Set<List<String>> set = new LinkedHashSet<>();
                            set.addAll(res);
                            res = new ArrayList<>(set);
                        } else if (action.getAfterAction().getAction().equals("filter")) {
                            String[] cmds = action.getAfterAction().getInput().split("#");
                            int index = 0;
                            for (int i = 0; i < res.get(0).size(); ++i) {
                                if (res.get(0).get(i).equals(cmds[0])) {
                                    index = i;
                                    break;
                                }
                            }
                            Iterator<List<String>> iterator = res.iterator();
                            if (cmds.length == 1) {
                                cmds = new String[2];
                                cmds[1] = "";
                            }
                            while (iterator.hasNext()) {
                                List<String> now = iterator.next();
                                if (now.get(index).matches(cmds[1])) {
                                    iterator.remove();
                                }
                            }
                        }

                        MyFileReader.writeFile(location + outputFileName, res, ",");
                    } else { //xml
                        List<List<String>> res = new ArrayList<>();
                        File fXmlFile = new File(location + fileName);
                        SAXReader reader = new SAXReader();
                        org.dom4j.Document document = null;
                        try {
                            document = reader.read(location + fileName);
                        } catch (Exception e) {
                            try {
                                String fXml = MyFileReader.readFileAll(location + fileName);
//                                    if (!fXml.startsWith("<?x")) {
//                                        fXml = "<?xml version='1.0' encoding='utf-8'?>" + fXml;
//                                    }
                                fXml = "<Result>" + fXml + "</Result>";
                                fXml = fXml.trim().replaceFirst("^([\\W]+)<", "<");
                                MyFileReader.writeFileUTF8(location + fileName, fXml);
//                                    MyFileReader.writeFile(location + fileName, fXml);
                                document = reader.read(location + fileName);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        for (SelectAttr attr : attrs) {
                            String at = attr.getAttribute();
                            String xpath = attr.getXpath();
                            System.out.println(xpath);
                            List<org.dom4j.Node> elements = document.selectNodes(xpath);
                            List<String> row = null;
                            row = elements.stream().map(t -> t.getStringValue()).collect(Collectors.toList());
                            row.add(0, attr.getName());
                            res.add(row);
                        }
                        if (newAttrs != null) {
                            for (UpdateAttr attr : newAttrs) {
                                String at = attr.getAttribute();
                                String xpath = attr.getXpath();
                                List<org.dom4j.Node> elements = document.selectNodes(xpath);
                                System.out.println(elements.size());
                                List<String> row = null;
                                row = elements.stream().map(t -> t.getStringValue()).collect(Collectors.toList());
                                row.add(0, at);
                                res.add(row);
                            }
                        }
                        int min = 0;
                        if (!res.isEmpty()) {
                            try {
                                min = res.stream().mapToInt(t -> t.size()).min().getAsInt();
                            } catch (Exception e) {

                            }
                        }
                        List<List<String>> bak = new ArrayList<>();
                        for (List<String> row : res) {
                            if (row.size() > min) {
                                bak.add(row.subList(0, min));
                            } else {
                                bak.add(row);
                            }
                        }
                        if (!bak.isEmpty()) {
                            res = MyUtils.transpose(bak);
                        }

                        if (action.getAfterAction().getAction().equals("removeDuplicate")) {
                            Set<List<String>> set = new LinkedHashSet<>();
                            set.addAll(res);
                            res = new ArrayList<>(set);
                        } else if (action.getAfterAction().getAction().equals("filter")) {
                            String[] cmds = action.getAfterAction().getInput().split("#");
                            int index = 0;
                            for (int i = 0; i < res.get(0).size(); ++i) {
                                if (res.get(0).get(i).equals(cmds[0])) {
                                    index = i;
                                    break;
                                }
                            }
                            Iterator<List<String>> iterator = res.iterator();
                            while (iterator.hasNext()) {
                                List<String> now = iterator.next();
                                if (!now.get(index).matches(cmds[1])) {
                                    iterator.remove();
                                }
                            }
                        } else if (action.getAfterAction().getAction().equals("rowReplace")) {
                            String[] cmds = action.getAfterAction().getInput().split("#");
                            List<String> rows = MyFileReader.rowReader(location + fileName);
                            System.out.println(action.getAfterAction().getInput());
                            System.out.println(cmds[0]);
                            for (int i = 0; i < rows.size(); ++i) {
                                String row = rows.get(i);
                                System.out.println(row);
                                if (row.matches(cmds[0])) {
                                    rows.set(i, cmds[1]);
                                }
                            }
                            System.out.println(rows);
                            MyFileReader.rowwriter(location + outputFileName, rows);
                        }
                        if (!action.getAfterAction().getAction().equals("rowReplace")) {
                            MyFileReader.writeFile(location + outputFileName, res, ",");
                        }
                    }
                    //output files
                    for (String name : action.getOutputFileNames()) {
                        outputs.add(location + name);
                    }
                } else if (action.getAct().equals("MergeTable")) {
                    if (action.getMergeTableSelect().equals("selectandappendbyrow")) {
                        String[] cmd = action.getMergeTableSelectInput().split("#");
                        String location = cur.getResourcesIn().get(0).getLocation();
                        String fileName = action.getOutputFileNames().get(0);
                        String outputFileName = action.getOutputFileNames().get(0);
                        List<SelectAttr> attrs = action.getSelectAttrs();
                        List<String> head1 = new ArrayList<>(), head2 = new ArrayList<>();
                        List<List<String>> content1 = new ArrayList<>(), content2 = new ArrayList<>();
                        if (cmd[0].toLowerCase().endsWith("csv")) {
                            head1 = MyFileReader.readCSVHead(location + cmd[0]);
                            List<List<String>> content11 = MyFileReader.readCSVContent(location + cmd[0]);
                            for (SelectAttr attr : attrs) {
                                if (attr.getResourceName().equals(cmd[0])) {
                                    int index = head1.indexOf(attr.getAttribute());
                                    if (content1.size() == 0) {
                                        for (int i = 0; i <= content11.size(); ++i) {
                                            content1.add(new ArrayList<>());
                                        }
                                    }
                                    content1.get(0).add(attr.getName());
                                    for (int i = 0; i < content11.size(); ++i) {
                                        content1.get(i + 1).add(content11.get(i).get(index));
                                    }
                                }
                                System.out.println(content1.size());
                            }
                        } else if (cmd[0].toLowerCase().endsWith("xml")) {
                            SAXReader reader = new SAXReader();
                            org.dom4j.Document document = null;
                            try {
                                document = reader.read(location + cmd[0]);
                            } catch (Exception e) {
                                try {
                                    String fXml = MyFileReader.readFileAll(location + cmd[0]);
                                    fXml = "<Result>" + fXml + "</Result>";
                                    fXml = fXml.trim().replaceFirst("^([\\W]+)<", "<");
                                    MyFileReader.writeFileUTF8(location + cmd[0], fXml);
                                    document = reader.read(location + cmd[0]);
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            }
                            for (SelectAttr attr : attrs) {
                                if (attr.getResourceName().equals(cmd[0])) {
                                    String at = attr.getAttribute();
                                    String xpath = attr.getXpath();
                                    System.out.println(xpath);
                                    List<org.dom4j.Node> elements = document.selectNodes(xpath);
                                    List<String> row = elements.stream().map(t -> t.getStringValue()).collect(Collectors.toList());
                                    row.add(0, attr.getName());
                                    if (content1.size() == 0) {
                                        for (int i = 0; i < row.size(); ++i) {
                                            content1.add(new ArrayList<>());
                                        }
                                    }
                                    for (int i = 0; i < row.size(); ++i) {
                                        content1.get(i).add(row.get(i));
                                    }
                                }
                            }
                        }

                        if (cmd[1].toLowerCase().endsWith("csv")) {
                            head2 = MyFileReader.readCSVHead(location + cmd[1]);
                            List<List<String>> content11 = MyFileReader.readCSVContent(location + cmd[1]);
                            for (SelectAttr attr : attrs) {
                                int index = head2.indexOf(attr.getAttribute());
                                if (attr.getResourceName().equals(cmd[1])) {
                                    if (content2.size() == 0) {
                                        for (int i = 0; i <= content11.size(); ++i) {
                                            content2.add(new ArrayList<>());
                                        }
                                    }
                                    content2.get(0).add(attr.getName());
                                    for (int i = 0; i < content11.size(); ++i) {
                                        content2.get(i + 1).add(content11.get(i).get(index));
                                    }
                                }
                            }
                        } else if (cmd[1].toLowerCase().endsWith("xml")) {
                            SAXReader reader = new SAXReader();
                            org.dom4j.Document document = null;
                            System.out.println(cmd[1]);
                            try {
                                document = reader.read(location + cmd[1]);
                            } catch (Exception e) {
                                try {
                                    String fXml = MyFileReader.readFileAll(location + cmd[1]);
                                    fXml = "<Result>" + fXml + "</Result>";
                                    fXml = fXml.trim().replaceFirst("^([\\W]+)<", "<");
                                    MyFileReader.writeFileUTF8(location + cmd[1], fXml);
                                    document = reader.read(location + cmd[1]);
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            }
                            System.out.println(cmd[1]);
                            for (SelectAttr attr : attrs) {
                                System.out.println(attr.getResourceName());
                                if (attr.getResourceName().equals(cmd[1])) {
                                    String at = attr.getAttribute();
                                    String xpath = attr.getXpath();
                                    System.out.println(xpath);
                                    List<org.dom4j.Node> elements = document.selectNodes(xpath);
                                    List<String> row = elements.stream().map(t -> t.getStringValue()).collect(Collectors.toList());
                                    System.out.println(row.size());
                                    System.out.println("##@%$");
                                    row.add(0, attr.getName());
                                    if (content2.size() == 0) {
                                        for (int i = 0; i < row.size(); ++i) {
                                            content2.add(new ArrayList<>());
                                        }
                                    }
                                    for (int i = 0; i < row.size(); ++i) {
                                        content2.get(i).add(row.get(i));
                                    }
                                }
                            }
                        }
                        System.out.println(content1.size());
//                        System.out.println(content1);
                        System.out.println(content2.size());
//                        System.out.println(content2);

                        for (int i = 0; i < content1.size(); ++i) {
                            content1.get(i).addAll(content2.get(i));
                        }
                        MyFileReader.writeFile(location + outputFileName, content1, ",");
                        //output files
                        for (String name : action.getOutputFileNames()) {
                            outputs.add(location + name);
                        }
                    } else if (action.getMergeTableSelect().equals("appendwithmapkey")) {
                        System.out.println(action.getMergeTableSelectInput());
                        String[] cmds = action.getMergeTableSelectInput().split("#");
                        String[] cmd1 = cmds[0].split(",");
                        String[] cmd2 = cmds[1].split(",");
                        String[] cmd3 = cmds[2].split(",");
                        int[] indexs1 = new int[cmd1.length - 1];
                        int[] indexs2 = new int[cmd2.length - 1];
                        int[] indexs3 = new int[cmd3.length];

                        String location = cur.getResourcesIn().get(0).getLocation();
                        String outputFileName = action.getOutputFileNames().get(0);

                        List<String> head1 = MyFileReader.readCSVHead(location + cmd1[0]);
                        List<List<String>> content1 = MyFileReader.readCSVContent(location + cmd1[0]);
                        List<String> head2 = MyFileReader.readCSVHead(location + cmd2[0]);
                        List<List<String>> content2 = MyFileReader.readCSVContent(location + cmd2[0]);
                        Map<Integer, List<String>> map1 = new HashMap<>();
                        Map<List<String>, Integer> map2 = new HashMap<>();
                        for (int i = 0; i < indexs1.length; ++i) {
                            indexs1[i] = head1.indexOf(cmd1[i + 1]);
                        }
                        for (int i = 0; i < indexs2.length; ++i) {
                            indexs2[i] = head2.indexOf(cmd2[i + 1]);
                        }
                        for (int i = 0; i < indexs3.length; ++i) {
                            indexs3[i] = head2.indexOf(cmd3[i]);
                        }

                        for (int i = 0; i < content1.size(); ++i) {
                            if (content1.get(i).size() != head1.size()) {
                                continue;
                            }
                            List<String> key = new ArrayList<>();
                            for (int j = 0; j < indexs1.length; ++j) {
                                if (!content1.get(i).get(indexs1[j]).isEmpty()) {
                                    key.add(content1.get(i).get(indexs1[j]));
                                }
                            }
                            map1.put(i, key);
                        }
                        for (int i = 0; i < content2.size(); ++i) {
                            if (content2.get(i).size() != head2.size()) {
                                continue;
                            }
                            List<String> key = new ArrayList<>();
                            for (int j = 0; j < indexs2.length; ++j) {
                                if (!content2.get(i).get(indexs2[j]).isEmpty()) {
                                    key.add(content2.get(i).get(indexs2[j]));
                                }
                            }
                            if (!map2.containsKey(key)) {
                                map2.put(key, i);
                            }
                        }
//                        System.out.println("******");
//                        System.out.println(map1);
//                        System.out.println("******");
//                        System.out.println(map2);
//                        System.out.println(content1);
                        for (int i = 0; i < content1.size(); ++i) {
                            if (content1.get(i).size() != head1.size()) {
                                continue;
                            }
                            List<String> tmp = new ArrayList<>();
                            if (map2.containsKey(map1.get(i))) {
                                int index = map2.get(map1.get(i));
                                for (int j = 0; j < indexs3.length; ++j) {
                                    tmp.add(content2.get(index).get(indexs3[j]));
                                }
                            } else {
                                for (int j = 0; j < indexs3.length; ++j) {
                                    tmp.add("");
                                }
                            }
                            content1.get(i).addAll(tmp);
                        }
                        for (int i = 0; i < cmd3.length; ++i) {
                            head1.add(cmd3[i]);
                        }
                        content1.add(0, head1);
                        MyFileReader.writeFile(location + outputFileName, content1, ",");

                        //output files
                        for (String name : action.getOutputFileNames()) {
                            outputs.add(location + name);
                        }
                    } else if (action.getMergeTableSelect().equals("differencewithkey")) {
                        System.out.println(action.getMergeTableSelectInput());
                        String[] cmds = action.getMergeTableSelectInput().split("#");
                        String[] cmd1 = cmds[0].split(",");
                        String[] cmd2 = cmds[1].split(",");
                        int[] indexs1 = new int[cmd1.length - 1];
                        int[] indexs2 = new int[cmd2.length - 1];

                        String location = cur.getResourcesIn().get(0).getLocation();
                        String outputFileName = action.getOutputFileNames().get(0);

                        List<String> head1 = MyFileReader.readCSVHead(location + cmd1[0]);
                        List<List<String>> content1 = MyFileReader.readCSVContent(location + cmd1[0]);
                        List<String> head2 = MyFileReader.readCSVHead(location + cmd2[0]);
                        List<List<String>> content2 = MyFileReader.readCSVContent(location + cmd2[0]);
                        Map<Integer, List<String>> map1 = new HashMap<>();
                        Map<List<String>, Integer> map2 = new HashMap<>();
                        for (int i = 0; i < indexs1.length; ++i) {
                            indexs1[i] = head1.indexOf(cmd1[i + 1]);
                        }
                        for (int i = 0; i < indexs2.length; ++i) {
                            indexs2[i] = head2.indexOf(cmd2[i + 1]);
                        }

                        for (int i = 0; i < content1.size(); ++i) {
                            if (content1.get(i).size() != head1.size()) {
                                continue;
                            }
                            List<String> key = new ArrayList<>();
                            for (int j = 0; j < indexs1.length; ++j) {
                                if (!content1.get(i).get(indexs1[j]).isEmpty()) {
                                    key.add(content1.get(i).get(indexs1[j]));
                                }
                            }
                            map1.put(i, key);
                        }
                        for (int i = 0; i < content2.size(); ++i) {
                            if (content2.get(i).size() != head2.size()) {
                                continue;
                            }
                            List<String> key = new ArrayList<>();
                            for (int j = 0; j < indexs2.length; ++j) {
                                if (!content2.get(i).get(indexs2[j]).isEmpty()) {
                                    key.add(content2.get(i).get(indexs2[j]));
                                }
                            }
                            if (!map2.containsKey(key)) {
                                map2.put(key, i);
                            }
                        }
//                        System.out.println("******");
//                        System.out.println(map1);
                        System.out.println("******");
//                        System.out.println(map2);
//                        System.out.println(content1);
                        List<List<String>> conts = new ArrayList<>();
                        for (int i = 0; i < content1.size(); ++i) {
                            if (content1.get(i).size() != head1.size()) {
                                continue;
                            }
                            List<String> tmp = new ArrayList<>();
                            if (!map2.containsKey(map1.get(i))) {
                                tmp = map1.get(i);
                                conts.add(tmp);
                            }
                        }
                        conts.add(0, head1);
                        System.out.println(conts);
                        MyFileReader.writeFile(location + outputFileName, conts, ",");

                        //output files
                        for (String name : action.getOutputFileNames()) {
                            outputs.add(location + name);
                        }
                    } else if (action.getMergeTableSelect().equals("selectandaddtabletobottom")) {
                        System.out.println(action.getMergeTableSelectInput());
                        String[] cmds = action.getMergeTableSelectInput().split("#");
                        String[] cmd1 = cmds[0].split(",");
                        String[] cmd2 = cmds[1].split(",");
                        int[] indexs1 = new int[cmd1.length - 1];
                        int[] indexs2 = new int[cmd2.length - 1];
                        String location = cur.getResourcesIn().get(0).getLocation();
                        String outputFileName = action.getOutputFileNames().get(0);

                        List<String> head1 = MyFileReader.readCSVHead(location + cmd1[0]);
                        List<String> selecthead1 = new ArrayList<>();
                        List<List<String>> content1 = MyFileReader.readCSVContent(location + cmd1[0]);
                        List<String> head2 = MyFileReader.readCSVHead(location + cmd2[0]);
                        List<String> selecthead2 = new ArrayList<>();
                        List<List<String>> content2 = MyFileReader.readCSVContent(location + cmd2[0]);

                        for (int i = 0; i < indexs1.length; ++i) {
                            indexs1[i] = head1.indexOf(cmd1[i + 1]);
                        }
                        for (int i = 0; i < indexs2.length; ++i) {
                            indexs2[i] = head2.indexOf(cmd2[i + 1]);
                        }

//                        System.out.println("******");
//                        System.out.println(map1);
//                        System.out.println("******");
//                        System.out.println(map2);
//                        System.out.println(content1);
                        List<List<String>> conts = new ArrayList<>();
                        for (int i = 0; i < content1.size(); ++i) {
                            if (content1.get(i).size() != head1.size()) {
                                continue;
                            }
                            List<String> tmp = new ArrayList<>();
                            for (int index : indexs1) {
                                tmp.add(content1.get(i).get(index));
                            }
                        }
                        for (int i = 0; i < content2.size(); ++i) {
                            if (content2.get(i).size() != head2.size()) {
                                continue;
                            }
                            List<String> tmp = new ArrayList<>();
                            for (int index : indexs2) {
                                tmp.add(content1.get(i).get(index));
                            }
                        }
                        conts.add(0, head1);
                        System.out.println(conts);
                        MyFileReader.writeFile(location + outputFileName, conts, ",");

                        //output files
                        for (String name : action.getOutputFileNames()) {
                            outputs.add(location + name);
                        }
                    }
                }
                cur.setOutputs(outputs);
            }
        }
        return "";
    }
    
    void analyticsExtract2(Map<String, List<String>> map, List<SelectAttr> attrs, org.w3c.dom.Node root, int[] cnt) {
        if (root.getNodeName().equals("#text")) {
            return;
        }
        for (SelectAttr attr : attrs) {
            if (root.getNodeName().equals(attr.getAttribute())) {
                List<String> val = map.get(root.getNodeName());
                val.add(root.getTextContent());
                map.put(root.getNodeName(), val);
                return;
            }
        }
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            analyticsExtract2(map, attrs, children.item(i), cnt);
        }
    }

    void analyticsExtract(Map<String, List<String>> map, List<SelectAttr> attrs, org.w3c.dom.Node root, int[] cnt) {
        if (root.getNodeName().equals("#text")) {
            --cnt[0];
            return;
        }
        for (SelectAttr attr : attrs) {
            if (Integer.valueOf(attr.getAttrIndex()) - 1 == cnt[0] % Integer.valueOf(attr.getTotalNodes())) {
                List<String> val = map.get(attr.getName());
                val.add(root.getTextContent());
            }
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                ++cnt[0];
                analyticsExtract(map, attrs, children.item(i), cnt);
            }
        }
    }
}
