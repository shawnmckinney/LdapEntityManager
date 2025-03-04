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

import java.util.ArrayList;
import java.util.List;
import org.apache.directory.lem.dao.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smckinn
 */
public class EntityTest
{

	private static final String CLS_NM = EntityTest.class.getName();
	private static final Logger LOG = LoggerFactory.getLogger(CLS_NM);

	public static void main(String[] args)
	{
		LOG.info("{} Entity Test", CLS_NM);
		EntityTest t = new EntityTest();

		t.readEntity("users.yml", "users-r1.yml", "org.apache.directory.lem.User");
		t.deleteEntity("groups.yml", "groups-d1.yml", "org.apache.directory.lem.Group");
		t.addEntity("groups.yml", "groups-d1.yml", "org.apache.directory.lem.Group");
		t.readEntity("groups.yml", "groups-d1.yml", "org.apache.directory.lem.Group");
		t.deleteEntity("users.yml", "users-d1.yml", "org.apache.directory.lem.User");
		t.addEntity("users.yml", "users-d1.yml", "org.apache.directory.lem.User");
		t.updateEntity("users.yml", "users-u1.yml", "org.apache.directory.lem.User");
		//t.readEntity( "users.yml", "users-d1.yml", "org.apache.directory.lem.User" );                
		t.readEntity("users.yml", "users-r1.yml", "org.apache.directory.lem.User");
		t.findEntities("users.yml", "users-r1.yml", "org.apache.directory.lem.User");
		//t.addUsers( "users.yml", "users-r1.yml", "org.apache.directory.lem.User" );

	}

	private void findEntities(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			List<Entity> entities = eMgr.find(modelFile, dataFile, className);
			if (entities != null && !entities.isEmpty())
			{
				for (Entity entity : entities)
				{
					LOG.info("Entity found [{}]", entity.toString());
				}

			} else
			{
				LOG.info("findEntities returned nothing");
			}
		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}

	private void readEntity(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			Object obj = eMgr.read(modelFile, dataFile, className);
			if (obj != null)
			{
				LOG.info("Successful Test Read [{}]", obj.toString());
			} else
			{
				LOG.info("readEntity failed");
			}

		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}

	private void addUsers(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			User model = (User) ResourceUtil.unmarshal(modelFile, className);
			User entity = (User) ResourceUtil.unmarshal(dataFile, className);
			List<String> addresses = new ArrayList<String>();
			addresses.add("Suite 1234$444 1st St.");
			addresses.add("San Juan");
			addresses.add("PR");
			List<String> emails = new ArrayList<String>();
			emails.add(entity.getKey() + "@gmail.com");
			emails.add(entity.getKey() + "@apache.org");
			List<String> phones = new ArrayList<String>();
			phones.add("555-555-5555");
			phones.add("123-45-6789");
			List<String> ocs = new ArrayList<String>();
			ocs.add("inetorgperson");
			ocs.add("posixaccount");
			String name = entity.getKey();
			for (int i = 0; i < 10; i++)
			{
				entity.setKey(name + i);
				entity.setName(entity.getKey());
				entity.setFirst_name(entity.getKey());
				entity.setFull_name(entity.getFirst_name() + " " + entity.getLast_name());
				entity.setLast_name("Fighters");
				entity.setAddresses(addresses);
				entity.setEmails(emails);
				entity.setPhones(phones);
				entity.setId("1000" + i);
				entity.setObject_class(ocs);
				entity.setGroup_id("2000" + i);
				entity.setHome("/home/" + entity.getKey() + i);
				entity.setLogin("bash");
				entity.setType("test");
				List<String> desc = new ArrayList<String>();
				desc.add(entity.getKey() + i + " desc");
				entity.setDescription(desc);
				entity.setPassword("secret");

				//entity
				//entity.setRdn("foo1000");
				eMgr.add(model, entity);
			}
			LOG.info("Successful Test Add Enities");
		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}

	private void addEntity(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			eMgr.add(modelFile, dataFile, className);
			LOG.info("Successful Test Add");
		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}

	private void updateEntity(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			eMgr.update(modelFile, dataFile, className);
			LOG.info("Successful Test Update");
		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}

	private void deleteEntity(String modelFile, String dataFile, String className)
	{
		EntityMgr eMgr = new EntityMgrImpl();
		try
		{
			eMgr.delete(modelFile, dataFile, className);
			LOG.info("Successful Test Delete");
		} catch (LemException e)
		{
			LOG.error(CLS_NM, e);
		}
	}
}
