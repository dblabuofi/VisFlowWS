/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mycompany.autointerfacews.algorithm.Execution;
import com.mycompany.autointerfacews.algorithm.Topk;
import com.mycompany.autointerfacews.bioflow.BioFlowParallelStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowParallelStatements;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.dataIcon.Edge;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.Library;
import com.mycompany.autointerfacews.mydata.MyGraph;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.RunData;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MyGraphHelper;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mymessage.ReturnMessage;
import com.mycompany.autointerfacews.parallel.AssignTask;
import com.mycompany.autointerfacews.parallel.ParallelExecution;
import com.mycompany.autointerfacews.parallel.TaskName;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

/**
 *
 * @author jupiter
 */
@Path("parse")
public class ParseResources {

    Gson gson;
    Topk topk;
    MyGraph myGraph;
    Execution execution;
    MyHttpClient myHttpClient;
    EXist eXist;
    ScheduledThreadPoolExecutor scheduledExecutorService;
    ThreadPoolExecutor executorService;
    BioFlowService bioFlowService;
    InputGenerator inputGenerator;
    WebResourceImageDownloader webResourceImageDownloader;
    HttpClient client;

    @Inject
    public ParseResources(Gson gson, Topk topk, MyGraph myGraph, Execution execution, MyHttpClient myHttpClient, EXist eXist,
            ScheduledThreadPoolExecutor scheduledExecutorService, ThreadPoolExecutor executorService, BioFlowService bioFlowService, InputGenerator inputGenerator, WebResourceImageDownloader webResourceImageDownloader, HttpClient client) {
        this.gson = gson;
        this.topk = topk;
        this.myGraph = myGraph;
        this.execution = execution;
        this.myHttpClient = myHttpClient;
        this.eXist = eXist;
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;
        this.bioFlowService = bioFlowService;
        this.inputGenerator = inputGenerator;
        this.webResourceImageDownloader = webResourceImageDownloader;
        this.client = client;
    }

    @GET
    @Path("runR")
    public Response testR() {
        String[] a = {"1002", "2003", "3304"};
        String code = "#!/usr/bin/env Rscript\n"
                + "args<-commandArgs(TRUE)\n"
                + "\n"
                + "arg1 <- substr(args[1], 2, nchar(args[1]) - 1);\n"
                + "mylist <- unlist(strsplit(arg1, \",\"));\n"
                + "\n"
                + "addbefore <- function(mylist) {\n"
                + "	mylist <- sapply(mylist, function(x) paste(args[2], x, sep=\"\"))\n"
                + "}\n"
                + "\n"
                + "invisible(mylist<-addbefore(mylist));\n"
                + "\n"
                + "print(mylist);";

        List<String> res = new ArrayList<>(Arrays.asList(a));
        System.out.println(gson.toJson(res));
        String[] args = new String[2];
        args[0] = gson.toJson(res).replace("\"", "");
        args[1] = "ncbi-id:";

        String fileName = MyFileReader.generateFile("", "102-20-335" + ".R", code);
        System.out.println(fileName);
//                RHelper.run(args, fileName);

        return Response.status(200)
                .entity("I am good too2     ! ")
                .build();
    }

