/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.utils;

import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import com.mycompany.visflowsmatch.data.trees.INodeData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author jupiter
 */
public class MyUtils {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric() {
        int count = (int) (Math.random() * 5) + 3;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static <T> List<List<T>> transpose(List<List<T>> table) {
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

    public static String getFileType(String fileURL) {
        return fileURL.lastIndexOf(".") == -1 ? "sql" : fileURL.substring(fileURL.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String getFileName(String fileURL) {
        return fileURL.lastIndexOf(".") == -1 ? fileURL : fileURL.substring(0, fileURL.lastIndexOf("."));
    }

    public static List<String> getFileHeader(String fileURL) {

        String type = getFileType(fileURL);
        List<String> header = null;
        if (type.equals("csv") || type.equals("sql")) {
            header = MyFileReader.readCSVHead(fileURL);
        } else {
            header = MyFileReader.readXMLHeaders(fileURL);
        }
        return header;
    }

    public static List<List<String>> getFile(String fileURL) {

        String type = getFileType(fileURL);
        if (type.equals("csv") || type.equals("sql")) {
            return MyFileReader.readCSV(fileURL);
        } else {
//            return MyFileReader.readXML(fileURL);
            return MyFileReader.readXML2(fileURL);
        }
    }

    public static List<List<String>> getFileContents(String fileURL) {

        String type = getFileType(fileURL);
        if (type.equals("csv") || type.equals("sql")) {
            return MyFileReader.readCSVContent(fileURL);
        } else {
            return MyFileReader.readXMLContent(fileURL);
        }
    }

    public static <E> Set<Set<E>> powerSet(Set<E> originalSet) {
        Set<Set<E>> sets = new HashSet<Set<E>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<E> list = new ArrayList<>(originalSet);
        E head = list.get(0);
        Set<E> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<E> set : powerSet(rest)) {
            Set<E> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public static void printContext(IContext context) {
        INode root = context.getRoot();

        Queue<INode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            INode curNode = queue.remove();
            System.out.println(((INodeData) curNode).getName());
            queue.addAll(curNode.getChildren());
        }

    }

}
