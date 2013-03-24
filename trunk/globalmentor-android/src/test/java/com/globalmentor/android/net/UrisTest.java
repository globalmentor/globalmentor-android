/*
 * Copyright © 2013 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.android.net;

import java.io.*;
import java.net.URI;

import android.net.Uri;

import com.globalmentor.io.Files;
import com.globalmentor.test.AbstractTest;

//import static org.junit.Assert.*;

//import org.junit.*;

/**
 * Tests of the {@link Uris} utility class.
 * 
 * @author Garret Wilson
 */
public class UrisTest extends AbstractTest
{

	private File tempFile;

	//@BeforeClass
	public void createTempFile() throws IOException
	{
		tempFile = Files.createTempFile(); //create a plain old type file
	}

	//@AfterClass
	public void deleteTempFile() throws IOException
	{
		Files.delete(tempFile);
	}

	/**
	 * Tests to ensure that a {@link Uri} created by {@link Uri#fromFile(File)} and one derived from a {@link URI} created via {@link Files#toURI(File)} are
	 * equal.
	 * @see Uri#fromFile(File)
	 * @see Files#toURI(File)
	 * @see Uris#createUri(URI)
	 */
	//@Test
	public void testFileURIsEqual()
	{
		final Uri androidUriFromFile = Uri.fromFile(tempFile); //create an Android Uri directly from a file
		final URI javaURIFromFile = Files.toURI(tempFile); //create a Java URI directly from a file
		final Uri androidUrifromJavaURIFomFile = Uris.createUri(javaURIFromFile); //create an Android Uri from the Java URI
		//assertEquals(androidUriFromFile, androidUrifromJavaURIFomFile);
	}

}
