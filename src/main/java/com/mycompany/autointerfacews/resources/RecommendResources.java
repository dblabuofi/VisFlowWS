/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.resources;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.dataIcon.AttrMatch;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.ISchema;
import com.mycompany.autointerfacews.dataIcon.Library;
import com.mycompany.autointerfacews.dataIcon.MapAttr;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.gordian.GordianAlgorithm;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.autointerfacews.smtch.SMatch;
import com.mycompany.autointerfacews.utils.MyUtils;
import com.mycompany.visflowsmatch.IMatchManager;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.mappings.IMappingElement;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.XPathSyntaxException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;

/**
 *
 * @author jupiter
 */
@Path("recommend")
public class RecommendResources {

    Gson gson;
    IMatchManager mm;
    IMatchManager manager;
    String resourcesLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Resources";
    List<IContext> resContexts = new ArrayList<>();
    List<MyResource> resources = new ArrayList<>();
    Set<MyResource> addedResources;
    String libLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Libraries";
    List<IMatchManager> libManagers;
    List<IContext> libContexts;
    List<Library> libraries;
    SMatch sMatch;
    InputGenerator inputGenerator;

//        String funcLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Functions";
//        List<IMatchManager> funcManagers;
//        List<IContext> funcContexts;
//        List<Function> functions;
    String funcLocation = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\Functions";
    List<IContext> funcContexts = new ArrayList<>();
    List<Function> functions = new ArrayList<>();

//        @Inject
//        public RecommendResources(Gson gson, IMatchManager mm,
//                 @Named("resContext") List<IContext> resContexts,
//                 @Named("resource") List<MyResource> resources,
//                 @Named("funcContext") List<IContext> funcContexts,
//                 @Named("function") List<Function> functions) {
//                this.gson = gson;
//                this.mm = mm;
//                this.resContexts = resContexts;
//                this.resources = resources;
//                this.funcContexts = funcContexts;
//                this.functions = functions;
    @Inject
    public RecommendResources(Gson gson,
            IMatchManager mm,
            SMatch sMatch,
            InputGenerator inputGenerator
    //                @Named("manager") IMatchManager manager 
    ) {
        this.gson = gson;
        this.mm = mm;
        this.sMatch = sMatch;
        this.inputGenerator = inputGenerator;
//                this.manager = manager;

        resources = MyFileReader.getResources(resourcesLocation);

        for (MyResource resource : resources) {
//                                addedResources.add(resource);
            System.out.println("resource name: " + resource.getResourceName());
            IContext context = mm.createContext();

            INode root = context.createRoot(resource.getResourceName());
            if (!resource.getUrlReturnFileSchema().isEmpty()) {
                root = MyFileReader.mapResourceSchemaToINode(root, resource.getUrlReturnFileSchema());
            }
//            if (resource.getDescription() != null) {
//                root.createChild(resource.getDescription());
//            }
            context.setRoot(root);
            resContexts.add(context);
        }

        //function
        //read library files and add to match manager
        functions = MyFileReader.getFunctions(funcLocation);

        for (Function function : functions) {
//                                System.out.println("function name: " + function.getLibraryName());
            IContext context = mm.createContext();

            INode root = context.createRoot(function.getFunctionName());
            if (function.getAttributes() != null) {
                for (MyAttribute attr : function.getAttributes()) {
                    root.createChild(attr.getName());
                }
            }
            funcContexts.add(context);
        }

    }

