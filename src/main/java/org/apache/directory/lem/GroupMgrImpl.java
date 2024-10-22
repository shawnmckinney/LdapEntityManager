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

import org.apache.directory.lem.dao.GroupDao;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class GroupMgrImpl implements GroupMgr
{
    private static final String CLS_NM = GroupMgrImpl.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    
    
    public Group add( Group group ) throws LemException
    {
        GroupDao gDao = new GroupDao();
        return gDao.create(group);
    }
    
    public Group update( Group group ) throws SecurityException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public Group delete( Group group ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public Group read( Group group ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public List<Group> find( Group group ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }   
}