    @GET
    @Path("runSQL")
    public Response testSQL() {
        String[] a = {"1002", "2003", "3304"};
        String code = "set @arg1 = SUBSTRING(@arg1, 2, CHAR_LENGTH(@arg1)-1);\n"
                + "DROP FUNCTION IF EXISTS SPLIT_STRING;\n"
                + "CREATE FUNCTION SPLIT_STRING(delim VARCHAR(12), str VARCHAR(255), pos INT)\n"
                + "RETURNS VARCHAR(255) DETERMINISTIC\n"
                + "RETURN\n"
                + "    REPLACE(\n"
                + "        SUBSTRING(\n"
                + "            SUBSTRING_INDEX(str, delim, pos),\n"
                + "            LENGTH(SUBSTRING_INDEX(str, delim, pos-1)) + 1\n"
                + "        ),\n"
                + "        delim, ''\n"
                + "    );\n"
                + "select  CONCAT(@arg2, SPLIT_STRING(',', @arg1, 1)),\n"
                + "		CONCAT(@arg2, SPLIT_STRING(',', @arg1, 2)),\n"
                + "		CONCAT(@arg2, SPLIT_STRING(',', @arg1, 3));";

        List<String> res = new ArrayList<>(Arrays.asList(a));
        System.out.println(gson.toJson(res));
        String[] args = new String[2];
        args[0] = gson.toJson(res).replace("\"", "");
        args[1] = "ncbi-id:";

        String finalCode = "set @arg1=\'" + args[0] + "\';set @arg2=\'" + args[1] + "\';" + code;
        String fileName = MyFileReader.generateFile("", "102-20-335" + ".sql", finalCode);

//                MySQLHelper.run(args, fileName);
        return Response.status(200)
                .entity("I am good too2     ! ")
                .build();
    }

    @GET
    @Path("runpython")
    public Response testPython() {
        String[] a = {"1002", "2003", "3304"};
        String code = "import json\n"
                + "import sys\n"
                + "\n"
                + "def addbefore():\n"
                + "	#arg[1] arrays to parse\n"
                + "	#arg[2] names to add\n"
                + "	input = json.loads(sys.argv[1]);\n"
                + "	output = [];\n"
                + "	for v in input:\n"
                + "		output.append(sys.argv[2] + str(v));\n"
                + "	print output;\n"
                + "	\n"
                + "addbefore();";
        String fileName = MyFileReader.generateFile("", "102-20-335" + ".py", code);

        List<String> res = new ArrayList<>(Arrays.asList(a));
        System.out.println(gson.toJson(res));
//                PythonHelper.run(gson.toJson(res) + " ncbi-id:", fileName);

        return Response.status(200)
                .entity("I am good too2     ! ")
                .build();
    }

    @GET
    @Path("run/resourceWS/{params}")
    public Response getResourceWS(@PathParam("params") String params) {
        String[] vals = gson.fromJson(params, String.class).split(",");
        List<MyResource> res = new ArrayList<>();
        String returnStr = null;
        //add index
        String INDEX_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/ResourceIndex/"; // Where the files are.
        String DATA_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Resources/"; // Where the Index files are.
//                Topk.deleteFileinDirectory(INDEX_FOLDER);
//                Topk.indexOnThisPath(INDEX_FOLDER); // Function for setting the Index Path
//                Topk.indexFileOrDirectory(DATA_FOLDER); // Indexing the files
//                Topk.closeIndex(); //Function for closing the files

        if (vals.length == 2) {
            List<String> files = Topk.searchInIndexAndShowResult(INDEX_FOLDER, vals[0] + "*", Integer.valueOf(vals[1]));
            for (String file : files) {
                res.add(MyFileReader.xmlToResource(file));
            }
        }
        returnStr = gson.toJson(res);
        System.out.println(returnStr);
        return Response.status(200)
                .entity(returnStr)
                .build();

    }

    @GET
    @Path("run/libraryWS/{params}")
    public Response getLibraryWS(@PathParam("params") String params) {
        String[] vals = gson.fromJson(params, String.class).split(",");
        List<Library> res = new ArrayList<>();
        String returnStr = null;
        //add index
        String INDEX_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/LibraryIndex/"; // Where the files are.
        String DATA_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Libraries/"; // Where the Index files are.
//                Topk.deleteFileinDirectory(INDEX_FOLDER);
//                Topk.indexOnThisPath(INDEX_FOLDER); // Function for setting the Index Path
//                Topk.indexFileOrDirectory(DATA_FOLDER); // Indexing the files
//                Topk.closeIndex(); //Function for closing the files

        if (vals.length == 2) {
            List<String> files = Topk.searchInIndexAndShowResult(INDEX_FOLDER, vals[0] + "*", Integer.valueOf(vals[1]));
            for (String file : files) {
                res.add(MyFileReader.xmlToLibrary(file));
            }
        }
        returnStr = gson.toJson(res);
        System.out.println(returnStr);
        return Response.status(200)
                .entity(returnStr)
                .build();

    }

