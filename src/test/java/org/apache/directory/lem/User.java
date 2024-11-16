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
package org.apache.directory.lem;

import java.io.Serializable;
import java.util.List;
import org.apache.directory.lem.Entity;


/**
 *
 * @author smckinn
 * 
*/
public class User implements Entity, Serializable
{
    private static final long serialVersionUID = 1L;
    private String key;    
    private String name;
    private String full_name;
    private String last_name;
    private String first_name;
    private String id;
    private String group_id;
    private String home;
    private String login;
    private String type;
    private String password;    
    private List<String> description;
    private List<String> object_class;
    private List<String> phones;
    private List<String> emails;    
    private List<String> addresses;        
    
    public String getKey() 
    {
        return key;
    }

    public void setKey(String key) 
    {
        this.key = key;
    }

    public String getFull_name() 
    {
        return full_name;
    }

    public void setFull_name(String full_name) 
    {
        this.full_name = full_name;
    }

    public String getLast_name() 
    {
        return last_name;
    }

    public void setLast_name(String last_name) 
    {
        this.last_name = last_name;
    }

    public String getFirst_name() 
    {
        return first_name;
    }

    public void setFirst_name(String first_name) 
    {
        this.first_name = first_name;
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getGroup_id() 
    {
        return group_id;
    }

    public void setGroup_id(String group_id) 
    {
        this.group_id = group_id;
    }
    
    public String getHome() 
    {
        return home;
    }

    public void setHome(String home) 
    {
        this.home = home;
    }

    public String getLogin() 
    {
        return login;
    }

    public void setLogin(String login) 
    {
        this.login = login;
    }

    public String getType() 
    {
        return type;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getPassword() 
    {
        return password;
    }

    public void setPassword(String password) 
    {
        this.password = password;
    }
    
    public List<String> getObject_class() 
    {
        return object_class;
    }

    public void setObject_class(List<String> object_class) 
    {
        this.object_class = object_class;
    }

    public List<String> getPhones() 
    {
        return phones;
    }

    public void setPhones(List<String> phones) 
    {
        this.phones = phones;
    }

    public List<String> getEmails() 
    {
        return emails;
    }

    public void setEmails(List<String> emails) 
    {
        this.emails = emails;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
    
    public List<String> getDescription() 
    {
        return description;
    }

    public List<String> getAddresses() 
    {
        return addresses;
    }

    public void setAddresses(List<String> addresses) 
    {
        this.addresses = addresses;
    }

    public void setDescription(List<String> description) 
    {
        this.description = description;
    }
    
    @Override
    public String toString() 
    {
        return "\nName: " + name + "\nDescription: " + description + "\nKey: " + key + "\nId: " + id + "\nOcs: " + object_class + "\nAddresses: " + addresses + "\n";        
    }    
}
