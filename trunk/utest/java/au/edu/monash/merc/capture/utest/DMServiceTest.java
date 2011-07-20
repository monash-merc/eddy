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

import java.io.File;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.service.DMService;
import au.edu.monash.merc.capture.service.UserService;
import au.edu.monash.merc.capture.util.CaptureUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
@Transactional
public class DMServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private UserService userService;

	@Autowired
	private DMService dmService;

	private String dataStorePath = "/opt/datastore";

	private String userPathPrefix = "uid_";

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setDmService(DMService dmService) {
		this.dmService = dmService;
	}

	@Test
	public void createCollection() {

		User user = new User();
		user.setFirstName("Simon");
		user.setLastName("Yu");
		user.setEmail("simonyu@gmail.com");
		user.setRegistedDate(Calendar.getInstance().getTime());
		this.userService.saveUser(user);

		Collection col = new Collection();
		String collectionPathName = CaptureUtil.generateIdBasedOnTimeStamp();

		String colPath = userPathPrefix + user.getId() + File.separator + collectionPathName;

		col.setName("adelaide River 2009-10-12 climate data");
		col.setDescription("This is a testing collection created by simon yu");
		col.setDirPathName(colPath);
		col.setOwner(user);
		col.setModifiedByUser(user);
		this.dmService.createCollection(col, dataStorePath);
		System.out.println("created a collection successfully at " + col.getDirPathName());

		this.dmService.deleteCollection(col, dataStorePath);
		System.out.println("deleted a collection successfully at " + col.getDirPathName());
		this.userService.deleteUser(user);

	}
 
}