    @GET
    @Path("run/functionWS/{params}")
    public Response getFunctionWS(@PathParam("params") String params) {
        String val = gson.fromJson(params, String.class);
        System.out.println(val);
        Set<Function> res = new HashSet<>();
        String returnStr = null;
        //add index
        String INDEX_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/FunctionIndex/"; // Where the files are.
        String DATA_FOLDER = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/Functions/"; // Where the Index files are.
//                Topk.deleteFileinDirectory(INDEX_FOLDER);
//                Topk.indexOnThisPath(INDEX_FOLDER); // Function for setting the Index Path
//                Topk.indexFileOrDirectory(DATA_FOLDER); // Indexing the files
//                Topk.closeIndex(); //Function for closing the files

        if (!val.isEmpty()) {
            String[] keys = val.split(",");
            for (int i = 0; i < keys.length - 1; ++i) {
                List<String> files = Topk.searchInIndexAndShowResult(INDEX_FOLDER, keys[i] + "*", Integer.valueOf(keys[keys.length - 1]));
                for (String file : files) {
                    res.add(MyFileReader.xmlToFunction(file));
                }
            }
        }
        returnStr = gson.toJson(res);
//                System.out.println(returnStr);
        return Response.status(200)
                .entity(returnStr)
                .build();

    }

    @GET
    public Response parse() {
        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("run/image/{params}")
    public Response getImage(@PathParam("params") String params) {
        String imageDataString = null;
        byte[] imageData = null;
        try {
            System.out.println(params);
            BufferedImage bufImgOne = null;
            String url = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
            File repositoryFile = new File(url + params);
            bufImgOne = ImageIO.read(repositoryFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufImgOne, "png", baos);
            imageData = baos.toByteArray();
            imageDataString = Base64.encodeBase64String(imageData);
        } catch (Exception e) {
        }
        System.out.println(imageDataString);
        return Response.status(200)
                .entity(imageDataString)
                .build();
    }

    @GET
    @Path("run/file/{params}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("params") String params) {
        System.out.println("getFile");
        File repositoryFile = null;
        try {
            System.out.println(params);

            String url = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\";

            if (new File(url + params).isFile()) {
                System.out.println(url + params);
                repositoryFile = new File(url + params);
            } else {
                String url1 = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                System.out.println(url1 + params);
                repositoryFile = new File(url1 + params);
            }
        } catch (Exception e) {
        }

        if (repositoryFile != null) {
            Response.ResponseBuilder response = Response.ok(repositoryFile, MediaType.APPLICATION_OCTET_STREAM);
            response.header("Content-Disposition", "attachment; filename=\"" + params + "\"");
            return response.build();
        } else {
            return Response.status(404)
                    .entity("something wrong")
                    .build();
        }
    }

    @GET
    @Path("run/fileToPrinter/{params}")
//        @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFileToPrinter(@PathParam("params") String params) {
        File repositoryFile = null;
        try {
            System.out.println(params);

            String url = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\librarys\\";
            if (new File(url + params).isFile()) {
                System.out.println(url + params);
                repositoryFile = new File(url + params);
            } else {
                String url1 = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
                System.out.println(url1 + params);
                repositoryFile = new File(url1 + params);
            }
        } catch (Exception e) {
        }

        if (repositoryFile != null) {
//                        Response.ResponseBuilder response = Response.ok(MyFileReader.fileToString(repositoryFile.getAbsolutePath()), MediaType.TEXT_PLAIN);
            Response.ResponseBuilder response = Response.ok(repositoryFile, MediaType.TEXT_PLAIN);
//                        response.header("Content-Disposition", "attachment; filename=\"" + params + "\"");
            return response.build();
        } else {
            return Response.status(404)
                    .entity("something wrong")
                    .build();
        }
    }