    @GET
    @Path("resourceWS")
    public Response getRecommandResouce(
            @QueryParam("queryTree") String queryTreeStr,
            @QueryParam("matchType") String matchType,
            @QueryParam("topk") Integer k,
            @QueryParam("username") String username) {
        System.out.println("query Resource Running " + queryTreeStr + "\nmatchType: " + matchType + " " + k);
        String res = "";
        try {
            System.out.println("Creating source context...");

            if (username != null) {
                List<MyResource> userResources = new ArrayList<>();
                String userLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/" + username + "/Resources/";
                userResources = MyFileReader.getResources(userLocation);
                for (MyResource resource : userResources) {
                    System.out.println("resource name: " + resource.getResourceName());
                    IContext context = mm.createContext();
                    INode root = context.createRoot(resource.getResourceName());
                    if (!resource.getUrlReturnFileSchema().isEmpty()) {
                        root = MyFileReader.mapResourceSchemaToINode(root, resource.getUrlReturnFileSchema());
                    }
                    context.setRoot(root);
                    resContexts.add(context);
                }
                resources.addAll(userResources);
            }

            IContext s = mm.createContext();
//                        INode sroot = s.createRoot("uofistudents");
//                        sroot.createChild("student");

            QueryTree tree = gson.fromJson(queryTreeStr, QueryTree.class);
            int totalNum = tree.getNums(tree);
            tree.generateIContext(s);

            res = recommendFunction(tree, matchType, k, mm, resContexts, resources, s, totalNum);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200).entity(res).build();
    }

    @GET
    @Path("functionWS")
    public Response getFunction(
            @QueryParam("queryTree") String queryTreeStr,
            @QueryParam("matchType") String matchType,
            @QueryParam("topk") Integer k,
            @QueryParam("library") String library,
            @QueryParam("username") String username) {
        System.out.println("query Library Running " + queryTreeStr + "\nmatchType: " + matchType + " " + k);
        String res = "";
        try {
            System.out.println("Creating source context...");

            if (username != null) {
                List<Function> userFunctions = new ArrayList<>();
                String userLocation = "C:/Users/jupiter/Documents/NetBeansProjects/AutoInterfaceWS/" + username + "/Functions/";
                userFunctions = MyFileReader.getFunctions(userLocation);

                for (Function function : userFunctions) {
                    IContext context = mm.createContext();
                    INode root = context.createRoot(function.getFunctionName());
                    if (function.getAttributes() != null) {
                        for (MyAttribute attr : function.getAttributes()) {
                            root.createChild(attr.getName());
                        }
                    }
                    funcContexts.add(context);
                }
                functions.addAll(userFunctions);
            }

            IContext s = mm.createContext();

            QueryTree tree = gson.fromJson(queryTreeStr, QueryTree.class);
            int totalNum = tree.getNums(tree);
            INode sroot = s.createRoot(tree.getText());
            tree.generateINodes(sroot);

            res = recommendFunction(tree, matchType, k, mm, funcContexts, functions, s, totalNum);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200).entity(res).build();
    }
//        @GET
//        @Path("libraryWS")
//        public Response getLibrary(
//              @QueryParam("queryTree") String queryTreeStr,
//              @QueryParam("matchType") String matchType,
//              @QueryParam("topk") Integer k) {
//                System.out.println("query Library Running " + queryTreeStr + "\nmatchType: " + matchType + " " + k);
//                String res = "";
//                try {
//                        System.out.println("Creating source context...");
//                        IContext s = mm.createContext();
////                        INode sroot = s.createRoot("uofistudents");
////                        sroot.createChild("student");
//
//                        QueryTree tree = gson.fromJson(queryTreeStr, QueryTree.class);
//                        int totalNum = tree.getNums(tree);
//                        tree.generateIContext(s);
//
//                        res = recommendFunction(tree, matchType, k, libManagers, libContexts, libraries, s, totalNum);
//
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
//
//                return Response.status(200).entity(res).build();
//        }

