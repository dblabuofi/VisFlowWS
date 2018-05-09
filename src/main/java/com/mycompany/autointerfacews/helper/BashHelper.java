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
public class BashHelper {
        
        static synchronized public String run(String location, String fileName, MyStatus status) throws CodeException {
                String res = "";
                String errorStr = "";
                String fileURL = location + fileName;
                try {
                        System.out.println(fileURL);
                        //get permision
                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
                        per.waitFor();
                        
                        //run
                        ProcessBuilder pb = new ProcessBuilder(new String[]{
                                "C:\\cygwin64\\bin\\bash.exe",
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
        
         static synchronized public String run(String location, String functionName, List<String> arguments, MyStatus status) throws CodeException {
                String res = "";
                String errorStr = "";
                String fileURL = location + functionName;
                try {
                        System.out.println(fileURL);
                        //get permision
                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
                        per.waitFor();
                        
                        //run
                        ProcessBuilder pb = new ProcessBuilder(arguments).inheritIO();
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
                                errorStr += line;
                                 status.addMessage(line);
                        }
                        in.close();
                        error.close();
                } catch (Exception e) {
                          status.addMessage(e.getMessage());
                        throw new CodeException(errorStr, e);
                }
                
                return res;
        }
         static synchronized public String run(String location, List<String> arguments) throws CodeException {
                String res = "";
                String errorStr = "";
                try {
                        //run
                        ProcessBuilder pb = new ProcessBuilder(arguments).inheritIO();
                        pb.directory(new File(location).getAbsoluteFile());

                        Process p = pb.start();
                        p.waitFor();
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        
                        while (in.ready()) {
                                String line = in.readLine();
                                System.out.println(line);
                                res += line;
                        }
                        
                        while (error.ready()) {
                                String line = error.readLine();
                                System.out.println(line);
                                errorStr += line;
                        }
                        in.close();
                        error.close();
                } catch (Exception e) {
                        throw new CodeException(errorStr, e);
                }
                
                return res;
        }
}
