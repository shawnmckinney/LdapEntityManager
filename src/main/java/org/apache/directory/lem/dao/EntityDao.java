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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
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
            //entry = read( ld, nodeDn, atrs );
            entry = read( ld, nodeDn, new String[]{"*"} );
            if (entry == null)
            {
                throw new LemException( "Entry DN [" + nodeDn + "] not found" );
            }
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
    
    
    public List<MultiValuedMap> find( String rootDn, MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
        List<MultiValuedMap> out = new ArrayList<>();
        String filter = null;
        try
        {
            /* get search filter and attr list: */
            List<String> attrs = new ArrayList<String>();
            for (String key : entryMap.keySet())
            {
                //LOG.debug( "key [{}], value: [{}]", key, entryMap.get( key ) );
                List<String> vals = (List)entryMap.get( key );                
                for ( String value : vals )
                {
                    if( key.toString().equalsIgnoreCase("filter"))
                    {
                        filter = value;
                    }
                    else
                    {
                        attrs.add( key );
                    }
                }                
            }           
            String[] atrs = attrs.toArray(new String[0]);
            
            ld = getConnection();            
            try ( SearchCursor searchResults = search( ld, rootDn, SearchScope.ONELEVEL, filter, atrs, false ) )
            //try ( SearchCursor searchResults = search( ld, rootDn, SearchScope.ONELEVEL, filter, new String[]{"*"}, false ) )
            {
                long sequence = 0;
                while ( searchResults.next() )
                {
                    MultiValuedMap <String, List> entity = unloadLdapEntry( searchResults.getEntry(), entryMap );
                    out.add( entity );
                }
            }
            catch ( IOException e )
            {
                String error = "find base DN [" + rootDn + "] caught IOException=" + e.getMessage();
                throw new LemException( error, e );
            }
            catch ( CursorException e )
            {
                String error = "find base DN [" + rootDn + "] caught CursorException=" + e.getMessage();
                throw new LemException( error, e );
            }        
        }
        catch ( LdapException e )
        {
            String error = "find base DN [" + rootDn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
        
        LOG.debug( "find base DN [{}]", rootDn );        
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

    private MultiValuedMap <String, List> unloadLdapEntry( Entry le, String[] atrs  )
        throws LdapInvalidAttributeValueException
    {
        MultiValuedMap map = new ArrayListValuedHashMap();   
        for ( String key : atrs )
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
        return map;
    }
    
    public void create( MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
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
                    else if( ! key.toString().equalsIgnoreCase("filter") && ( ! value.isEmpty() ) )
                    {
                        entry.add( key.toString(), value );                        
                    }
                }
            }            
            ld = getConnection();
            add( ld, entry );
            LOG.debug( "created group dn [{}]", entry.getDn() );
        }
        catch ( LdapException e )
        {
            String error = "create node dn [" + entry.getDn() + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
    }    

    public void mod( MultiValuedMap <String, List>entryMap ) throws LemException
    {
        LdapConnection ld = null;
        String nodeDn = null;
        List<Modification> mods = new ArrayList<Modification>();
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
                        nodeDn = value;
                    }
                    else if(!key.toString().equalsIgnoreCase("filter") && ( ! value.isEmpty() ) )
                    {
                        mods.add( new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, key.toString(), value ) );
                        //mods.add( new DefaultModification( ModificationOperation.ADD_ATTRIBUTE, key.toString(), value ) );                            
                    }
                }
            }            
            if ( mods.size() > 0 )
            {
                ld = getConnection();
                modify( ld, nodeDn, mods );
                LOG.debug( "updated group dn [{}]", nodeDn );
            }
        }
        catch ( LdapException e )
        {
            String error = "mod node dn [" + nodeDn + "] caught LDAPException=" + e;
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
