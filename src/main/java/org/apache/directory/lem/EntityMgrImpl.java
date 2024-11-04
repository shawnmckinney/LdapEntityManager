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
import org.apache.directory.lem.dao.EntityDao;
import org.apache.directory.lem.dao.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityMgrImpl implements EntityMgr
{
    private static final String CLS_NM = EntityMgrImpl.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    
    public void add( String modelFile, String dataFile, String className ) throws LemException
    {
        EntityDao eDao = new EntityDao();
        try
        {
            Object inModel = ResourceUtil.unmarshal( modelFile, className );
            Object inData = ResourceUtil.unmarshal( dataFile, className );
            LOG.info("Entity info [{}]", inModel.toString());
            eDao.create( inModel, inData );
            LOG.info("Successful Test");
        }
        catch ( LemException e )
        {
            LOG.error( CLS_NM, e );
        }        
    }
    
    public void update( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public void delete( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public Object read( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public List<Object> find( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }            
}
