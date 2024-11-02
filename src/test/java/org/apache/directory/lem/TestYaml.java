/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE groups
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this groups
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this groups except in compliance
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class TestYaml 
{
    private static final String CLS_NM = TestYaml.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        LOG.info("[{}] Test Yaml",CLS_NM );
        
        TestYaml t = new TestYaml();
        t.processGroup();
        t.processUser();
    }
    
    private void processUser()
    {
        // Loading the YAML groups from the /resources folder
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File users = new File(classLoader.getResource("users.yml").getFile());

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try
        {
            // Mapping the employee from the YAML groups to the Employee class
            User user = om.readValue(users, User.class);

            // Printing out the information
            LOG.info("Users info [{}]", user.toString());

            // Access the first element of the list and print it as well
            LOG.info("Accessing first element: [{}]", user.getObject_class().get(0));        
            
            inspect( user.getClass(), user );
        }
        catch ( java.io.IOException e )
        {
            LOG.error( CLS_NM, e );
        }        
    }
    
    private void processGroup()
    {
        // Loading the YAML groups from the /resources folder
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File groups = new File(classLoader.getResource("groups.yml").getFile());

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try
        {
            // Mapping the employee from the YAML groups to the Employee class
            Group group = om.readValue(groups, Group.class);

            // Printing out the information
            LOG.info("Groups info [{}]", group.toString());

            // Access the first element of the list and print it as well
            LOG.info("Accessing first element: [{}]", group.getMembers().get(0));        
            inspect( group.getClass(), group );
        }
        catch ( java.io.IOException e )
        {
            LOG.error( CLS_NM, e );
        }        
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
            catch (IllegalArgumentException ex) 
            {
                java.util.logging.Logger.getLogger(TestYaml.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IllegalAccessException ex) 
            {
                java.util.logging.Logger.getLogger(TestYaml.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
