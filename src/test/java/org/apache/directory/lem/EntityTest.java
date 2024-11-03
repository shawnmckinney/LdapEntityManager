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
import org.apache.directory.lem.dao.EntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityTest 
{
    private static final String CLS_NM = EntityTest.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    public static void main(String[] args) 
    {
        LOG.info("[{}] Test Yaml",CLS_NM );
        
        EntityTest t = new EntityTest();
        t.process();
    }
    
    /**
     * WIP
     */
    private void process()
    {
        // Loading the YAML groups from the /resources folder
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File model = new File(classLoader.getResource("users.yml").getFile());
        File data = new File(classLoader.getResource("users-d1.yml").getFile());
        EntityDao eDao = new EntityDao();

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try
        {
            // Mapping the employee from the YAML groups to the Employee class
            User inModel = om.readValue(model, User.class);
            User inData = om.readValue(data, User.class);            

            // Printing out the information
            LOG.info("Users info [{}]", inModel.toString());

            // Access the first element of the list and print it as well
            LOG.info("Accessing first element: [{}]", inModel.getObject_class().get(0));        
            
            //inspect( inModel.getClass(), inModel );
            
            eDao.create( inModel, inData );
            LOG.info("Successful Test");
        }
        catch ( java.io.IOException e )
        {
            LOG.error( CLS_NM, e );
        }        
        catch ( LemException e )
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
