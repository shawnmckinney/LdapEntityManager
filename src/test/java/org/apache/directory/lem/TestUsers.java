package org.apache.directory.lem;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class TestUsers 
{
    private static final String CLS_NM = TestUsers.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        System.out.println("Add User Foo");
        UserMgr gMgr = new UserMgrImpl();
        User user = new User();
        user.setName( "Foo2");
        List lst = new ArrayList();
        lst.add("Foo Fighters");
        user.setDescription( lst );
        
        
        try
        {
            gMgr.add(user);
            LOG.info( "User Add Successfull [{}]", user.getName() );
            User outUser = gMgr.read( user );
            LOG.info( "User Read Successfull [{}]", outUser.getName() );            
        }
        catch ( org.apache.directory.lem.LemException le )
        {
            LOG.error( CLS_NM, le );
        }        
    }    
}
