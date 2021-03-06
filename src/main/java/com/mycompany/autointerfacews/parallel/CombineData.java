/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.inject.Inject;
import com.mycompany.autointerfacews.bioflow.BioFlowCombineStatement;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.generator.InputGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 *
 * @author jupiter
 */
public class CombineData implements Callable<String> {

    Node cur;
    BioFlowService bioFlowService;
    EXist eXist;
    InputGenerator inputGenerator;
    public CombineData(Node cur, BioFlowService bioFlowService, EXist eXist, InputGenerator inputGenerator) {
        this.cur = cur;
        this.bioFlowService = bioFlowService;
        this.inputGenerator = inputGenerator;
        this.eXist = eXist;
    }

    @Override
    public String call() throws CodeException, IOException {
        try {
            System.out.println("combine data");

            BioFlowCombineStatement combineScript = bioFlowService.generateBioFlowCombineScript(cur, cur.getActions().get(0));

    //        String outputFileURL = bioFlowService.executeBioFlowCombineStatement(combineScript, cur.getResourcesIn().get(0).getLocation(), cur.getResourcesOut().get(0).getUrlReturnFileName(), cur.getActions().get(0).getLeftKeys(), cur.getActions().get(0).getRightKeys());
//            System.out.println("*************^^^^^^^^^^^^^^^^^%%%%%%%%%%%%%%%%%");
            System.out.println(inputGenerator == null);
            String outputFileURL = bioFlowService.executeBioFlowCombineStatement(combineScript, cur.getResourcesIn().get(0).getLocation(),
                    cur.getResourcesOut().get(0).getUrlReturnFileName(), cur.getActions().get(0).getLeftKeys(), cur.getActions().get(0).getRightKeys(),
                    cur.getResourcesIn().get(0).getOutAttributes().stream().map(t->t.getName()).collect(Collectors.toList()),
                    cur.getResourcesIn().get(1).getOutAttributes().stream().map(t->t.getName()).collect(Collectors.toList()),
                    inputGenerator
                    );
//            System.out.println("here!!");
//            System.out.println(outputFileURL);
//
            List<String> outputs = new ArrayList<>();
            outputs.add(outputFileURL);
            cur.setOutputs(outputs);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
