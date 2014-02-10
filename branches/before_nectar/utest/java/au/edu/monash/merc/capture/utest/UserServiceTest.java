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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.domain.Profile;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.service.ProfileService;
import au.edu.monash.merc.capture.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private UserService userService;
	@Autowired
	private ProfileService profileService;

	public static long persistUId = 0;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@BeforeTransaction
	public void beforeTransaction() {

		System.out.println("start to setup test data ...");
		User u = new User();
		u.setUniqueId("你好吗");
		u.setEmail("SimonYu@its.monash.edu.au");
		u.setRegistedDate(Calendar.getInstance().getTime());

		Profile pro = new Profile();

		pro.setGender("male");

		pro.setUser(u);
		u.setProfile(pro);
		this.userService.saveUser(u);
		persistUId = u.getId();

		System.out.println("persistUId : " + persistUId);
	}

	@Test
	public void addUserTest() {
		User user = new User();
		user.setUniqueId("simon");
		user.setEmail("simonyu2000@hotmail.com");
		user.setRegistedDate(Calendar.getInstance().getTime());
		Profile pro = new Profile();

		pro.setGender("male");
		user.setProfile(pro);

		pro.setUser(user);
		userService.saveUser(user);

		System.out.println("User Id: " + user.getId());

		Profile foundPro = this.profileService.getUserProfile(user.getId());

		System.out.println("==== user profile: gender: " + foundPro.getGender());

		Assert.assertNotNull(user.getId());
	}

	@Test
	public void testGetUserProfile() {

		Profile foundPro = this.profileService.getUserProfile(persistUId);
		System.out.println("====> user profile: gender: " + foundPro.getGender());
	}

	@AfterTransaction
	public void afterTransaction() {
		System.out.println("entering the afterTransaction....");
		User u = userService.getUserById(persistUId);
		userService.deleteUser(u);
	}

}
