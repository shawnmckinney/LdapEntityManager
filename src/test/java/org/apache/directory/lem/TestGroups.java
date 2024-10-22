/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.lem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class TestGroups 
{
    private static final String CLS_NM = TestGroups.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        System.out.println("Add Group Foo");
        GroupMgr gMgr = new GroupMgrImpl();
        Group group = new Group();
        group.setName( "Foo1");
        group.setDescription( "Fighters" );
        
        try
        {
            gMgr.add(group);
            LOG.info( "Group Add Successfull [{}]", group.getName() );
        }
        catch ( org.apache.directory.lem.LemException le )
        {
            LOG.error( CLS_NM, le );
        }        
    }
}
