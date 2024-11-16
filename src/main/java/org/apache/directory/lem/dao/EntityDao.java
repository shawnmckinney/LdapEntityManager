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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.lem.LemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityDao extends BaseDao
{
    private static final String CLS_NM = EntityDao.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );   
    
    public MultiValuedMap get( MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
        String nodeDn = null;
        MultiValuedMap out = null;
        Entry entry = new DefaultEntry();
        try
        {
            List<String> attrs = new ArrayList<String>();
            for (String key : entryMap.keySet())
            {
                //LOG.debug( "key [{}], value: [{}]", key, entryMap.get( key ) );
                List<String> vals = (List)entryMap.get( key );                
                for ( String value : vals )
                {
                    if( key.toString().equalsIgnoreCase("DN"))
                    {
                        nodeDn = value;
                    }
                    else
                    {
                        attrs.add( key );
                    }
                }                
            }           
            String[] atrs = attrs.toArray(new String[0]);
            ld = getConnection();            
            entry = read( ld, nodeDn, atrs );
            //entry = read( ld, nodeDn, new String[]{"*"} );
            out = unloadLdapEntry( entry, entryMap );
        }
        catch ( LdapException e )
        {
            String error = "create node dn [" + nodeDn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
        
        LOG.debug( "read dn [{}]", nodeDn );        
        return out;
    }    

    private MultiValuedMap <String, List> unloadLdapEntry( Entry le, MultiValuedMap <String, List>entryMap )
        throws LdapInvalidAttributeValueException
    {
        MultiValuedMap map = new ArrayListValuedHashMap();   
        for (String key : entryMap.keySet())
        {
            LOG.info( "attr [{}], val [{}]", key, getAttribute( le, key ));
            if( key.toString().equalsIgnoreCase("DN"))
            {
                map.put(key, le.getDn().toString());
            }
            else
            {
                List at = getAttributes( le, key );
                if ( at != null )
                {
                    for( Object var : at )
                    {
                        map.put(key, var.toString() );
                    }                    
                }
            }
        }        
        return map;
    }
    
    public void create( MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
        String nodeDn = null;
        Entry entry = new DefaultEntry();
        try
        {
            for (String key : entryMap.keySet())
            {
                LOG.debug( "key [{}], value: [{}]", key, entryMap.get( key ) );
                List<String> vals = (List)entryMap.get( key );
                for ( String value : vals )
                {
                    if( key.toString().equalsIgnoreCase("DN"))
                    {
                        entry.setDn( value );
                    }
                    else
                    {
                        entry.add( key.toString(), value );                        
                    }
                }
            }            
            ld = getConnection();
            add( ld, entry );
            LOG.debug( "created group dn [{}]", nodeDn );
        }
        catch ( LdapException e )
        {
            String error = "create node dn [" + nodeDn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
    }    

    /**
     * 
     * @param entryMap
     * @throws LemException 
     */    
    public void remove( MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
        String dn = null;
        try
        {
            List<String> vals = (List)entryMap.get( "dn" );
            dn = vals.get(0);
            ld = getConnection();
            ld.delete( dn );
            LOG.debug( "removed dn [{}]", dn );
        }
        catch ( LdapException e )
        {
            String error = "remove node dn [" + dn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
    }    
}
