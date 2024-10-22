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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.directory.api.ldap.extras.controls.ppolicy.PasswordPolicyRequest;
import org.apache.directory.api.ldap.extras.controls.ppolicy.PasswordPolicyRequestImpl;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.message.BindRequestImpl;
import org.apache.directory.api.ldap.model.message.BindResponse;
import org.apache.directory.api.ldap.model.message.CompareRequest;
import org.apache.directory.api.ldap.model.message.CompareRequestImpl;
import org.apache.directory.api.ldap.model.message.CompareResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;

/**
 *
 * @author smckinn
 */
abstract class DaoBase
{
    private static final PasswordPolicyRequest PP_REQ_CTRL = new PasswordPolicyRequestImpl();
    
    /**
     * Read the ldap record from specified location.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains ldap distinguished name.
     * @param attrs      array contains array names to pull back.
     * @return ldap entry.
     * @throws LdapException in the event system error occurs.
     */
    protected Entry read( LdapConnection connection, String dn, String[] attrs ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        return connection.lookup( dn, attrs );
    }


    /**
     * Read the ldap record from specified location.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains ldap distinguished name.
     * @param attrs      array contains array names to pull back.
     * @return ldap entry.
     * @throws LdapException in the event system error occurs.
     */
    protected Entry read( LdapConnection connection, Dn dn, String[] attrs ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        return connection.lookup( dn, attrs );
    }


    /**
     * Read the ldap record from specified location with user assertion.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains ldap distinguished name.
     * @param attrs      array contains array names to pull back.                                        ,
     *                   PoolMgr.ConnType.USER
     * @param userDn     string value represents the identity of user on who's behalf the request was initiated.  The
     *                   value will be stored in openldap auditsearch record AuthZID's attribute.
     * @return ldap entry.
     * @throws LdapException                in the event system error occurs.
     * @throws UnsupportedEncodingException for search control errors.
     */
    protected Entry read( LdapConnection connection, String dn, String[] attrs, String userDn ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        return connection.lookup( dn, attrs );
    }


    /**
     * Add a new ldap entry to the directory.  Do not add audit context.
     *
     * @param connection handle to ldap connection.
     * @param entry      contains data to add..
     * @throws LdapException in the event system error occurs.
     */
    protected void add( LdapConnection connection, Entry entry ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        connection.add( entry );
    }


    /**
     * Update exiting ldap entry to the directory.  Do not add audit context.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains distinguished node of entry.
     * @param mods       contains data to modify.
     * @throws LdapException in the event system error occurs.
     */
    protected void modify( LdapConnection connection, String dn, List<Modification> mods ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        connection.modify( dn, mods.toArray( new Modification[]{} ) );
    }


    /**
     * Update exiting ldap entry to the directory.  Do not add audit context.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains distinguished node of entry.
     * @param mods       contains data to modify.
     * @throws LdapException in the event system error occurs.
     */
    protected void modify( LdapConnection connection, Dn dn, List<Modification> mods ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        connection.modify( dn, mods.toArray( new Modification[]
            {} ) );
    }


    /**
     * Delete exiting ldap entry from the directory.  Do not add audit context.
     *
     * @param connection handle to ldap connection.
     * @param dn         contains distinguished node of entry targeted for removal..
     * @throws LdapException in the event system error occurs.
     */
    protected void delete( LdapConnection connection, String dn ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        connection.delete( dn );
    }


    /**
     * Perform normal ldap search accepting default batch size.
     *
     * @param connection is LdapConnection object used for all communication with host.
     * @param baseDn     contains address of distinguished name to begin ldap search
     * @param scope      indicates depth of search starting at basedn.  0 (base dn),
     *                   1 (one level down) or 2 (infinite) are valid values.
     * @param filter     contains the search criteria
     * @param attrs      is the requested list of attritubutes to return from directory search.
     * @param attrsOnly  if true pull back attribute names only.
     * @return result set containing ldap entries returned from directory.
     * @throws LdapException thrown in the event of error in ldap client or server code.
     */
    protected SearchCursor search( LdapConnection connection, String baseDn, SearchScope scope, String filter,
        String[] attrs, boolean attrsOnly ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        SearchRequest searchRequest = new SearchRequestImpl();
        searchRequest.setBase( new Dn( baseDn ) );
        searchRequest.setScope( scope );
        searchRequest.setFilter( filter );
        searchRequest.setTypesOnly( attrsOnly );
        searchRequest.addAttributes( attrs );
        return connection.search( searchRequest );
    }


