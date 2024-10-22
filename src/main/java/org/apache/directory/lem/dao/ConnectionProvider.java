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
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.*;
import org.apache.directory.lem.Config;
import org.apache.directory.lem.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author smckinn
 */
class ConnectionProvider
{
    private static final String CLS_NM = ConnectionProvider.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );
    private static LdapConnectionPool adminPool;
    private static volatile ConnectionProvider sINSTANCE = null;

    /**
     * Synchronized getter guards access to reference to self which is a singleton and only be created the first time invoked.
     *
     * @return reference to self.
     */
    static ConnectionProvider getInstance()
    {
        if ( sINSTANCE == null )
        {
            synchronized ( ConnectionProvider.class )
            {
                if ( sINSTANCE == null )
                {
                    sINSTANCE = new ConnectionProvider();
                }
            }
        }
        return sINSTANCE;
    }

    /**
     * Private constructor calls the init method which initializes the connection pools.
     *
     */
    private ConnectionProvider()
    {
        init();
    }

    /**
     * Initialize the three connection pools using settings and coordinates contained in the config.
     */
    private void init()
    {
        boolean IS_TLS = Config.getBoolean( Ids.TLS, false );
        boolean IS_LDAPS = Config.getBoolean( Ids.LDAPS, false );        
        String host = Config.getString( Ids.HOST, "localhost" );
        int port = Config.getInt( Ids.PORT, 389 );        
//        boolean testOnBorrow = Config.getInstance().getBoolean( GlobalIds.TEST_ON_BORROW, false );
//        boolean testWhileIdle = Config.getInstance().getBoolean( GlobalIds.TEST_ON_IDLE, false );
//        boolean isBlockOnMaxConnection = Config.getInstance().getBoolean( GlobalIds.IS_MAX_CONN_BLOCK, true );
//        int maxConnBlockTime = Config.getInstance().getInt( GlobalIds.MAX_CONN_BLOCK_TIME, 5000 );
//        int timeBetweenEvictionRunMillis = Config.getInstance().getInt( GlobalIds.LDAP_ADMIN_POOL_EVICT_RUN_MILLIS, 1000 * 60 * 30 );
//        int logTimeBetweenEvictionRunMillis = Config.getInstance().getInt( GlobalIds.LDAP_LOG_POOL_EVICT_RUN_MILLIS, 1000 * 60 * 30 );

        LOG.info( "LDAP POOL:  host=[{}], port=[{}]", host, port );
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost( host );
        config.setLdapPort( port );
        config.setName( Config.getString( Ids.BINDDN ) );
        config.setCredentials( Config.getString( Ids.BINDPW ) );
        // One will be set here:
        config.setUseTls( IS_TLS );
        config.setUseSsl( IS_LDAPS );

        // Can't use both!
        if ( IS_LDAPS && IS_TLS )
        {
            throw new RuntimeException( "Invalid config: ldaps and tls cannot be used simultaneously" );
        }

        if ( ( IS_TLS || IS_LDAPS ) && StringUtils.isNotEmpty( Config.getString( Ids.TRUST_STORE ) ) &&
            StringUtils.isNotEmpty( Config.getString( Ids.TRUST_STORE_PW ) ) )
        {
            LOG.debug("Initialize trustStore [{}]", Config.getString( Ids.TRUST_STORE ));
            // Always validate certificate but allow self-signed from this truststore:
            config.setTrustManagers( 
                    new TrustStoreManager( Config.getString( Ids.TRUST_STORE ), Config.getString( Ids.TRUST_STORE_PW ).toCharArray(), 
                    null, true ) );
        }
        
        PooledObjectFactory<LdapConnection> poolFactory = new ValidatingPoolableLdapConnectionFactory( config );

        // Create the Admin pool
        adminPool = new LdapConnectionPool( poolFactory );
        //adminPool.setTestOnBorrow( testOnBorrow );
        adminPool.setMaxTotal( Config.getInt( Ids.CONNECTIONS_MAX, 10 ) );        
        //adminPool.setBlockWhenExhausted( isBlockOnMaxConnection );
        //adminPool.setMaxWaitMillis( maxConnBlockTime );
        //adminPool.setMinIdle( min );
        //adminPool.setMaxIdle( -1 );
        //adminPool.setTestWhileIdle( testWhileIdle );
        //adminPool.setTimeBetweenEvictionRunsMillis( timeBetweenEvictionRunMillis );
    }

    void close(LdapConnection connection)
    {
        try
        {
            adminPool.releaseConnection( connection );
        }
        catch ( LdapException e )
        {
            LOG.warn( "Error closing admin connection: " + e );
        }
    }

    LdapConnection get() throws LdapException
    {
        return adminPool.getConnection();
    }

    static void closePool()
    {
        try
        {
            LOG.info( "Closing admin pool" );
            adminPool.close();
        }
        catch ( Exception e )
        {
            LOG.warn( "Error closing admin pool: " + e );
        }
    }
}
