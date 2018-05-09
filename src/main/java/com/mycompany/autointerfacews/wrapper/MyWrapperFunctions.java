/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.wrapper;

import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyExtractor;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jupiter
 */
public class MyWrapperFunctions {
        static public String generateURL(MyResource resource) {
                String url = resource.getUrl();
                if (resource.getResourceType().equals("REST")) {
                   for (MyAttribute attr : resource.getAttributes()) {
                                if (!attr.getValue().isEmpty()) {
                                        url = url.replace("{" + attr.getName() + "}", attr.getValue());
                                }
                        }
                } else if (resource.getResourceType().equals("HTTP")) {
                        if (resource.getMethod().equals("GET")) {
                                String paramString = "";
                                for (MyAttribute attr : resource.getAttributes()) {
                                        if (!attr.getValue().isEmpty()) {
                                                paramString += attr.getName() + "=" + attr.getValue() + "&";
                                        }
                                }
                                url += "?" + paramString;
                        }
                }
                return url;
        }
        //for EMBL-EBI sequence
        static public String myExactor(WebDriver driver, MyResource resource) {
                
                String url = generateURL(resource);
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                List<List<String>> returnFile = new ArrayList<>();
                MyExtractor extractor = resource.getExtractor();
                returnFile.add(extractor.getHeaders());
                try {
                        System.out.println(url);
                        driver.get(url);
                        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                        
                        
                        List<String> row = new ArrayList<>();
                        
//                        String value = driver.findElement(By.xpath("//*[@id=&quot;treefam_family&quot;]/a")).getText();
                        String value = driver.findElement(By.xpath("//*[@id='treefam_family']/a")).getText();
                        row.add(value);
                        String value1 = driver.findElement(By.xpath("//*[@id='species']")).getText();
                        row.add(value1);
                        String value2 = driver.findElement(By.xpath("//*[@id='annotation']")).getText();
                        row.add(value2);
                        String value3 = driver.findElement(By.xpath("//*[@id='protein_seq']")).getText();
                        row.add(value3);
                        String value4 = driver.findElement(By.xpath("//*[@id='cds_seq']")).getText();
                        row.add(value4);
                        
//                        for (Inductionrules rule : extractor.getInductionrules()) {
//                                row.add(value);
//                        }
                        returnFile.add(row);
                        
                        MyFileReader.writeFile(fileUrl, returnFile, ",");
                } catch (Exception e) {
                        e.printStackTrace();
                }
                
                return fileUrl;
        }
        
