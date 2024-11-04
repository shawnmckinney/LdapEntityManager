/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE groups
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this groups
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this groups except in compliance
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityTest 
{
    private static final String CLS_NM = EntityTest.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        LOG.info("{} Entity Test", CLS_NM );        
        EntityTest t = new EntityTest();
        t.addEntity( "groups.yml", "groups-d1.yml", "org.apache.directory.lem.Group" );
        t.addEntity( "users.yml", "users-d1.yml", "org.apache.directory.lem.User" );
    }
    
    private void addEntity( String modelFile, String dataFile, String className )
    {
        EntityMgr eMgr = new EntityMgrImpl();
        try
        {
            eMgr.add( modelFile, dataFile, className );
            LOG.info("Successful Test");
        }
        catch ( LemException e )
        {
            LOG.error( CLS_NM, e );
        }        
    }    
}
