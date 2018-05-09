/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.bioflow;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.MyTransform;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.generator.OutputGenerator;
import com.mycompany.autointerfacews.gordian.GordianAlgorithm;
import com.mycompany.autointerfacews.helper.BashHelper;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.helper.PythonHelper;
import com.mycompany.autointerfacews.helper.RHelper;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.mydata.MyWrapper;
import com.mycompany.autointerfacews.smtch.SMatch;
import com.mycompany.autointerfacews.utils.MyUtils;
import com.mycompany.autointerfacews.wrapper.WrapperCollections;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author jupiter
 */
public class BioFlowService {

    @Inject
    MyHttpClient myHttpClient;
    @Inject
    OutputGenerator outputGenerator;
    @Inject
    WrapperCollections wrapperCollections;
    @Inject
    EXist eXist;
    @Inject
    SMatch sMatch;

    public BioFlowExtractStatement generateBioFlowExtractScript(Node node, Action action) {
        List<String> extractAttributes = new ArrayList<>();
        String matcher = "";
        String wrapper = "";
        List<String> wrapperTargets = new ArrayList<>();
        String resultContainHeaderInfo = "";
        String filler = "";
        String from = "";
        List<String> submit = new ArrayList<>();
        String where = "";
        String resultMethod = "";
        String submitMethod = "";
        String submitProtocol = "";

        if (action.getTargetResource().getOutAttributes() != null) {
//            extractAttributes = action.getTargetResource().getOutAttributes()
            //problems??
            extractAttributes = action.getTargetResource().getOutAttributes()
                    .stream()
                    //                                .filter( t -> t.getSelected().equals("true") )
                    .map(t -> t.getName())
                    .collect(Collectors.toList());
        }
        //matcher is used before to suggested user
//                if (action.getTargetResource().getMatcher() != null) {
//                        matcher = action.getTargetResource().getMatcher();
//                }

        if (action.getTargetResource().getWrapper() != null) {
            wrapper = action.getTargetResource().getWrapper().getWrapperName();
            wrapperTargets = action.getTargetResource().getWrapper().getHeaders();
            resultContainHeaderInfo = action.getTargetResource().getWrapper().getResultContainHeaderInfo();
        }

        if (action.getTargetResource().getFiller() != null) {
            filler = action.getTargetResource().getFiller();
        }

        if (action.getTargetResource().getUrl() != null) {
            from = action.getTargetResource().getUrl();
        }

        //input attributes
        List<String> inputAttrs = action.getTargetResource().getAttributes().stream()
                .map(t -> t.getName())
                .collect(Collectors.toList());

        submit = inputAttrs;

        resultMethod = action.getResultMethod();
        submitMethod = action.getTargetResource().getMethod();
        submitProtocol = action.getTargetResource().getResourceType();

        return new BioFlowExtractStatement(extractAttributes, matcher, wrapper, wrapperTargets, resultContainHeaderInfo, filler, from, submit, where, resultMethod, submitMethod, submitProtocol);
    }

    public BioFlowCodeStatement generateBioFlowCodeScript(Node node, Action action) {
        String location = node.getResourcesIn().get(0).getLocation();
        String codeName = action.getCodeName();;
        String codeType = action.getCodeType();
        String code = action.getVal().replaceAll("\r\n", "\n");

        return new BioFlowCodeStatement(location, codeName, codeType, code);
    }

    public BioFLowFunctionStatement generateBioFlowFunctionScript(Node node, Action action) {
        String location = action.getTargetFunction().getLocation();
        String functionName = action.getTargetFunction().getFunctionName();
        String functionType = action.getTargetFunction().getFunctionType();

        List<String> attrs = action.getTargetFunction().getAttributes().stream()
                .filter(t -> !t.getValue().isEmpty() || (t.getRequired().equals("true") && t.getFrom().equals("default")))
                .map(t -> t.getName())
                .collect(Collectors.toList());

        String commandLineParams = action.getTargetFunction().getComandLine();

        return new BioFLowFunctionStatement(location, functionName, attrs, commandLineParams, functionType);
    }
    //assumming one file 

