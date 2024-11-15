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
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.directory.lem.dao.EntryDao;
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
    
    /**
     * WIP
     * @param modelFile
     * @param dataFile
     * @param className
     * @throws LemException 
     */    
    public void add( String modelFile, String dataFile, String className ) throws LemException
    {
        add( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }    

    public void add( Object model, Object data ) throws LemException
    {
        try
        {
            EntryDao eDao = new EntryDao();            
            MultiValuedMap map = EntityMapper.loadMap( model, data );            
            eDao.create(map);
            LOG.info("Successful Test");
        }
        catch ( LemException e )
        {
            LOG.error( CLS_NM, e );
        }        
    }
            
    public void update( Object model, Object data ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public void delete( Object model, Object data ) throws LemException
    {
        try
        {
            EntryDao eDao = new EntryDao();            
            MultiValuedMap map = EntityMapper.loadMap( model, data );            
            eDao.remove(map);
        }
        catch ( LemException e )
        {
            LOG.error( CLS_NM, e );
        }        
    }
            
    public Object read( String modelFile, String dataFile, String className ) throws LemException
    {
        return read( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }
            
    public Object read( Object model, Object data ) throws LemException
    {
        MultiValuedMap outMap = null;
        Object outEntity = null;
        try
        {
            EntryDao eDao = new EntryDao();            
            MultiValuedMap map = EntityMapper.loadMap2( model, data );            
            outMap = eDao.get(map);
            outEntity = EntityMapper.unloadMap(model, data, outMap);
            LOG.debug("finished");
            
        }
        catch ( LemException e )
        {
            LOG.error( CLS_NM, e );
        }        
        return outEntity;        
    }
    
    public List<Object> find( Object model, Object data ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }    
    
    public void update( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
            
    public void delete( String modelFile, String dataFile, String className ) throws LemException
    {
        delete( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );        
    }
            
    public List<Object> find( String modelFile, String dataFile, String className ) throws LemException
    {
        throw new java.lang.UnsupportedOperationException();
    }
}
