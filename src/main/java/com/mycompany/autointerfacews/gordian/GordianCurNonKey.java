/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.gordian;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class GordianCurNonKey {
        List<String> headers;
        Set<String> curNonKey;
        
        public void addAttrNo(int attrNo) {
                String key = headers.get(attrNo);
                curNonKey.add(key);
        }
        
        public void removeAttrNo(int attrNo) {
                String key = headers.get(attrNo);
                curNonKey.remove(key);
        }
        
        public Set<String> getCurNonKey() {
                return new HashSet<>(curNonKey); 
        }

        public GordianCurNonKey(List<String> headers) {
                this.headers = headers;
                curNonKey = new HashSet<>();
        }
   
        @Override
        public String toString() {
                String res = "";
                res += StringUtils.join(curNonKey, ", ") + "\n";
                return res;
        }
                
}
