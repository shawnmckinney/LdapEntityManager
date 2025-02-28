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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.lem.Entity;
import org.apache.directory.lem.LemException;

/**
 *
 * @author smckinn
 */
public class ResourceUtil
{

	/**
	 * Private constructor
	 */
	private ResourceUtil()
	{
	}

	/**
	 * Checks that the reference is either an absolute path to a file, or a
	 * reference to a file within reach of class loader resources. A directory
	 * is not accepted.
	 *
	 * @param _reference
	 * @return An InputStream to a file, or null if no file was found.
	 */
	public static InputStream getInputStream(String _reference)
	{
		InputStream result = null;
		if (null != _reference)
		{
			result = getInputStreamForFileAbsolutePath(_reference);
			if (null == result)
			{
				// When getting here, it's not a file and not a directory, but it might still translate into a directory within resources
				result = getInputStreamForFileInClassLoaderResources(_reference);
			}
		}
		return result;
	}

	/**
	 * Will check that the path is absolute and is pointing to an existing file.
	 * A directory is not accepted.
	 *
	 * @param _path absolute path to a file
	 * @return An InputStream if the path is absolute and is pointing to a file,
	 * or null if no file was found.
	 */
	public static InputStream getInputStreamForFileAbsolutePath(String _path)
	{
		InputStream result = null;
		if (null != _path)
		{
			File file = new File(_path);
			if (file.exists() && file.isAbsolute() && !file.isDirectory())
			{
				try
				{
					result = new FileInputStream(file);
				} catch (FileNotFoundException e)
				{
					// Don't care
				}
			}
		}
		return result;
	}

	/**
	 * Will check that the reference is a file, eventually within another jar. A
	 * directory is not accepted.
	 *
	 * @param _reference the relative path of a file to find within class loader
	 * resources.
	 * @return An InputStream if the reference resolves to a file within the
	 * reach of class loader resources, or null if no file was found.
	 */
	private static InputStream getInputStreamForFileInClassLoaderResources(String _reference)
	{
		InputStream result = null;
		URL url = ResourceUtil.class.getClassLoader().getResource(_reference);
		if (null != url)
		{
			if (url.getProtocol().equals("file"))
			{
				File file = new File(url.getPath());
				if (file.exists())
				{
					if (!file.isDirectory())
					{
						try
						{
							result = new FileInputStream(file);
						} catch (FileNotFoundException e)
						{
							// Don't care
						}
					}
				}
			} else if (url.getProtocol().equals("jar"))
			{
				result = ResourceUtil.class.getClassLoader().getResourceAsStream(_reference);
				if (null != result)
				{
					try (PushbackInputStream pushbackInputStream = new PushbackInputStream(result))
					{
						int b = -1;
						try
						{
							b = pushbackInputStream.read();
						} catch (IOException e)
						{
							// Don't care
						}
						if (b != -1)
						{
							result = pushbackInputStream;
							try
							{
								pushbackInputStream.unread(b);
							} catch (IOException e)
							{
								result = null;
							}
						}
					} catch (IOException ioe)
					{
						// Problem while closing the input stream... Nothing we can do
						ioe.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public static Entity unmarshal(String fileNm, String className) throws LemException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File fileIn = new File(classLoader.getResource(fileNm).getFile());
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		Entity outObj = null;
		try
		{
			outObj = (Entity) om.readValue(fileIn, ResourceUtil.createInstance(className).getClass());
		} catch (java.io.IOException e)
		{
			throw new LemException(e.getMessage());
		}
		return outObj;
	}

	public static Object createInstance(String className) throws LemException
	{
		Object target;

		try
		{
			if (StringUtils.isEmpty(className))
			{
				String error = "createInstance() null or empty classname";
				throw new LemException(error);
			}
			target = Class.forName(className).newInstance();
		} catch (ClassNotFoundException e)
		{
			String error = "createInstance() className [" + className + "] caught java.lang.ClassNotFoundException="
				+ e;
			throw new LemException(error);
		} catch (InstantiationException e)
		{
			String error = "createInstance()  [" + className + "] caught java.lang.InstantiationException=" + e;
			throw new LemException(error);
		} catch (IllegalAccessException e)
		{
			String error = "createInstance()  [" + className + "] caught java.lang.IllegalAccessException=" + e;
			throw new LemException(error);
		}
		return target;
	}
}
