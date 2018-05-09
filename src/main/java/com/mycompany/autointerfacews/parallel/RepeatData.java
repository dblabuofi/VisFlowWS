/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.mycompany.autointerfacews.bioflow.BioFlowService;
import static com.mycompany.autointerfacews.bioflow.BioFlowService.executeBooleanExp;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.MySQLHelper;
import com.mycompany.autointerfacews.mydata.MyStatus;
import com.mycompany.autointerfacews.resources.ParseResources;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 *
 * @author mou1609
 */
public class RepeatData implements Callable<String> {

    Node cur;
    MyStatus status;
    Map<String, Map<String, Set<String>>> mappedRepeated;

    public RepeatData(Node cur, MyStatus status, Map<String, Map<String, Set<String>>> mappedRepeated) {
        this.cur = cur;
        this.status = status;
        this.mappedRepeated = mappedRepeated;
    }

    @Override
    public String call() {
        String location = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data\\";
        try {
            System.out.println("repeat Data");
            System.out.println(cur);
            List<String> resources = new ArrayList<>();

            Action action = cur.getActions().get(0);
            String start = action.getRepeatNode().getId();
            String end = cur.getId();

            if (action.getconditionType().equals("count")) {
                if (Integer.valueOf(action.getRepeatTimes()) > 0) {//no  this
                    action.setRepeatTimes("" + (Integer.valueOf(action.getRepeatTimes()) - 1));
                    ParseResources.parallelExecution.generateRepeatedTask(mappedRepeated.get(cur.getId()));
                }
            } else {
                System.out.println("generate inputs");
                String fileName = action.getConditions().get(0).getResource();
                String type = MyUtils.getFileType(fileName);

                List<List<String>> inputs = new ArrayList<>();
                if (type.equals("csv")) {
                    inputs = MyFileReader.readCSV(location + fileName);
                } else if (type.equals("xml")) {
                    inputs = MyFileReader.readXML(location + fileName);
                } else if (type.equals("sql")) {
                    MySQLHelper.downLoadaTableToLocal(fileName, location, fileName);
                    inputs = MyFileReader.readCSV(location + fileName);
                }

                List<String> attributes = action.getConditions().stream().map(t -> t.getAttrbute()).collect(Collectors.toList());
                List<String> groupOP = action.getConditions().stream().map(t -> t.getGroupOP()).collect(Collectors.toList());
                List<String> conditions = action.getConditions().stream().map(t -> t.getCondition()).collect(Collectors.toList());
                List<String> values = action.getConditions().stream().map(t -> t.getValue()).collect(Collectors.toList());
                List<String> logic = action.getConditions().stream().map(t -> t.getLogic()).collect(Collectors.toList());

                List<String> headers = inputs.remove(0);
                int index = headers.indexOf(attributes.get(0));
                System.out.println(index);
                List<String> cols = inputs.stream()
                        .map(t -> t.get(index))
                        .collect(Collectors.toList());
                System.out.println(cols);
                boolean result = executeBooleanExp(cols, groupOP.get(0), conditions.get(0), values.get(0));
                System.out.println(result);
                //we do step by step
                for (int i = 1; i < attributes.size(); i += 2) {
                    Boolean first = null;
                    int index1 = headers.indexOf(attributes.get(i + 1));
                    cols = inputs.stream()
                            .map(t -> t.get(index1))
                            .collect(Collectors.toList());
                    first = executeBooleanExp(cols, groupOP.get(i + 1), conditions.get(i + 1), values.get(i + 1));
                    if (!logic.isEmpty() && logic.get(i).equals("and")) {
                        result &= first;
                    } else {
                        result |= first;
                    }
                }
                System.out.println(result);

                //remove node
                if (result) {
                    action.setRepeatTimes("0");
                } else {
                    ParseResources.parallelExecution.generateRepeatedTask(mappedRepeated.get(cur.getId()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("if data");
        List<String> outputs = new ArrayList<>();
        for (MyResource res : cur.getResourcesOut()) {
            outputs.add(location + res.getUrlReturnFileName());
        }
        cur.setOutputs(outputs);
        return "";
    }
}
