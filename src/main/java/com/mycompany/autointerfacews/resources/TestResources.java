/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mycompany.autointerfacews.algorithm.Execution;
import com.mycompany.autointerfacews.algorithm.Topk;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.gordian.GordianAlgorithm;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.mydata.MyGraph;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.utils.MyUtils;
import com.mycompany.visflowsmatch.IMatchManager;
import com.mycompany.visflowsmatch.MatchManager;
import com.mycompany.visflowsmatch.SMatchException;
import com.mycompany.visflowsmatch.classifiers.CNFContextClassifier;
import com.mycompany.visflowsmatch.classifiers.IContextClassifier;
import com.mycompany.visflowsmatch.data.mappings.HashMapping;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.mappings.IMappingElement;
import com.mycompany.visflowsmatch.data.mappings.IMappingFactory;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import com.mycompany.visflowsmatch.deciders.ISATSolver;
import com.mycompany.visflowsmatch.deciders.SAT4J;
import com.mycompany.visflowsmatch.elements.IElementMatcher;
import com.mycompany.visflowsmatch.filters.IMappingFilter;
import com.mycompany.visflowsmatch.filters.SPSMMappingFilter;
import com.mycompany.visflowsmatch.matchers.element.IStringBasedElementLevelSemanticMatcher;
import com.mycompany.visflowsmatch.matchers.element.RunnableElementMatcher;
import com.mycompany.visflowsmatch.matchers.element.string.EditDistanceOptimized;
import com.mycompany.visflowsmatch.matchers.element.string.NGram;
import com.mycompany.visflowsmatch.matchers.element.string.Prefix;
import com.mycompany.visflowsmatch.matchers.element.string.Suffix;
import com.mycompany.visflowsmatch.matchers.element.string.Synonym;
import com.mycompany.visflowsmatch.matchers.structure.node.DefaultNodeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.node.INodeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.tree.spsm.SPSMTreeMatcher;
import com.mycompany.visflowsmatch.oracles.ILinguisticOracle;
import com.mycompany.visflowsmatch.oracles.ISenseMatcher;
import com.mycompany.visflowsmatch.oracles.wordnet.WordNet;
import com.mycompany.visflowsmatch.preprocessors.IContextPreprocessor;
import com.mycompany.visflowsmatch.preprocessors.RunnableDefaultContextPreprocessor;
import com.mycompany.visflowsmatch.structure.tree.ITreeMatcher;
//import flanagan.analysis.CurveSmooth;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author jupiter
 */
@Path("test")
public class TestResources {

    Gson gson;
    Topk topk;
    MyGraph myGraph;
    Execution execution;
    MyHttpClient myHttpClient;
    EXist eXist;
    IMatchManager mm;

    @Inject
    public TestResources(Gson gson, Topk topk, MyGraph myGraph, Execution execution, MyHttpClient myHttpClient, EXist eXist, IMatchManager mm) {
        this.gson = gson;
        this.topk = topk;
        this.myGraph = myGraph;
        this.execution = execution;
        this.myHttpClient = myHttpClient;
        this.eXist = eXist;
        this.mm = mm;

    }

