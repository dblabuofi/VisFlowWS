package com.mycompany.autointerfacews.wrapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mycompany.autointerfacews.bioflow.BioFlowExtractStatement;
import com.mycompany.autointerfacews.helper.MyFileReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jupiter
 */
public class WrapperCollections {

    @Inject
    @Named("driver")
    WebDriver driver;

    public List<String> textTableWrapper(List<String> fileURLs, BioFlowExtractStatement script, String location, List<String> headers) {
        //we assume, the files we receive, seperator is \t or ,
        String seperator = ",";
        List<String> cons = MyFileReader.readFileByLine(fileURLs.get(0));
        if (cons.get(0).contains("\t")) { 
            seperator = "\t";
        } else if (cons.get(0).contains(",")) {
            seperator = ",";
        } else {//one file
            System.out.println(fileURLs);
            for (String fileURL : fileURLs) {
                String content = MyFileReader.readFileAll(fileURL).replaceAll("[\r\n]", "");//because append is read by line!!
//                System.out.println("*****HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
//                System.out.println(content);
                List<List<String>> contents = new ArrayList<>();
                contents.add(0, headers);
                contents.add(Arrays.asList(new String[]{content}));
                MyFileReader.writeFile(fileURL, contents, ",");
            }
            return fileURLs;
        }
        for (String fileURL : fileURLs) {
            List<List<String>> content = MyFileReader.readFile(fileURL, seperator);
            List<String> first = content.get(0);

            if (first.stream().allMatch(t -> headers.contains(t))) {
                MyFileReader.writeFile(fileURL, content, ",");
            } else {
                content.add(0, headers);
                MyFileReader.writeFile(fileURL, content, ",");
            }
        }

        return fileURLs;
    }

    public List<String> jsonTOxmlWrapper(List<String> fileURLs, BioFlowExtractStatement script, String location) {
        System.out.println("jsonTOxmlWrapper");
        for (String fileURL : fileURLs) {
            System.out.println(fileURL);
            MyFileReader.converJSONtoXML(fileURL, fileURL);
        }
//                List<String> returnFileURL = fileURLs.stream().map( t-> t = t + ".xml" ).collect(Collectors.toList());
        return fileURLs;
    }

    public List<String> jsonTOTableWrapper(List<String> fileURLs, BioFlowExtractStatement script, String location,
            String schema, List<String> attrs) {
        System.out.println("jsonTOTableWrapper");
        for (String fileURL : fileURLs) {
            System.out.println(fileURL);
            MyFileReader.converJSONtoTable(fileURL, fileURL, schema, attrs);
        }
        return fileURLs;
    }

