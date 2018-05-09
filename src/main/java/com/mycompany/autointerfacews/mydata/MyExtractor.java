/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class MyExtractor {
        String extractorName;
        List<Inductionrules> inductionrules;
        List<String> headers;
        //table extractor
        String tableXpath;
        String startTRXpath;

        public String getExtractorName() {
                return extractorName;
        }

        public List<Inductionrules> getInductionrules() {
                return inductionrules;
        }

        public List<String> getHeaders() {
                return headers;
        }

        public String getTableXpath() {
                return tableXpath;
        }

        public String getStartTRXpath() {
                return startTRXpath;
        }
         
}