    public BioFlowTransformStatement generateBioFlowTransformScript(Node node, Action action) {
        List<String> targetAttrs = new ArrayList<>();
        List<String> targetResources = new ArrayList<>();
        List<String> targetTypes = new ArrayList<>();

        List<String> conResources = new ArrayList<>();
        List<String> conTypes = new ArrayList<>();

        String location = node.getResourcesIn().get(0).getLocation();
        String targetFileName = action.getOutputFileNames().get(0);
        String fromFileName = node.getResourcesIn().get(0).getUrlReturnFileName();

        if (!action.getTransformResourcesAttributes().isEmpty()) {
            for (MyTransform targetAttr : action.getTransformResourcesAttributes()) {
                targetAttrs.add(targetAttr.getAttribute());
                targetResources.add(targetAttr.getResource());
                targetTypes.add(targetAttr.getType());
            }
        }

        if (!action.getTransformResources().isEmpty()) {
            for (MyTransform targetRes : action.getTransformResources()) {
                conResources.add(targetRes.getResource());
                conTypes.add(targetRes.getType());
            }
        }

        return new BioFlowTransformStatement(targetAttrs, targetResources, targetTypes, conResources, conTypes, location, targetFileName, fromFileName);
    }

    //assumming one file 
    public BioFlowIFStatement generateBioFlowIFScript(Node node, Action action) {
        List<String> attributes = action.getConditions().stream().map(t -> t.getAttrbute()).collect(Collectors.toList());
        List<String> conditions = action.getConditions().stream().map(t -> t.getCondition()).collect(Collectors.toList());
        List<String> groupOP = action.getConditions().stream().map(t -> t.getGroupOP()).collect(Collectors.toList());
        List<String> resource = action.getConditions().stream().map(t -> t.getResource()).collect(Collectors.toList());
        List<String> logic = action.getConditions().stream().map(t -> t.getLogic()).collect(Collectors.toList());
        List<String> values = action.getConditions().stream().map(t -> t.getValue()).collect(Collectors.toList());

        String trueBranchID = action.getTrueBranch().getId();
        String trueBranchLabel = action.getTrueBranch().getLabel();
        String falseBranchID = action.getFalseBranch().getId();
        String falseBranchLabel = action.getFalseBranch().getLabel();

        return new BioFlowIFStatement(attributes, conditions, groupOP, resource, logic, values, trueBranchID, falseBranchID, trueBranchLabel, falseBranchLabel);
    }

    //assumming one file 
    public BioFlowCombineStatement generateBioFlowCombineScript(Node node, Action action) {

        String r = node.getResourcesIn().get(0).getUrlReturnFileName();
        String s = node.getResourcesIn().get(1).getUrlReturnFileName();
        String matcher = action.getMatcher();
        String identifer = action.getIdentifier();
        String location = node.getResourcesIn().get(0).getLocation();

        return new BioFlowCombineStatement(r, s, matcher, identifer, location);
    }

    //assumming one file 
    public BioFlowFusionStatement generateBioFlowFusionScript(Node node, Action action) {

        String r = node.getResourcesIn().get(0).getUrlReturnFileName();
        String s = node.getResourcesIn().get(1).getUrlReturnFileName();
        String matcher = action.getMatcher();
        String identifer = action.getIdentifier();
        String location = node.getResourcesIn().get(0).getLocation();

        return new BioFlowFusionStatement(r, s, matcher, identifer, location);
    }

