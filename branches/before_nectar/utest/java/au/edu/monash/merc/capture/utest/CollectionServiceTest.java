/**
 * 	Copyright (c) 2010-2011, Monash e-Research Centre
 *	(Monash University, Australia)
 * 	All rights reserved.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *		* Redistributions of source code must retain the above copyright
 *    	  notice, this list of conditions and the following disclaimer.
 *		* Redistributions in binary form must reproduce the above copyright
 *    	  notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *		* Neither the name of the Monash University nor the
 *    	  names of its contributors may be used to endorse or promote products
 *    	  derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 *	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package au.edu.monash.merc.capture.utest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ucar.nc2.util.IO;
import au.edu.monash.merc.capture.adapter.DataCaptureAdapter;
import au.edu.monash.merc.capture.adapter.DataCaptureAdapterFactory;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.service.CollectionService;
import au.edu.monash.merc.capture.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
@Transactional
public class CollectionServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static long uid;

	private static long cid;

	@Autowired
	private UserService userService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private DataCaptureAdapterFactory adapterFactory;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	public void setAdapterFactory(DataCaptureAdapterFactory adapterFactory) {
		this.adapterFactory = adapterFactory;
	}

	@Test
	public void saveCollection() throws Exception {

		User u = new User();
		u.setUniqueId("simon");
		u.setRegistedDate(Calendar.getInstance().getTime());
		u.setEmail("simon@momash.edu");

		// save user
		this.userService.saveUser(u);
		uid = u.getId();

		// NetCDFDataCaptureAdapter captureAdapter = new NetCDFDataCaptureAdapter();
		String filename = "../../testData/AdelaideRiver_2008_L3.nc";

		File file = new File(filename);
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		InputStream in = new BufferedInputStream(new FileInputStream(filename));
		IO.copy(in, bos);
		DataCaptureAdapter adapter = adapterFactory.createInstance();
		Dataset ds = adapter.caputreData("AdelaideRiver_2008_L3.nc", filename, true, false);

		Collection col = new Collection();
		col.setName(ds.getSiteName());
		// col.setTimestampTag(ds.getTimestampTag());

		ds.setCollection(col);
		List<Dataset> dsList = new ArrayList<Dataset>();
		dsList.add(ds);

		col.setOwner(u);
		col.setDatasets(dsList);
		col.setModifiedByUser(u);

		this.collectionService.saveCollection(col);
		cid = col.getId();
		System.out.println("========> collection id: " + cid);
	}

	@Test
	public void deleteCollection() {

		System.out.println("========> collection id: " + cid);
		Collection col = this.collectionService.getCollectionById(cid);
		System.out.println("========> collection name and id: " + col.getName() + ", id: " + col.getId());
		this.collectionService.deleteCollection(col);
		System.out.println("=======> deleting a collection is successful");

		User user = this.userService.getUserById(uid);
		this.userService.deleteUser(user);
	}

}