    <T extends ISchema> String recommendFunction(QueryTree tree, String matchType, Integer k,
            IMatchManager manager, List<IContext> contexts, List<T> array, IContext s, Integer totalNum) throws Exception {

        System.out.println("************************");
        System.out.println(contexts.size());

        String res = "";
        List<T> returnTemps = null;
        if (matchType.equals("direct")) {
            Map<T, Integer> map = new HashMap<>();//map for sorting

            //it's a bug that you have to use the manager so many times???
            for (int i = 0; i < contexts.size(); ++i) {
                System.out.println("Matching...");

                IContextMapping<INode> result = manager.match(s, contexts.get(i));

                System.out.println("Processing results...");
                System.out.println("Printing matches:");
                for (IMappingElement<INode> e : result) {
                    System.out.println(e.getSource().nodeData().getName() + "\t" + e.getRelation() + "\t" + e.getTarget().nodeData().getName());
                }
                if (result.size() > 0) {
                    Integer count = 0;
                    for (IMappingElement<INode> e : result) {
                        if (e.getTarget().nodeData().getName().equals(e.getSource().nodeData().getName())) {
                            ++count;
                        }
                    }
                    if (count == totalNum) {
                        map.put(array.get(i), result.size());
                    }
                }

                System.out.println("Done");
            }
            //return max 30 of them
            returnTemps = map.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .limit(Math.min(k, 50))
                    .collect(Collectors.toList());

        } else if (matchType.equals("active")) {
//                                ItemRankCmp cmp = new ItemRankCmp();
//                                PriorityQueue<ItemRank> queue = new PriorityQueue<>(cmp);
//                                Map<T, Pair<QueryTree, Integer>> map = new HashMap<>();
            Map<T, Integer> map = new HashMap<>();

//                                Map<String, QueryTree> queryTreeMap = tree.getMapping(tree);
            //it's a bug that you have to use the manager so many times???
            for (int i = 0; i < contexts.size(); ++i) {
                System.out.println("Matching...");
                System.out.println(array.get(i));
                MyUtils.printContext(s);
                System.out.println("**********");
                MyUtils.printContext(contexts.get(i));

                IContextMapping<INode> result = manager.match(s, contexts.get(i));

                System.out.println("Processing results...");
                System.out.println("Printing matches:");
                System.out.println("getSchema");
                array.get(i).getSchema(tree, result);

//                                        for (IMappingElement<INode> e : result) {
//                                                System.out.println(e.getSource().nodeData().getName() + "\t" + e.getRelation() + "\t" + e.getTarget().nodeData().getName());
//                                        }
                if (result.size() > 0) {
//                                                queue.add(new ItemRank(result.size(), resources.get(i)));
                    map.put(array.get(i), result.size());
                }

                System.out.println("Done");
            }

            returnTemps = map.entrySet()
                    .stream()
                    //                                      .sorted(Comparator.comparing(Map.Entry::getValue))
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))//high to low
                    .map(Map.Entry::getKey)
                    .limit(Math.min(k, 50))
                    .collect(Collectors.toList());

        }

        res = gson.toJson(returnTemps);
        return res;
    }

    @GET
    @Path("keyPairs")
    public Response getKeyPairs(
            @QueryParam("location") String location,
            @QueryParam("file1") String file1,
            @QueryParam("file2") String file2,
            @QueryParam("globalmatch") String globalmatchstr
    ) throws CodeException, IOException {

        System.out.println(globalmatchstr);
        Type listType = new TypeToken<List<AttrMatch>>() {
        }.getType();
        List<AttrMatch> globalmatch = gson.fromJson(globalmatchstr, listType);

        Map<String, String> leftMap = new HashMap<>();
        Map<String, String> rightMap = new HashMap<>();

        for (AttrMatch match : globalmatch) {
            if (match.getResourceName().matches(file1)) {
                for (MapAttr attr : match.getMapped()) {
                    leftMap.put(attr.getOldAttr(), attr.getNewAttr());
                }
            } else if (match.getResourceName().matches(file2)) {
                for (MapAttr attr : match.getMapped()) {
                    rightMap.put(attr.getOldAttr(), attr.getNewAttr());
                }
            }
        }

        String fileUrlR = file1;
        String fileUrlS = file2;

        List<Set<Set<String>>> res = new ArrayList<>();
        //make it to csv 
        if (MyUtils.getFileType(fileUrlR).equals("sql")) {
            MySQLHelper.downLoadaTableToLocal(fileUrlR, location, fileUrlR + ".csv");
            fileUrlR += ".csv";
        }
        if (MyUtils.getFileType(fileUrlS).equals("sql")) {
            MySQLHelper.downLoadaTableToLocal(fileUrlS, location, fileUrlS + ".csv");
            fileUrlS += ".csv";
        }

        fileUrlR = location + fileUrlR;
        fileUrlS = location + fileUrlS;
        System.out.println(fileUrlR);
        System.out.println(fileUrlS);
        //read header
        try {

            List<String> headerR = MyFileReader.readCSVHead(fileUrlR);
            List<String> headerS = MyFileReader.readCSVHead(fileUrlS);
            //reversed
            Map<String, String> mappedHeaders = sMatch.matchLists(headerR, headerS);
            System.out.println(mappedHeaders);
            BiMap<String, String> mapHeader = HashBiMap.create();
            mapHeader.putAll(mappedHeaders);
            System.out.println("*******mapped headers********");
            mappedHeaders.entrySet().forEach(t -> System.out.println(t.getKey() + " " + t.getValue()));
            List<Set<String>> keyR = GordianAlgorithm.getKeys(fileUrlR);
            System.out.println("*******key R********");
            keyR.forEach(t -> System.out.println(StringUtils.join(t, ", ")));
            System.out.println("*******Key S********");
            List<Set<String>> keyS = GordianAlgorithm.getKeys(fileUrlS);
            keyS.forEach(t -> System.out.println(StringUtils.join(t, ", ")));

            Set<String> matchR = mappedHeaders.entrySet().stream()
                    .map(t -> t.getKey())
                    .collect(Collectors.toSet());

            Set<String> matchS = mappedHeaders.entrySet().stream()
                    .map(t -> t.getValue())
                    .collect(Collectors.toSet());

            if (mappedHeaders.isEmpty()) {
                return Response.status(200).entity("{}").build();
            }
            if (!keyR.stream().anyMatch(t -> t.containsAll(matchR)) || !keyS.stream().anyMatch(t -> t.containsAll(matchS))) {
                return Response.status(200).entity("{}").build();
            }

            //find keys
            //replace keyS with matched keys
            for (Set<String> key : keyS) {
                Iterator<String> it = key.iterator();
                Set<String> newKey = new HashSet<>();
                while (it.hasNext()) {
                    String k = it.next();
                    if (mapHeader.inverse().containsKey(k)) {
                        it.remove();
                        newKey.add(mapHeader.inverse().get(k));
                    }
                }
                key.addAll(newKey);
            }

            Set<Set<String>> matchedKeys = new LinkedHashSet<>(keyR);
            Set<Set<String>> mKeysR = new LinkedHashSet<>();
            matchedKeys.retainAll(keyS);
            System.out.println("********find keys*******");
            matchedKeys.forEach(t -> System.out.println(StringUtils.join(t, ", ")));
            for (Set<String> keys : matchedKeys) {
                Set<String> sKeys = new LinkedHashSet<>();
                for (String k : keys) {
                    sKeys.add(mapHeader.get(k));
                }
                mKeysR.add(sKeys);
            }
            res.add(matchedKeys);
            res.add(mKeysR);
        } catch (Exception e) {
            res.add(new HashSet<>());
            e.printStackTrace();
        }
        System.out.println(res);
        System.out.println(leftMap);
        System.out.println(rightMap);

        if (!leftMap.isEmpty()) {
            Set<Set<String>> arr = res.get(0);
            Set<Set<String>> newArr = new LinkedHashSet<>();
            for (Set<String> a : arr) {
                Set<String> newA = new LinkedHashSet<>();
                for (String k : a) {
                    if (leftMap.containsKey(k)) {
                        newA.add(leftMap.get(k));
                    } else {
                        newA.add(k);
                    }
                }
                newArr.add(newA);
            }
            res.set(0, newArr);
            System.out.println("******");
            System.out.println(res);
        }
        if (!rightMap.isEmpty()) {
            Set<Set<String>> arr = res.get(1);
            Set<Set<String>> newArr = new LinkedHashSet<>();
            for (Set<String> a : arr) {
                Set<String> newA = new LinkedHashSet<>();
                for (String k : a) {
                    if (rightMap.containsKey(k)) {
                        newA.add(rightMap.get(k));
                    } else {
                        newA.add(k);
                    }
                }
                newArr.add(newA);
            }
            res.set(1, newArr);
        }

        String restr = gson.toJson(res);
        System.out.println(restr);
        return Response.status(200).entity(restr).build();
    }

    @GET
    @Path("keyPairsXML")
    public Response getKeyPairsXML(
            @QueryParam("location") String location,
            @QueryParam("file1") String file1,
            @QueryParam("file2") String file2,
            @QueryParam("globalmatch") String globalmatchstr,
            @QueryParam("leftAttrs") String leftAttrs,
            @QueryParam("rightAttrs") String rightAttrs
    ) throws CodeException, IOException, XPathSyntaxException, JaxenException, Exception {

        System.out.println(globalmatchstr);
        System.out.println(leftAttrs);
        System.out.println(rightAttrs);
        Type listType = new TypeToken<List<AttrMatch>>() {
        }.getType();
        List<AttrMatch> globalmatch = gson.fromJson(globalmatchstr, listType);

        Map<String, String> leftMap = new HashMap<>();
        Map<String, String> rightMap = new HashMap<>();
//
        for (AttrMatch match : globalmatch) {
            if (match.getResourceName().matches(file1)) {
                for (MapAttr attr : match.getMapped()) {
                    leftMap.put(attr.getOldAttr(), attr.getNewAttr());
                }
            } else if (match.getResourceName().matches(file2)) {
                for (MapAttr attr : match.getMapped()) {
                    rightMap.put(attr.getOldAttr(), attr.getNewAttr());
                }
            }
        }
        List<Set<Set<String>>> res = new ArrayList<>();
        //Read XML documents as csv files
        List<List<String>> leftContent = new ArrayList<>();
        List<List<String>> rightContent = new ArrayList<>();
        List<String> leftHead = Arrays.asList(leftAttrs.split(","));
        List<String> rightHead = Arrays.asList(rightAttrs.split(","));
        //left
        for (String h : leftHead) {
            List<String> row = inputGenerator.readFromEXist(location, file1, h, new MyStatus());
            leftContent.add(row);
        }
//        System.out.println("left");
        leftContent = MyUtils.transpose(leftContent);
        leftContent.add(0, leftHead);
//        System.out.println(leftContent);
        //right
        for (String h : rightHead) {
            List<String> row = inputGenerator.readFromEXist(location, file2, h, new MyStatus());
            rightContent.add(row);
        }
//        System.out.println("right");
        rightContent = MyUtils.transpose(rightContent);
        rightContent.add(0, rightHead);
//        System.out.println(rightContent);

        String fileUrlR = file1 + MyUtils.randomAlphaNumeric();
        String fileUrlS = file2 + MyUtils.randomAlphaNumeric();
        fileUrlR = location + fileUrlR;
        fileUrlS = location + fileUrlS;
        System.out.println(fileUrlR);
        System.out.println(fileUrlS);
        MyFileReader.writeFile(fileUrlR, leftContent, ",");
        MyFileReader.writeFile(fileUrlS, rightContent, ",");

//        //read header
        try {
            List<String> headerR = MyFileReader.readCSVHead(fileUrlR);
            List<String> headerS = MyFileReader.readCSVHead(fileUrlS);
            //reversed
            Map<String, String> mappedHeaders = sMatch.matchLists(headerR, headerS);
            System.out.println(mappedHeaders);
            BiMap<String, String> mapHeader = HashBiMap.create();
            mapHeader.putAll(mappedHeaders);
            System.out.println("*******mapped headers********");
            mappedHeaders.entrySet().forEach(t -> System.out.println(t.getKey() + " " + t.getValue()));
            List<Set<String>> keyR = GordianAlgorithm.getKeys(fileUrlR);
            System.out.println("*******key R********");
            keyR.forEach(t -> System.out.println(StringUtils.join(t, ", ")));
            System.out.println("*******Key S********");
            List<Set<String>> keyS = GordianAlgorithm.getKeys(fileUrlS);
            keyS.forEach(t -> System.out.println(StringUtils.join(t, ", ")));

            Set<String> matchR = mappedHeaders.entrySet().stream()
                    .map(t -> t.getKey())
                    .collect(Collectors.toSet());

            Set<String> matchS = mappedHeaders.entrySet().stream()
                    .map(t -> t.getValue())
                    .collect(Collectors.toSet());

            if (mappedHeaders.isEmpty()) {
                return Response.status(200).entity("{}").build();
            }
            if (!keyR.stream().anyMatch(t -> t.containsAll(matchR)) || !keyS.stream().anyMatch(t -> t.containsAll(matchS))) {
                return Response.status(200).entity("{}").build();
            }

            //find keys
            //replace keyS with matched keys
            for (Set<String> key : keyS) {
                Iterator<String> it = key.iterator();
                Set<String> newKey = new HashSet<>();
                while (it.hasNext()) {
                    String k = it.next();
                    if (mapHeader.inverse().containsKey(k)) {
                        it.remove();
                        newKey.add(mapHeader.inverse().get(k));
                    }
                }
                key.addAll(newKey);
            }

            Set<Set<String>> matchedKeys = new LinkedHashSet<>(keyR);
            Set<Set<String>> mKeysR = new LinkedHashSet<>();
            matchedKeys.retainAll(keyS);
            System.out.println("********find keys*******");
            matchedKeys.forEach(t -> System.out.println(StringUtils.join(t, ", ")));
            for (Set<String> keys : matchedKeys) {
                Set<String> sKeys = new LinkedHashSet<>();
                for (String k : keys) {
                    sKeys.add(mapHeader.get(k));
                }
                mKeysR.add(sKeys);
            }
            res.add(matchedKeys);
            res.add(mKeysR);
        } catch (Exception e) {
            res.add(new HashSet<>());
            e.printStackTrace();
        }
        System.out.println(res);
        System.out.println(leftMap);
        System.out.println(rightMap);

        if (!leftMap.isEmpty()) {
            Set<Set<String>> arr = res.get(0);
            Set<Set<String>> newArr = new LinkedHashSet<>();
            for (Set<String> a : arr) {
                Set<String> newA = new LinkedHashSet<>();
                for (String k : a) {
                    if (leftMap.containsKey(k)) {
                        newA.add(leftMap.get(k));
                    } else {
                        newA.add(k);
                    }
                }
                newArr.add(newA);
            }
            res.set(0, newArr);
            System.out.println("******");
            System.out.println(res);
        }
        if (!rightMap.isEmpty()) {
            Set<Set<String>> arr = res.get(1);
            Set<Set<String>> newArr = new LinkedHashSet<>();
            for (Set<String> a : arr) {
                Set<String> newA = new LinkedHashSet<>();
                for (String k : a) {
                    if (rightMap.containsKey(k)) {
                        newA.add(rightMap.get(k));
                    } else {
                        newA.add(k);
                    }
                }
                newArr.add(newA);
            }
            res.set(1, newArr);
        }

        String restr = gson.toJson(res);
        System.out.println(restr);

        FileUtils.deleteQuietly(new File(fileUrlR));
        FileUtils.deleteQuietly(new File(fileUrlS));

        return Response.status(200).entity(restr).build();
    }

}
