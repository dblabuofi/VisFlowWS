package com.mycompany.autointerfacews.algorithm;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mycompany.autointerfacews.bioflow.BioFLowFunctionStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowCodeStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowCombineStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowExtractStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowFusionStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowIFStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import static com.mycompany.autointerfacews.bioflow.BioFlowService.executeBooleanExp;
import com.mycompany.autointerfacews.bioflow.BioFlowTransformStatement;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.AttrMatch;
import com.mycompany.autointerfacews.dataIcon.Edge;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.MapAttr;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.MyTransform;
import com.mycompany.autointerfacews.dataIcon.SelectAttr;
import com.mycompany.autointerfacews.dataIcon.UpdateAttr;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.helper.PythonHelper;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mymessage.ReturnMessage;
import com.mycompany.autointerfacews.resources.ParseResources;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.dom4j.io.SAXReader;

/**
 *
 * @author jupiter
 */
public class Execution {

//        MySQLCon mySQLCon;
    @Inject
    Gson gson;
    @Inject
    EXist eXist;
    @Inject
    BioFlowService bioFlowService;
    @Inject
    InputGenerator inputGenerator;
    @Inject
    WebResourceImageDownloader webResourceImageDownloader;

    @Inject
    @Named("driver")
    WebDriver driver;

    Type listlistType = new TypeToken<List<List<String>>>() {
    }.getType();
    Type listType = new TypeToken<List<String>>() {
    }.getType();

    public Execution() {
    }

    void readData(Node node, List<AttrMatch> globalmatch) {
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
            if (globalmatch == null) ;
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

    }

