package com.mycompany.autointerfacews.bioflow;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class BioFlowExtractStatement {
        List<String> extractAttributes;
        String matcher;
        String wrapper;
        List<String> wrapperTargets;
        String resultContainHeaderInfo;
        String filler;
        String from;
        List<String> submit;
        String where;
        //annotation
        String resultMethod;
        String submitMethod;
        String submitProtocol;
        @Override
        public String toString() {
                if (where.equals("")) {
                        return "extract " + resultMethod + "(" + StringUtils.join(extractAttributes, ", ") + ")" + "\n" 
                        + " using matcher " + matcher + " wrapper " + wrapper + "(" + StringUtils.join(wrapperTargets, ", ") + ")" + " filler " + filler + "\n" 
                        + " from " + submitMethod + "(" + from + ")" + "\n" 
                        + " submit " + StringUtils.join(submit);
                        
                } 
                return "extract " + StringUtils.join(extractAttributes, ", ") + "\n" 
                        + " using matcher " + matcher + " wrapper " + wrapper + " filler " + "\n" 
                        + " from " + from + "\n" 
                        + " submit " + submit + " where " + where;
        }

        public BioFlowExtractStatement() {
        }

        public BioFlowExtractStatement(List<String> extractAttributes, String matcher, String wrapper, List<String> wrapperTargets, String resultContainHeaderInfo, String filler, String from, List<String> submit, String where, String resultMethod, String submitMethod, String submitProtocol) {
                this.extractAttributes = extractAttributes;
                this.matcher = matcher;
                this.wrapper = wrapper;
                this.wrapperTargets = wrapperTargets;
                this.resultContainHeaderInfo = resultContainHeaderInfo;
                this.filler = filler;
                this.from = from;
                this.submit = submit;
                this.where = where;
                this.resultMethod = resultMethod;
                this.submitMethod = submitMethod;
                this.submitProtocol = submitProtocol;
        }

        public String getSubmitProtocol() {
                return submitProtocol;
        }

        

        public String getResultContainHeaderInfo() {
                return resultContainHeaderInfo;
        }

        

        public List<String> getWrapperTargets() {
                return wrapperTargets;
        }

        

        public List<String> getExtractAttributes() {
                return extractAttributes;
        }

        public String getMatcher() {
                return matcher;
        }

        public String getWrapper() {
                return wrapper;
        }

        public String getFiller() {
                return filler;
        }

        public String getFrom() {
                return from;
        }

        public List<String> getSubmit() {
                return submit;
        }

        public String getWhere() {
                return where;
        }

        public String getResultMethod() {
                return resultMethod;
        }

        public String getSubmitMethod() {
                return submitMethod;
        }

        
        
        
}
