package com.mycompany.autointerfacews.generator;

import com.google.inject.Inject;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static javax.ws.rs.core.Response.status;

/**
 *
 * @author jupiter
 */
public class InputGenerator {

    EXist eXist;

    public InputGenerator() {
    }

    public InputGenerator(EXist eXist) {
        this.eXist = eXist;
    }
    
    public synchronized List<List<String>> generateInput(Node node, Action action, MyStatus status, String location) throws CodeException {
//                String location = node.getResourcesIn().get(0).getLocation();
        List<List<String>> res = new ArrayList<>();
        List<MyAttribute> attrs = null;
        //resource 
        if (action.getTargetResource() != null) {
            attrs = action.getTargetResource().getAttributes();
        } else {
            attrs = action.getTargetFunction().getAttributes();
        }
//                for (MyAttribute attr : attrs) {
        for (int i = 0; i < attrs.size(); ++i) {
            //requires default always last one
            MyAttribute attr = attrs.get(i);
            if (attr.getFrom().equals("input")) {
                if (!attr.getValue().isEmpty()) {
                    List<String> row = new ArrayList<>();
                    row.add(attr.getValue());
                    res.add(row);
                }
            } else if (attr.getFrom().equals("default")) {//other resources 
            } else {
                res.add(readFromEXist(action.getTargetResource().getLocation(), attr.getFrom(), attr.getValue(), status));
            }
        }
        //default attrs
        for (MyAttribute attr : attrs) {
//                        if (attr.getRequired().equals("true") && attr.getFrom().equals("default")) {
            if (attr.getRequired() == true && attr.getFrom().equals("default")) {
                int size = 0;
                if (res.isEmpty()) {
                    size = 1;
                } else {
                    size = res.get(0).size();//get the total size
                }

                List<String> row = new ArrayList<>(Collections.nCopies(size, attr.getValue()));
                res.add(row);
            }
        }
        res = transpose(res);
        return res;
    }

    public synchronized List<String> readFromEXist(String location, String fileName, String attr, MyStatus status) throws CodeException {
        //check file types
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
//                String rootName = fileName.substring(0, fileName.lastIndexOf(".")).replaceAll("[ _-]", "");//don't allow spaces
        System.out.println("fileName");
        System.out.println(fileName);
        String rootName = fileName.lastIndexOf(".") == -1 ? fileName : fileName.substring(0, fileName.lastIndexOf(".")).replaceAll("[ _-]", "");//don't allow spaces
        if (type.toLowerCase().equals("xml")) {

        } else if (type.toLowerCase().equals("csv")) {
            //need change it to xml
            System.out.println(rootName + " " + type);
//                        MyFileReader.convertCSVtoXML(location + fileName, location + rootName + ".xml", rootName.replaceAll("[ ._-]", ""), ",");
            MyFileReader.convertCSVtoXML(location + fileName, location + rootName + ".xml");

        }

        //put file to eXist Database
        System.out.println("put file to eXist database");
        String[] args = new String[2];
        args[0] = "/db/apps/flowq/index";
        args[1] = location + rootName + ".xml";
        eXist.put(args);
        //get inputs

        args[1] = "doc(\'" + rootName + ".xml')//" + attr + "/text()";
        System.out.println("********************args 1 " + args[1]);
        List<String> row = eXist.runQuery2(args, status);
        //if it is empty we need try another way
        if (row.isEmpty()) {
            args[1] = "doc(\'" + rootName + ".xml')//" + attr + "/*";
            System.out.println("********************args 1 " + args[1]);
            row = eXist.runQuery2(args, status);
        }
        System.out.println(row.size());
        return row;
    }

    public String generateFinalFile(String location, String fileName, String templateFile, List<String> rightKeys, List<String> headers, List<List<String>> contents) {
        try {
            MyStatus status = new MyStatus();
            String templateRootName = templateFile.lastIndexOf(".") == -1 ? templateFile : templateFile.substring(0, templateFile.lastIndexOf(".")).replaceAll("[ _-]", "");//don't allow spaces
            //put file to eXist Database
            System.out.println("put file to eXist database");
            String[] args = new String[2];
            args[0] = "/db/apps/flowq/index";
            args[1] = location + templateRootName + ".xml";
            eXist.put(args);

            //get inputs
            String script = "let $doc := doc('" + templateRootName + ".xml')\n";
            for (String key : rightKeys) {
                script += "let $" + key + ":= $doc//" + key + "\n";
            }
            //get cla
            if (rightKeys.size() == 1) {
                script += "let $cla := $" + rightKeys.get(0) + "/..\n";
            } else if (rightKeys.size() > 1) {
                script += "let $cla :=(";
                for (int i = 0; i < rightKeys.size() - 1; ++i) {//here we only select varaibles, not functions
                    script += "$" + rightKeys.get(i) + "/ancestor-or-self::node() intersect ";
                }
                script += "$" + rightKeys.get(rightKeys.size() - 1) + "/ancestor-or-self::node() intersect ";
                script += ")[last()]\n";
            }
            script += "return $cla/name()";
            args[1] = script;
            System.out.println("********************args " + args[1]);
            List<String> rows = eXist.runQuery2(args, status);
            String key = rows.get(0);
            System.out.println("*#########");
            System.out.println(key);
            //we need loop to there and add them all
            return key;
        } catch (Exception e) {
            
        }
//        List<String> row = eXist.runQuery2(args, status);
        //if it is empty we need try another way
//        if (row.isEmpty()) {
//        }
//        System.out.println(row.size());
//        return row;
        return null;
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