    /**
     * Perform normal ldap search specifying default batch size and max entries to return.
     *
     * @param connection is LdapConnection object used for all communication with host.
     * @param baseDn     contains address of distinguished name to begin ldap search
     * @param scope      indicates depth of search starting at basedn.  0 (base dn),
     *                   1 (one level down) or 2 (infinite) are valid values.
     * @param filter     contains the search criteria
     * @param attrs      is the requested list of attritubutes to return from directory search.
     * @param attrsOnly  if true pull back attribute names only.
     * @param maxEntries specifies the maximum number of entries to return in this search query.
     * @return result set containing ldap entries returned from directory.
     * @throws LdapException thrown in the event of error in ldap client or server code.
     */
    protected SearchCursor search( LdapConnection connection, String baseDn, SearchScope scope, String filter,
        String[] attrs, boolean attrsOnly, int maxEntries ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        SearchRequest searchRequest = new SearchRequestImpl();
        searchRequest.setBase( new Dn( baseDn ) );
        searchRequest.setFilter( filter );
        searchRequest.setScope( scope );
        searchRequest.setSizeLimit( maxEntries );
        searchRequest.setTypesOnly( attrsOnly );
        searchRequest.addAttributes( attrs );
        return connection.search( searchRequest );
    }


    /**
     * This method uses the compare ldap func to assert audit record into the directory server's configured audit
     * logger.
     *
     * This is for one reason - to force the ldap server to maintain an audit trail on checkAccess api.
     *
     * Use proxy authz control (RFC4370) to assert the caller's id onto the record.
     *
     * @param connection is LdapConnection object used for all communication with host.
     * @param dn         contains address of distinguished name to begin ldap search
     * @param userDn     dn for user node
     * @param attribute  attribute used for compare
     * @return true if compare operation succeeds
     * @throws LdapException                thrown in the event of error in ldap client or server code.
     * @throws UnsupportedEncodingException in the event the server cannot perform the operation.
     */
    protected boolean compareNode( LdapConnection connection, String dn, String userDn,
        Attribute attribute ) throws org.apache.directory.api.ldap.model.exception.LdapException, UnsupportedEncodingException
    {
        CompareRequest compareRequest = new CompareRequestImpl();
        compareRequest.setName( new Dn( dn ) );
        compareRequest.setAttributeId( attribute.getId() );
        compareRequest.setAssertionValue( attribute.getString() );
        CompareResponse response = connection.compare( compareRequest );
        return response.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS;
    }


    /**
     * Method wraps ldap client to return multivalued attribute by name within a given entry and returns
     * as a list of strings.
     *
     * @param entry         contains the target ldap entry.
     * @param attributeName name of ldap attribute to retrieve.
     * @return List of type string containing attribute values.
     */
    protected List<String> getAttributes( Entry entry, String attributeName )
    {
        List<String> attrValues = new ArrayList<>();
        if ( entry != null )
        {
            Attribute attr = entry.get( attributeName );
            if ( attr != null )
            {
                for ( Value value : attr )
                {
                    attrValues.add( value.getString() );
                }
            }
            else
            {
                return null;
            }
        }
        return attrValues;
    }


    /**
     * Method wraps ldap client to return multivalued attribute by name within a given entry and returns
     * as a set of strings.
     *
     * @param entry         contains the target ldap entry.
     * @param attributeName name of ldap attribute to retrieve.
     * @return List of type string containing attribute values.
     */
    protected Set<String> getAttributeSet( Entry entry, String attributeName )
    {
        // create Set with case insensitive comparator:
        Set<String> attrValues = new TreeSet<>( String.CASE_INSENSITIVE_ORDER );

        if ( entry != null && entry.containsAttribute( attributeName ) )
        {
            for ( Value value : entry.get( attributeName ) )
            {
                attrValues.add( value.getString() );
            }
        }

        return attrValues;
    }


