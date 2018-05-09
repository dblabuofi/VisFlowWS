/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyExtractor;
import com.mycompany.autointerfacews.mydata.MyWrapper;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.autointerfacews.wrapper.MyWrapperFunctions;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.mappings.IMappingElement;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jupiter
 */
public class MyResource implements ISchema {
        String id;
        String resourceType;
        String organization;
        String resourceName;//resource name default name
        String description;
        String aggregateName;
        //rest 
        String url;
        String postURL;//for post
        String urlExample;
        String method;
        List<MyAttribute> attributes;
        List<MyAttribute> outAttributes;
        String methodReturnFileType;//this method returned file type
        String methodReturnFileSchema;//this method returned file type
        String urlReturnFileType;
        String urlReturnFileSchema;
        QueryTree jsUrlReturnFileSchema;
        QueryTree jsUrlReturnFileSchemaJSON;
        String fileName;//file name for this resource xml
        String location;//directory name
        String urlReturnFileName;//this is the acture files store data
        List<String> outputFileNames;
        List<String> outputFileTypes;
        String suggestOutputFileName;
        
        MyWrapper wrapper;
        String matcher;
        String filler;
        MyExtractor extractor;
        
//        other
        String name;
        String originalResourceName;
        String isReturn;

        //jstree
        String text;
        
        @Override
         public QueryTree getSchema(QueryTree originalTree,  IContextMapping<INode> result){
                 
                 jsUrlReturnFileSchema = new QueryTree();
                 jsUrlReturnFileSchema = MyFileReader.mapResourceSchemaToQueryTree(jsUrlReturnFileSchema, urlReturnFileSchema);
//                 System.out.println("*********************");
//                 System.out.println(jsUrlReturnFileSchema.getText());
//                 jsUrlReturnFileSchema.print(jsUrlReturnFileSchema);
                 if (jsUrlReturnFileSchema == null) return null;
                 for (IMappingElement<INode> e : result) {
                        System.out.println(e.getSource().nodeData().getName() + "\t" + e.getRelation() + "\t" + e.getTarget().nodeData().getName());
                        String originalColor =  originalTree.search(originalTree, e.getSource().nodeData().getName()).getA_attr().getStyle();
                         System.out.println(originalColor);
                        jsUrlReturnFileSchema.search(jsUrlReturnFileSchema, e.getTarget().nodeData().getName()).getA_attr().setStyle(originalColor);
                }
                 
                 return jsUrlReturnFileSchema;
         }

        //return stored filelocation
        public String readResource(WebDriver driver, MyHttpClient myHttpClient) {
                String fileUrl = null;
                
                 if (resourceType.equals("FTP")) {
                        String server = url.substring(0, url.indexOf("/"));
                        String remoteFile = url.substring(url.indexOf("/") + 1);
                        int port = 21;
                        String user = "anonymous";
                        String pass = "anonymous";
                        FTPClient ftpClient = new FTPClient();
                        fileUrl = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\" + urlReturnFileName;
                        File downloadFile1 = new File(fileUrl);
                        try {
                                ftpClient.connect(server, port);
                                ftpClient.login(user, pass);
                                ftpClient.enterLocalPassiveMode();
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                                boolean success = ftpClient.retrieveFile(remoteFile, outputStream1);
                                outputStream1.close();
                                if (success) {
                                        System.out.println("File " + urlReturnFileName + " has been downloaded successfully.");
                                }
                        } catch (IOException ex) {
                                System.out.println("Error: " + ex.getMessage());
                                ex.printStackTrace();
                        } finally {
                                try {
                                        if (ftpClient.isConnected()) {
                                                ftpClient.logout();
                                                ftpClient.disconnect();
                                        }
                                } catch (IOException ex) {
                                        ex.printStackTrace();
                                }
                        }

                } else if (resourceType.equals("HTTP")) {//resource is from the internet we need get it
                        if (method.equals("GET")) {
                                if (methodReturnFileType.equals("HTML")) {//need extract
                                     if (extractor.getExtractorName().equals("myTableExactor")) {
                                            fileUrl = location + urlReturnFileName;
                                            MyWrapperFunctions.myTableExactor(driver, this);
                                    }
                                } else {
                                        if (urlReturnFileType.equals("XML") || urlReturnFileType.equals("TEXT")) {
                                                fileUrl = location + urlReturnFileName;
                                                myHttpClient.runHttpGet(this);
                                        } else if (urlReturnFileType.equals("JSON")) {
                                                //need change
                                                System.out.println("resource from internet");
                                                System.out.println("store location: " + location + urlReturnFileName);
                                                fileUrl = location + urlReturnFileName;
                                                try {
                                                        driver.get(url);
                                                        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                                                        WebElement preElement = driver.findElement(By.tagName("pre"));
                                                        String pageSource = preElement.getText();
                                                        MyFileReader.writeFile(fileUrl, pageSource);

                                                } catch (TimeoutException e) {
                                                        System.out.println("timeout expection!!");
                                                        e.printStackTrace();
                                                } catch (Exception e) {
                                                        e.printStackTrace();
                                                }
                                        } 
                                }
                        } else if (method.equals("POST")) {
                                if (methodReturnFileType.equals("HTML")) {//need extract
                                         
                                } else { 
                                         if (urlReturnFileType.equals("XML") || urlReturnFileType.equals("TEXT")) {
                                                 fileUrl = location + urlReturnFileName;
                                                 myHttpClient.runHttpPost(this);
                                         } else if (urlReturnFileType.equals("JSON")) {
                                                 
                                         }
                                }
                        }
                } else if (resourceType.equals("REST")) {
                            if (methodReturnFileType.equals("HTML")) {//need extract
                                    if (extractor.getExtractorName().equals("myExactor")) {
                                            fileUrl = location + urlReturnFileName;
                                            MyWrapperFunctions.myExactor(driver, this);
                                    } else if (extractor.getExtractorName().equals("myTableExactor")) {
                                            fileUrl = location + urlReturnFileName;
                                            MyWrapperFunctions.myTableExactor(driver, this);
                                    }
                            } else { 
                                         if (urlReturnFileType.equals("XML") || urlReturnFileType.equals("TEXT")) {
                                                   fileUrl = location + urlReturnFileName;
                                                   myHttpClient.runHttpGetREST(this);
                                         } else if (urlReturnFileType.equals("JSON")) {
                                                 
                                         }
                            }     
                } else {
                        System.out.println("store location: " + location + urlReturnFileName);
                        fileUrl = location + urlReturnFileName;
                }
                 
                //after we get the data back see if we need change the format txt file
//                if (afterprocess != null) {
//                        if (afterprocess.getProcessName().equals("texttransform")) {
//                                
//                                List<List<String>> readFile = new ArrayList<>(); 
//                                //add header
//                                readFile.add(afterprocess.getHeaders());
//                                readFile.addAll(MyFileReader.readFile(fileUrl, afterprocess.getSeparator()));
//                                List<Integer> removeIndexs = afterprocess.getIndexs();
//                                //high to low
//                                if (removeIndexs != null) {
//                                        Collections.sort(removeIndexs, Collections.reverseOrder());
//                                        for (int i = 0; i < readFile.size(); ++i) {
//                                                List<String> row = readFile.get(i);
//                                                removeIndexs.forEach(t -> {
//                                                        row.remove(t.intValue());
//                                                });
//                                                readFile.set(i, row);
//                                        }
//                                }
//                                MyFileReader.writeFile(fileUrl, readFile, ",");
//                        }
//                }

                return fileUrl;
        }

