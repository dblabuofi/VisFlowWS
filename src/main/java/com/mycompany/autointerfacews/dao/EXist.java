/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.exist.util.serializer.SAXSerializer;
import org.exist.util.serializer.SerializerPool;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.CompiledExpression;

import org.exist.storage.serializers.EXistOutputKeys;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.json.JSONObject;
import org.json.XML;
import org.xmldb.api.modules.CollectionManagementService;

import javax.xml.transform.OutputKeys;
import org.apache.commons.lang3.StringEscapeUtils;

import org.exist.xmldb.XPathQueryServiceImpl;
import org.exist.xmldb.XmldbURI;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;

/**
 *
 * @author jupiter
 */
public class EXist {

    @Inject
    Gson gson;
    Type listType = new TypeToken<List<MyResource>>() {
    }.getType();

    public EXist() {
    }
//    @Inject
//    public EXist(Gson gson) {
//        this.gson = gson;
//    }
    protected String URIR = "xmldb:exist://";

    protected String driver = "org.exist.xmldb.DatabaseImpl";

    /**
     * Read the xquery file and return as string.
     */
    protected String readFile(String file) throws IOException {
        BufferedReader f = new BufferedReader(new FileReader(file));
        String line;
        StringBuffer xml = new StringBuffer();
        while ((line = f.readLine()) != null) {
            xml.append(line);
        }
        f.close();
        return xml.toString();
    }

//    protected String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    protected String URI = "xmldb:exist://localhost:8899/exist/xmlrpc";

    protected void usage() {
        System.out.println("usage: org.exist.examples.xmldb.Retrieve collection docName");
        System.exit(0);
    }

    public String getResources(String args[]) {
        try {
            if (args.length < 2) {
                usage();
                return null;
            }
            String collection = args[0];
            // initialize database drivers
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            // Set to TRUE to connectect over HTTPS-uri 
            // like 'xmldb:exist://localhost:8443/exist/xmlrpc' (port changed 8080->8443)
            database.setProperty("ssl-enable", "false");
            DatabaseManager.registerDatabase(database);
            // get the collection
            Collection col = DatabaseManager.getCollection(URI + collection);
            col.setProperty(OutputKeys.INDENT, "yes");
            col.setProperty(EXistOutputKeys.EXPAND_XINCLUDES, "no");
            col.setProperty(EXistOutputKeys.PROCESS_XSL_PI, "yes");
            XMLResource document = (XMLResource) col.getResource(args[1]);
            if (document == null) {
                System.out.println("document not found!");
            }
            return document.getContent().toString();
        } catch (Exception e) {
            System.out.println("getResources wrong!!");
            e.printStackTrace();
        }
        return null;
    }