    void analyticsData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException {
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
//                            || cur.getId().equals("a0425694-2148-4808-9c11-48c9bbc340a8")
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
//                        if (cur.getId().equals("a0425694-2148-4808-9c11-48c9bbc340a8")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\mineral.csv");
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

    void adapterData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException, IOException {
        System.out.println("adpter data");
        status.addMessage("Adapter Data");
        List<String> outputs = new ArrayList<>();
        if (cur.getActions() != null && cur.getActions().size() > 0) {
            for (Action action : cur.getActions()) {
                if (action.getAct().equals("Resource")) {//need access remote web resources
//                    if (1 == 0
//                            || cur.getId().equals("a0425694-2148-4808-9c11-48c9bbc340a8")
//                            || cur.getId().equals("f4072fd6-5ccf-4a47-ade0-39b9daaa5e46")
//                            || cur.getId().equals("cea9a0c1-4663-464c-815d-6317205fdd38")
//                            || cur.getId().equals("8dc8eeea-fcd1-4df6-892f-71805b2e74e6")
//                            || cur.getId().equals("a1d405ef-8c6f-44e4-9652-b6204639fde3")
//                            || cur.getId().equals("3a746d59-bf29-4bb9-8ae8-59a21e6cabd0")
//                            || cur.getId().equals("35954ad3-4271-4641-80b7-19cc58975a4f")) {
//                        System.out.println("testing only!!");
//                        if (cur.getId().equals("f4072fd6-5ccf-4a47-ade0-39b9daaa5e46")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\orthology.csv");
//                        }
//                        if (cur.getId().equals("cea9a0c1-4663-464c-815d-6317205fdd38")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\orthologygen.csv");
//                        }
//                        if (cur.getId().equals("8dc8eeea-fcd1-4df6-892f-71805b2e74e6")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\hsapath.csv");
//                        }
//                        if (cur.getId().equals("a1d405ef-8c6f-44e4-9652-b6204639fde3")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\mmupath.csv");
//                        }
//                        if (cur.getId().equals("3a746d59-bf29-4bb9-8ae8-59a21e6cabd0")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\rnopath.csv");
//                        }
//                        if (cur.getId().equals("a0425694-2148-4808-9c11-48c9bbc340a8")) {
//                            outputs.add("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\mineral.csv");
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
                    String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                    String codeName = action.getCodeName();
                    status.addMessage("Code");
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

                        for (MyResource resource : cur.getResourcesIn()) {
                            String fileName = resource.getUrlReturnFileName();
                            String rootName = MyUtils.getFileName(fileName);
                            String typeIn = resource.getUrlReturnFileType().toLowerCase();
                            if (typeIn.equals("xml")) {
                                String tmpFile = "tmpFile3" + MyUtils.randomAlphaNumeric();
                                MyFileReader.converXMLtoCSV(location + fileName, location + tmpFile);
                                MySQLHelper.uploadFileToSQL(location, tmpFile, rootName, status);
                                File tmp = new File(location + tmpFile);
                                tmp.deleteOnExit();
                            } else if (typeIn.equals("csv")) {
                                //upload it to the server
                                MySQLHelper.uploadFileToSQL(location, fileName, rootName, status);
                            } else if (typeIn.equals("sql")) {
                                MySQLHelper.uploadFileToSQL(location, fileName, rootName, status);
                            }
                        }
                        //run query
//                        MySQLHelper.runScript(location, action.getVal(), status);

                    }

                    status.addMessage("Generate bioflow script");
                    //generate bioflow statement
                    BioFlowCodeStatement codeScript = bioFlowService.generateBioFlowCodeScript(cur, action);
                    System.out.println(codeScript);
                    System.out.println("run bioflow");
                    bioFlowService.executeBioFlowCodeStatement(codeScript, status);
                    //after that we deal with it 
                    if (action.getCodeType().equals("sql")) {
                        for (int i = 0; i < action.getOutputFileNames().size(); ++i) {
                            //convert whatever it is 
                            String typeOut = MyUtils.getFileType(action.getOutputFileNames().get(i));
                            String rootName = MyUtils.getFileName(action.getOutputFileNames().get(i));
                            String returnFileName = action.getOutputFileNames().get(i);
                            if (typeOut.equals("csv") || typeOut.equals("sql")) {
                                MySQLHelper.downLoadaTableToLocal(rootName, location, returnFileName);
                            } else if (typeOut.equals("xml")) {
                                String tmpFile = "tmpFile44";
                                MySQLHelper.downLoadaTableToLocal(rootName, location, tmpFile);
                                MyFileReader.convertCSVtoXML(location + tmpFile, location + returnFileName);
                                File tmp = new File(location + tmpFile);
                                tmp.deleteOnExit();
                            }
                        }
                    }
                    if (action.getCodeType().equals("xquery")) {
                        for (int i = 0; i < action.getOutputFileNames().size(); ++i) {
                            //convert whatever it is 
                            String typeOut = MyUtils.getFileType(action.getOutputFileNames().get(i)).toLowerCase();
                            String rootName = MyUtils.getFileName(action.getOutputFileNames().get(i));
                            String returnFileName = action.getOutputFileNames().get(i);
                            if (typeOut.equals("csv") || typeOut.equals("sql")) {
                                File file = new File(location + returnFileName);
                                System.out.println(location + rootName + ".xml");
                                System.out.println(location + returnFileName);
                                if (file.exists()) {
                                    file.createNewFile();
                                }
                                MyFileReader.converXMLtoCSV(location + rootName + ".xml", location + returnFileName);
                            } else if (typeOut.equals("sql")) {
                                MyFileReader.converXMLtoCSV(location + rootName + ".xml", location + rootName);
                                MySQLHelper.uploadFileToSQL(location, rootName, rootName, status);
                            }
                        }
                    }
                    //output files
                    for (String name : action.getOutputFileNames()) {
                        outputs.add(location + name);
                    }

                } else if (action.getAct().equals("Transformer Function")) {
                    status.addMessage("Transformer Function");
                    /*
                                        deal with one file only
                     */
                    String location = cur.getResourcesIn().get(0).getLocation();
                    //we only do csv and xml files
                    //generate bioflow statement
//                    BioFlowTransformStatement transformScript = bioFlowService.generateBioFlowTransformScript(cur, action);
//                    System.out.println(transformScript);
//                    System.out.println("run bioflow");
//                    bioFlowService.executeBioFlowTransformStatement(transformScript, status);

                    //one file a time
                    if (action.getUpdateAttrs().size() != 0) {
                        List<UpdateAttr> attrs = action.getUpdateAttrs();
                        String inputFile = action.getInputFileNames().get(0);
                        String outputFile = action.getOutputFileNames().get(0);
                        List<String> files = new ArrayList<>();
                        files.add(location + inputFile);
                        for (int i = 1; i < attrs.size(); ++i) {
                            files.add(location + "tmpFile" + i);
                        }
                        files.add(location + outputFile);
                        System.out.println(files);
                        for (int i = 0; i < attrs.size(); ++i) {
                            Function attrAction = attrs.get(i).getAction();
                            List<String> arguments = new ArrayList<>();
                            arguments.add(attrAction.getFunctionName());
                            for (MyAttribute attr : attrAction.getAttributes()) {
                                if (!attr.getValue().isEmpty()) {
                                    if (attr.getLabel().equals("outFile")) {
                                        arguments.add(attr.getName());
                                        arguments.add(files.get(i + 1));
                                    } else if (attr.getLabel().equals("inputFile")) {
                                        arguments.add(attr.getName());
                                        arguments.add(files.get(i));
                                    } else {
                                        arguments.add(attr.getName());
                                        arguments.add(attr.getValue());
                                    }
                                }
                            }
                            System.out.println(arguments);
                            if (attrAction.getFunctionType().startsWith("python")) {
                                PythonHelper.runWithAgrument(location, arguments, status);
                            }
                        }
                        for (int i = 1; i < files.size() - 1; ++i) {
                            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(files.get(i)));
                        }
                    }

                    if (action.getNewAttrs().size() != 0) {
                        List<UpdateAttr> attrs = action.getNewAttrs();
                        String inputFile = action.getInputFileNames().get(0);
                        String outputFile = action.getOutputFileNames().get(0);
                        List<String> files = new ArrayList<>();
                        files.add(location + inputFile);
                        for (int i = 1; i < attrs.size(); ++i) {
                            files.add(location + "tmpFile" + i);
                        }
                        files.add(location + outputFile);
                        System.out.println(files);
                        for (int i = 0; i < attrs.size(); ++i) {
                            Function attrAction = attrs.get(i).getAction();
                            List<String> arguments = new ArrayList<>();
                            arguments.add(attrAction.getFunctionName());
                            for (MyAttribute attr : attrAction.getAttributes()) {
                                if (!attr.getValue().isEmpty()) {
                                    if (attr.getLabel().equals("outFile")) {
                                        arguments.add(attr.getName());
                                        arguments.add(files.get(i + 1));
                                    } else if (attr.getLabel().equals("inputFile")) {
                                        arguments.add(attr.getName());
                                        arguments.add(files.get(i));
                                    } else {
                                        arguments.add(attr.getName());
                                        arguments.add(attr.getValue());
                                    }
                                }
                            }
                            System.out.println(arguments);
                            if (attrAction.getFunctionType().startsWith("python")) {
                                PythonHelper.runWithAgrument(location, arguments, status);
                            }
                        }
                        for (int i = 1; i < files.size() - 1; ++i) {
                            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(files.get(i)));
                        }
                    }

                    //after all transfer is done, then we transfer file to another one 
                    if (!action.getTransformResources().isEmpty()) {
//                        List<String> conResources = action.getTransformResources();
                        for (MyTransform transRes : action.getTransformResources()) {
                            String fileURL = transRes.getResource();
                            String conType = transRes.getType().toLowerCase();
                            String fileName = MyUtils.getFileName(fileURL);
                            String returnFileName = action.getOutputFileNames().get(0);
                            String inputType = transRes.getInputType().toLowerCase();
                            if (inputType.equals("csv") && conType.equals("xml")) {
                                status.addMessage("convert csv to xml");
                                MyFileReader.convertCSVtoXML(location + fileURL, location + returnFileName);
                            } else if (inputType.equals("xml") && conType.equals("csv")) {
                                System.out.println("convert xml to csv");
                                MyFileReader.converXMLtoCSV(location + fileURL, location + returnFileName);
                            } else if (inputType.equals("csv") && conType.equals("sql")) {
                                System.out.println("convert csv to sql");
                                System.out.println(fileURL);
                                System.out.println(returnFileName);
                                File returned = new File(location + returnFileName);
                                if (!returned.exists()) {
                                    returned.createNewFile();
                                }
                                Files.copy(new File(location + fileURL), returned);
                                MySQLHelper.uploadFileToSQL(location, fileURL, returnFileName, status);
                            } else if (inputType.equals("xml") && conType.equals("sql")) {
                                System.out.println("convert xml to sql");
                                File returned = new File(location + returnFileName);
                                if (!returned.exists()) {
                                    returned.createNewFile();
                                }
                                MySQLHelper.uploadFileToSQLXML(location, fileURL, returnFileName, status);
                            } else if (inputType.equals("sql") && conType.equals("csv")) {
                                System.out.println("convert sql to csv");
                                MySQLHelper.downLoadaTableToLocal(fileName, location, returnFileName);

                            } else if (inputType.equals("sql") && conType.equals("xml")) {
                                System.out.println("convert sql to xml");
                                String tempFile = "tempFile" + MyUtils.randomAlphaNumeric();
                                MySQLHelper.downLoadaTableToLocal(fileName, location, tempFile);
                                MyFileReader.convertCSVtoXML(location + tempFile, location + returnFileName);
                                File tmpFile = new File(location + tempFile);
                                tmpFile.deleteOnExit();
                            }
                        }
                    }

                    for (MyResource res : cur.getResourcesOut()) {
                        outputs.add(location + res.getUrlReturnFileName());
                    }
                }

                cur.setOutputs(outputs);
            }
        }
    }

    Set<String> nextStep(List<Edge> edges, String curID) {
        return edges.stream().filter(t -> t.getFrom().equals(curID)).map(t -> t.getTo()).collect(Collectors.toSet());
    }

    Set<String> generateReachList(List<Edge> edges, String curId) {
        Set<String> res = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(curId);

        while (!queue.isEmpty()) {
            String cur = queue.remove();
            res.add(cur);
            Set<String> next = nextStep(edges, cur);
            next.removeAll(res);
            queue.addAll(next);
        }

        return res;
    }

    //remove del nodes
    List<String> generateDeleteNodes(Map<String, Node> nodes, List<Edge> edges, String cur, String del) {
        Set<String> curReached = generateReachList(edges, cur);
        Set<String> delReached = generateReachList(edges, del);
        delReached.removeAll(curReached);

        return new ArrayList<>(delReached);
    }
    //one file only deal with csv and xml files

    public String ifData(Map<String, Node> nodes, List<Edge> edges, Node cur, Queue<String> queue, MyStatus status) throws CodeException, IOException {
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
                System.out.println("**********if output************");
                System.out.println(result);
                status.addMessage("IF condition result:" + result);
                List<String> deleteNodes = null;
                //remove nodes
                System.out.println("I am here************ " + (result == true));
                if (result) {
                    System.out.println("herehreh************");
                    deleteNodes = generateDeleteNodes(nodes, edges, action.getTrueBranch().getId(), action.getFalseBranch().getId());
                    System.out.println("delete nodes");
                    deleteNodes.forEach(t -> System.out.println(t));
                    queue.removeAll(deleteNodes);
                } else {
                    deleteNodes = generateDeleteNodes(nodes, edges, action.getFalseBranch().getId(), action.getTrueBranch().getId());
                    System.out.println("delete nodes");
                    deleteNodes.forEach(t -> System.out.println(t));
                    queue.removeAll(deleteNodes);
                }
                System.out.println("********remove nodes*************");
                deleteNodes.forEach(t -> System.out.println(t));

            }
        }

        for (MyResource res : cur.getResourcesOut()) {
            outputs.add(location + res.getUrlReturnFileName());
        }
        cur.setOutputs(outputs);

        return null;
    }

    String printerData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) {
        System.out.println("printerData");
        List<String> outputs = new ArrayList<>();
        List<ReturnMessage> result = new ArrayList<>();

        //get print type
        for (MyResource resourcesIn : cur.getResourcesIn()) {
            result.add(new ReturnMessage("Printer", resourcesIn.getResourceType(), resourcesIn.getUrlReturnFileName(), cur.getId()));
        }
        cur.setOutputs(outputs);
        return gson.toJson(result);
    }

    /**
     * ***
     *
     * @param nodes
     * @param edges
     * @param cur
     * @param status
     * @return JSON List<PrintTerminalMessage>
     */
    public String printToTerminal(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) {
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
                List<List<String>> content = MyUtils.getFile(location + action.getOutputFileNames().get(0));
                ReturnMessage m = new ReturnMessage("Terminal", action.getPrintType(), action.getOutputFileNames().get(0), content, cur.getId(), action.getSubmit(), action.getNumOfWins(), location, action.getColFuns());
                result.add(m);
            } else if (action.getPrintType().equals("barchart")) {

            } else if (action.getPrintType().equals("piechart")) {

            } else if (action.getPrintType().equals("linechart")) {

            }
        }
        cur.setOutputs(outputs);
        return gson.toJson(result);
    }