    @POST
    @Path("run/subgraph")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSubGraph(String g) throws CodeException, IOException {
        System.out.println("get subgraph");
        MyStatus status = new MyStatus();
        StatusResource.status.clear();
        StatusResource.status.add(status);
        status.addMessage("generate graph");
//         System.out.println(g);
        RunData runData = null;
        try {
            runData = gson.fromJson(g, RunData.class);

//            List<Node> nodes = runData.getNodes();
//
//            for (Node node : nodes) {
//                if (node.getType().equals("nested")) {
//                    System.out.println(node);
//                    System.out.println("tree");
//                }
//            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        List<Node> _nodes = runData.getNodes();
        List<Edge> edges = runData.getEdges();

        System.out.println("nodes");
        _nodes.stream().forEach(t -> System.out.print(t));
        System.out.println("\nedges");
        edges.stream().forEach(t -> System.out.print(t));
        status.addMessage("filter graph");
        runData = MyGraphHelper.filterNodesAndEdges(runData);
        List<Node> _nodes1 = runData.getNodes();
        List<Edge> edges1 = runData.getEdges();

        System.out.println("\nfiltered");
        //we just need nodes stops and from the beginning
        System.out.println("\nnodes");
        _nodes1.stream().forEach(t -> System.out.print(t));
        System.out.println("\nedges");
        edges1.stream().forEach(t -> System.out.print(t));
        System.out.println("");
        myGraph.setGraph(edges1);

        Map<String, Node> nodes = new HashMap<>();
        _nodes1.stream().forEach(t -> nodes.put(t.getId(), t));

        System.out.println("graph");
        myGraph.printGraph();
        status.addMessage("generate workflow");
        List<String> workflow = myGraph.topsort();
        if (workflow.size() == 0) {//means we are in the first node
            workflow.add(_nodes1.iterator().next().getId());
        }
        System.out.println("workflow");
        System.out.println(StringUtils.join(workflow.toArray(), " , "));
        System.out.println("running");
        String res = execution.run(nodes, edges1, workflow, runData.getGlobalmatch(), status);
//        String res = "";
//        System.out.println(res.length());

        System.out.println(res);

        return Response.status(200)
                .entity(res)
                .build();
    }

    static Thread thread = null;
    public static ParallelExecution parallelExecution = null;
    public static String result = null;

//    public class VisFlowThread implements Runnable {
//
//        Map<String, Node> nodes;
//        List<Edge> edges1;
//        List<String> workflow;
//        MyStatus status;
//
//        public VisFlowThread(Map<String, Node> nodes, List<Edge> edges1, List<String> workflow, MyStatus status) {
//            this.nodes = nodes;
//            this.edges1 = edges1;
//            this.workflow = workflow;
//            this.status = status;
//        }
//
//        public void run() {
//            try {
//                result = execution.run(nodes, edges1, workflow, status);
//                System.out.println(result.length());
//                System.out.println(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static List<ReturnMessage> resultList = Collections.synchronizedList(new ArrayList<ReturnMessage>());

    @POST
    @Path("run/graph")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGraph(String g) throws CodeException, IOException, InterruptedException {
        System.out.println("get Graph");
        resultList.clear();
        long startTime = System.currentTimeMillis();

        Queue<MyStatus> status = StatusResource.status;
//         System.out.println(g);
        RunData runData = null;
        try {
            runData = gson.fromJson(g, RunData.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        List<Node> _nodes = runData.getNodes();
        List<Edge> edges = runData.getEdges();
//        status.addMessage("generate graph");
        myGraph.setGraph(edges);

        Map<String, Node> nodes = new ConcurrentHashMap<>();
        List<String> tasks = Collections.synchronizedList(new ArrayList<>());

        _nodes.stream().forEach(t -> {
            nodes.put(t.getId(), t);
            tasks.add(t.getId());
        });

        System.out.println("graph");
//              myGraph.printGraph();
        //remove circles in repeat
        List<String[]> repeatPair = new ArrayList<>();
        for (Node node : _nodes) {
            if (node.getType().equals("repeat")) {
                repeatPair.add(new String[]{node.getId(), node.getActions().get(0).getRepeatNode().getId()});
            }
        }
        for (Iterator<Edge> it = edges.listIterator(); it.hasNext();) {
            Edge cur = it.next();
            for (String[] pair : repeatPair) { 
                if (cur.getFrom().equals(pair[0]) && cur.getTo().equals(pair[1])) {
                    it.remove();
                    break;
                }
            }
        }

        //new code
        //parallel bioflow code
        BioFlowParallelStatements parallelStatements = new BioFlowParallelStatements(myGraph, nodes);
        List<BioFlowParallelStatement> statements = parallelStatements.generateParallelStatement();
        statements.forEach(t -> System.out.println(t));

//        Map<String, Set<String>> nodeParents = myGraph.getNodeParents(edges);
        Map<String, Set<String>> nodeParents = myGraph.getNodeParents2(edges);
        System.out.println("nodeParents");
        for (Map.Entry<String, Set<String>> entity : nodeParents.entrySet()) {
            System.out.println(entity.getKey() + " -> " + StringUtils.join(entity.getValue(), ","));
        }
        
        Map<String, Map<String, Set<String>>> mappedRepeated = myGraph.getmappedRepeated(repeatPair, edges, nodeParents);
        System.out.println("mappedRepeated");
        System.out.println(mappedRepeated);
        
//        
        System.out.println("***********test******************");
        System.out.println(parallelExecution == null);
        parallelExecution = new ParallelExecution(
                executorService,
                bioFlowService,
                eXist,
                inputGenerator,
                gson,
                webResourceImageDownloader,
                nodeParents, tasks, nodes, edges, status, client, runData.getGlobalmatch(), mappedRepeated);
        ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(
                parallelExecution,
                0, 1000, TimeUnit.MILLISECONDS);
        parallelExecution.setFuture(future);

        long cnt = 0;
        while (!future.isDone()) {
            sleep(1000);
            ++cnt;
            if (cnt == 1200) {//1sec * 1200
                future.cancel(false);
                System.out.println("I have been canceled");
            }
        }
        
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        List<MyResource> oldInputs = new ArrayList<>();
        nodes.entrySet().stream().forEach(t -> {
            if (t.getValue().getType().equals("data")) {
                oldInputs.addAll(t.getValue().getResources());
            }
        });
        for (MyResource input : oldInputs) {
            String f = input.getResourceName();
            File file = new File(location + f + ".bak");
            if (file.exists()) {
                File newFile = new File(location + f);
                FileUtils.copyFile(file, newFile);
                file.delete();
            }
        }
        
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format(totalTime / 1000d) + " seconds");
        String res = gson.toJson(resultList);
        System.out.println("^^^^^^^^^^^^^^^^^");
        System.out.println(res);
        System.out.println("^^^^^^^^^^^^^^^^^");
        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("run/pausegraph")
    public Response pauseGraph() throws CodeException, IOException {
        System.out.println("pauseGraph");
        System.out.println(parallelExecution == null);
//        if (thread != null) {
//            try {
//                System.out.println("wait");
//                thread.suspend();
//                System.out.println("waited");
//            } catch (Exception e) {
//            }
//        }
        if (parallelExecution != null) {
            try {
                System.out.println("wait");
                parallelExecution.suspend();
                System.out.println("waited");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.status(200)
                .entity("Done")
                .build();
    }

    @GET
    @Path("run/resumegraph")
    public Response resumeGraph() throws CodeException, IOException {
        System.out.println("resumegraph");
        if (parallelExecution != null) {
            try {
                parallelExecution.resume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if (thread != null) {
//            try {
//                thread.resume();
//            } catch (Exception e) {
//            }
//        }
        return Response.status(200)
                .entity("Done")
                .build();
    }

    @GET
    @Path("run/stopgraph")
    public Response stopGraph() throws CodeException, IOException {
        System.out.println("stopGraph");
        if (parallelExecution != null) {
            try {
                parallelExecution.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if (thread != null) {
//            try {
//                thread.stop();
//            } catch (Exception e) {
//            }
//        }
        return Response.status(200)
                .entity("Done")
                .build();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("run/graph2")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGraph2(String g) throws CodeException, IOException, InterruptedException {
        System.out.println("get Graph");
        long startTime = System.currentTimeMillis();

        Queue<MyStatus> status = StatusResource.status;
//         System.out.println(g);

//*******************************************************
        
//********************************************************************************
RunData runData = null;
        try {
            runData = gson.fromJson(g, RunData.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        List<Node> _nodes = runData.getNodes();
        List<Edge> edges = runData.getEdges();
//        status.addMessage("generate graph");
        myGraph.setGraph(edges);

        Map<String, Node> nodes = new ConcurrentHashMap<>();
        List<String> tasks = Collections.synchronizedList(new ArrayList<>());

        _nodes.stream().forEach(t -> {
            nodes.put(t.getId(), t);
            tasks.add(t.getId());
        });

        System.out.println("graph");
//              myGraph.printGraph();
        //remove circles in repeat
        List<String[]> repeatPair = new ArrayList<>();
        for (Node node : _nodes) {
            if (node.getType().equals("repeat")) {
                repeatPair.add(new String[]{node.getId(), node.getActions().get(0).getRepeatNode().getId()});
            }
        }
        for (Iterator<Edge> it = edges.listIterator(); it.hasNext();) {
            Edge cur = it.next();
            for (String[] pair : repeatPair) { 
                if (cur.getFrom().equals(pair[0]) && cur.getTo().equals(pair[1])) {
                    it.remove();
                    break;
                }
            }
        }

        //new code
        //parallel bioflow code
        BioFlowParallelStatements parallelStatements = new BioFlowParallelStatements(myGraph, nodes);
        List<BioFlowParallelStatement> statements = parallelStatements.generateParallelStatement();
        statements.forEach(t -> System.out.println(t));

//        Map<String, Set<String>> nodeParents = myGraph.getNodeParents(edges);
        Map<String, Set<String>> nodeParents = myGraph.getNodeParents2(edges);
        System.out.println("nodeParents");
        for (Map.Entry<String, Set<String>> entity : nodeParents.entrySet()) {
            System.out.println(entity.getKey() + " -> " + StringUtils.join(entity.getValue(), ","));
        }
        
        Map<String, Map<String, Set<String>>> mappedRepeated = myGraph.getmappedRepeated(repeatPair, edges, nodeParents);
        System.out.println("mappedRepeated");
        System.out.println(mappedRepeated);

        ParallelExecution parallelExecution = new ParallelExecution(
                executorService,
                bioFlowService,
                eXist,
                inputGenerator,
                gson,
                webResourceImageDownloader,
                nodeParents, tasks, nodes, edges, status, client, runData.getGlobalmatch(), mappedRepeated);
        parallelExecution.setSubClass(true);
        ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(
                parallelExecution,
                0, 1000, TimeUnit.MILLISECONDS);
        parallelExecution.setFuture(future);

        long cnt = 0;
        while (!future.isDone()) {
            sleep(1000);
            ++cnt;
            if (cnt == 1200) {//1sec * 1200
                future.cancel(false);
                System.out.println("I have been canceled");
            }
        }
        
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        List<MyResource> oldInputs = new ArrayList<>();
        nodes.entrySet().stream().forEach(t -> {
            if (t.getValue().getType().equals("data")) {
                oldInputs.addAll(t.getValue().getResources());
            }
        });
        for (MyResource input : oldInputs) {
            String f = input.getResourceName();
            File file = new File(location + f + ".bak");
            if (file.exists()) {
                File newFile = new File(location + f);
                FileUtils.copyFile(file, newFile);
                file.delete();
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format(totalTime / 1000d) + " seconds");
        String res = gson.toJson(resultList);
        System.out.println("^^^^^^^^^^^^^^^^^");
        System.out.println(res);
        System.out.println("^^^^^^^^^^^^^^^^^");
        return Response.status(200)
                .entity(res)
                .build();
    }

}
