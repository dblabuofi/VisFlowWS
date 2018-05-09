/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.wrapper.MyWrapperFunctions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class MyCallFunction {
        
        /*
                return list of file names
         */
        static public List<String> callWebService(String fileName, Action action, String location, EXist eXist, MyHttpClient myHttpClient) {
                System.out.println("callWebService ");
                List<String> outputs = new ArrayList<>();

                for (Function webservice : action.getWebservices()) {
                        if (webservice.getMethod().equals("GET")) {
                                        //get requeredinputs
                                        List<MyAttribute> attributes = webservice.getAttributes()
                                                .stream()
                                                .filter(t -> t.getShown() == true)
                                                .collect(Collectors.toList());

                                        //get inputs using wrapper        
//                                        List<List<String>> inputs = MyWrapperFunctions.functionWrapper(fileName, fileName.substring(fileName.lastIndexOf("\\") + 1), attributes, eXist);

                                        //submit to webservice
                                        String outFileUrl = location + action.getOutputFileNames().get(0);
//                                        myHttpClient.get(inputs, webservice, outFileUrl);
                                        outputs.add(outFileUrl);
                        }
                }
                return outputs;
        }
        
         static public List<String> callWebServiceIamge(String fileName, Action action, String location, EXist eXist, MyHttpClient myHttpClient) {
                System.out.println("callWebService Image");
                List<String> outputs = new ArrayList<>();

                for (Function webservice : action.getWebservices()) {
                        if (webservice.getMethod().equals("GET")) {
                                        //get requeredinputs
                                        List<MyAttribute> attributes = webservice.getAttributes()
                                                .stream()
                                                .filter(t -> t.getShown() == true)
                                                .collect(Collectors.toList());

                                        //get inputs using wrapper        
//                                        List<List<String>> inputs = MyWrapperFunctions.functionWrapper(fileName, fileName.substring(fileName.lastIndexOf("\\") + 1), attributes, eXist);

                                        //submit to webservice
                                        String outFileUrl = location + action.getOutputFileNames().get(0);
//                                        myHttpClient.getImage(inputs, webservice, outFileUrl);
                                        outputs.add(outFileUrl);
                        }
                }
                return outputs;
        }
}
