/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.exception;

/**
 *
 * @author jupiter
 */
public class CodeException extends Exception {

        public CodeException(String errorDescription) {
                super(errorDescription);
        }

        public CodeException(String errorDescription, Throwable cause) {
                super(errorDescription, cause);
        }
}