    public List<String> htmlTableExactor(List<String> fileURLs, BioFlowExtractStatement script, String location,
            String schema, List<String> attrs, int tableIndex) {
        System.out.println("htmlTableExactor");
        for (String fileURL : fileURLs) {
            try {
                //temp files, need add html to the end to get it called
                String fileURLExt = fileURL + ".html";
                Files.copy(Paths.get(fileURL), Paths.get(fileURLExt));
                String cont = MyFileReader.readFileAll(fileURLExt);
                if (!cont.startsWith("<html") && !cont.startsWith("<!doctype html>")) {
                    cont = "<html><body>" + cont + "</body></html>";
                    MyFileReader.writeFile(fileURLExt, cont);
                }
                System.out.println(fileURL);
                driver.get("file:///" + fileURLExt);
//                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//                Thread.sleep(1000);
                //find all table elements
                List<WebElement> tables = driver.findElements(By.tagName("table"));
//                System.out.println("find table: " + tables.size());
                WebElement targetTable = tables.get(tableIndex);

                List<List<String>> content = new ArrayList<>();

                //get values
                /**
                 * we add column + number for
                 */
                List<WebElement> trElements = targetTable.findElements(By.tagName("tr"));
                int colIndex = 0;
                for (int i = 0; i < trElements.size(); ++i) {
                    WebElement trElement = trElements.get(i);
                    List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
                    List<String> row = new ArrayList<>();
                    if (i == 0) {
                        for (WebElement td : tdElements) {
                            String value = td.getText();
                            if (value.matches("(.)*(\\w)(.)*")) {
                                row.add(value);
                            } else {
                                row.add("column" + colIndex++);
                            }
                        }
                    } else {
                        for (WebElement td : tdElements) {
                            String value = td.getText();
                            row.add(value);
                        }
                    }
                    content.add(row);
                }
                MyFileReader.writeFile(fileURL, content, ",");
                File file = new File(fileURLExt);
                if (file.exists()) {
                    file.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileURLs;
    }

    public List<String> htmlTableExactor2(List<String> fileURLs, BioFlowExtractStatement script, String location,
            String schema, List<String> attrs, int tableIndex) {
        System.out.println("htmlTableExactor");
        for (String fileURL : fileURLs) {
            try {
                //temp files, need add html to the end to get it called
                String fileURLExt = fileURL + ".html";
                Files.copy(Paths.get(fileURL), Paths.get(fileURLExt));
                String cont = MyFileReader.readFileAll(fileURLExt);
                if (!cont.startsWith("<")) {
                    cont = "<html>" + cont + "</html>";
                    MyFileReader.writeFile(fileURLExt, cont);
                }

                File inputFile = new File(fileURLExt);
                Document doc = Jsoup.parse(inputFile, "UTF-8"); 
                
                Elements tables = doc.select("table");
                Element table = tables.get(tableIndex);
                
                List<List<String>> content = new ArrayList<>();
                Elements trs = table.select("tr");
                
                int colIndex = 0;
                for (int i = 0; i < trs.size(); i++) {
                    Element tr = trs.get(i);
                    Elements tds = tr.select("td");
                    List<String> row = new ArrayList<>();
                    if (i == 0) {
                        for (int j = 0; j < tds.size(); j++) {
                            Element nNode = tds.get(j);
                            String value = nNode.text();
                            if (value.matches("(.)*(\\w)(.)*") || !value.isEmpty()) {
                                row.add(value);
                            } else {
                                row.add("column" + colIndex++);
                            }
                        }
                    } else {
                        for (int j = 0; j < tds.size(); j++) {
                            Element nNode = tds.get(j);
                            String value = nNode.text();
                            row.add(value);
                        }
                    }
                    content.add(row);
                }
                MyFileReader.writeFile(fileURL, content, ",");
                File file = new File(fileURLExt);
                if (file.exists()) {
                    file.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileURLs;
    }

    public List<String> myTableExactor(List<String> fileURLs, BioFlowExtractStatement script, String location) {

        for (String fileURL : fileURLs) {
            try {
                //temp files, need add html to the end to get it called
                String fileURLExt = fileURL + ".html";
                Files.copy(Paths.get(fileURL), Paths.get(fileURLExt));

                System.out.println(fileURL);
                driver.get("file:///" + fileURLExt);
                driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                Thread.sleep(60000);
                saveFile("test");
                //headers
                Integer requiredColumns = script.getWrapperTargets().size();
                System.out.println("reauiredColumns: " + requiredColumns);
                //find all table elements
                List<WebElement> tables = driver.findElements(By.tagName("table"));
                System.out.println("find table: " + tables.size());
                WebElement targetTable = null;

//                        Map<WebElement, Integer> matchedTables = new HashMap<>();
                Map<WebElement, Pair<Integer, Integer>> matchedTables = new HashMap<>();

                for (WebElement table : tables) {
                    System.out.println("try table");
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
                System.out.println("matchedTables:" + matchedTables.size());
                if (targetTable == null) {
                    //get the most matched columns
                    targetTable = matchedTables.entrySet()
                            .stream()
                            .sorted((e1, e2) -> {
                                Integer result = Integer.compare(e2.getValue().getLeft(), e1.getValue().getLeft());
                                if (result == 0) {
                                    return Integer.compare(e2.getValue().getRight(), e1.getValue().getRight());
                                }
                                return result;
                            })
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList()).get(0);
                }
                List<List<String>> content = new ArrayList<>();
                content.add(script.getWrapperTargets());
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
                        content.add(row);
                    }
                }

                //check if we have headers in the table
                if (content.get(0).get(0).toLowerCase().contains(content.get(1).get(0).toLowerCase())) {
                    //we have head
                    content.remove(1);
                }

                MyFileReader.writeFile(fileURL, content, ",");

                File file = new File(fileURLExt);
                if (file.exists()) {
                    file.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return fileURLs;
    }

    public void saveFile(String fileName) {
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\" + fileName + ".png"));
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }

}
