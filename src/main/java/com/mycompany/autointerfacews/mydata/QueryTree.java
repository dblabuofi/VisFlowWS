/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author jupiter
 */
public class QueryTree {

    String text;
    String id;
//        String style;
    A_ttr a_attr;
    JSTreeState state;
    List<QueryTree> children;
    String xpath;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public String toString() {
        return text + " " + id + " " + a_attr.getStyle();
    }

    public QueryTree() {
        a_attr = new A_ttr();
        state = new JSTreeState();
    }

    public QueryTree(String text) {
        this.text = text;
        a_attr = new A_ttr();
        state = new JSTreeState();
        children = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setA_attr(A_ttr a_attr) {
        this.a_attr = a_attr;
    }

    public A_ttr getA_attr() {
        return a_attr;
    }

    public void setChildren(List<QueryTree> children) {
        this.children = children;
    }

    public String getText() {
        return text;
    }

    public List<QueryTree> getChildren() {
        return children;
    }

    public Integer getNums(QueryTree root) {
        Integer res = 1;

        if (!root.getChildren().isEmpty() && root.getChildren().size() > 0) {
            for (QueryTree child : root.getChildren()) {
                res += getNums(child);
            }
        }
        return res;
    }

    public void generateIContext(IContext s) {
        INode rootNode = s.createRoot(text);
        generateINode(rootNode, children);
    }

    public void generateINodes(INode node) {
        generateINode(node, children);
    }

    void generateINode(INode node, List<QueryTree> children) {
        for (QueryTree child : children) {
            INode tmp = node.createChild(child.getText());
            generateINode(tmp, child.getChildren());
        }
    }

    public QueryTree search(QueryTree root, String text) {
        if (root.getText().equals(text)) {
            return root;
        }
        if (!root.getChildren().isEmpty() && root.getChildren().size() > 0) {
            for (QueryTree child : root.getChildren()) {
                QueryTree tmp = search(child, text);
                if (tmp != null) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public void print(QueryTree root) {
        System.out.println(root);
        if (!root.getChildren().isEmpty() && root.getChildren().size() > 0) {
            for (QueryTree child : root.getChildren()) {
                print(child);
            }
        }
    }

    public Map<String, QueryTree> getMapping(QueryTree root) {
        Map<String, QueryTree> res = new HashMap<>();
        res.put(root.id, root);

        if (!root.getChildren().isEmpty() && root.getChildren().size() > 0) {
            for (QueryTree child : root.getChildren()) {
                res.putAll(getMapping(child));
            }
        }
        return res;
    }

    public static String generateSchema(QueryTree root) {
        System.out.println("test");
        System.out.println(root.getText().split(":")[0]);
//        System.out.println(root.getText().split(":")[0].replace(" ", "&#x20;"));
        String res = "<" + root.getText().split(":")[0].replace(" ", "&#x20;") + ">";
//        String res = "<" + root.getText().split(":")[0] + ">";
//        System.out.println(root.getText().split(":")[0].replace(" ", "&#x20;"));
        List<QueryTree> children = root.getChildren();
        for (QueryTree child : children) {
            res += generateSchema(child);
        }
        res += "</" + root.getText().split(":")[0].replace(" ", "&#x20;") + ">";

        return res;
    }

    public static List<MyAttribute> generateAttributeList(QueryTree root) {
        List<MyAttribute> res = new ArrayList<>();
        if (root.getChildren().isEmpty()) {
            MyAttribute cur = null;
            if (root.getText().contains(":")) {
                cur = new MyAttribute(root.getText().split(":")[0], root.getText().split(":")[1], root.getText().split(":")[2]);
            } else {
                cur = new MyAttribute(root.getText().split(":")[0], "", "");
            }
            res.add(cur);
        }

        List<QueryTree> children = root.getChildren();
        for (QueryTree child : children) {
            res.addAll(generateAttributeList(child));
        }

        return res;
    }

    public static QueryTree generateSchemaFromFile(String location, String fileName) {
        QueryTree tree = null;

        String fileURL = location + fileName;
        try {
            if (fileURL.endsWith("xml")) {
                File fXmlFile = new File(fileURL);
                System.out.println(fXmlFile);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                Node root = doc.getDocumentElement();
                Map<QueryNode, Set<QueryNode>> map = new HashMap<>();
                Map<QueryNode, Integer> cnts = new HashMap<>();

                mapNode(root, map, "", 0);
                System.out.println(map);
                Map<QueryNode, QueryTree> queryTree = new HashMap<>();
                for (Map.Entry<QueryNode, Set<QueryNode>> entry : map.entrySet()) {
                    if (!queryTree.containsKey(entry.getKey())) {
                        queryTree.put(entry.getKey(), new QueryTree(entry.getKey().getValue()));
                    }
                    cnts.putIfAbsent(entry.getKey(), 0);
                    for (QueryNode v : entry.getValue()) {
                        cnts.put(v, cnts.getOrDefault(v, 0) + 1);
                        if (!queryTree.containsKey(v)) {
                            queryTree.put(v, new QueryTree(v.getValue()));
                        }
                        queryTree.get(entry.getKey()).children.add(queryTree.get(v));
                    }
                }
                QueryNode rootStr = null;
                for (Map.Entry<QueryNode, Integer> entry : cnts.entrySet()) {
                    if (entry.getValue() == 0) {
                        rootStr = entry.getKey();
                        break;
                    }
                }
                tree = queryTree.get(rootStr);
                generateXPath(tree, "", 0);
            } else if (fileURL.endsWith("csv")) {
                List<String> headers = MyFileReader.readCSVHead(fileURL);
                System.out.println(headers);
                tree = generateSchemaFromFileHelperCSV(fileName, headers);
                return tree;
            } else {//sql
                MySQLHelper.downLoadaTableToLocal(fileName, location, fileName + ".csv");
                List<String> headers = MyFileReader.readCSVHead(fileURL + ".csv");
                System.out.println(headers);
                tree = generateSchemaFromFileHelperCSV(fileName + ".csv", headers);
                return tree;
            }

        } catch (org.xml.sax.SAXParseException e) {
            try {
                String fXml = MyFileReader.readFileAll(fileURL);
                fXml = "<Result>" + fXml + "</Result>";
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(fXml)));
                doc.getDocumentElement().normalize();
                Node root = doc.getDocumentElement();
                 Map<QueryNode, Set<QueryNode>> map = new HashMap<>();
                Map<QueryNode, Integer> cnts = new HashMap<>();
                mapNode(root, map, "", 0);
                System.out.println(map);
                Map<QueryNode, QueryTree> queryTree = new HashMap<>();
                for (Map.Entry<QueryNode, Set<QueryNode>> entry : map.entrySet()) {
                    if (!queryTree.containsKey(entry.getKey())) {
                        queryTree.put(entry.getKey(), new QueryTree(entry.getKey().getValue()));
                    }
                    cnts.putIfAbsent(entry.getKey(), 0);
                    for (QueryNode v : entry.getValue()) {
                        cnts.put(v, cnts.getOrDefault(v, 0) + 1);
                        if (!queryTree.containsKey(v)) {
                            queryTree.put(v, new QueryTree(v.getValue()));
                        }
                        queryTree.get(entry.getKey()).children.add(queryTree.get(v));
                    }
                }
                QueryNode rootStr = null;
                for (Map.Entry<QueryNode, Integer> entry : cnts.entrySet()) {
                    if (entry.getValue() == 0) {
                        rootStr = entry.getKey();
                        break;
                    }
                }
                tree = queryTree.get(rootStr);
                generateXPath(tree, "", 0);
                System.out.println(tree);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tree;
    }

    static void generateXPath(QueryTree root, String path, int index) {
        if (index == 0) {
            path += "/" + root.text;
        } else {
            path += "/" + root.text + "[" + index + "]";
        }
        root.setXpath(new String(path));
        for (int i = 0; i < root.children.size(); ++i) {
            QueryTree v = root.children.get(i);
            int cnt = 0;
            for (int j = 0; j < i; ++j) {
                if (root.children.get(j).text.equals(v.text)) {
                    ++cnt;
                }
            }
            generateXPath(v, path, cnt);
        }
    }

    static boolean sameTree(QueryTree t1, QueryTree t2) {
        if (t1 == null && t2 == null) {
            return true;
        }
        if (t1 == null || t2 == null) {
            return false;
        }
        if (!t1.text.equals(t2.text)) {
            return false;
        }
        if (t1.children.isEmpty() && t2.children.isEmpty()) {
            return t1.text.equals(t2.text);
        }
        if (t1.children.isEmpty() || t2.children.isEmpty()) {
            return false;
        }
        if (t1.children.size() != t2.children.size()) {
            return false;
        }
        for (int i = 0; i < t1.children.size(); ++i) {
            if (!sameTree(t1.children.get(i), t2.children.get(i))) {
                return false;
            }
        }
        return true;
    }

    static QueryTree generateSchemaFromFileHelperCSV(String fileName, List<String> headers) {
        QueryTree cur = new QueryTree();
        cur.setText(fileName);
        List<QueryTree> children = new ArrayList<>();
        for (int i = 0; i < headers.size(); ++i) {
            children.add(new QueryTree(headers.get(i)));
        }
        cur.setChildren(children);
        return cur;
    }

    static QueryTree generateSchemaFromFileHelper(Node node, Map<String, Set<String>> map) {
        QueryTree cur = new QueryTree();
        cur.setText(node.getNodeName());

        NodeList nNodes = node.getChildNodes();
        List<QueryTree> children = new ArrayList<>();
        for (int i = 0; i < nNodes.getLength(); ++i) {
            if (nNodes.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            children.add(generateSchemaFromFileHelper(nNodes.item(i), map));
        }
        cur.setChildren(children);
        return cur;
    }

    static void mapNode(Node node, Map<QueryNode, Set<QueryNode>> map, String path, int cnt) {
        String key = node.getNodeName();
        if (cnt == 0) {
            path += "/" + key;
        } else {
            path += "/" + key + "[" + cnt + "]";
        }
        QueryNode cur = new QueryNode(key, path);
        map.putIfAbsent(cur, new HashSet<QueryNode>());

        NodeList nNodes = node.getChildNodes();
        for (int i = 0; i < nNodes.getLength(); ++i) {
            Node child = nNodes.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            int c = 0;
            for (int j = 0; j < i; ++j) {
                if (nNodes.item(j).getNodeName().equals(child.getNodeName())) {
                    ++c;
                }
            }
            String path2 = path;
            if (cnt == 0) {
                path2 += "/" + child.getNodeName();
            } else {
                path2 += "/" + child + "[" + c + "]";
            }
            map.get(cur).add(new QueryNode(child.getNodeName(), path2));
        }
        for (int i = 0; i < nNodes.getLength(); ++i) {
            if (nNodes.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            mapNode(nNodes.item(i), map, path, cnt);
        }
    }

}
