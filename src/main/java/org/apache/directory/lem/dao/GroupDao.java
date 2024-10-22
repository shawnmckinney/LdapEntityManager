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

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.lem.Config;
import org.apache.directory.lem.Group;
import org.apache.directory.lem.Ids;
import org.apache.directory.lem.LemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class GroupDao extends DaoBase
{
    private static final String CLS_NM = GroupDao.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    
    private String GROUP_OBJECT_CLASS_IMPL = Config.getString( Ids.GROUPS_OBJECT_CLASS );
    private String GROUP_MEMBER_ATR_IMPL = Config.getString( Ids.MEMBER_ATR );
    
    public Group create( Group group ) throws LemException
    {
        LdapConnection ld = null;
        String nodeDn = getDn( group.getName() );
        try
        {
            LOG.debug( "create group dn [{}]", nodeDn );
            Entry myEntry = new DefaultEntry( nodeDn );
            myEntry.add( SchemaConstants.OBJECT_CLASS_AT, GROUP_OBJECT_CLASS_IMPL );
            myEntry.add( SchemaConstants.CN_AT, group.getName() );
            loadAttrs( group.getMembers(), myEntry, GROUP_MEMBER_ATR_IMPL );
            if ( StringUtils.isNotEmpty( group.getDescription() ) )
            {
                myEntry.add( SchemaConstants.DESCRIPTION_AT, group.getDescription() );
            }

            LOG.debug("groups GROUP_OBJ_CLASS: [{}], GROUP_MEMBER_ATR_IMPL: [{}], CN: [{}]",
                    GROUP_OBJECT_CLASS_IMPL,
                    GROUP_MEMBER_ATR_IMPL,
                    nodeDn );
            ld = getConnection();
            add( ld, myEntry );
        }
        catch ( LdapException e )
        {
            String error = "create group node dn [" + nodeDn + "] caught LDAPException=" + e;
            throw new LemException( error, e );
        }
        finally
        {
            closeConnection( ld );
        }

        return group;
    }
    
    
    /**
     * @param le
     * @return
     * @throws LdapException
     */
    private Group unloadLdapEntry( Entry le )
        throws LdapInvalidAttributeValueException
    {
        Group entity = new Group();
        entity.setName( getAttribute( le, SchemaConstants.CN_AT ) );
        entity.setDescription( getAttribute( le, SchemaConstants.DESCRIPTION_AT ) );
        entity.setMembers( getAttributes( le, GROUP_MEMBER_ATR_IMPL ) );
        return entity;
    }

    static String getDn( String name )
    {
        return SchemaConstants.CN_AT + "=" + name + "," + Config.getString( Ids.GROUPS );
    }
}