    /**
     * Method wraps ldap client to return attribute value by name within a given entry and returns as a string.
     *
     * @param entry         contains the target ldap entry.
     * @param attributeName name of ldap attribute to retrieve.
     * @return value contained in a string variable.
     * @throws LdapInvalidAttributeValueException When we weren't able to get the attribute from the entry
     */
    protected String getAttribute( Entry entry, String attributeName ) throws LdapInvalidAttributeValueException
    {
        if ( entry != null )
        {
            Attribute attr = entry.get( attributeName );

            if ( attr != null )
            {
                return attr.getString();
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    /**
     * Method will retrieve the relative distinguished name from a distinguished name variable.
     *
     * @param dn contains ldap distinguished name.
     * @return rDn as string.
     */
    protected String getRdn( String dn )
    {
        try
        {
            return new Dn( dn ).getRdn().getName();
        }
        catch ( LdapInvalidDnException lide )
        {
            return null;
        }
    }


    /**
     * Create multi-occurring ldap attribute given array of strings and attribute name.
     *
     * @param name   contains attribute name to create.
     * @param values array of string that contains attribute values.
     * @return Attribute containing multivalued attribute set.
     * @throws LdapException in the event of ldap client error.
     */
    protected Attribute createAttributes( String name, String values[] ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        return new DefaultAttribute( name, values );
    }


    /**
     * Given an ldap attribute name and a list of attribute values, construct an ldap attribute set to be added to directory.
     *
     * @param list     list of type string containing attribute values to load into attribute set.
     * @param entry    contains ldap attribute set targeted for adding.
     * @param attrName name of ldap attribute being added.
     * @throws LdapException If we weren't able to add the attributes into the entry
     */
    protected void loadAttrs( List<String> list, Entry entry, String attrName ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        if ( list != null && list.size() > 0 )
        {
            entry.add( attrName, list.toArray( new String[]
                {} ) );
        }
    }


    /**
     * Given an ldap attribute name and a list of attribute values, construct an ldap modification set to be updated
     * in directory.
     *
     * @param list     list of type string containing attribute values to load into modification set.
     * @param mods     contains ldap modification set targeted for updating.
     * @param attrName name of ldap attribute being modified.
     */
    protected void loadAttrs( List<String> list, List<Modification> mods, String attrName )
    {
        if ( ( list != null ) && ( list.size() > 0 ) )
        {
            mods.add( new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, attrName,
                list.toArray( new String[]
                    {} ) ) );
        }
    }

    /**
     * Given an ldap attribute name and a set of attribute values, construct an ldap attribute set to be added to
     * directory.
     *
     * @param values   set of type string containing attribute values to load into attribute set.
     * @param entry    contains ldap entry to pull attrs from.
     * @param attrName name of ldap attribute being added.
     * @throws LdapException If we weren't able to add the values into the entry
     */
    protected void loadAttrs( Set<String> values, Entry entry, String attrName ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        if ( ( values != null ) && ( values.size() > 0 ) )
        {
            entry.add( attrName, values.toArray( new String[]
                {} ) );
        }
    }


    /**
     * Calls the PoolMgr to perform an LDAP bind for a user/password combination.  This function is valid
     * if and only if the user entity is a member of the USERS data set.
     *
     * @param connection connection to ldap server.
     * @param szUserDn   contains the LDAP dn to the user entry in String format.
     * @param password   contains the password in clear text.
     * @return bindResponse contains the result of the operation.
     * @throws LdapException in the event of LDAP error.
     */
    protected BindResponse bind( LdapConnection connection, String szUserDn, String password ) throws org.apache.directory.api.ldap.model.exception.LdapException
    {
        Dn userDn = new Dn( szUserDn );
        BindRequest bindReq = new BindRequestImpl();
        bindReq.setDn( userDn );
        bindReq.setCredentials( password );
        bindReq.addControl( PP_REQ_CTRL );
        return connection.bind( bindReq );
    }


    /**
     * Calls the PoolMgr to close the close (return) LDAP connection.
     *
     * @param connection handle to ldap connection object.
     */
    protected void closeConnection( LdapConnection connection )
    {
        ConnectionProvider.getInstance().close(connection);
    }

    
    /**
     * Calls the PoolMgr to get a connection to the LDAP server.
     *
     * @return ldap connection.
     * @throws LdapException If we had an issue getting an LDAP connection
     */
    public LdapConnection getConnection() throws LdapException
    {
        return ConnectionProvider.getInstance().get();
    }
}
