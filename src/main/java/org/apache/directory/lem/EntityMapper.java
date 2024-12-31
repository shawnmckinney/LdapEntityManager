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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityMapper 
{
    private static final String CLS_NM = EntityMapper.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );   

    public static Entity unloadMap ( Entity inmodel, Entity inentity, MultiValuedMap map ) throws LemException
    {
        Field[] fields = inentity.getClass().getDeclaredFields();
        try 
        {      
            for ( Field entity : fields ) 
            {
                entity.setAccessible(true);
                String name = entity.getName();
                LOG.debug( "Name: {}", name );
                Field model = inmodel.getClass().getDeclaredField( name );
                Field outField = inentity.getClass().getDeclaredField( name );
                model.setAccessible( true );
                outField.setAccessible( true );
                switch ( entity.getType().getSimpleName() )
                {
                    case "List" -> 
                    {
                        LOG.debug("LIST: {}", entity.get(inmodel));
                        if (model.get ( inmodel ) != null )
                        {
                            List modelAttrs = (List)model.get(inmodel);
                            int i = 0;
                            List<String> attrValues = new ArrayList<String>();
                            
                            for ( var modelAttrValue : (List)model.get ( inmodel ))
                            {
                                LOG.debug("LIST: {}, ENTITY VALUE: {}, NM {}", entity.get(inmodel), model.get ( inentity ), modelAttrValue );
                                String modelAttrName;
                                if( modelAttrs.size() > 1 )
                                {
                                    modelAttrName = (String)modelAttrs.get(i);
                                }
                                else
                                {
                                    modelAttrName = (String)modelAttrs.get(0);
                                }
                                
                                Collection<String> mapVals = map.get(modelAttrName);                                
                                for( String val : mapVals )
                                {
                                    attrValues.add(val);                        
                                }     
                                i++;
                            }
                            outField.set(inentity, attrValues);
                        }
                    }
                    case "String" -> 
                    {
                        entity.setAccessible( true );
                        String modelAttrName = (String)model.get(inmodel);
                        String entityAttrValue = (String)entity.get( inentity );                        
                        LOG.debug("String ATTR NM: {}, VALUE: {}", modelAttrName, entityAttrValue);
                        Collection<String> va1 = map.get(modelAttrName);                                
                        for( String v : va1 )
                        {
                            outField.set(inentity, v);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) 
        {
            java.util.logging.Logger.getLogger(CLS_NM).log(Level.SEVERE, null, ex);
        }
        return inentity;
    }  
    
    /**
     * This routine uses Java Reflection to iterate over an object's fields and convert into LDAP attributes.
     * It uses two objects: the entity's name is pulled from "model", the values from the "entity".
     * @param inmodel The mappings between the logical and physical data model.
     * @param inentity The data to be loaded into the directory. It's formatted using the logical structure.
     * @throws LemException 
     */
    public static MultiValuedMap loadMap ( Entity inmodel, Entity inentity ) throws LemException
    {
        Field[] fields = inentity.getClass().getDeclaredFields();
        MultiValuedMap map = new ArrayListValuedHashMap();
        try 
        {      
            for ( Field entity : fields ) 
            {
                entity.setAccessible(true);
                String name = entity.getName();
                LOG.debug( "Name: {}", name );
                Field model = inmodel.getClass().getDeclaredField( name );
                model.setAccessible( true );
                switch ( entity.getType().getSimpleName() )
                {
                    case "List" -> 
                    {
                        LOG.debug("LIST: {}", entity.get(inmodel));                  
                        if (model.get ( inmodel ) != null && entity.get ( inentity ) != null)
                        {
                            List modelAttrs = (List)model.get(inmodel);
                            /* Rules for Attr lists:
                            1. If model's attr has one value (type), it's a multival ldap attr, e.g. emails
                            2024-10-31 19:47:020 INFO  EntityDao:82 - LIST AAR: [objectClass], ENTITY VALUE: [inetorgperson, posixaccount]
                            2. If model's attr list > 1, it's a collection of single value attrs, e.g. addresses LIST: [ postalAddress, l, postalCode ]
                            */            
                            int i = 0;
                            for ( var entityAttrValue : (List)entity.get ( inentity ))
                            {    
                                LOG.debug("LIST: {}, ENTITY VALUE: {}, NM {}", entity.get(inmodel), model.get ( inentity ), entityAttrValue );
                                String modelAttrName;
                                if( modelAttrs.size() > 1 )
                                {
                                    modelAttrName = (String)modelAttrs.get(i);
                                    map.put( modelAttrName, entityAttrValue.toString() );
                                }
                                else
                                {
                                    modelAttrName = (String)modelAttrs.get(0);
                                    map.put( modelAttrName, entityAttrValue.toString() );
                                }
                                LOG.debug("LIST ATTR NM: {}, VALUE: {}", modelAttrName, entityAttrValue);
                                i++;
                            }   
                        }
                    }
                    case "String" -> 
                    {
                        entity.setAccessible( true );
                        String modelAttrName = (String)model.get(inmodel);
                        String entityAttrValue = (String)entity.get( inentity );                        
                        LOG.debug("String ATTR NM: {}, VALUE: {}", modelAttrName, entityAttrValue);
                        if ( entityAttrValue != null )
                        {
                            //if ( name.compareToIgnoreCase("rdn") == 0 )
                            if ( name.compareToIgnoreCase("key") == 0 )
                            {
                                String nodeDn = modelAttrName + "=" + entityAttrValue + "," + Config.getString( inentity.getClass().getTypeName() );
                                LOG.debug("NODE DN: {}", nodeDn );
                                map.put( "dn", nodeDn );                                
                            }
                            else
                            {
                                map.put( modelAttrName, entityAttrValue );
                            }                            
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) 
        {
            java.util.logging.Logger.getLogger(CLS_NM).log(Level.SEVERE, null, ex);
        }
        return map;
    }  
    
    private <T> void inspect(Class<T> klazz, Object object) 
    {
        Field[] fields = klazz.getDeclaredFields();
        System.out.printf("%d fields:%n", fields.length);
        for (Field field : fields) 
        {
            field.setAccessible(true);
            try 
            {
                LOG.info("{} {} {} {}",
                        Modifier.toString(field.getModifiers()),
                        field.getType().getSimpleName(),
                        field.getName(),
                        field.get(object)
                );
            } 
            catch (IllegalArgumentException | IllegalAccessException ex) 
            {
                java.util.logging.Logger.getLogger(CLS_NM).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
