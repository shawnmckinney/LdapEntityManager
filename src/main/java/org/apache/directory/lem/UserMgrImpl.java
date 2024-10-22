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

import java.util.List;
import org.apache.directory.lem.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class UserMgrImpl implements UserMgr
{
    private static final String CLS_NM = UserMgrImpl.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    
    
    public User add( User user ) throws LemException
    {
        UserDao uDao = new UserDao();
        return uDao.create(user);
    }
    
    public User update( User user ) throws SecurityException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public User delete( User user ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public User read( User user ) throws LemException
    {
        UserDao uDao = new UserDao();
        return uDao.getUser(user);
    }
            
    public List<User> find( User user ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }       
}
