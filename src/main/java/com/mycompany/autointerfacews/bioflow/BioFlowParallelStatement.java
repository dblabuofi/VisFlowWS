/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.bioflow;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mou1609
 */
public class BioFlowParallelStatement {
    List<String> parallels;
    List<String> afters;

    public BioFlowParallelStatement() {
    }

    public BioFlowParallelStatement(List<String> parallels, List<String> afters) {
        this.parallels = parallels;
        this.afters = afters;
    }

    public List<String> getParallels() {
        return parallels;
    }

    public List<String> getAfters() {
        return afters;
    }

    @Override
    public String toString() {
        return "perform parallel " + StringUtils.join(parallels, ", ") + " after " + StringUtils.join(afters, ", ") + ';';
    }
    
    
    
}