//    public void fusionData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException {
    public void combineData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException {
        System.out.println("combine data");
        BioFlowCombineStatement combineScript = bioFlowService.generateBioFlowCombineScript(cur, cur.getActions().get(0));
        String outputFileURL = bioFlowService.executeBioFlowCombineStatement(combineScript, cur.getResourcesIn().get(0).getLocation(),
                cur.getResourcesOut().get(0).getUrlReturnFileName(), cur.getActions().get(0).getLeftKeys(), cur.getActions().get(0).getRightKeys(),
                cur.getResourcesIn().get(0).getOutAttributes().stream().map(t -> t.getName()).collect(Collectors.toList()),
                cur.getResourcesIn().get(1).getOutAttributes().stream().map(t -> t.getName()).collect(Collectors.toList()),
                inputGenerator
        );
        List<String> outputs = new ArrayList<>();
        outputs.add(outputFileURL);
        cur.setOutputs(outputs);
    }

//    public void combineData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException {
    public void fusionData(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException {
        System.out.println("fusion data");
        BioFlowFusionStatement fusionScript = bioFlowService.generateBioFlowFusionScript(cur, cur.getActions().get(0));
        String outputFileURL = bioFlowService.executeBioFlowFusionStatement(fusionScript, cur.getResourcesIn().get(0).getLocation(),
                cur.getResourcesOut().get(0).getUrlReturnFileName(), cur.getActions().get(0).getLeftKeys(), cur.getActions().get(0).getRightKeys(),
                cur.getResourcesIn().get(0).getOutAttributes().stream().map(t -> t.getName()).collect(Collectors.toList()),
                cur.getResourcesIn().get(1).getOutAttributes().stream().map(t -> t.getName()).collect(Collectors.toList()),
                inputGenerator
        );
        List<String> outputs = new ArrayList<>();
        outputs.add(outputFileURL);
        cur.setOutputs(outputs);
    }
    static HttpClient client = HttpClientBuilder.create().build();

    public void procedure(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) throws CodeException, IOException {
        String res = "", line = "";
        try {
            System.out.println("procedure");
            status.addMessage("procedure");
            String location = cur.getResourcesIn().isEmpty() ? "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\" : cur.getResourcesIn().get(0).getLocation();
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
                System.out.println(file.exists());
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
                if (from.equals(to)) {
                    continue;
                }
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
                if (from.equals(to)) {
                    continue;
                }
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
    }

    public void generalIO(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) {
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
    }

    public void connect(Map<String, Node> nodes, List<Edge> edges, Node cur, MyStatus status) {
        try {
            System.out.println("connect Data");
            System.out.println(cur);
            List<String> resources = new ArrayList<>();
            String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
            //only support from database right now, need working for user files
            for (MyResource resource : cur.getResourcesIn()) {
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                resources.add(fileUrl);
            }
            cur.setOutputs(resources);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    List<String> getNodesBetween(List<String> workflow, String start, String end) {
        List<String> res = new ArrayList<>(workflow);
        int s = workflow.indexOf(start);
        int e = workflow.indexOf(end);
        res.subList(s, e + 1);
        Collections.reverse(res);
        return res;
    }

    public void repeat(Map<String, Node> nodes, List<Edge> edges, Queue<String> queue, List<String> workflow, Node cur, MyStatus status) {
        try {
            System.out.println("repeat Data");
            System.out.println(cur);
            List<String> resources = new ArrayList<>();
            String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";

            Action action = cur.getActions().get(0);
            String start = action.getRepeatNode().getId();
            String end = cur.getId();
            List<String> nodesBetween = getNodesBetween(workflow, start, end);
            System.out.println(action.getRepeatTimes());

            if (action.getconditionType().equals("count")) {
                if (Integer.valueOf(action.getRepeatTimes()) >= 0) {//here is slightly different
                    action.setRepeatTimes("" + (Integer.valueOf(action.getRepeatTimes()) - 1));
                    for (String nd : nodesBetween) {
                        ((Deque) queue).addFirst(nd);
                    }
                    System.out.println(queue);
                }
            } else {
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

                List<String> attributes = action.getConditions().stream().map(t -> t.getAttrbute()).collect(Collectors.toList());
                List<String> groupOP = action.getConditions().stream().map(t -> t.getGroupOP()).collect(Collectors.toList());
                List<String> conditions = action.getConditions().stream().map(t -> t.getCondition()).collect(Collectors.toList());
                List<String> values = action.getConditions().stream().map(t -> t.getValue()).collect(Collectors.toList());
                List<String> logic = action.getConditions().stream().map(t -> t.getLogic()).collect(Collectors.toList());

                List<String> headers = inputs.remove(0);
                int index = headers.indexOf(attributes.get(0));
                System.out.println(index);
                List<String> cols = inputs.stream()
                        .map(t -> t.get(index))
                        .collect(Collectors.toList());
                System.out.println(cols);
                boolean result = executeBooleanExp(cols, groupOP.get(0), conditions.get(0), values.get(0));
                System.out.println(result);
                //we do step by step
                for (int i = 1; i < attributes.size(); i += 2) {
                    Boolean first = null;
                    int index1 = headers.indexOf(attributes.get(i + 1));
                    cols = inputs.stream()
                            .map(t -> t.get(index1))
                            .collect(Collectors.toList());
                    first = executeBooleanExp(cols, groupOP.get(i + 1), conditions.get(i + 1), values.get(i + 1));
                    if (!logic.isEmpty() && logic.get(i).equals("and")) {
                        result &= first;
                    } else {
                        result |= first;
                    }
                }
                System.out.println(result);

                //remove nodes
                if (result) {
                    action.setRepeatTimes("0");
                } else {
                    for (String nd : nodesBetween) {
                        ((Deque) queue).addFirst(nd);
                    }
                }
            }

            //only support from database right now, need working for user files
            for (MyResource resource : cur.getResourcesIn()) {
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                resources.add(fileUrl);
            }
            cur.setOutputs(resources);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void waiton(Node cur, MyStatus status) {
        System.out.println("read Data");
        List<String> resources = new ArrayList<>();
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        //only support from database right now, need working for user files
        for (MyResource resource : cur.getResourcesOut()) {
            String fileUrl = location + resource.getUrlReturnFileName();
            resources.add(fileUrl);
        }
        cur.setOutputs(resources);
    }

    /**
     * *
     *
     * @param nodes
     * @param edges
     * @param workflow
     * @param status
     * @return JSON string
     */
    public String run(Map<String, Node> nodes,
            List<Edge> edges,
            List<String> workflow,
            List<AttrMatch> globalmatch,
            MyStatus status) throws CodeException, IOException {

        status.setMessage("");
        String res = "";
        Node cur = null;
        Queue<String> queue = new LinkedList<>(workflow);

        while (!queue.isEmpty()) {
            String id = queue.remove();
            cur = nodes.get(id);
            System.out.println("id: " + id);
            status.setId(id);

//                        try {
//                                Thread.sleep(2000);
//                        } catch (Exception e) {
//                        }
            //add all
            List<String> inputs = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge.getTo().equals(cur.getId())) {
                    Node from = nodes.get(edge.getFrom());
                    if (from.getType().equals("data")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("combine")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("fusion")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("adapter")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("analytics")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("terminal")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("printer")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("IO")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("if")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("repeat")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("connect")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("waiton")) {
                        inputs.addAll(from.getOutputs());
                    }
                    if (from.getType().equals("nested")) {
                        inputs.addAll(from.getOutputs());
                    }
                }
            }
            cur.setInputs(inputs);

            //stop
            if (cur.getStop() != null && cur.getStop().equals("input")) {
                for (MyResource resource : cur.getResourcesIn()) {
                    if (resource.getIsReturn() != null && resource.getIsReturn().equals("return")) {
                        System.out.println(resource.getLocation());
                        String location = resource.getLocation();
                        if (location == null) {
                            location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                        }
                        res = MyFileReader.fileToString(location + resource.getUrlReturnFileName());
                        //make sure just return some thing you can see
                        if (res.length() > 65536) {
                            res = res.substring(0, 65536);
                            res += "\n...\nFile too large too show all";
                        }
                        res = gson.toJson(res);

                        List<MyResource> oldInputs = new ArrayList<>();
                        nodes.entrySet().stream().forEach(t -> {
                            if (t.getValue().getType().equals("data")) {
                                oldInputs.addAll(t.getValue().getResources());
                            }
                        });
                        for (MyResource input : oldInputs) {
                            String f = input.getResourceName();
                            File file = new File(location + f + ".bak");
                            if (file.exists()) {
                                File newFile = new File(location + f);
                                FileUtils.copyFile(file, newFile);
                                file.delete();
                            }
                        }

                        return res;
                    }
                }
            }
            //deal with nodes      
            if (cur.getType().equals("data")) {
                status.addMessage("data icon id:" + cur.getId());
                cur.setOutputs(cur.getInputs());
                readData(cur, globalmatch);
            } else if (cur.getType().equals("adapter")) {
                status.addMessage("adapter icon id:" + cur.getId());
                adapterData(nodes, edges, cur, status);
            } else if (cur.getType().equals("analytics")) {
                status.addMessage("analytics icon id:" + cur.getId());
                analyticsData(nodes, edges, cur, status);
            } else if (cur.getType().equals("combine")) {
                status.addMessage("combine icon id:" + cur.getId());
                combineData(nodes, edges, cur, status);
            } else if (cur.getType().equals("fusion")) {
                status.addMessage("fusion icon id:" + cur.getId());
                fusionData(nodes, edges, cur, status);
            } else if (cur.getType().equals("if")) {
                status.addMessage("if icon id:" + cur.getId());
                ifData(nodes, edges, cur, queue, status);
            } else if (cur.getType().equals("printer")) {
                status.addMessage("printer icon id:" + cur.getId());
                res = printerData(nodes, edges, cur, status);
                System.out.println("printer");
            } else if (cur.getType().equals("terminal")) {
                status.addMessage("terminal icon id:" + cur.getId());
                res = printToTerminal(nodes, edges, cur, status);
            } else if (cur.getType().equals("nested")) {
                status.addMessage("nested icon id:" + cur.getId());
                procedure(nodes, edges, cur, status);
            } else if (cur.getType().equals("IO")) {
                status.addMessage("General IO icon id:" + cur.getId());
                generalIO(nodes, edges, cur, status);
            } else if (cur.getType().equals("connect")) {
                status.addMessage("connect icon id:" + cur.getId());
                connect(nodes, edges, cur, status);
            } else if (cur.getType().equals("repeat")) {
                status.addMessage("repeat icon id:" + cur.getId());
                repeat(nodes, edges, queue, workflow, cur, status);
            } else if (cur.getType().equals("waiton")) {
                status.addMessage("repeat icon id:" + cur.getId());
                waiton(cur, status);
            } else {
                System.out.println("***********something wrong*************************");
            }
            nodes.put(id, cur);

            //stop
            if (cur.getStop() != null && cur.getStop().equals("output")) {
                if (cur.getType().equals("repeat") && !cur.getActions().get(0).getRepeatTimes().equals("0")) {
                    continue;
                }
                System.out.println("stop here");
                for (MyResource resource : cur.getResourcesOut()) {
                    System.out.println(resource);
                    if (resource.getIsReturn() != null && resource.getIsReturn().equals("return")) {
                        Integer index = cur.getResourcesOut().indexOf(resource);
                        System.out.println(cur.getOutputs().get(index));
                        res = MyFileReader.fileToString(cur.getOutputs().get(index));
                        //make sure just return some thing you can see
                        if (res.length() > 65536) {
                            res = res.substring(0, 65536);
                            res += "\n...\nFile too large too show all";
                        }
                        res = gson.toJson(res);

                        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                        List<MyResource> oldInputs = new ArrayList<>();
                        nodes.entrySet().stream().forEach(t -> {
                            if (t.getValue().getType().equals("data")) {
                                oldInputs.addAll(t.getValue().getResources());
                            }
                        });
                        for (MyResource input : oldInputs) {
                            String f = input.getResourceName();
                            File file = new File(location + f + ".bak");
                            if (file.exists()) {
                                File newFile = new File(location + f);
                                FileUtils.copyFile(file, newFile);
                                file.delete();
                            }
                        }

                        return res;
                    }
                }
            }
        }
        status.setId("");

        System.out.println("return");
        return res;
    }

}
