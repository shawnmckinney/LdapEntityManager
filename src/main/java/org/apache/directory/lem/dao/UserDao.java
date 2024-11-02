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

import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapNoSuchObjectException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.lem.Config;
import org.apache.directory.lem.User;
import org.apache.directory.lem.Ids;
import org.apache.directory.lem.LemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class UserDao extends DaoBase 
{
    private static final String CLS_NM = UserDao.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    
    private String USER_OBJECT_CLASS_IMPL = Config.getString( Ids.USERS_OBJECT_CLASS );
    
    public User create( User user ) throws LemException
    {
        LdapConnection ld = null;
        String nodeDn = getDn( user.getName() );
        try
        {
            LOG.debug( "create user dn [{}]", nodeDn );
            Entry myEntry = new DefaultEntry( nodeDn );
            myEntry.add( SchemaConstants.OBJECT_CLASS_AT, USER_OBJECT_CLASS_IMPL );
            myEntry.add( SchemaConstants.CN_AT, user.getName() );
            myEntry.add( SchemaConstants.SN_AT, user.getName() );
            myEntry.add( SchemaConstants.UID_AT, user.getName() );            
            loadAttrs( user.getDescription(), myEntry, SchemaConstants.DESCRIPTION_AT );

            LOG.debug("users USER_OBJ_CLASS: [{}], CN: [{}]",
                    USER_OBJECT_CLASS_IMPL,
                    nodeDn );
            ld = getConnection();
            add( ld, myEntry );
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

        return user;
    }
    
    
    public User getUser( User user ) throws LemException
    {
        User entity = null;
        LdapConnection ld = null;
        String userDn = getDn( user.getName() );
        Entry findEntry;

        try
        {
            ld = getConnection();
            findEntry = read( ld, userDn, defaultAtrs );
        }
        catch ( LdapNoSuchObjectException e )
        {
            String warning = "getUser LdapNoSuchObjectException userDn [" + userDn + "]";
            throw new LemException( warning );
        }
        catch ( LdapException e )
        {
            String error = "getUser [" + userDn + "]= caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }
        try
        {
            if ( findEntry != null )
            {
                entity = unloadLdapEntry( findEntry );
            }
        }
        catch ( LdapInvalidAttributeValueException e )
        {
            entity = null;
        }

        if ( entity == null )
        {
            String warning = "getUser userDn [" + userDn + "] not found";
            throw new LemException( warning );
        }
        return entity;
    }
    /**
     * @param le
     * @return
     * @throws LdapException
     */
    private User unloadLdapEntry( Entry le )
        throws LdapInvalidAttributeValueException
    {
        User entity = new User();
        entity.setName( getAttribute( le, SchemaConstants.CN_AT ) );
        entity.setDescription( getAttributes( le, SchemaConstants.DESCRIPTION_AT ) );
        //entity.setDescription( getAttribute( le, SchemaConstants.DESCRIPTION_AT ) );
        return entity;
    }

    static String getDn( String name )
    {
        return SchemaConstants.UID_AT + "=" + name + "," + Config.getString( Ids.USERS );
    }    
    
    
    private static String[] defaultAtrs = new String[]
    {
        SchemaConstants.UID_AT,
        SchemaConstants.USER_PASSWORD_AT,
        SchemaConstants.DESCRIPTION_AT,
        SchemaConstants.OU_AT,
        SchemaConstants.CN_AT,
        SchemaConstants.SN_AT,
        SchemaConstants.POSTAL_ADDRESS_AT,
        SchemaConstants.L_AT,
        SchemaConstants.POSTALCODE_AT,
        SchemaConstants.POSTOFFICEBOX_AT,
        SchemaConstants.ST_AT,
        SchemaConstants.PHYSICAL_DELIVERY_OFFICE_NAME_AT,
        SchemaConstants.TELEPHONE_NUMBER_AT,
        SchemaConstants.MAIL_AT,
        SchemaConstants.TITLE_AT,
    };
    
}
