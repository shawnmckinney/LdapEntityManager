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
package org.apache.directory.lem.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.lem.Config;
import org.apache.directory.lem.LemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityDao extends DaoBase
{
    private static final String CLS_NM = EntityDao.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );   
    
    /**
     * This routine uses Java Reflection to iterate over an object's fields and convert into physical LDAP.
     * It uses two objects: the entity's name is pulled from "model", the values from the "entity".
     * @param inmodel The mappings between the logical and physical data model.
     * @param inentity The data to be loaded into the directory. It's formatted using the logical structure.
     * @param type is bound to the users and groups container coordinates in config.properties.
     * @throws LemException 
     */
    public void create( Object inmodel, Object inentity, String type ) throws LemException
    {
        LdapConnection ld = null;
        Field[] fields = inentity.getClass().getDeclaredFields();
        String nodeDn = null;
        try 
        {      
            Entry myEntry = new DefaultEntry( );
            for ( Field entity : fields ) 
            {
                entity.setAccessible(true);
                String name = entity.getName();
                LOG.debug( "Name: {}", name );
                Field model = inmodel.getClass().getDeclaredField( name );
                model.setAccessible( true );
                switch ( entity.getType().getSimpleName() )
                {
                    case "List":
                        LOG.info("LIST: {}", entity.get(inmodel));
                        if ((List)entity.get( inmodel ) != null && model.get ( inentity ) != null )
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
                                String modelAttrName = null;
                                if( modelAttrs.size() > 1 )
                                {
                                    modelAttrName = (String)modelAttrs.get(i);
                                }
                                else
                                {
                                    modelAttrName = (String)modelAttrs.get(0);
                                }
                                LOG.debug("LIST ATTR NM: {}, VALUE: {}", modelAttrName, entityAttrValue);
                                myEntry.add(modelAttrName, entityAttrValue.toString());
                                i++;
                            }   
                        }
                        break;

                    case "String":
                        entity.setAccessible( true );
                        String modelAttrName = (String)model.get(inmodel);
                        String entityAttrValue = (String)entity.get( inentity );                        
                        LOG.debug("String ATTR NM: {}, VALUE: {}", modelAttrName, entityAttrValue);
                        if ( name.compareToIgnoreCase("rdn") == 0 )
                        {
                            nodeDn = modelAttrName + "=" + entityAttrValue + "," + Config.getString( type );
                            myEntry.setDn( nodeDn );
                            LOG.debug("NODE DN: {}", nodeDn );
                        }
                        else
                        {
                            myEntry.add(modelAttrName, entityAttrValue);                            
                        }
                        break;
                }
            }
            ld = getConnection();
            add( ld, myEntry );
        }
        catch (IllegalArgumentException | IllegalAccessException ex) 
        {
            java.util.logging.Logger.getLogger(EntityDao.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NoSuchFieldException ex) 
        {
            java.util.logging.Logger.getLogger(EntityDao.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SecurityException ex) 
        {
            java.util.logging.Logger.getLogger(EntityDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch ( LdapException e )
        {
            String error = "create user node dn [" + nodeDn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }        
        finally
        {
            closeConnection( ld );
        }
    }        
}
