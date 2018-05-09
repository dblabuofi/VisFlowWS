/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 * @author jupiter
 */
public class MySQLHelper {

    static synchronized public void run(String location, String outputFileName, String code) throws CodeException {
        runScript(location, code, new MyStatus());
        //we need copy the file to working directory
        String sqlLocation = "C:\\ProgramData\\MySQL\\MySQL Server 5.7\\Data\\flowqdb\\";
    }

    static synchronized public void run1(String[] args, String fileName) {
        try {
            System.out.println("mysql --user=root --password=hasan!xin flowqdb --default-character-set=utf8 -e \"source C:\\\\Users\\\\jupiter\\\\Documents\\\\NetBeansProjects\\\\AutoInterfaceWS\\\\data\\\\" + fileName + "\"");
            Process p = Runtime.getRuntime().exec("mysql --user=root --password=root flowqdb --default-character-set=utf8 -e \"source C:\\\\Users\\\\jupiter\\\\Documents\\\\NetBeansProjects\\\\AutoInterfaceWS\\\\data\\\\" + fileName + "\"");

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
//            p.waitFor();
            System.out.println("ok!");
            in.close();

        } catch (Exception e) {
            System.out.println("something wrong");
            e.printStackTrace();
        }
    }

    static public synchronized String runScript(String location, String code, MyStatus status) throws CodeException {
        String res = "";
        String errorStr = "";
        try {
            System.out.println(code);
            //run
            String hostname = "Unknown";
            InetAddress addr;
            String mysqlURL = "";
            try {
                addr = InetAddress.getLocalHost();
                hostname = addr.getHostName();
                mysqlURL = hostname.contains("CS-PREC3620HJ") ? "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql"
//                        : "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.7\\bin\\mysql";
                        : "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql";
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProcessBuilder pb = new ProcessBuilder(new String[]{
                mysqlURL,
                "--user=root",
                "--password=hasan!xin",
                "flowqdb",
                "--default-character-set=utf8",
                "-e",
                "\"" + code + "\""
            }
            ).inheritIO();
            pb.directory(new File(location).getAbsoluteFile());

            Process p = pb.start();
            p.waitFor();

            System.out.println("mysql --user=root --password=root flowqdb --default-character-set=utf8 -e \"" + code + "\"");
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
            e.printStackTrace();
            throw new CodeException("MySQLHelper function runscript falied: " + code, e);
        }
        return res;
    }

    static synchronized public String generateDownLoadaTable(String tableName, String location, String fileName) throws CodeException {
//        System.out.println("generateDownLoadaTable");
//        System.out.println(location);
        String res = "";
        String errorStr = "";
        String code = "select * from " + tableName;
//        String code = "select * from " + tableName;
//        code += " into outfile " + location + fileName;
//        code += " FIELDS ENCLOSED BY '\"' \n";
//        code += "TERMINATED BY ',' \n";
//        code += "ESCAPED BY '\"' \n";
//        code += "LINES TERMINATED BY '\\r\\n'";
        System.out.println(code);
        String hostname = "Unknown";
        InetAddress addr;
        String mysqlURL = "";
        try {
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            mysqlURL = hostname.contains("CS-PREC3620HJ") ? "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql"
                    : "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql";
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(mysqlURL);
        try {
            //run
            ProcessBuilder pb = new ProcessBuilder(new String[]{
                mysqlURL,
                "--user=root",
                "--password=hasan!xin",
                "--default-character-set=utf8",
                "-e",
                "\"" + code + "\"",
                "flowqdb"
            }
//                        ).inheritIO();
            ); 
            pb.directory(new File(location).getAbsoluteFile());
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            StringJoiner sj = new StringJoiner("\n");
            in.lines().iterator().forEachRemaining(sj::add);
            res = sj.toString();
//            while (in.ready()) { 
//                String line = in.readLine();
//                res += line + "\n";  
//            }

            p.waitFor();
            in.close();
            p.destroy();
        } catch (Exception e) {
            throw new CodeException("MySQLHelper function runscript falied: " + code, e);
        }
        return res;
    }

    static synchronized public String downLoadaTableToLocal(String tableName, String location, String fileName) throws CodeException {
        String res = generateDownLoadaTable(tableName, location, fileName);
        List<List<String>> contents = new ArrayList<>();
        for (String line : res.split("\n")) {
            System.out.println(line);
            contents.add(new ArrayList<>(Arrays.asList(line.split("\t"))));
        }
        MyFileReader.writeFile(location + fileName, contents, ",");

        return fileName;
    }

    //csv only 
    static synchronized public void uploadFileToSQL(String location, String fileName, String rootName, MyStatus status) throws CodeException {
        System.out.println("upload file to MySQL database: " + fileName);

        //change types
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        List<String> headers = MyFileReader.readCSVHead(location + fileName);
        System.out.println(headers);
        //create table
        String createScript = generateCreateTable(rootName, headers);
        System.out.println(createScript);
        runScript(location, createScript, status);
        //load files
        String uploadScript = generateLoadTable(rootName, location, fileName, headers);
        System.out.println(uploadScript);
        runScript(location, uploadScript, status);
    }

    static synchronized public void uploadFileToSQLXML(String location, String fileURL, String rootName, MyStatus status) throws CodeException {
        System.out.println("upload file to MySQL database xml: " + fileURL);

        //change types
        MyFileReader.converXMLtoCSV(location + fileURL, location + rootName);
        List<String> headers = MyFileReader.readCSVHead(location + fileURL);
        //create table
        String createScript = generateCreateTable(rootName, headers);
        System.out.println(createScript);
        runScript(location, createScript, status);
        //load files
//        File uploadFile = new File(location + "copyTemp");
//        try {
//            if (!uploadFile.exists()) {
//                uploadFile.createNewFile();
//            }
//            Files.copy(new File(location + rootName), uploadFile);
//            List<List<String>> conts = MyFileReader.readCSVContent(uploadFile.getAbsolutePath());
//            MyFileReader.writeFile(uploadFile.getAbsolutePath(), conts, ",");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String uploadScript = generateLoadTable(rootName, location, "copyTemp", headers);
        String uploadScript = generateLoadTable(rootName, location, fileURL, headers);
        System.out.println(uploadScript);
        runScript(location, uploadScript, status);
    }

    static synchronized String generateLoadTable(String tableName, String location, String fileName, List<String> attrs) {
        String res = "load data local infile '" + fileName + "' into table " + tableName;
        res += " fields terminated by ',' ";
        res += " ENCLOSED BY '\\\"' ";
        res += " lines terminated by '\\r\\n' ";
        res += " ignore 1 lines ";

//        res += "(";
//        for (int i = 0; i < attrs.size() - 1; ++i) {
//            res += "'" + attrs.get(i) + "',";
//        }
//        res += "'" + attrs.get(attrs.size() - 1);
//        res += "')";

        return res;
    }

    static synchronized String generateCreateTable(String tableName, List<String> attrs) {
        String res = "";
        res += "drop table if exists " + tableName + ";";
        res += "create table " + tableName + "(";
        
        int colSize = 65536 / attrs.size() / 5;
        colSize = Math.min(1000, colSize);
        
        for (int i = 0; i < attrs.size() - 1; ++i) {
            String attr = attrs.get(i);
            String type = attrs.get(i);

            res += "`" + attr + "` varchar(" + colSize + "),"; 

//                        switch (type) {
//                                case "string":
//                                        res += "varchar(1000), \n";
//                                        break;
//                                case "ingeter":
//                                        res += "int, \n";
//                                        break;
//                                case "double":
//                                        res += "double, \n";
//                                        break;
//                                default:
//                                        res += "varchar(1000) \n";
//                                        break;
//                        }
        }
        res += "`" + attrs.get(attrs.size() - 1) + "` varchar(" + colSize + ")";
        res += ");";

        return res;
    }

}
