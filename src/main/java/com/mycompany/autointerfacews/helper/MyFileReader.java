/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.Library;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyWrapper;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.autointerfacews.mydata.RegisterAttribute;
import com.mycompany.autointerfacews.utils.MyUtils;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author jupiter
 */
public class MyFileReader {

    static private Gson gson = new GsonBuilder().create();

    static public List<String> rowReader(String csvFile) {
        List<String> res = new ArrayList<>();
        String line;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                res.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    static public String rowwriter(String fileurl, List<String> words) {
        try {
            PrintWriter out = new PrintWriter(fileurl) {
                @Override
                public void println(String x) {
                    print(x + "\n");
                }
            };
            System.out.println(fileurl);
            for (String line : words) {
                out.println(line);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileurl;
    }

    static public List<List<String>> readCSV(String csvFile) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<List<String>> res = null;
        res = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\"")) {//quoates
                    List<String> allMatches = new ArrayList<String>();
                    Matcher m = Pattern.compile("\"[^\"]*\"")
                            .matcher(line);
                    while (m.find()) {
                        allMatches.add(m.group().replace("\"", ""));
                    }
                    List<String> row = allMatches;
                    res.add(row);
                    if (row.size() < res.get(0).size()) {
                        for (int i = 0; i < res.get(0).size() - row.size(); ++i) {
                            row.add("");
                        }
                    }
                } else {
                    // use comma as separator
                    String[] cells = line.split(cvsSplitBy);
                    List<String> row = new ArrayList<>(Arrays.asList(cells));
                    res.add(row);
                    if (row.size() < res.get(0).size()) {
                        for (int i = 0; i < res.get(0).size() - row.size(); ++i) {
                            row.add("");
                        }
                    }
                    //                                System.out.println(line);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    static public List<List<String>> readCSVContent(String csvFile) {
        List<List<String>> res = readCSV(csvFile);
        res.remove(0);
        return res;
    }

    static public List<String> readCSVHead(String csvFile) {
        List<List<String>> res = readCSV(csvFile);
        return res.get(0);
    }

    static String convertType(String value, String type) {
        String newValue = "";
        switch (type) {
            case "String":
                newValue = String.valueOf(value);
                break;
            case "Integer":
                newValue = String.valueOf(Integer.valueOf(value));
                break;
            case "Double":
                newValue = String.valueOf(Double.valueOf(value));
                break;
            case "Boolean":
                newValue = String.valueOf(Boolean.valueOf(value));
                break;
        }
        return newValue;
    }

    static public int convertCSVAttribute(String fromFileURL, String toFileURL, String attribute, String type) {
        List<List<String>> contents = readCSV(fromFileURL);
        List<String> header = contents.remove(0);
        int index = header.indexOf(attribute);
        for (List<String> row : contents) {
            String value = row.get(index);
            String newValue = convertType(value, type);
            row.remove(index);
            row.add(index, newValue);
        }
        contents.add(0, header);
        writeFile(toFileURL, contents, ",");

        return 0;
    }

    static public List<List<String>> readXML(String csvFile, List<String> colNames, String nodeName) {
        List<List<String>> res = new ArrayList<>();
        try {

            File fXmlFile = new File(csvFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(nodeName);

            for (int temp = 0; temp < nList.getLength(); temp++) {
                List<String> row = new ArrayList<>();
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    for (int i = 0; i < colNames.size(); ++i) {
                        row.add(eElement.getElementsByTagName(colNames.get(i)).item(0).getTextContent());
                    }
                }
                res.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static public List<List<String>> readXML(String csvFile) {
        List<List<String>> res = new ArrayList<>();
        try {
            File fXmlFile = new File(csvFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            Node node = doc.getChildNodes().item(0);

            Map<String, List<String>> map = new HashMap<>();
            generateMappingFromXML(node, map);
//                        System.out.println(map.size());

            List<String> headers = new ArrayList<>(map.keySet());
            List<List<String>> content = map.entrySet()
                    .stream()
                    .map(t -> t.getValue())
                    .collect(Collectors.toList());

            content = MyUtils.transpose(content);

            res.add(0, headers);
            res.addAll(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static public List<List<String>> readXML2(String csvFile) {
        List<List<String>> res = new ArrayList<>();
        List<String> row = new ArrayList<>();
        try {
            String str = MyFileReader.readFileAll(csvFile);
            row.add(str);
            res.add(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    //we assume the last layer is flat  

    static public List<String> readXMLHeaders(String csvFile) {
        List<List<String>> res = readXML(csvFile);
        return res.get(0);
    }

    static public List<List<String>> readXMLContent(String csvFile) {
        List<List<String>> res = readXML(csvFile);
        res.remove(0);
        return res;
    }

    //we assume the last layer is flat  
    static public int convertXMLAttribute(String fromFileURL, String toFileURL, String attribute, String type) {
        List<List<String>> res = new ArrayList<>();
        try {
            File fXmlFile = new File(fromFileURL);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nNodes = doc.getElementsByTagName(attribute);

            for (int i = 0; i < nNodes.getLength(); ++i) {
                Node node = nNodes.item(i);
                //update textnode
                String value = node.getFirstChild().getTextContent().trim();
                String newValue = convertType(value, type);
                node.getFirstChild().setTextContent(newValue);
            }

            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(toFileURL));
                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                Source src = new DOMSource(doc);
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

        return 0;
    }

    static public void generateMappingFromXML(Node cur, Map<String, List<String>> map) {
        if (cur.getChildNodes().getLength() == 0
                //                        && cur.getNodeType() == Node.ELEMENT_NODE 
                && !cur.getTextContent().trim().isEmpty()) {
            String nodeName = cur.getParentNode().getNodeName();
//                        String nodeValue = "\"" + cur.getTextContent().trim() + "\"";
            String nodeValue = cur.getTextContent().trim();
//                        System.out.println(cur.getNodeType() + ";" + nodeName + ";" + nodeValue + " ; ");

            if (map.containsKey(nodeName)) {
                List<String> row = map.get(nodeName);
                row.add(nodeValue);
            } else {
                List<String> row = new ArrayList<>();
                row.add(nodeValue);
                map.put(nodeName, row);
            }
            return;
        }

        for (int i = 0; i < cur.getChildNodes().getLength(); ++i) {
            Node next = cur.getChildNodes().item(i);
            generateMappingFromXML(next, map);
        }

    }

    //we assume the last layer is flat  
    static public int converXMLtoCSV(String inputFileURL, String outputFileURL) {
        List<List<String>> res = new ArrayList<>();
        try {

            File fXmlFile = new File(inputFileURL);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            Node node = doc.getChildNodes().item(0);

            Map<String, List<String>> map = new HashMap<>();
            generateMappingFromXML(node, map);
            System.out.println(map.size());

            List<String> headers = new ArrayList<>(map.keySet());
            List<List<String>> content = map.entrySet()
                    .stream()
                    .map(t -> t.getValue())
                    .collect(Collectors.toList());

//            System.out.println("*************************");
//            System.out.println(content);
//            System.out.println(content.get(0).size());
//            System.out.println(content.get(1).size());
//                        int minSize = content.get(0).size();
//                        for (List<String> val : content) {
//                            minSize = Math.min(minSize, val.size());
//                        }
//                        List<List<String>> changed = new ArrayList<>();
//                        
//                        for (List<String> val : content) {
//                            if (val.size() == minSize) {
//                                changed.add(val);
//                            } else {
//                                int size = val.size() / minSize;
//                                List<String>[] con = new List[size];
//                                for (int i = 0; i < size; ++i) {
//                                    con[i] = new ArrayList<>();
//                                }
//                                int i = 0;
//                                while (i < val.size()) {
//                                    for (int j = 0; j < size; ++j) {
//                                        con[j].add(val.get(i++));
//                                    }
//                                }
//                                for (int k = 0; k < size; ++k) {
//                                    changed.add(con[k]);
//                                }
//                            }
//                        }
//                        content = changed;
            content = MyUtils.transpose(content);

            content.add(0, headers);
            writeFile(outputFileURL, content, ",");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    static public int convertCSVtoXML(String csvFileURL, String xmlFIleURL) {

        int rowsCount = -1;
        BufferedReader csvReader;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("resultsets");
            newDoc.appendChild(rootElement);
            // Read csv file
            //** Now using the OpenCSV **//
            CSVReader reader = new CSVReader(new FileReader(csvFileURL), ',');
            //CSVReader reader = new CSVReader(csvReader);
            String[] nextLine;
            int line = 0;
            List<String> headers = new ArrayList<>();
//                        System.out.println("while");
            while ((nextLine = reader.readNext()) != null) {
                if (line == 0) { // Header row
                    for (String col : nextLine) {
                        headers.add(col.replace("\"", ""));
                    }
                } else {
                    Element rowElement = newDoc.createElement("row");
                    rootElement.appendChild(rowElement);
                    int col = 0;
                    for (String value : nextLine) {
                        String header = headers.get(col);
                        Element curElement = newDoc.createElement(header);
                        curElement.appendChild(newDoc.createTextNode(value.replace("\"", "").trim()));
                        rowElement.appendChild(curElement);
                        col++;
                    }
                }
                line++;
            }
//                        System.out.println("writing");
            rowsCount = line;
            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(xmlFIleURL));
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
            // Output to console for testing
            // Resultt result = new StreamResult(System.out);
        } catch (IOException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    static public int convertCSVtoXML(String csvFileName, String xmlFileName, String rootName, String delimiter) {

        int rowsCount = -1;
        BufferedReader csvReader;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement(rootName.trim());
            newDoc.appendChild(rootElement);
            // Read csv file
            //** Now using the OpenCSV **//
            CSVReader reader = new CSVReader(new FileReader(csvFileName), delimiter.charAt(0));
            //CSVReader reader = new CSVReader(csvReader);
            String[] nextLine;
            int line = 0;
            List<String> headers = new ArrayList<>();
//                        System.out.println("while");
            while ((nextLine = reader.readNext()) != null) {
                if (line == 0) { // Header row
                    for (String col : nextLine) {
                        headers.add(col);
                    }
                } else {
//                                        Element rowElement = newDoc.createElement("row");
//                                        rootElement.appendChild(rowElement); 
                    int col = 0;
                    for (String value : nextLine) {
                        String header = headers.get(col++);
                        Element curElement = newDoc.createElement(header.trim());
                        curElement.appendChild(newDoc.createTextNode(value.trim()));
                        rootElement.appendChild(curElement);
                    }
                }
                line++;
            }
//                        System.out.println("writing");
            rowsCount = line;
            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(xmlFileName));
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
            // Output to console for testing
            // Resultt result = new StreamResult(System.out);
        } catch (IOException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    static public int convertCSVtoXML(String csvFileName, String xmlFileName, String rootName, String delimiter, List<Integer> selectedAttrsIndex) {

        int rowsCount = -1;
        BufferedReader csvReader;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement(rootName);
            newDoc.appendChild(rootElement);
            // Read csv file
            //** Now using the OpenCSV **//
            CSVReader reader = new CSVReader(new FileReader(csvFileName), delimiter.charAt(0));
            //CSVReader reader = new CSVReader(csvReader);
            String[] nextLine;
            int line = 0;
            List<String> headers = new ArrayList<String>(1000);
//                        System.out.println("while");
            while ((nextLine = reader.readNext()) != null) {
                if (line == 0) { // Header row
                    for (String col : nextLine) {
                        headers.add(col);
                    }
                } else {
                    Element rowElement = newDoc.createElement("row");
                    rootElement.appendChild(rowElement);
                    int col = 0;
                    for (String value : nextLine) {
                        if (selectedAttrsIndex.contains(col)) {
                            String header = headers.get(col);
                            Element curElement = newDoc.createElement(header);
                            curElement.appendChild(newDoc.createTextNode(value.trim()));
                            rowElement.appendChild(curElement);
                        }
                        col++;
                    }
                }
                line++;
            }
//                        System.out.println("writing");
            rowsCount = line;
            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(xmlFileName));
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
            // Output to console for testing
            // Resultt result = new StreamResult(System.out);
        } catch (IOException exp) {
            System.out.println(exp.toString());
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    static public String generateXML(String id, List<String> headers, List<String> inputs) {
        String res = "";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement(id);
            newDoc.appendChild(rootElement);
            // Read csv file
//            csvReader = new BufferedReader(new FileReader(csvFileName));
            String[] nextLine;
            int line = 0;
            for (String input : inputs) {
                Element rowElement = newDoc.createElement("row");
                rootElement.appendChild(rowElement);
                int col = 0;
                String[] values = input.split("\t");
                for (String value : values) {
                    String header = headers.get(col);
                    Element curElement = newDoc.createElement(header);
                    curElement.appendChild(newDoc.createTextNode(value.trim()));
                    rowElement.appendChild(curElement);
                    col++;
                }
            }
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(newDoc), new StreamResult(sw));
            res = sw.toString();
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }

        return res;
    }

    static public String generateFile(String location, String filename, String code) {
        try {
//                        System.out.println(code);
            PrintWriter out = new PrintWriter(location + filename) {
                @Override
                public void println(String x) {
                    print(x + "\n");
                }
            };
            out.println(code);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location + filename;
    }

    static public String fileToString(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static public List<List<String>> readFile(String fileName, String separator) {
        List<List<String>> res = new ArrayList<>();
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith("\n")) {
                    line = line.substring(0, line.length() - 1);//remove newline
                }
                List<String> words = new ArrayList(Arrays.asList(line.split(separator)));
                if (words.get(0).startsWith("\"") && words.get(0).endsWith("\"")) {
                    words = words.stream().map(t -> t.replace("\"", "")).collect(Collectors.toList());
                }
                res.add(words);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static public List<String> readFileByLine(String fileName) {
        List<String> res = new ArrayList<>();
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                res.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static public String writeFile(String location, String filename, List<List<String>> words, String separator) {
        try {
            PrintWriter out = new PrintWriter(location + filename) {
                @Override
                public void println(String x) {
                    print(x + "\n");
                }
            };
            System.out.println(location + filename);
            for (List<String> wordsLine : words) {
                String line = StringUtils.join(wordsLine, separator);
//                                System.out.println(line);
                out.println(line);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location + filename;
    }

    static public String writeFile(String fileUrl, List<List<String>> words, String separator) {
        try (PrintWriter out = new PrintWriter(fileUrl)) {
            for (List<String> wordsLine : words) {
                if (wordsLine.get(0).startsWith("\"")) {
                    String line = StringUtils.join(wordsLine, separator);
                    out.println(line);
                } else {

                    String line = StringUtils.join(wordsLine, "\"" + separator + "\"");
                    out.println("\"" + line + "\"");
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    static public String writeFile(String fileUrl, String content) {
        try (PrintWriter out = new PrintWriter(fileUrl)) {
            out.print(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return fileUrl;
    }

    static public String writeFileUTF8(String fileUrl, String content) {
        try {
//                        File file = new File(fileUrl);
//                        file.createNewFile(); 
            File file = new File(fileUrl);
            file.createNewFile();
            FileOutputStream fop = new FileOutputStream(file);

            fop.write(content.getBytes("UTF-8"));
            fop.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    static public String writeFileAppend(String fileUrl, String content) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(fileUrl), true))) {
            out.print(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    static public String readFileAll(String fileUrl) {
        String res = "";
        String lineSeparator = System.getProperty("line.separator");
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileUrl)))) {
            String line;
            while ((line = br.readLine()) != null) {
                res += line + lineSeparator;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static public Library xmlToLibrary(String fileUrl) {
        String fileStr = readFileAll(fileUrl);
        JSONObject jsonObj = XML.toJSONObject(fileStr).getJSONObject("library");
        //need check if it is array or object, if it is object, need change it to array
        //This is a well known problem from xml to json
        try {
            jsonObj.getJSONArray("function");
        } catch (Exception e) {
            //it's not array we are not good
//                        System.out.println("change array");
            JSONObject obj = jsonObj.getJSONObject("function");
            JSONArray arr = new JSONArray();
            arr.put(obj);
            jsonObj.put("function", arr);
        }
//                System.out.println(jsonObj.toString());
        Library res = gson.fromJson(jsonObj.toString(), Library.class);
        return res;
    }

    static public Function xmlToFunction(String fileUrl) {
        String fileStr = readFileAll(fileUrl);
        JSONObject jsonObj = XML.toJSONObject(fileStr).getJSONObject("function");

        //need check if it is array or object, if it is object, need change it to array
        //This is a well known problem from xml to json
        try {
            jsonObj.getJSONArray("inputAttributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("inputAttributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("inputAttributes", arr);
            } catch (Exception e1) {
            }
        }
        try {
            jsonObj.getJSONArray("outAttributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("outAttributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("outAttributes", arr);
            } catch (Exception e1) {
            }
        }

        Function res = gson.fromJson(jsonObj.toString(), Function.class);
        return res;
    }

    static public MyResource xmlToResource(String fileUrl) {
        String fileStr = readFileAll(fileUrl);
        JSONObject jsonObj = XML.toJSONObject(fileStr).getJSONObject("resource");
        //lets deal with attributes
        try {
            jsonObj.getJSONArray("attributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("attributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("attributes", arr);
            } catch (Exception e1) {
            }
        }

        try {
            jsonObj.getJSONArray("outAttributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("outAttributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("outAttributes", arr);
            } catch (Exception e1) {
            }
        }

        try {
            jsonObj.getJSONArray("attrs");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("attrs");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("attrs", arr);
            } catch (Exception e1) {
            }
        }

        MyResource res = gson.fromJson(jsonObj.toString(), MyResource.class);
        return res;
    }

    static public String getXMLFileValues(String fileUrl) {
        String fileStr = readFileAll(fileUrl);
        JSONObject jsonObj = XML.toJSONObject(fileStr).getJSONObject("function");

        //need check if it is array or object, if it is object, need change it to array
        //This is a well known problem from xml to json
        try {
            jsonObj.getJSONArray("inputAttributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("inputAttributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("inputAttributes", arr);
            } catch (Exception e1) {
            }
        }
        try {
            jsonObj.getJSONArray("outAttributes");
        } catch (Exception e) {
            //it's not array we are not good
            try {
                JSONObject obj = jsonObj.getJSONObject("outAttributes");
                JSONArray arr = new JSONArray();
                arr.put(obj);
                jsonObj.put("outAttributes", arr);
            } catch (Exception e1) {
            }
        }

        Function function = gson.fromJson(jsonObj.toString(), Function.class);

        String res = function.toString();

        return res;
    }

    static public List<String> getXMLFileURLs(File file) {
        List<String> filesUrls = new ArrayList<>();
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                filesUrls.addAll(getXMLFileURLs(f));
            }
        } else {
            String filename = file.getName().toLowerCase();
            if (filename.endsWith(".xml")) {
                filesUrls.add(file.getAbsolutePath());
            }
        }
        return filesUrls;
    }

    static public List<Library> getLibraries(String location) {
        List<Library> libraries = new ArrayList<>();
        List<String> libraryURLs = getXMLFileURLs(new File(location));

        for (String file : libraryURLs) {
            libraries.add(xmlToLibrary(file));
        }

        return libraries;
    }

    static public List<Function> getFunctions(String location) {
        List<Function> functions = new ArrayList<>();
        List<String> functionURLs = getXMLFileURLs(new File(location));

        for (String file : functionURLs) {
            functions.add(xmlToFunction(file));
        }

        return functions;
    }

    static public List<MyResource> getResources(String location) {
        List<MyResource> resources = new ArrayList<>();
        List<String> functionURLs = getXMLFileURLs(new File(location));

        for (String file : functionURLs) {
//                        System.out.println("file: " + file);
            resources.add(xmlToResource(file));
        }

        return resources;
    }

    static public void inputStreamToFile(InputStream inputStream, String fileURL) {
        OutputStream outputStream = null;
        try {
            // write the inputStream to a FileOutputStream
            outputStream
                    = new FileOutputStream(new File(fileURL));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    //root is dummy 

    static public INode mapResourceSchemaToINode(INode root, String schemaUrl) {
//                System.out.println(schemaUrl);
        try {
            JSONObject schema = XML.toJSONObject(schemaUrl);
            Queue<INode> queueNode = new LinkedList<>();
            Queue<JSONObject> queueObject = new LinkedList<>();

            queueObject.add(schema);
            queueNode.add(root);

            while (!queueObject.isEmpty()) {
                JSONObject cur = queueObject.remove();
                INode curNode = queueNode.remove();
                Iterator iter = cur.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    INode nextNode = curNode.createChild(key);
//                                          System.out.println(key);
                    try {
                        queueObject.add(cur.getJSONObject(key));
                        queueNode.add(nextNode);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        INode res = root.getChildren().get(0);
        res.setParent(null);
        return res;
    }

    static public void mapXMLToINodeHelper(INode curNode, Node node) {
        try {
            for (Node object = node.getFirstChild(); object != null; object = object.getNextSibling()) {
//                           Node item = objects.getFirstChild();
                if (object instanceof Element) {
                    Element e = (Element) object;
                    INode tmp = curNode.createChild(e.getTagName());
                    mapXMLToINodeHelper(tmp, object);
                }
            }
        } catch (Exception e) {
        }
    }
    //root is dummy node

    static public QueryTree mapResourceSchemaToQueryTree(QueryTree root, String schemaUrl) {
        try {
            JSONObject schema = XML.toJSONObject(schemaUrl);
            Queue<QueryTree> queueNode = new LinkedList<>();
            Queue<QueryTree> queueNode1 = new LinkedList<>();
            Queue<JSONObject> queueObject = new LinkedList<>();

            //get root
//                          root.setText((String)schema.keys().next());
            queueObject.add(schema);
            queueNode.add(root);

            while (!queueObject.isEmpty()) {
                JSONObject cur = queueObject.remove();
                QueryTree curNode = queueNode.remove();
                Iterator iter = cur.keys();
                queueNode1.clear();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    QueryTree nextNode = new QueryTree(key);
//                                          System.out.println(key);
                    queueNode1.add(nextNode);
                    try {//if it is node
                        queueObject.add(cur.getJSONObject(key));
                        queueNode.add(nextNode);
                    } catch (Exception e) {//leaf

                    }
                }
                curNode.setChildren(new ArrayList<>(queueNode1));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root.getChildren().isEmpty() ? null : root.getChildren().get(0);
    }

    static public void converJSONtoXML(String jsonFileURL, String xmlFileURL) {
        String content = readFileAll(jsonFileURL);
        System.out.println(content);
        String xmlContent = "";
        try {
            JSONObject json = new JSONObject(content);
            xmlContent = XML.toString(json);
        } catch (Exception e) {
            xmlContent = "<ConverJSONError></ConverJSONError>";
        }
        writeFile(xmlFileURL, xmlContent);
    }

    static public void converJSONtoTable(String jsonFileURL, String tableFileURL, String schema, List<String> heads) {
        String content = readFileAll(jsonFileURL);
        System.out.println(content);
        Map<String, List<String>> map = new HashMap<>();
        for (String head : heads) {
            map.put(head, new ArrayList<>());
        }

        String xmlContent = "";
        try {
            schema = schema.replaceAll("[\r\n\t]", "");
            JSONObject json = new JSONObject(content);
            xmlContent = XML.toString(json);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documentMatch = builder.parse(new InputSource(new StringReader(schema)));
            Document document = null;
            try {
                document = builder.parse(new InputSource(new StringReader(xmlContent)));
            } catch (Exception e) {
                xmlContent = "<root>" + xmlContent + "</root>";
                document = builder.parse(new InputSource(new StringReader(xmlContent)));
            }
            System.out.println("******json to table*****");
            System.out.println(xmlContent);
            System.out.println(schema);

            Queue<Node> q = new LinkedList<>();
            Queue<Node> qMatch = new LinkedList<>();
            q.add(document.getFirstChild());
            qMatch.add(documentMatch.getFirstChild());
            while (!q.isEmpty() && !qMatch.isEmpty()) {
                Node c = q.remove();
                Node cMatch = qMatch.remove();
                if (c == null || cMatch == null) {
                    continue;
                }
                NodeList nList = c.getChildNodes();
                NodeList nMatchList = cMatch.getChildNodes();
                for (int i = 0; i < nList.getLength(); i++) {
                    Node cNode = nList.item(i);
                    if (cNode.getNodeType() == Node.TEXT_NODE) {
                        System.out.println(cNode.getTextContent() + "$ $" + cMatch.getTextContent());
                    } else {
                        System.out.println(cNode.getNodeName() + "# #" + cMatch.getNodeName());
                    }
                    Node cMatchNode = nMatchList.item(i);
                    if (cNode.getNodeType() == Node.TEXT_NODE) {
//                                    System.out.println(cNode.getTextContent() + " " + cMatchNode.getTextContent());
                        if (map.containsKey(cMatchNode.getTextContent())) {
                            List<String> val = map.get(cMatchNode.getTextContent());
                            val.add(cNode.getTextContent());
                            map.put(cMatchNode.getTextContent(), val);
                        }
                    }
                    q.add(cNode);
                    qMatch.add(cMatchNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            xmlContent = "";
        }
        List<List<String>> newContent = map.entrySet().stream().map(t -> {
            List<String> con = t.getValue();
            con.add(0, t.getKey());
            return con;
        }).collect(Collectors.toList());
        System.out.println(newContent);
        newContent = MyUtils.transpose(newContent);

        writeFile(tableFileURL, newContent, ",");
    }

    static public void saveResource(String resourceType, String organization, String resourceName,
            String description, String URL, QueryTree schemaTree, List<RegisterAttribute> attributes,
            String methodReturnFileType, String urlReturnFileType,
            String wrapper, List<MyAttribute> headers,
            String fileURL, String location, String aggregateName) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("resource");
            newDoc.appendChild(rootElement);
            Element idElement = newDoc.createElement("id");
            rootElement.appendChild(idElement);
            UUID uid = UUID.randomUUID();
            idElement.appendChild(newDoc.createTextNode(uid.toString()));

            Element resourceTypeElement = newDoc.createElement("resourceType");
            rootElement.appendChild(resourceTypeElement);
            resourceTypeElement.appendChild(newDoc.createTextNode(resourceType));

            Element orgElement = newDoc.createElement("organization");
            rootElement.appendChild(orgElement);
            orgElement.appendChild(newDoc.createTextNode(organization));

            Element fileNameElement = newDoc.createElement("fileName");
            rootElement.appendChild(fileNameElement);
            fileNameElement.appendChild(newDoc.createTextNode(resourceName));

            Element resourceNameElement = newDoc.createElement("resourceName");
            rootElement.appendChild(resourceNameElement);
            resourceNameElement.appendChild(newDoc.createTextNode(resourceName));

            Element descrptionElement = newDoc.createElement("description");
            rootElement.appendChild(descrptionElement);
            descrptionElement.appendChild(newDoc.createTextNode(description));

            Element aggNameElement = newDoc.createElement("aggregateName");
            rootElement.appendChild(aggNameElement);
            aggNameElement.appendChild(newDoc.createTextNode(aggregateName));

            Element returnFileElement = newDoc.createElement("urlReturnFileName");
            rootElement.appendChild(returnFileElement);

            Element urlElement = newDoc.createElement("url");
            rootElement.appendChild(urlElement);

            Element locationElement = newDoc.createElement("location");
            rootElement.appendChild(locationElement);
            locationElement.appendChild(newDoc.createTextNode(location));

            Element schemaElement = newDoc.createElement("urlReturnFileSchema");
            rootElement.appendChild(schemaElement);
//            schemaElement.appendChild(newDoc.createTextNode(QueryTree.generateSchema(schemaTree)));
            System.out.println(QueryTree.generateSchema(schemaTree));
            schemaElement.setTextContent(QueryTree.generateSchema(schemaTree));

            Element methodReturnFileElement = newDoc.createElement("methodReturnFileType");
            rootElement.appendChild(methodReturnFileElement);
            methodReturnFileElement.appendChild(newDoc.createTextNode(methodReturnFileType));

            Element returnFileTypeElement = newDoc.createElement("urlReturnFileType");
            rootElement.appendChild(returnFileTypeElement);
            returnFileTypeElement.appendChild(newDoc.createTextNode(urlReturnFileType));

            List<MyAttribute> outAttributes = QueryTree.generateAttributeList(schemaTree);

            for (MyAttribute attr : outAttributes) {
                Element outAttrElement = newDoc.createElement("outAttributes");
                rootElement.appendChild(outAttrElement);

                Element outAttrNameElement = newDoc.createElement("name");
                outAttrNameElement.appendChild(newDoc.createTextNode(attr.getName()));
                outAttrElement.appendChild(outAttrNameElement);

                Element outAttrLabelElement = newDoc.createElement("label");
                outAttrLabelElement.appendChild(newDoc.createTextNode(attr.getName()));
                outAttrElement.appendChild(outAttrLabelElement);

                Element outAttrTypeElement = newDoc.createElement("type");
                outAttrTypeElement.appendChild(newDoc.createTextNode(attr.getType()));
                outAttrElement.appendChild(outAttrTypeElement);

                Element outAttrDescElement = newDoc.createElement("description");
                outAttrDescElement.appendChild(newDoc.createTextNode(attr.getDescription()));
                outAttrElement.appendChild(outAttrDescElement);
            }

            for (RegisterAttribute attr : attributes) {
                Element attrElement = newDoc.createElement("attributes");
                rootElement.appendChild(attrElement);

                Element attrLabelElement = newDoc.createElement("label");
                attrLabelElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrLabelElement);

                Element attrNameElement = newDoc.createElement("name");
                attrNameElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrNameElement);

                Element attrTypeElement = newDoc.createElement("type");
                attrTypeElement.appendChild(newDoc.createTextNode(attr.getAttributeType()));
                attrElement.appendChild(attrTypeElement);

                Element attrValueElement = newDoc.createElement("value");
                attrValueElement.appendChild(newDoc.createTextNode(attr.getValue()));
                attrElement.appendChild(attrValueElement);

                Element attrRequiredElement = newDoc.createElement("required");
                attrRequiredElement.appendChild(newDoc.createTextNode(attr.getRequired()));
                attrElement.appendChild(attrRequiredElement);

                Element attrShowElement = newDoc.createElement("shown");
                attrShowElement.appendChild(newDoc.createTextNode(attr.getShown()));
                attrElement.appendChild(attrShowElement);

                Element attrInputTypeElement = newDoc.createElement("attributeType");
                attrInputTypeElement.appendChild(newDoc.createTextNode("input"));
                attrElement.appendChild(attrInputTypeElement);

                Element attrExampleElement = newDoc.createElement("example");
                attrExampleElement.appendChild(newDoc.createTextNode(attr.getExample()));
                attrElement.appendChild(attrExampleElement);

                Element attrDescElement = newDoc.createElement("description");
                attrDescElement.appendChild(newDoc.createTextNode(attr.getDescription()));
                attrElement.appendChild(attrDescElement);

                Element attrFromElement = newDoc.createElement("from");
                attrFromElement.appendChild(newDoc.createTextNode("default"));
                attrElement.appendChild(attrFromElement);

            }
            //need wrapper
            if (wrapper != null && !wrapper.equals("")) {
                Element wrapperElement = newDoc.createElement("wrapper");
                rootElement.appendChild(wrapperElement);
                wrapperElement.appendChild(newDoc.createTextNode(wrapper));

                if (!headers.isEmpty()) {
                    for (MyAttribute attr : headers) {
                        Element outAttrElement = newDoc.createElement("headers");
                        wrapperElement.appendChild(outAttrElement);
                        outAttrElement.appendChild(newDoc.createTextNode(attr.getName()));
                    }
                }
            }

            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(fileURL));
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
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    static public void saveResourceWeb(String resourceType, String organization, String resourceName,
            String description,
            String URL,
            String URLExample,
            QueryTree schemaTree,
            List<RegisterAttribute> attributes,
            String methodReturnFileType,
            String urlReturnFileType,
            String wrapper,
            List<MyAttribute> headers,
            String separator,
            String textTableWrapperHeaders,
            boolean resultContainsHeaderInfo,
            String jsonToxmlWrapperAttributes,
            String myTableExactorHeaders,
            String fileURL,
            String location, String aggregateName, String suggestOutputFileName,
            String method
            ) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("resource");
            newDoc.appendChild(rootElement);
            Element idElement = newDoc.createElement("id");
            rootElement.appendChild(idElement);
            UUID uid = UUID.randomUUID();
            idElement.appendChild(newDoc.createTextNode(uid.toString()));

            Element resourceTypeElement = newDoc.createElement("resourceType");
            rootElement.appendChild(resourceTypeElement);
            resourceTypeElement.appendChild(newDoc.createTextNode(resourceType));

            Element orgElement = newDoc.createElement("organization");
            rootElement.appendChild(orgElement);
            orgElement.appendChild(newDoc.createTextNode(organization));

            Element fileNameElement = newDoc.createElement("fileName");
            rootElement.appendChild(fileNameElement);
            fileNameElement.appendChild(newDoc.createTextNode(resourceName));

            Element resourceNameElement = newDoc.createElement("resourceName");
            rootElement.appendChild(resourceNameElement);
            resourceNameElement.appendChild(newDoc.createTextNode(MyUtils.getFileName(resourceName)));

            Element descrptionElement = newDoc.createElement("description");
            rootElement.appendChild(descrptionElement);
            descrptionElement.appendChild(newDoc.createTextNode(description));

            Element aggNameElement = newDoc.createElement("aggregateName");
            rootElement.appendChild(aggNameElement);
            aggNameElement.appendChild(newDoc.createTextNode(aggregateName));

            Element returnFileElement = newDoc.createElement("urlReturnFileName");
            rootElement.appendChild(returnFileElement);

            Element locationElement = newDoc.createElement("location");
            rootElement.appendChild(locationElement);
            locationElement.appendChild(newDoc.createTextNode(location));

            Element schemaElement = newDoc.createElement("urlReturnFileSchema");
            rootElement.appendChild(schemaElement);
            schemaElement.appendChild(newDoc.createTextNode(QueryTree.generateSchema(schemaTree)));

            Element methodReturnFileElement = newDoc.createElement("methodReturnFileType");
            rootElement.appendChild(methodReturnFileElement);
            methodReturnFileElement.appendChild(newDoc.createTextNode(methodReturnFileType));

            Element returnFileTypeElement = newDoc.createElement("urlReturnFileType");
            rootElement.appendChild(returnFileTypeElement);
            returnFileTypeElement.appendChild(newDoc.createTextNode(urlReturnFileType));

            Element urlElement = newDoc.createElement("url");
            rootElement.appendChild(urlElement);
            urlElement.appendChild(newDoc.createTextNode(URL));

            Element urlExampleElement = newDoc.createElement("urlExmaple");
            rootElement.appendChild(urlExampleElement);
            urlExampleElement.appendChild(newDoc.createTextNode(URLExample));

            Element suggestOutputFileNameElement = newDoc.createElement("suggestOutputFileName");
            rootElement.appendChild(suggestOutputFileNameElement);
            suggestOutputFileNameElement.appendChild(newDoc.createTextNode(suggestOutputFileName));

            Element methodElement = newDoc.createElement("method");
            rootElement.appendChild(methodElement);
            methodElement.appendChild(newDoc.createTextNode(method));

            List<MyAttribute> outAttributes = QueryTree.generateAttributeList(schemaTree);

            for (RegisterAttribute attr : attributes) {
                Element attrElement = newDoc.createElement("attributes");
                rootElement.appendChild(attrElement);

                Element attrLabelElement = newDoc.createElement("label");
                attrLabelElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrLabelElement);

                Element attrNameElement = newDoc.createElement("name");
                attrNameElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrNameElement);

                Element attrTypeElement = newDoc.createElement("type");
                attrTypeElement.appendChild(newDoc.createTextNode(attr.getAttributeType()));
                attrElement.appendChild(attrTypeElement);

                Element attrValueElement = newDoc.createElement("value");
                attrValueElement.appendChild(newDoc.createTextNode(attr.getValue()));
                attrElement.appendChild(attrValueElement);

                Element attrRequiredElement = newDoc.createElement("required");
                attrRequiredElement.appendChild(newDoc.createTextNode(attr.getRequired()));
                attrElement.appendChild(attrRequiredElement);

                Element attrShowElement = newDoc.createElement("shown");
                attrShowElement.appendChild(newDoc.createTextNode(attr.getShown()));
                attrElement.appendChild(attrShowElement);

                Element attrInputTypeElement = newDoc.createElement("attributeType");
                attrInputTypeElement.appendChild(newDoc.createTextNode("input"));
                attrElement.appendChild(attrInputTypeElement);

                Element attrExampleElement = newDoc.createElement("example");
                attrExampleElement.appendChild(newDoc.createTextNode(attr.getExample()));
                attrElement.appendChild(attrExampleElement);

                Element attrDescElement = newDoc.createElement("description");
                attrDescElement.appendChild(newDoc.createTextNode(attr.getDescription()));
                attrElement.appendChild(attrDescElement);

                Element attrFromElement = newDoc.createElement("from");
                attrFromElement.appendChild(newDoc.createTextNode("default"));
                attrElement.appendChild(attrFromElement);

            }
            for (MyAttribute attr : outAttributes) {
                Element outAttrElement = newDoc.createElement("outAttributes");
                rootElement.appendChild(outAttrElement);

                Element outAttrNameElement = newDoc.createElement("name");
                outAttrNameElement.appendChild(newDoc.createTextNode(attr.getName()));
                outAttrElement.appendChild(outAttrNameElement);

                Element outAttrLabelElement = newDoc.createElement("label");
                outAttrLabelElement.appendChild(newDoc.createTextNode(attr.getName()));
                outAttrElement.appendChild(outAttrLabelElement);

                Element outAttrTypeElement = newDoc.createElement("type");
                outAttrTypeElement.appendChild(newDoc.createTextNode(attr.getType()));
                outAttrElement.appendChild(outAttrTypeElement);

                Element outAttrDescElement = newDoc.createElement("description");
                outAttrDescElement.appendChild(newDoc.createTextNode(attr.getDescription()));
                outAttrElement.appendChild(outAttrDescElement);
            }
            //need wrapper
//            if (wrapper != null && !wrapper.equals("")) {
//                Element wrapperElement = newDoc.createElement("wrapper");
//                rootElement.appendChild(wrapperElement);
//
//                Element wrapperNameElement = newDoc.createElement("wrapperName");
//                wrapperElement.appendChild(wrapperNameElement);
//                wrapperNameElement.appendChild(newDoc.createTextNode(wrapper));

//                 String separator,
//            String textTableWrapperHeaders,
//            boolean resultContainsHeaderInfo,
//            String jsonToxmlWrapperAttributes,
//            String myTableExactorHeaders,
//                if (wrapper.equals("textTableWrapper")) {
//                    Element separatorElement = newDoc.createElement("separator");
//                    wrapperElement.appendChild(separatorElement);
//                    separatorElement.appendChild(newDoc.createTextNode(separator));
//
//                    for (String attr : textTableWrapperHeaders.split(",")) {
//                        Element outAttrElement = newDoc.createElement("headers");
//                        wrapperElement.appendChild(outAttrElement);
//                        outAttrElement.appendChild(newDoc.createTextNode(attr));
//                    }
//                    Element containsHeaderElement = newDoc.createElement("resultContainHeaderInfo");
//                    wrapperElement.appendChild(containsHeaderElement);
//                    containsHeaderElement.appendChild(newDoc.createTextNode(resultContainsHeaderInfo ? "true" : "false"));
//                } else if (wrapper.equals("jsonToxmlWrapper")) {
//                    for (String attr : jsonToxmlWrapperAttributes.split(",")) {
//                        Element outAttrElement = newDoc.createElement("attrs");
//                        wrapperElement.appendChild(outAttrElement);
//                        outAttrElement.appendChild(newDoc.createTextNode(attr));
//                    }
//                } else if (wrapper.equals("myTableExactor")) {
//                    for (String attr : jsonToxmlWrapperAttributes.split(",")) {
//                        Element outAttrElement = newDoc.createElement("headers");
//                        wrapperElement.appendChild(outAttrElement);
//                        outAttrElement.appendChild(newDoc.createTextNode(attr));
//                    }
//                }
//            }
            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(fileURL));
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
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    static public void saveFunction(String functionName, String functionType, String organization,
            String description, List<RegisterAttribute> attributes,
            String fileURL, String location, String aggregateName) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("function");
            newDoc.appendChild(rootElement);
            Element idElement = newDoc.createElement("id");
            rootElement.appendChild(idElement);
            UUID uid = UUID.randomUUID();
            idElement.appendChild(newDoc.createTextNode(uid.toString()));

            Element resourceTypeElement = newDoc.createElement("functionName");
            rootElement.appendChild(resourceTypeElement);
            resourceTypeElement.appendChild(newDoc.createTextNode(functionName));

            //jstree
            Element typeElement = newDoc.createElement("type");
            rootElement.appendChild(typeElement);
            typeElement.appendChild(newDoc.createTextNode("function"));

            Element orgElement = newDoc.createElement("functionType");
            rootElement.appendChild(orgElement);
            orgElement.appendChild(newDoc.createTextNode(functionType));

            Element fileNameElement = newDoc.createElement("organization");
            rootElement.appendChild(fileNameElement);
            fileNameElement.appendChild(newDoc.createTextNode(organization));

            Element descrptionElement = newDoc.createElement("description");
            rootElement.appendChild(descrptionElement);
            descrptionElement.appendChild(newDoc.createTextNode(description));

            Element aggregateElement = newDoc.createElement("aggregateName");
            rootElement.appendChild(aggregateElement);
            aggregateElement.appendChild(newDoc.createTextNode(aggregateName));

            Element locationElement = newDoc.createElement("location");
            rootElement.appendChild(locationElement);
            locationElement.appendChild(newDoc.createTextNode(location));

            for (RegisterAttribute attr : attributes) {
                Element attrElement = newDoc.createElement("attributes");
                rootElement.appendChild(attrElement);

                Element attrLabelElement = newDoc.createElement("label");
                attrLabelElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrLabelElement);

                Element attrNameElement = newDoc.createElement("name");
                attrNameElement.appendChild(newDoc.createTextNode(attr.getName()));
                attrElement.appendChild(attrNameElement);

                Element attrTypeElement = newDoc.createElement("type");
                attrTypeElement.appendChild(newDoc.createTextNode(attr.getAttributeType()));
                attrElement.appendChild(attrTypeElement);

                Element attrValueElement = newDoc.createElement("value");
                attrValueElement.appendChild(newDoc.createTextNode(attr.getValue()));
                attrElement.appendChild(attrValueElement);

                Element attrRequiredElement = newDoc.createElement("required");
                attrRequiredElement.appendChild(newDoc.createTextNode(attr.getRequired()));
                attrElement.appendChild(attrRequiredElement);

                Element attrShowElement = newDoc.createElement("shown");
                attrShowElement.appendChild(newDoc.createTextNode(attr.getShown()));
                attrElement.appendChild(attrShowElement);

                Element attrInputTypeElement = newDoc.createElement("attributeType");
                attrInputTypeElement.appendChild(newDoc.createTextNode("input"));
                attrElement.appendChild(attrInputTypeElement);

                Element attrExampleElement = newDoc.createElement("example");
                attrExampleElement.appendChild(newDoc.createTextNode(attr.getExample()));
                attrElement.appendChild(attrExampleElement);

                Element attrDescElement = newDoc.createElement("description");
                attrDescElement.appendChild(newDoc.createTextNode(attr.getDescription()));
                attrElement.appendChild(attrDescElement);

                Element attrFromElement = newDoc.createElement("from");
                attrFromElement.appendChild(newDoc.createTextNode("default"));
                attrElement.appendChild(attrFromElement);

            }

            //** End of CSV parsing**//
            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(fileURL));
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
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    static public Node filterXMLHelper(List<String> queires, List<String> curPath, Node root, Document newDoc) {

        System.out.println("*************");
        System.out.println(StringUtils.join(curPath, "/"));
        System.out.println(root.getNodeName());
        System.out.println(root.getNodeValue());
        if (queires.contains(StringUtils.join(curPath, "/"))) {
            System.out.println("done");
            Node newRoot = newDoc.importNode(root, true);
            return newRoot;
        }
        Node newRoot = newDoc.importNode(root, false);

        if (curPath.size() > queires.get(0).split("/").length) {
            return newRoot;
        }

        NodeList nNodes = root.getChildNodes();
        for (int i = 0; i < nNodes.getLength(); ++i) {
            if (nNodes.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            curPath.add(nNodes.item(i).getNodeName());
            if (queires.stream().anyMatch(t -> t.contains(StringUtils.join(curPath, "/")))) {
                newRoot.appendChild(filterXMLHelper(queires, curPath, nNodes.item(i), newDoc));
            }
            curPath.remove(nNodes.item(i).getNodeName());
        }
        return newRoot;
    }

    static public int filterXML(List<String> queries, String fileURL, String xmlFileURL) {
        //requires the same level
        int rowsCount = -1;
        BufferedReader csvReader;
        try {
            File xmlFile = new File(fileURL);
            DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
            Document doc = dBuilder1.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Node root = doc.getDocumentElement();

            List<String> path = new ArrayList<>();
            path.add(root.getNodeName());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDoc = dBuilder.newDocument();
//                        Element newRoot = newDoc.createElement(root.getNodeName());
//                        newDoc.appendChild(newRoot);

            Node newRootReturn = filterXMLHelper(queries, path, root, doc);
            Node newRootCopyed = newDoc.importNode(newRootReturn, true);
            newDoc.appendChild(newRootCopyed);

//                         for (int i = 0; i < newRootCopyed.getChildNodes().getLength(); ++i) {
//                                newRoot.appendChild(newRootCopyed.getChildNodes().item(i));
//                         }                        
            System.out.println(newRootReturn.getChildNodes().getLength());
            System.out.println(newRootReturn.getNodeName());

            for (int i = 0; i < newRootReturn.getChildNodes().getLength(); ++i) {
                Node cur = newRootReturn.getChildNodes().item(i);
                System.out.println(cur.getNodeName() + " " + cur.getNodeValue());
                System.out.println(cur.getChildNodes().item(0).getNodeName() + " " + cur.getChildNodes().item(0).getNodeValue());
                System.out.println(cur.getChildNodes().item(0).getChildNodes().item(0).getNodeName() + " " + cur.getChildNodes().item(0).getChildNodes().item(0).getNodeValue());
            }

            FileWriter writer = null;
            try {
                writer = new FileWriter(new File(xmlFileURL));
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
            // Output to console for testing
            // Resultt result = new StreamResult(System.out);
        } catch (IOException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return rowsCount;
        // "XLM Document has been created" + rowsCount;
    }

    public static void appendToTheEnd(String fileURL, String content) {
        try {
            Files.write(Paths.get(fileURL), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

}
