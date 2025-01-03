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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
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
    
    /**
     * @param modelFile
     * @param dataFile
     * @param className
     * @throws LemException 
     */    
    public void add( String modelFile, String dataFile, String className ) throws LemException
    {
        add( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }    

    public void add( Entity model, Entity data ) throws LemException
    {
        EntityDao eDao = new EntityDao();            
        MultiValuedMap map = EntityMapper.loadMap( model, data, false );
        eDao.create(map);
    }
            
    public void update( Entity model, Entity data ) throws LemException
    {
        EntityDao eDao = new EntityDao();            
        MultiValuedMap map = EntityMapper.loadMap( model, data, false );
        eDao.mod(map);
    }
            
    public void delete( Entity model, Entity data ) throws LemException
    {
        EntityDao eDao = new EntityDao();            
        MultiValuedMap map = EntityMapper.loadMap( model, data, false );        
        eDao.remove(map);
    }
            
    public Entity read( String modelFile, String dataFile, String className ) throws LemException
    {
        return read( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }
            
    public Entity read( Entity model, Entity data ) throws LemException
    {
        EntityDao eDao = new EntityDao();            
        MultiValuedMap inMap = EntityMapper.loadMap( model, data, true );
        MultiValuedMap outMap = eDao.get(inMap);
        Entity entity = instantiate ( model );
        EntityMapper.unloadMap(model, entity, outMap);
        return entity;
    }
    
    public List<Entity> find( Entity model, Entity data ) throws LemException
    {
        List<Entity> entities = null;
        EntityDao eDao = new EntityDao();            
        MultiValuedMap inMap = EntityMapper.loadMap( model, data, true );
        List<MultiValuedMap> outMap = eDao.find( Config.getString( model.getClass().getTypeName() ), inMap );
        if ( outMap != null && !outMap.isEmpty() )
        {
            entities = new ArrayList<>();
            for ( MultiValuedMap item : outMap )
            {
                Entity entity = instantiate ( model );
                EntityMapper.unloadMap(model, entity, item);
                entities.add( entity );
            }
        }
        return entities;
    }    

    private Entity instantiate ( Entity model ) throws LemException
    {
        Entity empty = null;
        try 
        {                    
            empty = model.getClass().getDeclaredConstructor().newInstance();
        } 
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException  ex) 
        {
            throw new LemException( "instantiate caught exception: " + ex.getMessage(), ex );
        }      
        return empty;
    }
    
    public void update( String modelFile, String dataFile, String className ) throws LemException
    {
        update( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }
            
    public void delete( String modelFile, String dataFile, String className ) throws LemException
    {
        delete( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );        
    }
            
    public List<Entity> find( String modelFile, String dataFile, String className ) throws LemException
    {
        return find( ResourceUtil.unmarshal( modelFile, className ), ResourceUtil.unmarshal( dataFile, className ) );
    }
}
