/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author jupiter
 */
public class RHelper {

        static public String run(String location, String fileName, MyStatus status) throws CodeException {
                String res = "";
                String errorStr = "";
                String fileURL = location + fileName;
                
                try {
                        System.out.println(fileURL);
                        //get permision
                        Process per = Runtime.getRuntime().exec("chmod 777 " + fileURL);
                        per.waitFor();
                        
                        //need call x64 version
                        Process p = Runtime.getRuntime().exec("C:\\\\Program Files\\\\R\\\\R-3.3.1\\\\bin\\\\x64\\\\Rscript " + location + fileName);
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

}