        @Override
        public String toString() {
                return resourceType + " " + organization + " " + resourceName;
        }

    public String getMethodReturnFileSchema() {
        return methodReturnFileSchema;
    }
        
        @Override
        public boolean equals(Object o) {
                if (o instanceof MyResource) {
                        MyResource other = (MyResource) o;
                        return id.equals(other.id);
                }
                return false;
        }

        @Override
        public int hashCode() {
                int hash = 3;
                hash = 59 * hash + Objects.hashCode(this.id);
                hash = 59 * hash + Objects.hashCode(this.resourceName);
                return hash;
        }

        public List<String> getOutputFileNames() {
                return outputFileNames;
        }

        public List<String> getOutputFileTypes() {
                return outputFileTypes;
        }
        
        public List<MyAttribute> getOutAttributes() {
                return outAttributes;
        }

        public QueryTree getJsUrlReturnFileSchema() {
                return jsUrlReturnFileSchema;
        }

        public QueryTree getJsUrlReturnFileSchemaJSON() {
                return jsUrlReturnFileSchemaJSON;
        }

        public String getMatcher() {
                return matcher;
        }

        public MyWrapper getWrapper() {
                return wrapper;
        }

        public String getFiller() {
                return filler;
        }
        
        public MyExtractor getExtractor() {
                return extractor;
        }

        
        public String getMethodReturnFileType() {
                return methodReturnFileType;
        }
        
        
        public String getPostURL() {
                return postURL;
        }

        public String getUrlReturnFileName() {
                return urlReturnFileName;
        }

        public String getId() {
                return id;
        }

        public String getResourceType() {
                return resourceType;
        }

        public String getOrganization() {
                return organization;
        }

        public String getResourceName() {
                return resourceName;
        }

        public String getDescription() {
                return description;
        }

        public String getUrl() {
                return url;
        }

        public String getUrlExample() {
                return urlExample;
        }

        public String getMethod() {
                return method;
        }

        public List<MyAttribute> getAttributes() {
                return attributes;
        }

        public String getUrlReturnFileType() {
                return urlReturnFileType;
        }

        public String getUrlReturnFileSchema() {
                return urlReturnFileSchema;
        }

        public String getFileName() {
                return fileName;
        }

        public String getLocation() {
                return location;
        }

        public String getIsReturn() {
                return isReturn;
        }

        public String getName() {
                return name;
        }

        public String getOriginalResourceName() {
                return originalResourceName;
        }

        public String getText() {
                return text;
        }

        public void setText(String text) {
                this.text = text;
        }
        
}
