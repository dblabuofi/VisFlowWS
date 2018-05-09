/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.trees.INode;

/**
 *
 * @author jupiter
 */
public interface ISchema {
        public QueryTree getSchema(QueryTree originalTree,  IContextMapping<INode> result);
}
