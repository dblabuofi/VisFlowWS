/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dao;

import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.mycompany.autointerfacews.bioflow.BioFlowExtractStatement;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author jupiter
 */
public class MyHttpClient {

    @Inject
    CloseableHttpClient client;
    @Named("httpsClient")
    @Inject
    CloseableHttpClient httpsClient;

    @Inject
    WebResourceImageDownloader webResourceImageDownloader;

    public MyHttpClient() {
    }

    public MyHttpClient(CloseableHttpClient client) {
        this.client = client;
    }

    public List<String> getUrl(String url) {
        List<String> result = new ArrayList<>();
        System.out.println("url: " + url);
        BufferedReader rd = null;
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.add(line);
                System.out.println(line);
            }
            rd.close();
            return result;
        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        } finally {
            try {
                if (rd != null) {
                    rd.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getUrl2_old(String url, MyStatus status) {
        String result = "";
        System.out.println("url: " + url);
        try {
            HttpGet request = new HttpGet(url);
            // add request header
//            request.addHeader("User-Agent", USER_AGENT);
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            try {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    if (!line.endsWith("\n")) {
                        result += line + '\n';
                    } else {
                        result += line;
                    }
                    status.addMessage(line);
                    System.out.println(line);
                }
                rd.close();
                response.close();
            } catch (Exception e) {
                status.addMessage(e.getMessage());
                e.printStackTrace();
            } finally {
                response.close();
            }
            return result;
        } catch (Exception e) {
            status.addMessage(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    synchronized public String getUrl2(String url, MyStatus status) {
        String result = "";
        System.out.println("url: " + url);
        try {
            HttpGet request = new HttpGet(url);
            int timeout = 5; 
            RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();
            try (
                    CloseableHttpClient closeableHttpClient =  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                    CloseableHttpResponse response = closeableHttpClient.execute(request);) {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    if (!line.endsWith("\n")) {
                        result += line + '\n';
                    } else {
                        result += line;
                    }
                    status.addMessage(line);
                    System.out.println(line);
                }
                rd.close();
                response.close();
            } catch (Exception e) {
                status.addMessage(e.getMessage());
                e.printStackTrace();
            }
            return result;
        } catch (Exception e) {
            status.addMessage(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public void get(List<List<String>> inputs, Function webservice, String urlReturnFileUrl) {
        System.out.println("get");
        //we assume the input file matches the url attributes
        List<MyAttribute> attrs = webservice.getAttributes();
        for (List<String> line : inputs) {
            String finalUrl = webservice.getUrlFormat() + "?";
            for (int i = 0; i < attrs.size(); ++i) {
                MyAttribute attr = attrs.get(i);
                System.out.println(attr.getName() + " " + attr.getShown());
                try {
                    if (attr.getShown() == true) {
                        //finalUrl += attr.getName() + "=" + line.get(i) + "&";
                        //not sure this will help
                        finalUrl += attr.getName() + "=" + URLEncoder.encode(line.get(i), "UTF-8") + "&";
                    } else if (attr.getShown() == false && attr.getValue() != null) {//default value
                        finalUrl += attr.getName() + "=" + attr.getValue() + "&";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(finalUrl);
            //return webservice lines
            String returnString = getUrl2(finalUrl, null);

            MyFileReader.writeFile(urlReturnFileUrl, returnString);
        }
    }

    public void getImage(List<List<String>> inputs, Function webservice, String urlReturnFileUrl) {
        System.out.println("get");
        //we assume the input file matches the url attributes
        List<MyAttribute> attrs = webservice.getAttributes();
        for (List<String> line : inputs) {
            String finalUrl = webservice.getUrlFormat() + "?";
            for (int i = 0; i < attrs.size(); ++i) {
                MyAttribute attr = attrs.get(i);
                System.out.println(attr.getName() + " " + attr.getShown());
                try {
                    if (attr.getShown() == true) {
                        //finalUrl += attr.getName() + "=" + line.get(i) + "&";
                        //not sure this will help
                        finalUrl += attr.getName() + "=" + URLEncoder.encode(line.get(i), "UTF-8") + "&";
                    } else if (attr.getShown() == false && attr.getValue() != null) {//default value
                        finalUrl += attr.getName() + "=" + attr.getValue() + "&";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(finalUrl);
            //return webservice lines
            webResourceImageDownloader.download(finalUrl, urlReturnFileUrl);
        }
    }

    public String getUrls() {
        String res = null;

        try {

            String url = "http://www.rcsb.org/pdb/rest/describePDB?";
            String returnFileName = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Resources\\text.txt";
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("structureId", String.valueOf("4hhb, 1hhb, 4hhc")));
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += paramString;
            HttpGet request = new HttpGet(url);
            //add request header
            //request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String returnFile = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                returnFile += line;
            }
            MyFileReader.writeFile(returnFileName, returnFile);
            rd.close();
        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        }

        return res;
    }

    public String runHttpGet(MyResource resource) {
        System.out.println(resource.toString());
        String returnFileUrl = null;
        try {
            String url = resource.getUrl();
            returnFileUrl = resource.getLocation() + resource.getUrlReturnFileName();

//                        List<NameValuePair> params = new LinkedList<NameValuePair>();
//                        
//                        for (MyAttribute attr : resource.getAttributes()) {
//                                if (!attr.getValue().isEmpty()) {
//                                        params.add(new BasicNameValuePair(attr.getName(), String.valueOf(attr.getValue())));
//                                }
//                        }
//                        
//                        String paramString = URLEncodedUtils.format(params, "utf-8");
//                        String paramString = params.toString();
            String paramString = "";
            for (MyAttribute attr : resource.getAttributes()) {
                if (!attr.getValue().isEmpty()) {
                    paramString += attr.getName() + "=" + attr.getValue() + "&";
                }
            }

            url += "?" + paramString;
            System.out.println(url);
            HttpGet request = new HttpGet(url);
            //add request header
            //request.addHeader("User-Agent", USER_AGENT);
            CloseableHttpResponse response = client.execute(request);
            try {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                String returnFile = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    if (!line.endsWith("\n")) {
                        line += "\n";
                    }
                    returnFile += line;
                }
                MyFileReader.writeFile(returnFileUrl, returnFile);
                rd.close();
            } finally {
                response.close();
            }

        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        }
        return returnFileUrl;
    }

    public String runHttpPostFake() {
        String returnFileUrl = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\tmp.txt";
        String url = "http://yeastmine.yeastgenome.org/yeastmine/template.do";
        try {

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("constraint1", "Gene.goAnnotation.ontologyTerm[GOTerm].parents[GOTerm].name"));
            params.add(new BasicNameValuePair("attributeOps(1)", "0"));
            params.add(new BasicNameValuePair("attributeValues(1)", "*ascospore*"));
            params.add(new BasicNameValuePair("name", "GOTerm_GeneOrganism"));
            params.add(new BasicNameValuePair("skipBuilder", "Show Results"));
            params.add(new BasicNameValuePair("extraValue", ""));
            params.add(new BasicNameValuePair("scope", ""));
            params.add(new BasicNameValuePair("actionType", ""));
//                        
//                        for (MyAttribute attr : resource.getAttributes()) {
//                                if (!attr.getValue().isEmpty()) {
//                                        params.add(new BasicNameValuePair(attr.getName(), String.valueOf(attr.getValue())));
//                                }
//                        }
//                        
//                        String paramString = URLEncodedUtils.format(params, "utf-8");
//                        String paramString = params.toString();
//                        String paramString = "";
//                         for (MyAttribute attr : resource.getAttributes()) {
//                                if (!attr.getValue().isEmpty()) {
//                                        paramString += attr.getName() + "=" + attr.getValue() + "&";
//                                }
//                        }

            System.out.println(url);
            HttpPost post = new HttpPost(url);
            post.setHeader("Host", "yeastmine.yeastgenome.org");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Cache-Control", "max-age=0");
            post.setHeader("Origin", "http://yeastmine.yeastgenome.org");
            post.setHeader("Upgrade-Insecure-Requests", "1");
            post.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;");
            post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Referer", "http://yeastmine.yeastgenome.org/yeastmine/template.do?name=GOTerm_GeneOrganism");

            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(post);
            try {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                String line = "";
                String returnFile = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    //some web service don't have \n we need add it
                    if (!line.endsWith("\n")) {
                        returnFile += line + "\n";
                    } else {
                        returnFile += line;
                    }
                }
                MyFileReader.writeFile(returnFileUrl, returnFile);
                rd.close();
            } finally {
                response.close();
            }

        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        }
        return returnFileUrl;
    }

    public String runHttpPost(MyResource resource) {
        System.out.println("post");
        String returnFileUrl = resource.getLocation() + resource.getUrlReturnFileName();
        String url = resource.getPostURL();
        try {

            List<NameValuePair> params = new LinkedList<NameValuePair>();

            for (MyAttribute attr : resource.getAttributes()) {
                if (!attr.getValue().isEmpty()) {
                    params.add(new BasicNameValuePair(attr.getName(), String.valueOf(attr.getValue())));
                }
            }

            System.out.println(url);
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(post);
            try {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                String line = "";
                String returnFile = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    //some web service don't have \n we need add it
                    if (!line.endsWith("\n")) {
                        returnFile += line + "\n";
                    } else {
                        returnFile += line;
                    }
                }
                MyFileReader.writeFile(returnFileUrl, returnFile);
                rd.close();
            } finally {
                response.close();
            }

        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        }
        return returnFileUrl;
    }

    public String runHttpGetREST(MyResource resource) {
        System.out.println(resource.toString());
        String returnFileUrl = null;
        try {
            String url = resource.getUrl();
            returnFileUrl = resource.getLocation() + resource.getUrlReturnFileName();

//                        List<NameValuePair> params = new LinkedList<NameValuePair>();
//                        
            for (MyAttribute attr : resource.getAttributes()) {
                if (!attr.getValue().isEmpty()) {
                    url = url.replace("{" + attr.getName() + "}", attr.getValue());
                }
            }
//                        
            System.out.println(url);
            HttpGet request = new HttpGet(url);
            //add request header
            //request.addHeader("User-Agent", USER_AGENT);
            CloseableHttpResponse response = client.execute(request);
            try {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                String returnFile = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    if (!line.endsWith("\n")) {
                        line += "\n";
                    }
                    returnFile += line;
                }
                MyFileReader.writeFile(returnFileUrl, returnFile);
                rd.close();
            } finally {
                response.close();
            }

        } catch (Exception e) {
            System.out.println("http exception");
            e.printStackTrace();
        }
        return returnFileUrl;
    }

    public List<String> get(List<List<String>> inputs, BioFlowExtractStatement script, String location, MyStatus status) {
        System.out.println("get");
        //we assume the input file matches the url attributes
        List<String> attrs = script.getSubmit();
        List<String> tempFiles = new ArrayList<>();

        for (int i = 0; i < inputs.size(); ++i) {

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
            List<String> line = inputs.get(i);
            String fileURL = location + "MyHttpClientTmpFile" + MyUtils.randomAlphaNumeric();
            tempFiles.add(fileURL);
            String finalUrl = script.getFrom() + "?";
            for (int j = 0; j < attrs.size(); ++j) {
                String attr = attrs.get(j);
                try {
                    finalUrl += attr + "=" + URLEncoder.encode(line.get(j), "UTF-8") + "&";
                } catch (Exception e) {
                    status.addMessage(e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println(finalUrl);
//                        status.addMessage(fileURL);
            String returnString = getUrl2(finalUrl, status);
            MyFileReader.writeFile(fileURL, returnString);
        }

        return tempFiles;
    }

    public List<String> post(List<List<String>> inputs, BioFlowExtractStatement script, String location, MyStatus status) {
        System.out.println("post");
        List<String> attrs = script.getSubmit();
        List<String> tempFiles = new ArrayList<>();
        try {
            for (int i = 0; i < inputs.size(); ++i) {
                String url = script.getFrom();
                String fileURL = location + "MyHttpClientTmpFile" + MyUtils.randomAlphaNumeric();;
                tempFiles.add(fileURL);
                List<String> inputLine = inputs.get(i);
                List<NameValuePair> params = new LinkedList<NameValuePair>();
                for (int j = 0; j < attrs.size(); ++j) {
                    String attr = attrs.get(j);
                    params.add(new BasicNameValuePair(attr, inputLine.get(j)));
                }

                System.out.println(url);
                status.addMessage(url);
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(params));

                CloseableHttpResponse response = client.execute(post);
                try {
                    System.out.println("Response Code : "
                            + response.getStatusLine().getStatusCode());
                    status.addMessage("Response Code : "
                            + response.getStatusLine().getStatusCode());
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    String line = "";
                    String content = "";
                    while ((line = rd.readLine()) != null) {
                        status.addMessage(line);
                        System.out.println(line);
                        //some web service don't have \n we need add it
                        if (!line.endsWith("\n")) {
                            content += line + "\n";
                        } else {
                            content += line;
                        }
                    }
                    MyFileReader.writeFile(fileURL, content);
                    rd.close();
                } catch (Exception e) {
                    status.addMessage(e.getMessage());
                } finally {
                    response.close();
                }
            }

        } catch (Exception e) {
            status.addMessage(e.getMessage());
            e.printStackTrace();
        }
        return tempFiles;
    }

    public List<String> rest(List<List<String>> inputs, BioFlowExtractStatement script, String location, MyStatus status) {
        System.out.println("rest");

        List<String> attrs = script.getSubmit();
        List<String> tempFiles = new ArrayList<>();
        try {
            for (int i = 0; i < inputs.size(); ++i) {
                String url = script.getFrom();
                String fileURL = location + "MyHttpClientTmpFile" + MyUtils.randomAlphaNumeric();;
                tempFiles.add(fileURL);
                List<String> inputLine = inputs.get(i);

                for (int j = 0; j < attrs.size(); ++j) {
                    url = url.replace("{" + attrs.get(j) + "}", inputLine.get(j));
                }

                System.out.println(url);
                status.addMessage(url);
                HttpGet request = new HttpGet(url);
                CloseableHttpResponse response = client.execute(request);
                try {
                    System.out.println("Response Code : "
                            + response.getStatusLine().getStatusCode());
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String content = "";
                    while ((line = rd.readLine()) != null) {
//                                                System.out.println(line);
                        if (!line.endsWith("\n")) {
                            line += "\n";
                        }
                        content += line;
                        status.addMessage(line);
                    }
                    MyFileReader.writeFile(fileURL, content);
//                    System.out.println(content);
                    rd.close();
                } catch (Exception e) {
                    status.addMessage(e.getMessage());
                } finally {
                    response.close();
                }
            }

        } catch (Exception e) {
            status.addMessage(e.getMessage());
            e.printStackTrace();
        }

        return tempFiles;
    }

    public List<String> httpsGet(List<List<String>> inputs, BioFlowExtractStatement script, String location, MyStatus status) {
        System.out.println("https get****");
        //we assume the input file matches the url attributes
        List<String> attrs = script.getSubmit();
        List<String> tempFiles = new ArrayList<>();

        for (int i = 0; i < inputs.size(); ++i) {
//                        for (int i = 0; i < Math.min(5, inputs.size()); ++i) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            List<String> line = inputs.get(i);
            String fileURL = location + "MyHttpClientTmpFile" + MyUtils.randomAlphaNumeric();;
            System.out.println(fileURL);
            tempFiles.add(fileURL);
            String finalUrl = script.getFrom() + "?";
            for (int j = 0; j < attrs.size(); ++j) {
                String attr = attrs.get(j);
                try {
                    finalUrl += attr + "=" + URLEncoder.encode(line.get(j), "UTF-8") + "&";
                } catch (Exception e) {
                    status.addMessage(e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println(finalUrl);
//                        status.addMessage(fileURL);
            String result = "";
            System.out.println("url: " + finalUrl);
            HttpGet request = new HttpGet(finalUrl);
            try {
                CloseableHttpResponse response = httpsClient.execute(request);
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String returnline = "";
                while ((returnline = rd.readLine()) != null) {
                    if (!returnline.endsWith("\n")) {
                        result += returnline + '\n';
                    } else {
                        result += returnline;
                    }
                    status.addMessage(returnline);
                    System.out.println(line);
                }
                response.close();
                MyFileReader.writeFile(fileURL, result);
                rd.close();
            } catch (Exception e) {
                status.addMessage(e.getMessage());
                e.printStackTrace();
                MyFileReader.writeFile(fileURL, "No Data");
            }
        }

//                        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
//                        HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
        return tempFiles;

    }
}
