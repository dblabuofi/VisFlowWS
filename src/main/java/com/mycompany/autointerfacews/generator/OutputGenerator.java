/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.generator;

import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class OutputGenerator {

    static public String generateAppendOutput(List<String> inputFiles, String outputFileURL) {

        String type = MyUtils.getFileType(outputFileURL);

        if (type.toLowerCase().equals("csv") || type.toLowerCase().equals("sql")) {
            List<List<String>> contentList = new ArrayList<>();

            contentList.addAll(MyFileReader.readCSV(inputFiles.get(0)));
            for (int i = 1; i < inputFiles.size(); ++i) {
                contentList.addAll(MyFileReader.readCSVContent(inputFiles.get(i)));
            }

            MyFileReader.writeFile(outputFileURL, contentList, ",");

        } else if (type.toLowerCase().equals("xml")) {
            String content = "";
            if (inputFiles.size() == 1) {
                content = MyFileReader.readFileAll(inputFiles.get(0));

            } else {
                content = "<AppendResultSet>";
                for (String fileURL : inputFiles) {
                    String tent = MyFileReader.readFileAll(fileURL);
                    if (tent.startsWith("<?xml version")) {
                        int index = tent.indexOf(">");
                        tent = tent.substring(index + 1);
                        //                                           tent = tent.replaceAll("<!DOCTYPE[^>]*>\n", "");
                        tent = tent.replaceAll("<!DOCTYPE[^>]*>(<.*>)?[^>]*>", "");
                    }
                    content += tent;
                }
                content += "</AppendResultSet>";
            }

            MyFileReader.writeFile(outputFileURL, content);
        } else {
            String content = "";
            for (String fileURL : inputFiles) {
                content += MyFileReader.readFileAll(fileURL);
            }

            MyFileReader.writeFile(outputFileURL, content);
        }

        return outputFileURL;
    }

    static public String generateAppendCSVOutput(List<String> inputFiles, String outputFileURL) {

        List<List<String>> contentList = new ArrayList<>();

        contentList.addAll(MyFileReader.readCSV(inputFiles.get(0)));
        for (int i = 1; i < inputFiles.size(); ++i) {
            contentList.addAll(MyFileReader.readCSVContent(inputFiles.get(i)));
        }

        MyFileReader.writeFile(outputFileURL, contentList, ",");

        return outputFileURL;
    }
}
