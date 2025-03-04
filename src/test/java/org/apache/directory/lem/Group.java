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
 */
public class Group implements Entity, Serializable
{

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String key;
	private String id;
	private List<String> members;
	private List<String> object_class;

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public List<String> getMembers()
	{
		return members;
	}

	public void setMembers(List<String> members)
	{
		this.members = members;
	}

	public List<String> getObject_class()
	{
		return object_class;
	}

	public void setObject_class(List<String> object_class)
	{
		this.object_class = object_class;
	}

	@Override
	public String toString()
	{
		return "\nName: " + name + "\nDescription: " + description + "\nKey: " + key + "\nId: " + id + "\nObject Classes: " + object_class + "\nMembers: " + members + "\n";
	}
}