    @GET
    @Path("bwa")
    public Response runBWA() {
        final String BWALocation = "bwa-0.7.15/";
        final String libraryLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys";
        final String dataLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/data/tavernadata";
        String res = "";
        String[] env = new String[]{"path=%PATH%;C:/cygwin64/bin/"};

        try {
//                        Process p = Runtime.getRuntime().exec(
//                                "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\a.exe"
//                                "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\bwa mem genome.fa P1.R1.fastq.gz P1.R2.fastq.gz > bam.mem"
//                                "ls -la"
//                        );
//                        Process p = Runtime.getRuntime().exec(
////                                "C:/cygwin64/bin/bash.exe -c 'cd C:/cygwin64/home/jupiter/bwa-0.7.15; ./bwa mem data/genome.fa data/P1.R1.fastq.gz data/P1.R2.fastq.gz > bam2.mem'", 
//                                "C:/cygwin64/bin/bash.exe -c 'cd C:/cygwin64/home/jupiter/bwa-0.7.15;ls -la '",
//                                env
//                        );
//                        p.waitFor();
            System.out.println("waiting   i i i i i");
//                        final String folderLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\";
            final String folderLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys/";
//                        String str =  folderLocation + "bwa mem "+ folderLocation +"genome.fa "+folderLocation+"P1.R1.fastq.gz "+folderLocation+"P1.R2.fastq.gz";
            String str = folderLocation + "bwa ";
            System.out.println(str);
//                        Process p = Runtime.getRuntime().exec(
//                                new String[]{
//                                        "C:\\cygwin64\\bin\\bash.exe",
//                                        "-c",
//                                        folderLocation + "a >" + folderLocation + "outputfile.txt"  
//                                }
//                        );
//                        Process p = Runtime.getRuntime().exec(
//                                new String[]{
//                                       str,
//                                       "mem",
//                                       folderLocation + "genome.fa",
//                                       folderLocation + "P1.R1.fastq.gz",
//                                       folderLocation + "P1.R2.fastq.gz",
//                                }
//                        );
            File output = new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\outputfile.txt");
            File input = new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\inputfile.txt");
//                        ProcessBuilder pb =
////                                   new ProcessBuilder("C:/cygwin64/bin/bash.exe", "-c", " 'cd C:/cygwin64/home/jupiter ; pwd' ").inheritIO();
//                                   new ProcessBuilder(new String[]{
////                                        "C:/cygwin64/bin/bash.exe",
////                                           "-c",
////                                        "ls"
//                                           "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\a.exe", 
//                                        }
//                                   ).inheritIO();
            ProcessBuilder pb
                    = //                                   new ProcessBuilder("C:/cygwin64/bin/bash.exe", "-c", " 'cd C:/cygwin64/home/jupiter ; pwd' ").inheritIO();
                    new ProcessBuilder(new String[]{
                str,
                "mem",
                folderLocation + "genome.fa",
                folderLocation + "P1.R1.fastq.gz",
                folderLocation + "P1.R2.fastq.gz"
            }
                    ).inheritIO();
            pb.redirectOutput(output);
//                        pb.redirectInput(input);
            Process p = pb.start();

            System.out.println("waiting");
            p.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while (in.ready()) {
                System.out.println(in.readLine());
            }
            System.out.println("**************************************************");
            while (error.ready()) {
                System.out.println(error.readLine());
            }

//                        String line;
//                        while ((line = in.readLine()) != null) {
//                                res += line;
//                                System.out.println(line);
//                        }
            in.close();
        } catch (Exception e) {
            System.out.println("something wrong");
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("picard")
    public Response runPicard() {
        final String BWALocation = "bwa-0.7.15/";
        final String libraryLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys";
        final String dataLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/data/tavernadata";
//                final String folderLocation = "/cygdrive/C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys/";
        final String folderLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys/";
//                        String str =  folderLocation + "bwa mem "+ folderLocation +"genome.fa "+folderLocation+"P1.R1.fastq.gz "+folderLocation+"P1.R2.fastq.gz";
        String str = "java -jar picard.jar SortSam I=" + folderLocation + "bam.mem O=" + folderLocation + "Sorted_bam SO=coordinate";
        try {
            ProcessBuilder pb
                    = //                                   new ProcessBuilder("C:/cygwin64/bin/bash.exe", "-c", " 'cd C:/cygwin64/home/jupiter ; pwd' ").inheritIO();
                    new ProcessBuilder(new String[]{
                "java",
                "-jar",
                folderLocation + "picard.jar",
                "SortSam",
                "I=" + folderLocation + "bam.mem",
                "O=" + folderLocation + "Sorted_bam",
                "SO=coordinate"
            }
                    ).inheritIO();

            Process p = pb.start();
            System.out.println("waiting");
            p.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while (in.ready()) {
                System.out.println(in.readLine());
            }
            System.out.println("**************************************************");
            while (error.ready()) {
                System.out.println(error.readLine());
            }
            in.close();
        } catch (Exception e) {
            System.out.println("something wrong");
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("bash")
    public Response runBash() {
        final String BWALocation = "bwa-0.7.15/";
        final String libraryLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys";
        final String dataLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/data/tavernadata";
//                final String folderLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\";
        final String folderLocation = "/cygdrive/c/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/librarys/";
//                        String str =  folderLocation + "bwa mem "+ folderLocation +"genome.fa "+folderLocation+"P1.R1.fastq.gz "+folderLocation+"P1.R2.fastq.gz";
//                String str = "C:\\cygwin64\\bin\\bash.exe " + folderLocation + "run.sh" ;
        String str = "C:\\cygwin64\\bin\\bash " + folderLocation + "run.sh";
        System.out.println(str);
        try {
            ProcessBuilder pb
                    = //                                   new ProcessBuilder("C:/cygwin64/bin/bash.exe", "-c", " 'cd C:/cygwin64/home/jupiter ; pwd' ").inheritIO();
                    new ProcessBuilder(new String[]{
                "C:\\cygwin64\\bin\\bash",
                folderLocation + "run.sh"
            }
                    ).inheritIO();

            Process p = pb.start();
            System.out.println("waiting");
            p.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while (in.ready()) {
                System.out.println(in.readLine());
            }
            System.out.println("**************************************************");
            while (error.ready()) {
                System.out.println(error.readLine());
            }
            in.close();
        } catch (Exception e) {
            System.out.println("something wrong");
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }
    
    @GET
    @Path("python")
    public Response runPython() {
        try {
            
        } catch (Exception e) {
            System.out.println("something wrong");
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }
    
    @GET
    @Path("file")
    public Response getFile() {

        File file = new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\run.sh");

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"run.sh\"");
        return response.build();

    }

    @GET
    @Path("xmltojson")
    public Response getxml() {

        System.out.println(MyFileReader.xmlToLibrary("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Libraries\\KEGG API.xml"));

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("search")
    public Response getSearch() {
        //add index
        String INDEX_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/FunctionIndex/"; // Where the files are.
        String DATA_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Functions/"; // Where the Index files are.
//                Topk.deleteFileinDirectory(INDEX_FOLDER);
//                Topk.indexOnThisPath(INDEX_FOLDER); // Function for setting the Index Path
//                Topk.indexFileOrDirectory(DATA_FOLDER); // Indexing the files
//                Topk.closeIndex(); //Function for closing the files

        List<String> files = Topk.searchInIndexAndShowResult(INDEX_FOLDER, "kegg", 5);
        for (String file : files) {
            System.out.println(file);
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("exists")
    public Response getExists() throws CodeException {

        String[] args = new String[2];
        args[0] = "/db/apps/flowq/index";
        args[1] = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\hhbc.xml";
        eXist.put(args);

        String script = "let $source-doc := doc(\"databaseindexs.xml\")//resource/location\n"
                + "let $filename := \"aaaa.xml\"\n"
                + "let $target-directory := \"C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\\"\n"
                + "\n"
                + "let $target-path := concat($target-directory, $filename)\n"
                + "\n"
                + "\n"
                + "return \n"
                + "    file:serialize($source-doc, $target-path, (\"omit-xml-declaration=yes\", \"indent=yes\"))";
//                    String script = "let $source-doc := doc(\"databaseindexs.xml\")//resource/location\n" +
//"let $filename := \"aaaa.xml\"\n" +
//"let $target-directory := \"C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\\"\n" +
//"\n" +
//"let $target-path := concat($target-directory, $filename)\n" + 
//"return true;";
//                    String script = "doc('databaseindexs.xml')//resource/location";
//                    String script = "doc('databaseindexs.xml')//resource/location";
        MyStatus status = new MyStatus();
        args[1] = script;
        eXist.runQuery2(args, status);

//                    eXist.runXMLQuery("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\xqueryRun.xml");
//                    eXist.runXMLQuery(script);
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("ftp")
    public Response getFTP() {

        try {
            String ftpHost = "ftp.ncbi.nlm.nih.gov";
            int ftpPort = 22;
            String ftpUserName = "anonymous";
            String ftpPassword = "anonymous";
            String ftpRemoteDirectory = "/genomes/Apis_cerana/protein/protein.fa.gz";
            String fileToTransmit = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\protein.fa.gz";
            System.out.println("Creating session.");
            JSch jsch = new JSch();

            Session session = null;
            Channel channel = null;
            ChannelSftp c = null;

            //
            // Now connect and SFTP to the SFTP Server
            //
            try {
                // Create a session sending through our username and password
                session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
                System.out.println("Session created.");
                session.setPassword(ftpPassword);

                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                System.out.println("Session connected before.");
                session.connect();
                System.out.println("Session connected.");

                System.out.println("OPEN SFTP CHANNEL");
                //
                // Open the SFTP channel
                //
                System.out.println("Opening Channel.");
                channel = session.openChannel("sftp");
                channel.connect();
                c = (ChannelSftp) channel;
                System.out.println("Now checing status");
            } catch (Exception e) {
                System.err.println("Unable to connect to FTP server."
                        + e.toString());
                throw e;
            }

            //
            // Change to the remote directory
            //
            System.out.println("Now performing operations");
            ftpRemoteDirectory = "/home/pooja111/";
            System.out.println("Changing to FTP remote dir: "
                    + ftpRemoteDirectory);
            c.cd(ftpRemoteDirectory);

            //
            // Send the file we generated
            //
            try {
                File f = new File(fileToTransmit);
                System.out.println("Storing file as remote filename: "
                        + f.getName());
                c.put(new FileInputStream(f), f.getName());
            } catch (Exception e) {
                System.err
                        .println("Storing remote file failed." + e.toString());
                throw e;
            }

            //
            // Disconnect from the FTP server
            //
            try {
                c.quit();
            } catch (Exception exc) {
                System.err.println("Unable to disconnect from FTPserver. "
                        + exc.toString());
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("ftp2")
    public Response getFTP2() {

        String server = "ftp.ddbj.nig.ac.jp";
        int port = 21;
        String user = "anonymous";
        String pass = "anonymous";

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // APPROACH #1: using retrieveFile(String, OutputStream)
//                        String remoteFile1 = "/genomes/Apis_cerana/protein/proten.fa.gz";
            String remoteFile1 = "/ddbj_database/biosample/ddbj_biosample_set.xml.gz";
            File downloadFile1 = new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\proten.fa.gz");
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();

            if (success) {
                System.out.println("File #1 has been downloaded successfully.");
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

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("resourceText")
    public Response getresourceText() {
        String resourcesLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Resources";

        List<MyResource> resources = MyFileReader.getResources(resourcesLocation);
        System.out.println(resources.get(0).getUrlReturnFileSchema());

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("http")
    public Response getresourceHttp() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        MyHttpClient myHttpClient = new MyHttpClient(client);

        myHttpClient.getUrl("http://www.rcsb.org/pdb/rest/smilesQuery?smiles=NC(=O)C1=CC=CC=C1&search_type=exact&");

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("httppost")
    public Response getHTTPpost() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        MyHttpClient myHttpClient = new MyHttpClient(client);

//                myHttpClient.getUrl("http://www.rcsb.org/pdb/rest/smilesQuery?smiles=NC(=O)C1=CC=CC=C1&search_type=exact&");
        myHttpClient.runHttpPostFake();

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("gettextfile")
    public Response getTEXTfile() {
        String fileURL = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\anno.txt";

        List<List<String>> res = MyFileReader.readFile(fileURL, "\t");

        for (String s : res.get(0)) {
            System.out.println(s);
        }
        for (String s : res.get(1)) {
            System.out.println(s);
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("conCSVtoXML")
    public Response getCSVtoXML() {
        String in = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\RCSB PDB Chemical Structure search Input.csv";
        String out = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\RCSB PDB Chemical Structure search Input.xml";

        MyFileReader.convertCSVtoXML(in, out, "search", ",");

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("readnoto")
    public Response getNoto() {
        String in = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\noto.csv";

        MyFileReader.readCSV(in);

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("driverLocal")
    public Response getDriverLocal() {
        String in = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\MyHttpClientTmpFile0.html";

        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "C:\\Program Files\\phantomjs-2.0.0\\bin\\phantomjs.exe"
        );
        WebDriver driver = new PhantomJSDriver(caps);

//                driver.get("file:///" + in);
        driver.get("file:///C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/data/MyHttpClientTmpFile0");
//                driver.get("file:///C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/data/MyHttpClientTmpFile0.html");
        saveFile(driver, "test");
//                WebElement element = driver.findElement(By.tagName("form"));
//                System.out.println(element.getAttribute("action"));
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    public void saveFile(WebDriver driver, String fileName) {
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\" + fileName + ".png"));
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }

    @GET
    @Path("xmlTocsv")
    public Response getxmlTocsv() {
        String xml = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustudents.xml";
        String csv = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustudents.csv";

        MyFileReader.converXMLtoCSV(xml, csv);

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("csvToXML")
    public Response getcsvToXML() {
        String csv = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustudents.csv";
        String xml = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustuds.xml";

        MyFileReader.convertCSVtoXML(csv, xml);

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("convertXMLAttrs")
    public Response getConverXMLAttribute() {
        String xml = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustuds.xml";
        String xml1 = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\wsustuds1.xml";
        try {
            MyFileReader.convertXMLAttribute(xml, xml1, "age", "Boolean");
        } catch (Exception e) {

        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("convertCSVAttrs")
    public Response getconvertCSVAttribute() {
        String xml = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\notoC.csv";
        String xml1 = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\notoC1.csv";
        try {
            MyFileReader.convertCSVAttribute(xml, xml1, "Locus", "Boolean");
        } catch (Exception e) {
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("gordian")
    public Response getGordian() {
        String file = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gordian.csv";
        try {
            GordianAlgorithm.getKeys(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("jsonToXML")
    public Response getJSONtoXML() {
        String file = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gsspLabels.json";
        String fileXML = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\gsspLabels.xml";
        try {
            MyFileReader.converJSONtoXML(file, fileXML);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("testhttps")
    public Response getHTTPS() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sc,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();

            HttpGet httpget = new HttpGet("https://gws.gplates.org/reconstruct/reconstruct_points/?points=95,54&time=140");
//                        HttpGet httpget = new HttpGet("https://gws.gplates.org/reconstruct/reconstruct_points/?points=-42.3200%2C75.1000&time=0.0117&");

            System.out.println("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse response = httpclient.execute(httpget);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String result = "";
            while ((line = rd.readLine()) != null) {
                if (!line.endsWith("\n")) {
                    result += line + '\n';
                } else {
                    result += line;
                }
                System.out.println(line);
            }
            response.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("smooth")
    public Response getSmooth() {
        double[] xData = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        double[] yData = {0, 900, 800, 700, 600, 500, 400, 8, 9};

//        CurveSmooth csm = new CurveSmooth(xData, yData);
//
//        Double average = Arrays.stream(yData).average().getAsDouble();
//
//        int sgFilterWidth = average.intValue();
//        System.out.println(sgFilterWidth);
//////                double[] smoothedData = csm.savitzkyGolay(sgFilterWidth);
////                csm.savitzkyGolay(sgFilterWidth, 2);
////                csm.savitzkyGolayPlusFirstDeriv(sgFilterWidth);
////                double[] smoothedData = csm.getSavitzkyGolayDerivatives(1);
//////                 smoothedData = csm.getSavitzkyGolaySmoothedValues();
////                for (int i = 0; i < smoothedData.length; ++i) {
////                        System.out.println(smoothedData[i]);
////                }
////                csm.savitzkyGolayPlusSecondDeriv(sgFilterWidth);
////                smoothedData = csm.getSavitzkyGolayDerivatives(2);
//////                 smoothedData = csm.getSavitzkyGolaySmoothedValues();
////                for (int i = 0; i < smoothedData.length; ++i) {
////                        System.out.println(smoothedData[i]);
////                }
//
////                double[] smoothedData = csm.movingAverage(sgFilterWidth);
//        double[] smoothedData = csm.movingAverage(10);
//        for (int i = 0; i < smoothedData.length; ++i) {
//            System.out.println(smoothedData[i]);
//        }

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("matchFunction")
    public Response getMatch() {
        try {

            System.out.println("Creating source context...");
            String resourcesLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Resources";
            List<IContext> contexts = new ArrayList<>();
            List<MyResource> array = new ArrayList<>();
            array = MyFileReader.getResources(resourcesLocation).subList(0, 1);

            for (MyResource resource : array) {
//                                addedResources.add(resource);
                System.out.println("resource name: " + resource.getResourceName());
                IContext context = mm.createContext();

                INode dummiRoot = context.createRoot(resource.getResourceName());
                INode root = MyFileReader.mapResourceSchemaToINode(dummiRoot, resource.getUrlReturnFileSchema());
                context.setRoot(root);
                contexts.add(context);
            }

            IContext s = mm.createContext();
            INode rootNode = s.createRoot("Updated");

            for (int i = 0; i < contexts.size(); ++i) {
                System.out.println("Matching...");
                System.out.println(array.get(i));
                MyUtils.printContext(s);
                System.out.println("**********");
                MyUtils.printContext(contexts.get(i));

                IContextMapping<INode> result = mm.match(contexts.get(i), s);

                System.out.println("Processing results...");
                System.out.println("Printing matches:");

                for (IMappingElement<INode> e : result) {
                    System.out.println(e.getSource().nodeData().getName() + "\t" + e.getRelation() + "\t" + e.getTarget().nodeData().getName());
                }

                System.out.println("Done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("jsontoxml")
    public Response getjsonToxml() {
        List<String> heads = Arrays.asList(new String[]{"lat", "lng"});
        MyFileReader.converJSONtoTable(
                "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\MyHttpClientTmpFile5",
                "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\MyHttpClientTmpFile5.csv",
                "<gplates><coordinates><array>lat</array><array>lng</array></coordinates><type>type</type></gplates>",
                heads
        );

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("/xmlTest")
    public Response testXML() throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\ageTable.xml");
        List<Node> elements = document.selectNodes("/Result/results/bindings/coordinates/value");
        System.out.println(elements.size());
        for (Node e : elements) {
            System.out.println(e.getName() + "$$" + "$$" + e.getStringValue());
        }
//            System.out.println("***********");
//            List<Node> elements2 = document.selectNodes("//*:CssParameter");
//            List<Node> elements2 = document.selectNodes("*[local-name()='CssParameter']");
        List<Node> elements2 = document.selectNodes("//*[local-name()='CssParameter']");
        System.out.println(elements2.size());
        for (Node e : elements2) {
            System.out.println(e.getName() + "$$" + "$$" + e.getStringValue());
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }
    
    
     @GET
    @Path("/cube")
    public Response getCube() throws Exception {
        
        String hostname = "Unknown";
        InetAddress addr;
        String mysqlURL = "";
        try {
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            mysqlURL = hostname.contains("CS-PREC3620HJ") ? "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql"
                    : "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.7\\bin\\mysql";
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
                "select * from cuberes",
                "flowqdb"
            }
//                        ).inheritIO();
            ); 
            pb.directory(new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data").getAbsoluteFile());
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            StringJoiner sj = new StringJoiner("\n");
            in.lines().iterator().forEachRemaining(sj::add);
            
             System.out.println(sj.toString());
            p.waitFor();
            in.close();

        } catch (Exception e) {
            throw new CodeException("MySQLHelper function runscript falied: ", e);
        }
        
        return Response.status(200)
                .entity("I am good")
                .build();

    }
    
    
}