    public String runQuery(String args[]) {
        String resStr = "";
        try {
            if (args.length < 2) {
                usage();
            }

            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);

            String query = args[1];
//            System.out.println("query :" + query);
            // get root-collection
            Collection col
                    //                    = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc" + args[0]);
                    = DatabaseManager.getCollection(URI + args[0]);
            // get query-service
            XQueryService service
                    = (XQueryService) col.getService("XQueryService", "1.0");

            // set pretty-printing on
            service.setProperty(OutputKeys.INDENT, "yes");
            service.setProperty(OutputKeys.ENCODING, "UTF-8");

            CompiledExpression compiled = service.compile(query);

            long start = System.currentTimeMillis();

            // execute query and get results in ResourceSet
            ResourceSet result = service.execute(compiled);

            long qtime = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();

            Properties outputProperties = new Properties();
            outputProperties.setProperty(OutputKeys.INDENT, "yes");
            SAXSerializer serializer = (SAXSerializer) SerializerPool.getInstance().borrowObject(SAXSerializer.class);
            serializer.setOutput(new OutputStreamWriter(System.out), outputProperties);

            for (int i = 0; i < (int) result.getSize(); i++) {
                XMLResource resource = (XMLResource) result.getResource((long) i);
                resource.getContentAsSAX(serializer);
            }

            SerializerPool.getInstance().returnObject(serializer);
            long rtime = System.currentTimeMillis() - start;
//            System.out.println("hits:          " + result.getSize());
//            System.out.println("query time:    " + qtime);
//            System.out.println("retrieve time: " + rtime);
            for (ResourceIterator it = result.getIterator(); it.hasMoreResources();) {
                Resource res = it.nextResource();
//                System.out.println(res.getId() + " " + res.getResourceType() + " " + res.getContent());
                resStr += res.getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resStr;
    }

    //add files
    public void put(String args[]) {
        try {
            if (args.length < 2) {
                usage();
            }

            String collection = args[0], file = args[1];

            // initialize driver
            String driver = "org.exist.xmldb.DatabaseImpl";
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);

            // try to get collection
            Collection col
                    = DatabaseManager.getCollection(URI + collection);
            if (col == null) {
                // collection does not exist: get root collection and create.
                // for simplicity, we assume that the new collection is a
                // direct child of the root collection, e.g. /db/test.
                // the example will fail otherwise.
                Collection root = DatabaseManager.getCollection(URI + XmldbURI.ROOT_COLLECTION);
                CollectionManagementService mgtService
                        = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
                col = mgtService.createCollection(collection.substring((XmldbURI.ROOT_COLLECTION + "/").length()));
            }
            File f = new File(file);
            // create new XMLResource
            XMLResource document = (XMLResource) col.createResource(f.getName(), "XMLResource");
            document.setContent(f);
            System.out.print("storing document " + document.getId() + "...");
            col.storeResource(document);
            System.out.println("ok.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MyResource> getResources() {
//        String[] args = {"/db/apps/flowq/index", "databaseindexs.xml"};
        String[] args = {"/db/apps/flowq/index", "//resource"};
        String document = runQuery(args);
        JSONObject soapDatainJsonObject = XML.toJSONObject(document);
//        System.out.println(soapDatainJsonObject);
//        System.out.println(soapDatainJsonObject.getJSONArray("resource").toString());
//        System.out.println(soapDatainJsonObject.getJSONArray("resource").toString());
//        MyResource my = gson.fromJson(soapDatainJsonObject.getJSONObject("resource").toString(), MyResource.class);
        List<MyResource> res = gson.fromJson(soapDatainJsonObject.getJSONArray("resource").toString(), listType);

        return res;
    }

    //resource id return resources
    public MyResource getResource(String id) {
//        String[] args = {"/db/apps/flowq/index", "databaseindexs.xml"};
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/index";
        args[1] = "//resource[id=" + id + "]";

        String document = runQuery(args);
        JSONObject soapDatainJsonObject = XML.toJSONObject(document);
        System.out.println(soapDatainJsonObject);
        MyResource res = gson.fromJson(soapDatainJsonObject.getJSONObject("resource").toString(), MyResource.class);

        return res;
    }

    public void addBefore(Node node, Action action) {
        String query = "for $" + action.getAttr() + " in //row/" + action.getAttr() + "\n"
                + "let $newtext := concat('" + action.getVal() + "', $" + action.getAttr() + "/text())\n"
                + "return\n"
                + "update replace $" + action.getAttr() + "/text() with $newtext";

//        System.out.println(query);
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/data";
        args[1] = query;
        runQuery(args);
    }

    //use this one
    public List<String> runQuery2(String[] args, MyStatus status) throws CodeException {
        List<String> res = new ArrayList<>();
        try {

            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection col
                    //                    = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc" + args[0]);
                    = DatabaseManager.getCollection(URI + args[0]);
            // get query-service
            XPathQueryServiceImpl service
                    = (XPathQueryServiceImpl) col.getService("XPathQueryService", "3.0");

            // set pretty-printing on
            service.setProperty(OutputKeys.INDENT, "yes");
            service.setProperty(OutputKeys.ENCODING, "UTF-8");

            // execute queries
            ResourceSet set = null;

//            System.out.println();
//            System.out.println("Query 1");
//            System.out.println("=======");
            set = service.query(args[1]);
            for (ResourceIterator it = set.getIterator(); it.hasMoreResources();) {
                Resource resResource = it.nextResource();
                String line = StringEscapeUtils.unescapeXml(resResource.getContent().toString());
                System.out.println(line);
                status.addMessage(line);
                res.add(line);
            }
        } catch (Exception e) {
            status.addMessage(e.getMessage());
            throw new CodeException("run eXist query has problems query: " + args[1], e);
        }
        return res;
    }

    public List<String> readData(Action action) throws CodeException {
        List<String> res = new ArrayList<>();
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/data";
        args[1] = "//row/" + action.getAttr().trim() + "/text()";
//        System.out.println(args[1]);
        res = runQuery2(args, new MyStatus());

        return res;
    }

    public void storeData(String id, List<String> output, Action action) {
        try {
//            String query = MyFileReader.generateXML(id, action.getReturnattrs(), output);
//            System.out.println("store data");
//            System.out.println(query);
//
//            Class<?> cl = Class.forName(driver);
//            Database database = (Database) cl.newInstance();
//            database.setProperty("create-database", "true");
//            DatabaseManager.registerDatabase(database);
//            Collection col
//                    = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/apps/flowq/data");
//                    = DatabaseManager.getCollection(URI + "/db/apps/flowq/data");
//            XMLResource res = (XMLResource) col.createResource(id + ".xml", "XMLResource");
//            res.setContent(query);
//            col.storeResource(res);
        } catch (Exception e) {
        }
    }

    public List<String> getValues(String attr) throws CodeException {
        List<String> res = new ArrayList<>();
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/data";
        args[1] = "distinct-values(//" + attr + ")";
        res = runQuery2(args, new MyStatus());

        return res;
    }

    public List<List<String>> getInputs(MyResource resource, String rootName) throws CodeException {
        List<List<String>> res = new ArrayList<>();

        String[] args = new String[2];
        args[0] = "apps/flowq/data";
        args[1] = "//" + rootName + "/row";
        List<String> resStr = runQuery2(args, new MyStatus());

        for (String lineStr : resStr) {
            JSONObject soapDatainJsonObject = XML.toJSONObject(lineStr);
//                     System.out.println(soapDatainJsonObject);
            List<String> line = new ArrayList<>();
            for (MyAttribute attr : resource.getAttributes()) {
                line.add(soapDatainJsonObject.getJSONObject("row").get(attr.getName()).toString());
            }
            res.add(line);
        }
        return res;
    }

    public String runXMLQuery(String query) {
        try {
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);

//                    String query = readFile(fileUrl);
            // get root-collection
            Collection col
                    = DatabaseManager.getCollection(URI + "/db/apps/flowq/index");
            // get query-service
            XQueryService service
                    = (XQueryService) col.getService("XQueryService", "3.0");

            // set pretty-printing on
            service.setProperty(OutputKeys.INDENT, "yes");
            service.setProperty(OutputKeys.ENCODING, "UTF-8");

            CompiledExpression compiled = service.compile(query);

            long start = System.currentTimeMillis();

            // execute query and get results in ResourceSet
            ResourceSet result = service.execute(compiled);

            long qtime = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();

            Properties outputProperties = new Properties();
            outputProperties.setProperty(OutputKeys.INDENT, "yes");
            SAXSerializer serializer = (SAXSerializer) SerializerPool.getInstance().borrowObject(SAXSerializer.class);
            serializer.setOutput(new OutputStreamWriter(System.out), outputProperties);

            for (int i = 0; i < (int) result.getSize(); i++) {
                XMLResource resource = (XMLResource) result.getResource((long) i);
                resource.getContentAsSAX(serializer);
            }

            SerializerPool.getInstance().returnObject(serializer);
            long rtime = System.currentTimeMillis() - start;
            System.out.println("hits:          " + result.getSize());
            System.out.println("query time:    " + qtime);
            System.out.println("retrieve time: " + rtime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String uploadFileToExist(String location, String fileName) throws CodeException {
        System.out.println("upload file to eXist database: " + fileName);

        //change types
        String type = MyUtils.getFileType(fileName);
        //assume no spaces are allowed in the files
        String rootName = MyUtils.getFileName(fileName);
        if (type.equals("xml")) {

        } else if (type.equals("csv")) {
            //need change it to xml
            System.out.println(rootName + " " + type);
            MyFileReader.convertCSVtoXML(location + fileName, location + rootName + ".xml", rootName.replaceAll("[ ._-]", ""), ",");
        } else if (type.equals("sql")) {
            String tempFile = "mytempfile" + MyUtils.randomAlphaNumeric();
            MySQLHelper.downLoadaTableToLocal(rootName, location, tempFile);
            MyFileReader.convertCSVtoXML(location + tempFile, location + rootName + ".xml", rootName.replaceAll("[ ._-]", ""), ",");
            File file = new File(tempFile);
            file.deleteOnExit();
        }
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/index";
        args[1] = location + rootName + ".xml";
        put(args);
        return location + fileName;
    }

    public List<String> run(String eXistScript, MyStatus status) throws CodeException {
        System.out.println("run xquery query: " + eXistScript);
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/index";
        args[1] = eXistScript;
        List<String> res = runQuery2(args, status);

        return res;
    }

}
