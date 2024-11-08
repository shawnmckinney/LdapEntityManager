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

import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.lem.LemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntryDao extends DaoBase
{
    private static final String CLS_NM = EntryDao.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );   
    
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
    
}
