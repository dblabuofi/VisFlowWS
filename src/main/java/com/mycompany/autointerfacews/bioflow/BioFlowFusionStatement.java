/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.bioflow;

/**
 *
 * @author jupiter
 */
public class BioFlowFusionStatement {
            String r;
        String s;
        String matcher;
        String identifer;
        String location;

        public BioFlowFusionStatement(String r, String s, String matcher, String identifer, String location) {
                this.r = r;
                this.s = s;
                this.matcher = matcher;
                this.identifer = identifer;
                this.location = location;
        }

        public String getR() {
                return r;
        }

        public String getS() {
                return s;
        }

        public String getMatcher() {
                return matcher;
        }

        public String getIdentifer() {
                return identifer;
        }

        public String getLocation() {
                return location;
        }
        
        
        
        
        
}