        static public String myTableExactor(WebDriver driver, MyResource resource) {
                
                String url = generateURL(resource);
                String fileUrl = resource.getLocation() + resource.getUrlReturnFileName();
                List<List<String>> returnFile = new ArrayList<>();
                MyExtractor extractor = resource.getExtractor();
                //add headers
                returnFile.add(extractor.getHeaders());
                try {
                        System.out.println(url);
                        driver.get(url);
                        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                        //headers
                        Integer requiredColumns = extractor.getHeaders().size();
                        System.out.println("reauiredColumns: " + requiredColumns);
                        //find all table elements
                        List<WebElement> tables = driver.findElements(By.tagName("table"));
                        System.out.println("find table: " + tables.size());
                        WebElement targetTable = null;
                        
//                        Map<WebElement, Integer> matchedTables = new HashMap<>();
                        Map<WebElement, Pair<Integer, Integer>> matchedTables = new HashMap<>();
                        
                        for (WebElement table : tables) {
                                System.out.println("try table");
//                                try {//if we have thead
//                                        List<WebElement> thElements = table.findElements(By.tagName("th"));
//                                        System.out.println(thElements.size());
//                                        if (thElements.size() >= requiredColumns) {
//                                                targetTable = table;
//                                                break;
//                                        } else if (thElements.size() != 0) {//not what we want
//                                                continue;
//                                        }
//                                } catch (Exception e) {
//                                }
                                int countExact = 0;
                                int countLarge = 0;
                                //we cound the number of matched columns
                                List<WebElement> trElements = table.findElements(By.tagName("tr"));
                                for (WebElement trElement : trElements) {
                                        List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
                                        if (tdElements.size() == requiredColumns) {
                                                ++countExact;
                                        }
                                        if (tdElements.size() > requiredColumns) {
                                                ++countLarge;
                                        }
                                }
                                System.out.println("count: " + countExact + " " + countLarge);
                                matchedTables.put(table, Pair.of(countExact, countLarge));
                        }
                        if (targetTable == null) {
                                //get the most matched columns
                                targetTable = matchedTables.entrySet()
                                        .stream()
                                        .sorted((e1, e2) -> {
                                                Integer result = Integer.compare(e2.getValue().getLeft(), e1.getValue().getRight());
                                                if (result == 0) {
                                                        return Integer.compare(e2.getValue().getRight(), e1.getValue().getRight());
                                                }
                                                return result;
                                        })
                                        .map(Map.Entry::getKey)
                                        .collect(Collectors.toList()).get(0);
                        }
                        System.out.println("the target table is: ");
                        System.out.println(targetTable.getAttribute("bgcolor"));
                        //get values
                        List<WebElement> trElements = targetTable.findElements(By.tagName("tr"));
                          for (WebElement trElement : trElements) {
                                List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
                                if (tdElements.size() >= requiredColumns) {//we assume it might have more
                                        List<String> row = new ArrayList<>();
                                        for (WebElement td : tdElements) {
                                                String value = td.getText();
                                                row.add(value);
                                        }
                                        returnFile.add(row);
                                }
                        }
                        //check if we have headers in the table
                        if (returnFile.get(0).get(0).toLowerCase().contains(returnFile.get(1).get(0).toLowerCase())) {
                                //we have head
                                returnFile.remove(1);
                        }
                          
                        MyFileReader.writeFile(fileUrl, returnFile, ",");
                } catch (Exception e) {
                        e.printStackTrace();
                }
                
                return fileUrl;
        }
        //input file
        static public List<List<String>> functionWrapper(String fileUrl, String fileName, List<MyAttribute> attributes, EXist eXist) throws CodeException {
                List<List<String>> res = new ArrayList<>();
                
                //put file to eXist Database
                System.out.println("put file to eXist database");
                String[] args = new String[2];
                args[0] = "/db/apps/flowq/index";
                args[1] = fileUrl;
                eXist.put(args);
                //get inputs
                
//                for (MyAttribute attribute : attributes) {
//                        if (attribute.getShown().equals(true)) {
//                                args[1] = "doc(\'" + fileName + "')//" + attribute.getName() + "/text()";
//                                System.out.println("********************args 1 " + args[1]);
////                                List<String> row = eXist.runQuery2(args);
//                                res.add(row);
//                        }
//                }
//                //if it is empty we need try another way
//                if (res.get(0).isEmpty()) {
//                        res.clear();
//                        for (MyAttribute attribute : attributes) {
//                                if (attribute.getShown().equals(true)) {
//                                        args[1] = "doc(\'" + fileName + "')//" + attribute.getName() + "/*";
//                                        System.out.println("********************args 1 " + args[1]);
////                                        List<String> row = eXist.runQuery2(args);
//                                        res.add(row);
//                                }
//                        }
//                }
                System.out.println(res.size());
                System.out.println(res.get(0).size());
                if (res.size() == 2)
                        System.out.println(res.get(1).size());
                res = transpose(res);
                System.out.println(res.get(0).get(0));
                return res;
        }
        
        static <T> List<List<T>> transpose(List<List<T>> table) {
                List<List<T>> ret = new ArrayList<List<T>>();
                final int N = table.get(0).size();
                for (int i = 0; i < N; i++) {
                        List<T> col = new ArrayList<T>();
                        for (List<T> row : table) {
                                col.add(row.get(i));
                        }
                        ret.add(col);
                }
                return ret;
        }
}
