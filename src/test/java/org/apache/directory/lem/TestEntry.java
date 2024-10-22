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

import java.util.HashMap;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.lem.dao.EntryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class TestEntry 
{
    private static final String CLS_NM = TestEntry.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        HashMap map = new HashMap<String, String>();
        // WIP:
        String uid = "foo";
        map.put(SchemaConstants.OBJECT_CLASS_AT, Config.getString( Ids.USERS_OBJECT_CLASS ));
        map.put(SchemaConstants.CN_AT, "foo bar");
        map.put(SchemaConstants.SN_AT, "bar");
        map.put(SchemaConstants.UID_AT, uid);
        map.put(SchemaConstants.DESCRIPTION_AT, " ... ");        
        map.put("DN", SchemaConstants.UID_AT + "=" + uid + "," + Config.getString( Ids.USERS ));                
        EntryDao eDao = new EntryDao();
        try
        {
            eDao.create( map );
            LOG.info( "Add Successfull [{}]", map.get("DN"));
        }
        catch ( org.apache.directory.lem.LemException le )
        {
            LOG.error( CLS_NM, le );
        }        
    }        
}