/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class PythonHelper {

        static synchronized public String run(String location, String fileName, MyStatus status) throws CodeException {
                String res = "";
                String errorStr = "";
                 String fileURL = location + fileName;
                try {
//                        System.out.println("Python ********");
//                        System.out.println(fileURL);
                        //get permision
//                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
//                        per.waitFor();

                        ProcessBuilder pb = new ProcessBuilder(new String[]{
                                "python",
                                fileName
                        }
                        ).inheritIO();
                        
                        pb.directory(new File(location).getAbsoluteFile());
                        Process p = pb.start();
                        p.waitFor();
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        while (in.ready()) {
                                String line = in.readLine();
                                System.out.println(line);
                                res += line;
                                status.addMessage(line);
                        }
                        while (error.ready()) {
                                String line = error.readLine();
                                System.out.println(line);
                                status.addMessage(line);
                                errorStr += line;
                        }
                        in.close();
                        error.close();
                } catch (Exception e) {
                        status.addMessage(e.getMessage());
                        throw new CodeException(errorStr, e);
                }
                return res;
        }
        
          static synchronized public String runWithAgrument(String location, List<String> arguments, MyStatus status) throws CodeException {
                  
                String res = "";
                String errorStr = "";
                try {
                        System.out.println("Python ********");
                        System.out.println(arguments);
                        //get permision
//                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
//                        per.waitFor();
                        arguments.add(0, "python");
                        String[] myArray = arguments.toArray(new String[arguments.size()]);
                        ProcessBuilder pb = new ProcessBuilder(
                                myArray
                        ).inheritIO();
                        
                        pb.directory(new File(location).getAbsoluteFile());
                        Process p = pb.start();
                        p.waitFor();
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        while (in.ready()) {
                                String line = in.readLine();
                                System.out.println(line);
                                res += line;
                                status.addMessage(line);
                        }
                        while (error.ready()) {
                                String line = error.readLine();
                                System.out.println(line);
                                status.addMessage(line);
                                errorStr += line;
                        }
                        in.close();
                        error.close();
                } catch (Exception e) {
                        status.addMessage(e.getMessage());
                        throw new CodeException(errorStr, e);
                }
                return res;
                  
          }
        
//          static public String runWithAgrument(String location, String arguments, MyStatus status) throws CodeException {
//                String res = "";
//                String errorStr = "";
//                try {
//                        System.out.println("Python ********");
//                        System.out.println(arguments);
//                        //get permision
////                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
////                        per.waitFor();
//                        arguments = "python " + arguments;
//                        String[] myArray = arguments.toArray(new String[arguments.size()]);
//                        ProcessBuilder pb = new ProcessBuilder(
//                                myArray
//                        ).inheritIO();
//                        
//                        pb.directory(new File(location).getAbsoluteFile());
//                        Process p = pb.start();
//                        p.waitFor();
//                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                        while (in.ready()) {
//                                String line = in.readLine();
//                                System.out.println(line);
//                                res += line;
//                                status.addMessage(line);
//                        }
//                        while (error.ready()) {
//                                String line = error.readLine();
//                                System.out.println(line);
//                                status.addMessage(line);
//                                errorStr += line;
//                        }
//                        in.close();
//                        error.close();
//                } catch (Exception e) {
//                        status.addMessage(e.getMessage());
//                        throw new CodeException(errorStr, e);
//                }
//                return res;
//                  
//          }

}
