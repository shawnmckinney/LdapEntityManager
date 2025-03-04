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

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

/**
 * Config wrapper for Apache Commons Config. Used for data access property
 * retrieval.
 *
 * @author smckinn
 */
public class Config
{

	static Configuration config;

	static
	{
		Configurations configs = new Configurations();
		try
		{
			// properties file test/conf/
			config = configs.properties(new File("config.properties"));
		} catch (ConfigurationException ce)
		{
			throw new RuntimeException("Apache Commons Config failure: " + ce);
		}
	}

	public static String getString(String name)
	{
		return config.getString(name);
	}

	public static String getString(String name, String value)
	{
		return config.getString(name, value);
	}

	public static int getInt(String name)
	{
		return config.getInt(name);
	}

	public static int getInt(String name, int value)
	{
		return config.getInt(name, value);
	}

	public static boolean getBoolean(String name)
	{
		return config.getBoolean(name);
	}

	public static boolean getBoolean(String name, boolean value)
	{
		return config.getBoolean(name, value);
	}
}
