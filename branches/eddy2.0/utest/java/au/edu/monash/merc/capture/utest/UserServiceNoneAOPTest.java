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

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.domain.Profile;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.service.ProfileService;
import au.edu.monash.merc.capture.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@Transactional
public class UserServiceNoneAOPTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private UserService userService;

	@Autowired
	private ProfileService profileService;

	private static long uid = 0;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@Before
	public void setupTestData() {

		User u = new User();
		u.setUniqueId("xiyu");
		u.setRegistedDate(Calendar.getInstance().getTime());
		u.setEmail("xiyu@its.momash.edu.au");

		Profile pro = new Profile();
		pro.setGender("femail");
		pro.setUser(u);
		u.setProfile(pro);

		this.userService.saveUser(u);
		uid = u.getId();
		System.out.println("create a new user id: " + uid);
		System.out.println("===> create a new user profile id: " + u.getProfile().getId());

	}

	@Test
	public void checkNameAvailable() {

		String uniqueName = "xiyu";
		boolean existed = this.userService.checkUserUniqueIdExisted(uniqueName);
		System.out.println("==========> user: " + uniqueName + " existed: " + existed);
	}

	@Test
	public void modifyUser() {
		User u = this.userService.getUserById(uid);
		u.setEmail("changedxiyuEmail@its.monash.edu.au");
		u.setActivatedDate(Calendar.getInstance().getTime());
		u.setUniqueId("Melbourne");
		this.userService.updateUser(u);
		System.out.println("=== user name: " + u.getUniqueId());
		User modifiedU = this.userService.getUserById(uid);
		Assert.assertEquals(u.getUniqueId(), modifiedU.getUniqueId());

	}

	@Test
	public void updateUserProfile() {
		Profile p = this.profileService.getUserProfile(uid);
		System.out.println("=======>try to update user profile: profile id " + p.getId());
		p.setGender("male");
		p.setAddress("9 birchfield crescent");
		p.setCity("Melbourne");
		p.setCountry("Australia");
		p.setIndustryField("IT");
		p.setPostcode("3152");
		this.profileService.updateProfile(p);
		Profile updatedP = this.profileService.getUserProfile(uid);
		Assert.assertEquals(updatedP.getPostcode(), p.getPostcode());

	}
}
