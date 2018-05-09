/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class Action {

    String act;
    String id;
    List<String> inputFileNames;
    List<String> outputFileNames;
    List<String> outputFileTypes;
    String resultMethod;
    MyResource targetResource;
    Function targetFunction;
    //
    String methodReturnFileSchema;
    SelectAttr afterAction;
    List<SelectAttr> selectAttrs;
    List<UpdateAttr> updateAttrs;
    List<UpdateAttr> newAttrs;

    String val;
    String codeType;
    String codeName;
    String resourceName;
    String attr;
    String url;

    List<String> xqueries;

    //combine
    String matcher;
    String identifier;
    List<String> leftKeys;
    List<String> rightKeys;
    
    //mergetable
    String mergeTableSelect;
    String mergeTableSelectInput;

    //if
    List<MyCondition> conditions;
    MyBranch trueBranch;
    MyBranch falseBranch;
    
    //repeat
    String conditionType;
    RepeatNode repeatNode;
    String repeatTimes;
    
    //tramsform
    List<MyTransform> transformResources;
    List<MyTransform> transformResourcesAttributes;

    //other
    List<Function> webservices;

    //terminal
    String printType;
    Integer numOfWins;
    TerminalFormation submit;
    List<ColFunction> colFuns;
    
    
    //for procedure
    Module module;
    List<ProcedureReplace> inputReplace;
    List<ProcedureReplace> outputReplace;

    public void setRepeatTimes(String repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public String getRepeatTimes() {
        return repeatTimes;
    }

    public RepeatNode getRepeatNode() {
        return repeatNode;
    }

    public String getconditionType() {
        return conditionType;
    }

    public List<String> getLeftKeys() {
        return leftKeys;
    }

    public List<String> getRightKeys() {
        return rightKeys;
    }

    public Module getModule() {
        return module;
    }

    public List<ProcedureReplace> getInputReplace() {
        return inputReplace;
    }

    public List<ProcedureReplace> getOutputReplace() {
        return outputReplace;
    }
    
    public List<UpdateAttr> getUpdateAttrs() {
        return updateAttrs;
    }

    public List<UpdateAttr> getNewAttrs() {
        return newAttrs;
    }

    public String getMergeTableSelect() {
        return mergeTableSelect;
    }

    public String getMergeTableSelectInput() {
        return mergeTableSelectInput;
    }

    public String getMethodReturnFileSchema() {
        return methodReturnFileSchema;
    }

    public SelectAttr getAfterAction() {
        return afterAction;
    }

    public String getMatcher() {
        return matcher;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getXqueries() {
        return xqueries;
    }

    public List<SelectAttr> getSelectAttrs() {
        return selectAttrs;
    }

    public List<MyCondition> getConditions() {
        return conditions;
    }

    public MyBranch getTrueBranch() {
        return trueBranch;
    }

    public MyBranch getFalseBranch() {
        return falseBranch;
    }

    public List<MyTransform> getTransformResources() {
        return transformResources;
    }

    public List<MyTransform> getTransformResourcesAttributes() {
        return transformResourcesAttributes;
    }

    public Function getTargetFunction() {
        return targetFunction;
    }

    public String getAct() {
        return act;
    }

    public String getId() {
        return id;
    }

    public List<String> getInputFileNames() {
        return inputFileNames;
    }

    public List<String> getOutputFileNames() {
        return outputFileNames;
    }

    public List<String> getOutputFileTypes() {
        return outputFileTypes;
    }

    public String getResultMethod() {
        return resultMethod;
    }

    public MyResource getTargetResource() {
        return targetResource;
    }

    public String getVal() {
        return val;
    }

    public String getCodeType() {
        return codeType;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getPrintType() {
        return printType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getAttr() {
        return attr;
    }

    public String getUrl() {
        return url;
    }

    public List<Function> getWebservices() {
        return webservices;
    }

    public Integer getNumOfWins() {
        return numOfWins;
    }

    public TerminalFormation getSubmit() {
        return submit;
    }

    public List<ColFunction> getColFuns() {
        return colFuns;
    }

}
