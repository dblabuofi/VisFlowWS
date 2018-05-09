/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.mycompany.autointerfacews.bioflow.BioFlowCodeStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowExtractStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.bioflow.BioFlowTransformStatement;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.MyTransform;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.dataIcon.UpdateAttr;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.helper.PythonHelper;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author jupiter
 */
public class AdapterData implements Callable<String> {

    Node cur;
    MyStatus status;
    BioFlowService bioFlowService;
    EXist eXist;
    InputGenerator inputGenerator;

    public AdapterData(Node cur, MyStatus status, BioFlowService bioFlowService, EXist eXist, InputGenerator inputGenerator) {
        this.cur = cur;
        this.status = status;
        this.bioFlowService = bioFlowService;
        this.eXist = eXist;
        this.inputGenerator = inputGenerator;
    }

    @Override
    public String call() throws CodeException, IOException {
        try {
            System.out.println("adpter data");
            status.addMessage("Adapter Data");
            List<String> outputs = new ArrayList<>();
            if (cur.getActions() != null && cur.getActions().size() > 0) {
                for (Action action : cur.getActions()) {
                    if (action.getAct().equals("Resource")) {//need access remote web resources
//                        if (1 == 0
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