    //only one file is allowed
    public String executeBioFlowExtractStatement(BioFlowExtractStatement script, List<List<String>> inputs, String location, String outputFileName, MyStatus status,
            String methodReturnFileSchema, List<String> attrs, MyWrapper wrapper) {
        //list of files to store web returns
        List<String> tempFileNames = null;
        //submit
        if (script.getSubmitProtocol().equals("HTTP")) {
            if (script.getSubmitMethod().equals("GET")) {
                tempFileNames = myHttpClient.get(inputs, script, location, status);
            } else if (script.getSubmitMethod().equals("POST")) {
                tempFileNames = myHttpClient.post(inputs, script, location, status);
            }
        } else if (script.getSubmitProtocol().equals("HTTPS")) {
            if (script.getSubmitMethod().equals("GET")) {
                tempFileNames = myHttpClient.httpsGet(inputs, script, location, status);
            } 
        } else if (script.getSubmitProtocol().equals("REST")) {
            if (script.getSubmitMethod().equals("REST") || script.getSubmitMethod().equals("GET")) { 
                tempFileNames = myHttpClient.rest(inputs, script, location, status);
            }
        }
        System.out.println(tempFileNames);
        //wrapper 
        if (script.getWrapper().equals("textTableWrapper")) {
            tempFileNames = wrapper == null ? wrapperCollections.textTableWrapper(tempFileNames, script, location, new ArrayList<>())
                    : wrapperCollections.textTableWrapper(tempFileNames, script, location, wrapper.getHeaders());
        } else if (script.getWrapper().equals("myTableExactor")) {
            tempFileNames = wrapperCollections.myTableExactor(tempFileNames, script, location);
        } else if (script.getWrapper().equals("jsonTOxmlWrapper")) {
            tempFileNames = wrapperCollections.jsonTOxmlWrapper(tempFileNames, script, location);
        } else if (script.getWrapper().equals("jsonTOTableWrapper")) {
            tempFileNames = wrapperCollections.jsonTOTableWrapper(tempFileNames, script, location, methodReturnFileSchema, attrs);
        } else if (script.getWrapper().equals("HTMLWrapper")) {
//            tempFileNames = wrapperCollections.htmlTableExactor(tempFileNames, script, location, methodReturnFileSchema, attrs, wrapper.getTableIndex());
            tempFileNames = wrapperCollections.htmlTableExactor2(tempFileNames, script, location, methodReturnFileSchema, attrs, wrapper.getTableIndex());
        }
        //append
        System.out.println(script.getResultMethod());
        if (script.getResultMethod().equals("Append")) {
            if (script.getWrapper().equals("jsonTOTableWrapper")) {//didn't use
                outputGenerator.generateAppendCSVOutput(tempFileNames, location + outputFileName);
            } else if (script.getWrapper().equals("jsonTOxmlWrapper")) {
                outputGenerator.generateAppendOutput(tempFileNames, location + outputFileName);
            } else if (script.getWrapper().equals("XMLWrap")) {
                outputGenerator.generateAppendOutput(tempFileNames, location + outputFileName);
            } else {
                outputGenerator.generateAppendOutput(tempFileNames, location + outputFileName);
            }
        } else if (script.getResultMethod().equals("Remove Duplicated")) {

        }

        //delete temp files
        for (String fileURL : tempFileNames) {
            File file = new File(fileURL);
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return location + outputFileName; 
    }

    public String executeBioFlowCodeStatement(BioFlowCodeStatement script, MyStatus status) throws CodeException {
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        String res = "";//output
        if (script.getCodeType().equals("bash")) {
            res = BashHelper.run(location, script.getCodeName(), status);
        } else if (script.getCodeType().equals("python")) {
            res = PythonHelper.run(location, script.getCodeName(), status);
        } else if (script.getCodeType().equals("r")) {
            res = RHelper.run(location, script.getCodeName(), status);
        } else if (script.getCodeType().equals("xquery")) {
            eXist.run(script.getCode(), status);
        } else if (script.getCodeType().equals("sql")) {
            MySQLHelper.runScript(location, script.getCode(), status);
        }

        return res;
    }

    public String executeBioFlowFunctionStatement(BioFLowFunctionStatement script, List<List<String>> inputs, String location, List<String> outputFileNames, MyStatus status) throws CodeException {
        String res = "";
        List<String> attrs = script.getAttrs();

        for (List<String> row : inputs) {
            List<String> arguments = new ArrayList<>();
            arguments.add(script.getLocation() + script.getFunctionName());
            for (int i = 0; i < attrs.size(); ++i) {
                arguments.add(attrs.get(i));
                arguments.add(row.get(i));
            }
            arguments.addAll(Arrays.asList(script.getCommandLineParams().split(" ")));
            System.out.println("**********");
            arguments.forEach(t -> System.out.println(t));
            if (script.getFunctionType().startsWith("cmd")) {
                res = BashHelper.run(location, script.getFunctionName(), arguments, status);
            } else if (script.getFunctionType().startsWith("python")) {

                res = PythonHelper.runWithAgrument(location, arguments, status);
            }
        }
        return res;
    }

    public String executeBioFlowTransformStatement(BioFlowTransformStatement script, MyStatus status) throws CodeException, IOException {
        List<String> targetAttrs = script.getTargetAttrs();
        List<String> targetResources = script.getTargetResources();
        List<String> targetTypes = script.getTargetTypes();

        List<String> conResources = script.getConResources();
        List<String> conTypes = script.getConTypes();
        String targetFileName = script.getTargetFileName();

        String location = script.getLocation();

        String tempFile = "adapterTempFile" + MyUtils.randomAlphaNumeric();
        if (!targetAttrs.isEmpty()) {
            status.addMessage("convert Attributes");
            for (int i = 0; i < targetAttrs.size(); ++i) {
                String fileName = targetResources.get(i);
                String type = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (type.toLowerCase().equals("xml")) {
                    MyFileReader.convertXMLAttribute(location + fileName, location + tempFile, targetAttrs.get(i), targetTypes.get(i));
                } else if (type.toLowerCase().equals("csv")) {
                    MyFileReader.convertCSVAttribute(location + fileName, location + tempFile, targetAttrs.get(i), targetTypes.get(i));
                }
            }
        } else {
            System.out.println("copy files");
            System.out.println(script.getFromFileName());
            Files.copy(new File(location + script.getFromFileName()), new File(location + tempFile));
        }

        if (!conResources.isEmpty()) {
            status.addMessage("convert Resources");
            for (int i = 0; i < conResources.size(); ++i) {
                String fileName = conResources.get(i).substring(0, conResources.get(i).lastIndexOf("."));
                if (conTypes.get(i).toLowerCase().equals("xml")) {
                    MyFileReader.convertCSVtoXML(location + tempFile, location + targetFileName);
                } else if (conTypes.get(i).toLowerCase().equals("csv")) {
                    System.out.println("convert xml to csv");
                    System.out.println(location + tempFile);
                    System.out.println(targetFileName);
                    MyFileReader.converXMLtoCSV(location + tempFile, location + targetFileName);
                }
            }
        } else {
            Files.copy(new File(location + tempFile), new File(location + targetFileName));
        }
        File file = new File(location + tempFile);
        file.delete();
        return null;
    }

    public static Boolean executeBooleanExp(List<String> row, String groupOP, String condition, String value) {
        Boolean res = false;
        Double target = null;
        List<Double> doubleRow = null;
        switch (groupOP) {
            case "max":
                doubleRow = row.stream().map(t -> Double.valueOf(t)).collect(Collectors.toList());
                target = Collections.max(doubleRow);
                break;
            case "min":
                doubleRow = row.stream().map(t -> Double.valueOf(t)).collect(Collectors.toList());
                target = Collections.min(doubleRow);
                break;
            case "avg":
                doubleRow = row.stream().map(t -> Double.valueOf(t)).collect(Collectors.toList());
                target = doubleRow.stream().mapToDouble(t -> t).average().getAsDouble();
                break;
            case "con":
                return row.stream().anyMatch(t -> t.contains(value));
            case "num":
                target = Double.valueOf(row.size());
                break;
        }

        switch (condition) {
            case "equal":
                return target.compareTo(Double.valueOf(value)) == 0;
            case "lessthan":
                return target.compareTo(Double.valueOf(value)) < 0;
            case "greaterthan":
                return target.compareTo(Double.valueOf(value)) > 0;
            case "lessorequal":
                return target.compareTo(Double.valueOf(value)) <= 0;
            case "greaterorequal":
                return target.compareTo(Double.valueOf(value)) >= 0;
            case "notequal":
                return target.compareTo(Double.valueOf(value)) != 0;
        }

        return res;
    }

    public Boolean executeBioFlowIFStatement(BioFlowIFStatement script, List<List<String>> inputs) {
        Boolean res = null;
        try {

            List<String> attributes = script.getAttributes();
            List<String> conditions = script.getConditions();
            List<String> groupOP = script.getGroupOP();
            List<String> logic = script.getLogic();
            List<String> values = script.getValues();
            List<String> headers = inputs.remove(0);
            //                System.out.println("*********************");
            //                headers.forEach(  t-> System.out.println(t));
            System.out.println(attributes);
            System.out.println(conditions);
            System.out.println(groupOP);
            System.out.println(logic);
            System.out.println(values);
            System.out.println(headers);

            int index = headers.indexOf(attributes.get(0));
            System.out.println(index);
            List<String> cols = inputs.stream()
                    .map(t -> t.get(index))
                    .collect(Collectors.toList());
            System.out.println(cols);
            res = executeBooleanExp(cols, groupOP.get(0), conditions.get(0), values.get(0));

            System.out.println(res);
            //we do step by step
            for (int i = 1; i < attributes.size(); i += 2) {
                Boolean first = null;
                int index1 = headers.indexOf(attributes.get(i + 1));
                cols = inputs.stream()
                        .map(t -> t.get(index1))
                        .collect(Collectors.toList());
                first = executeBooleanExp(cols, groupOP.get(i + 1), conditions.get(i + 1), values.get(i + 1));
                if (!logic.isEmpty() && logic.get(i).equals("and")) {
                    res &= first;
                } else {
                    res |= first;
                }
            }
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public String executeBioFlowCombineStatement(BioFlowCombineStatement script, String location, String outputFileName,
            List<String> leftKeys,
            List<String> rightKeys,
            List<String> leftHead,
            List<String> rightHead,
            InputGenerator inputGenerator
    ) {
        try {
            String file1 = script.getR();
            String file2 = script.getS();
            System.out.println(file1);
            System.out.println(file2);
            //make it to csv 
            if (MyUtils.getFileType(file1).equals("sql")) {
                MySQLHelper.downLoadaTableToLocal(file1, location, file1 + ".csv");
                file1 += ".csv";
            }
            if (MyUtils.getFileType(file2).equals("sql")) {
                MySQLHelper.downLoadaTableToLocal(file2, location, file2 + ".csv");
                file2 += ".csv";
            }
            String fileUrlR = location + file1;
            String fileUrlS = location + file2;
            //xml document
            if (MyUtils.getFileType(file1).equals("xml") && MyUtils.getFileType(file2).equals("xml")) {
                List<List<String>> leftContent = new ArrayList<>();
                List<List<String>> rightContent = new ArrayList<>();
                //left
                for (String h : leftHead) {
                    List<String> row = inputGenerator.readFromEXist(location, file1, h, new MyStatus());
                    leftContent.add(row);
                }
//        System.out.println("left");
                leftContent = MyUtils.transpose(leftContent);
                leftContent.add(0, leftHead);
//        System.out.println(leftContent);
                //right
                for (String h : rightHead) {
                    List<String> row = inputGenerator.readFromEXist(location, file2, h, new MyStatus());
                    rightContent.add(row);
                }
//        System.out.println("right");
                rightContent = MyUtils.transpose(rightContent);
                rightContent.add(0, rightHead);
//        System.out.println(rightContent);

                fileUrlR = file1 + MyUtils.randomAlphaNumeric();
                fileUrlS = file2 + MyUtils.randomAlphaNumeric();
                System.out.println(fileUrlR);
                System.out.println(fileUrlS);
                MyFileReader.writeFile(fileUrlR, leftContent, ",");
                MyFileReader.writeFile(fileUrlS, rightContent, ",");
            }
            //read header
            List<String> headerR = MyFileReader.readCSVHead(fileUrlR);
            List<String> headerS = MyFileReader.readCSVHead(fileUrlS);

            List<List<String>> contentR = MyFileReader.readCSVContent(fileUrlR);
            List<List<String>> contentS = MyFileReader.readCSVContent(fileUrlS);
            Map<String, String> mappedHeaders = new HashMap<>();
            for (int i = 0; i < leftKeys.size(); ++i) {
                mappedHeaders.put(leftKeys.get(i), rightKeys.get(i));
            }

            BiMap<String, String> mapHeader = HashBiMap.create();
            mapHeader.putAll(mappedHeaders);

            List<Integer> indexsR = leftKeys.stream()
                    .map(t -> headerR.indexOf(t))
                    .collect(Collectors.toList());
            List<Integer> indexsRLeft = IntStream.iterate(0, i -> i + 1)
                    .limit(headerR.size())
                    .boxed()
                    .collect(Collectors.toList());
            indexsRLeft.removeAll(indexsR);
            List<Integer> indexsS = leftKeys.stream()
                    .map(t -> headerS.indexOf(mapHeader.get(t)))
                    .collect(Collectors.toList());
            List<Integer> indexsSLeft = IntStream.iterate(0, i -> i + 1)
                    .limit(headerS.size())
                    .boxed()
                    .collect(Collectors.toList());
            indexsSLeft.removeAll(indexsS);
            List<List<String>> content = new ArrayList<>();
            List<List<String>> firstContent = new ArrayList<>();
            Map<Set<String>, Integer> matchedIndex = new HashMap<>();
            Map<Integer, List<String>> matchedIndexRight = new HashMap<>();
            Map<Integer, List<String>> matchedIndexLeft = new HashMap<>();
            List<String> firstHead = new ArrayList<>();
            List<String> secondHead = new ArrayList<>();
            List<String> headRef = new ArrayList<>();
            List<Integer> keyIndex = new ArrayList<>();
            List<Integer> keyIndexMatch = new ArrayList<>();
            if (contentR.size() > contentS.size()) {
                firstHead = headerR;
                keyIndex = indexsR;
                keyIndexMatch = indexsS;
                headRef = headerS;
                secondHead = indexsSLeft.stream().map(t -> headerS.get(t)).collect(Collectors.toList());
                firstContent = contentR;
                for (int i = 0; i < contentS.size(); ++i) {
                    List<String> row = contentS.get(i);
                    Set<String> values = indexsS.stream().map(t -> row.get(t)).collect(Collectors.toSet());
                    List<String> valuesRight = indexsS.stream().map(t -> row.get(t)).collect(Collectors.toList());
                    List<String> valuesLeft = indexsSLeft.stream().map(t -> row.get(t)).collect(Collectors.toList());
                    matchedIndex.put(values, i);
                    matchedIndexRight.put(i, valuesRight);
                    matchedIndexLeft.put(i, valuesLeft);
                }
            } else {
                firstHead = headerS;
                keyIndex = indexsS;
                keyIndexMatch = indexsR;
                headRef = headerR;
//                        System.out.println( StringUtils.join(indexsR, ", "));
                secondHead = indexsRLeft.stream().map(t -> headerR.get(t)).collect(Collectors.toList());
                firstContent = contentS;
                for (int i = 0; i < contentR.size(); ++i) {
                    List<String> row = contentR.get(i);
                    Set<String> values = indexsR.stream().map(t -> row.get(t)).collect(Collectors.toSet());
                    List<String> valuesRight = indexsR.stream().map(t -> row.get(t)).collect(Collectors.toList());
                    List<String> valuesLeft = indexsRLeft.stream().map(t -> row.get(t)).collect(Collectors.toList());
                    matchedIndex.put(values, i);
                    matchedIndexRight.put(i, valuesRight);
                    matchedIndexLeft.put(i, valuesLeft);
                }
            }
            firstHead.addAll(secondHead);
            content.add(firstHead);
//                matchedIndex.entrySet().forEach(  t -> System.out.println( StringUtils.join(t.getKey(), ", ") + " value " + t.getValue()) );

            for (List<String> valueTuple : firstContent) {
                List<String> row = new ArrayList<>();
                Set<String> values = keyIndex.stream().map(t -> valueTuple.get(t)).collect(Collectors.toSet());
                Integer index = matchedIndex.get(values);
                if (index == null) {
                    row.addAll(valueTuple);
                    List<String> emptyCells = new ArrayList<>();
                    for (int i = 0; i < secondHead.size(); ++i) {
                        emptyCells.add("");
                    }
                    row.addAll(emptyCells);
                } else {
                    try {
                        List<String> subRow = matchedIndexLeft.get(index);
                        matchedIndexLeft.remove(index);
                        row.addAll(valueTuple);
                        if (subRow != null) {
                            row.addAll(subRow);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                }
                content.add(row);
            }
            System.out.println("second for loop");
//                BiMap<Set<String>, Integer> mapRef = HashBiMap.create();
//                mapRef.putAll(matchedIndex);
            for (Map.Entry<Integer, List<String>> entry : matchedIndexLeft.entrySet()) {
                List<String> row = new ArrayList<>();
                List<String> emptyCells = new ArrayList<>();
                for (int i = 0; i < firstHead.size() - secondHead.size(); ++i) {
                    emptyCells.add("");
                }
                for (Integer i : keyIndex) {
                    List<String> values = matchedIndexRight.get(entry.getKey());
                    for (int j = 0; j < values.size(); ++j) {
                        emptyCells.remove(firstHead.indexOf(mapHeader.get(headRef.get(keyIndexMatch.get(j)))));
                        emptyCells.add(firstHead.indexOf(mapHeader.get(headRef.get(keyIndexMatch.get(j)))), values.get(j));
                    }
                }
                row.addAll(emptyCells);
                row.addAll(entry.getValue());
                content.add(row);
            }
            
            String outputFileURL = location + outputFileName;
            MyFileReader.writeFile(outputFileURL, content, ",");
            if (MyUtils.getFileType(file1).equals("xml")) {
                //remove tmp file
                List<String> newHeaders = MyFileReader.readCSVHead(outputFileURL);
                String key = inputGenerator.generateFinalFile(location, outputFileName, file2, rightKeys, newHeaders, content);

                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document newDoc = dBuilder.newDocument();

                    Document template = dBuilder.parse(new File(location + file2));
                    template.getDocumentElement().normalize();
                    List<String> path = new ArrayList<>(), res = new ArrayList<>();
                    Element root = template.getDocumentElement();
                    dfs(root, path, res, key);
                    res.add(0, root.getNodeName());
                    res.add(key);

                    //now generate the new documents
                    Element rootElement = newDoc.createElement(res.get(0));
                    newDoc.appendChild(rootElement);
                    List<String> headers = content.get(0);
                    for (int i = 1; i < content.size(); ++i) {
                        //append path
                        Element curPath = rootElement;
                        for (int j = 1; j < res.size(); ++j) {
                            Element cur = newDoc.createElement(res.get(j));
                            curPath.appendChild(cur);
                            curPath = cur;
                        }
                        //put everything in here
                        for (int j = 0; j < content.get(i).size(); ++j) {
                            Element cur = newDoc.createElement(headers.get(j));
                            cur.appendChild(newDoc.createTextNode(content.get(i).get(j)));
                            curPath.appendChild(cur);
                        }
                    }

                    //** End of CSV parsing**//
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(new File(outputFileURL));
                        TransformerFactory tranFactory = TransformerFactory.newInstance();
                        Transformer aTransformer = tranFactory.newTransformer();
                        aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                        aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                        aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                        aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                        Source src = new DOMSource(newDoc);
                        Result result = new StreamResult(writer);
                        aTransformer.transform(src, result);
                        writer.flush();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception e) {
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return outputFileURL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void dfs(Element root, List<String> path, List<String> res, String key) {
        if (root.getNodeName().equals(key)) {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println(path);
            res = new ArrayList<>(path);
            return;
        }
        NodeList nList = root.getChildNodes();
        for (int temp = 0; temp < nList.getLength() && path.isEmpty(); temp++) {
            org.w3c.dom.Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                path.add(nNode.getNodeName());
                Element eElement = (Element) nNode;
                dfs(eElement, path, res, key);
                path.remove(path.size() - 1);
            }
        }
    }

    public String executeBioFlowFusionStatement(BioFlowFusionStatement script, String location, String outputFileName,
            List<String> leftKeys,
            List<String> rightKeys,
            List<String> leftHead,
            List<String> rightHead,
            InputGenerator inputGenerator
    ) throws CodeException {

        String file1 = script.getR();
        String file2 = script.getS();
        System.out.println(file1);
        System.out.println(file2);
        //make it to csv 
        if (MyUtils.getFileType(file1).equals("sql")) {
            MySQLHelper.downLoadaTableToLocal(file1, location, file1 + ".csv");
            file1 += ".csv";
        }
        if (MyUtils.getFileType(file2).equals("sql")) {
            MySQLHelper.downLoadaTableToLocal(file2, location, file2 + ".csv");
            file2 += ".csv";
        }
        String fileUrlR = location + file1;
        String fileUrlS = location + file2;
        //xml document
        if (MyUtils.getFileType(file1).equals("xml") && MyUtils.getFileType(file2).equals("xml")) {
            List<List<String>> leftContent = new ArrayList<>();
            List<List<String>> rightContent = new ArrayList<>();
            //left
            for (String h : leftHead) {
                List<String> row = inputGenerator.readFromEXist(location, file1, h, new MyStatus());
                leftContent.add(row);
            }
//        System.out.println("left");
            leftContent = MyUtils.transpose(leftContent);
            leftContent.add(0, leftHead);
//        System.out.println(leftContent);
            //right
            for (String h : rightHead) {
                List<String> row = inputGenerator.readFromEXist(location, file2, h, new MyStatus());
                rightContent.add(row);
            }
//        System.out.println("right");
            rightContent = MyUtils.transpose(rightContent);
            rightContent.add(0, rightHead);
//        System.out.println(rightContent);

            fileUrlR = file1 + MyUtils.randomAlphaNumeric();
            fileUrlS = file2 + MyUtils.randomAlphaNumeric();
            System.out.println(fileUrlR);
            System.out.println(fileUrlS);
            MyFileReader.writeFile(fileUrlR, leftContent, ",");
            MyFileReader.writeFile(fileUrlS, rightContent, ",");
        }

        //read header
        List<String> headerR = MyFileReader.readCSVHead(fileUrlR);
        List<String> headerS = MyFileReader.readCSVHead(fileUrlS);

        List<List<String>> contentR = MyFileReader.readCSVContent(fileUrlR);
        List<List<String>> contentS = MyFileReader.readCSVContent(fileUrlS);

        Map<String, String> mappedHeaders = new HashMap<>();
        for (int i = 0; i < leftKeys.size(); ++i) {
            mappedHeaders.put(leftKeys.get(i), rightKeys.get(i));
        }

        BiMap<String, String> mapHeader = HashBiMap.create();
        mapHeader.putAll(mappedHeaders);

        System.out.println(contentS);
        List<Integer> indexsR = leftKeys.stream()
                .map(t -> headerR.indexOf(t))
                .collect(Collectors.toList());
        List<Integer> indexsRLeft = IntStream.iterate(0, i -> i + 1)
                .limit(headerR.size())
                .boxed()
                .collect(Collectors.toList());
        indexsRLeft.removeAll(indexsR);
        List<Integer> indexsS = rightKeys.stream()
                .map(t -> headerS.indexOf(t))
                .collect(Collectors.toList());
        List<Integer> indexsSLeft = IntStream.iterate(0, i -> i + 1)
                .limit(headerS.size())
                .boxed()
                .collect(Collectors.toList());
        indexsSLeft.removeAll(indexsS);

        List<List<String>> content = new ArrayList<>();
        List<List<String>> firstContent = new ArrayList<>();
        Map<List<String>, Integer> matchedIndex = new HashMap<>();
        Map<Integer, List<String>> matchedIndexLeft = new HashMap<>();
        List<String> firstHead = new ArrayList<>();
        List<String> secondHead = new ArrayList<>();
        List<Integer> keyIndex = new ArrayList<>();
        if (contentR.size() > contentS.size()) {
            firstHead = headerR;
            keyIndex = indexsR;
            secondHead = indexsSLeft.stream().map(t -> headerS.get(t)).collect(Collectors.toList());
            firstContent = contentR;
            for (int i = 0; i < contentS.size(); ++i) {
                List<String> row = contentS.get(i);
                List<String> values = indexsS.stream().map(t -> row.get(t)).collect(Collectors.toList());
                List<String> valuesLeft = indexsSLeft.stream().map(t -> row.get(t)).collect(Collectors.toList());
                matchedIndex.put(values, i);
                matchedIndexLeft.put(i, valuesLeft);
            }
        } else {
            firstHead = headerS;
            keyIndex = indexsS;
            secondHead = indexsRLeft.stream().map(t -> headerR.get(t)).collect(Collectors.toList());
            firstContent = contentS;
            for (int i = 0; i < contentR.size(); ++i) {
                List<String> row = contentR.get(i);
                List<String> values = indexsR.stream().map(t -> row.get(t)).collect(Collectors.toList());
                List<String> valuesLeft = indexsRLeft.stream().map(t -> row.get(t)).collect(Collectors.toList());
                matchedIndex.put(values, i);
                matchedIndexLeft.put(i, valuesLeft);
            }
        }
        firstHead.addAll(secondHead);
        content.add(firstHead);
        for (List<String> valueTuple : firstContent) {
            List<String> row = new ArrayList<>();
            List<String> values = keyIndex.stream().map(t -> valueTuple.get(t)).collect(Collectors.toList());
            Integer index = matchedIndex.get(values);
            if (index == null) {
                continue;
            }
            List<String> subRow = matchedIndexLeft.get(index);
            if (subRow == null) {
                continue;
            }
            row.addAll(valueTuple);
            row.addAll(subRow);
            content.add(row);
        }

        String outputFileURL = location + outputFileName;
        MyFileReader.writeFile(outputFileURL, content, ",");

        if (MyUtils.getFileType(file1).equals("xml")) {
            //remove tmp file
            List<String> newHeaders = MyFileReader.readCSVHead(outputFileURL);
            String key = inputGenerator.generateFinalFile(location, outputFileName, file2, rightKeys, newHeaders, content);

            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document newDoc = dBuilder.newDocument();

                Document template = dBuilder.parse(new File(location + file2));
                template.getDocumentElement().normalize();
                List<String> path = new ArrayList<>(), res = new ArrayList<>();
                Element root = template.getDocumentElement();
                dfs(root, path, res, key);
                res.add(0, root.getNodeName());
                res.add(key);

                //now generate the new documents
                Element rootElement = newDoc.createElement(res.get(0));
                newDoc.appendChild(rootElement);
                List<String> headers = content.get(0);
                for (int i = 1; i < content.size(); ++i) {
                    //append path
                    Element curPath = rootElement;
                    for (int j = 1; j < res.size(); ++j) {
                        Element cur = newDoc.createElement(res.get(j));
                        curPath.appendChild(cur);
                        curPath = cur;
                    }
                    //put everything in here
                    for (int j = 0; j < content.get(i).size(); ++j) {
                        Element cur = newDoc.createElement(headers.get(j));
                        cur.appendChild(newDoc.createTextNode(content.get(i).get(j)));
                        curPath.appendChild(cur);
                    }
                }

                //** End of CSV parsing**//
                FileWriter writer = null;
                try {
                    writer = new FileWriter(new File(outputFileURL));
                    TransformerFactory tranFactory = TransformerFactory.newInstance();
                    Transformer aTransformer = tranFactory.newTransformer();
                    aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                    aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    Source src = new DOMSource(newDoc);
                    Result result = new StreamResult(writer);
                    aTransformer.transform(src, result);
                    writer.flush();
                } catch (Exception exp) {
                    exp.printStackTrace();
                } finally {
                    try {
                        writer.close();
                    } catch (Exception e) {
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return outputFileURL;
    }

}
