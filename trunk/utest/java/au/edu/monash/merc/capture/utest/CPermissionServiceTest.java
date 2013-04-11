/*
 * Copyright (c) 2010-2013, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package au.edu.monash.merc.capture.utest;

import au.edu.monash.merc.capture.domain.CPermission;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.PermType;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.dto.InheritPermissionBean;
import au.edu.monash.merc.capture.service.CPermissionService;
import au.edu.monash.merc.capture.service.CollectionService;
import au.edu.monash.merc.capture.service.UserService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 26/03/13 4:33 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@Transactional
public class CPermissionServiceTest {

    @Autowired
    private CPermissionService cPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CollectionService collectionService;

    private static long userId;

    private static long collectionId;


    public void setcPermissionService(CPermissionService cPermissionService) {
        this.cPermissionService = cPermissionService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCollectionService(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Before
    public void setupTestData() {
        User u = new User();
        u.setUniqueId("simon");
        u.setRegistedDate(Calendar.getInstance().getTime());
        u.setEmail("xiaoming.yu@momash.edu");

        userId = u.getId();

        User allRegUser = new User();
        allRegUser.setUniqueId("allregisteredUser");
        allRegUser.setRegistedDate(Calendar.getInstance().getTime());
        allRegUser.setEmail("allregisteredUser@momash.edu");

        User anonymous = new User();
        anonymous.setUniqueId("anonymous");
        anonymous.setRegistedDate(Calendar.getInstance().getTime());
        anonymous.setEmail("anonymous@momash.edu");


        // save user
        this.userService.saveUser(u);
        this.userService.saveUser(allRegUser);
        this.userService.saveUser(anonymous);

        userId = u.getId();

        Collection col = new Collection();
        col.setName("simon unit testing collection");
        col.setDescription("simon unit testing collection desc");

        col.setOwner(u);
        col.setModifiedByUser(u);

        this.collectionService.saveCollection(col);
        ;
        collectionId = col.getId();
        CPermission permAnonymous = new CPermission();
        permAnonymous.setViewAllowed(1);
        permAnonymous.setUpdateAllowed(0);
        permAnonymous.setImportAllowed(0);
        permAnonymous.setExportAllowed(1);
        permAnonymous.setMdRegisterAllowed(0);
        permAnonymous.setDeleteAllowed(0);
        permAnonymous.setAcAllowed(0);
        permAnonymous.setRacAllowed(0);
        permAnonymous.setPermType(PermType.ANONYMOUS.code());
        permAnonymous.setCollection(col);
        permAnonymous.setPermForUser(anonymous);

        CPermission permAllRegUser = new CPermission();
        permAllRegUser.setViewAllowed(1);
        permAllRegUser.setUpdateAllowed(0);
        permAllRegUser.setImportAllowed(0);
        permAllRegUser.setExportAllowed(1);
        permAllRegUser.setMdRegisterAllowed(0);
        permAllRegUser.setDeleteAllowed(0);
        permAllRegUser.setAcAllowed(0);
        permAllRegUser.setRacAllowed(0);
        permAllRegUser.setPermType(PermType.ALLREGUSER.code());
        permAllRegUser.setCollection(col);
        permAllRegUser.setPermForUser(allRegUser);


        this.cPermissionService.savePermission(permAllRegUser);
        this.cPermissionService.savePermission(permAnonymous);

//        CPermission permissionUser = new CPermission();
//        permissionUser.setViewAllowed(1);
//        permissionUser.setUpdateAllowed(1);
//        permissionUser.setImportAllowed(1);
//        permissionUser.setExportAllowed(1);
//        permissionUser.setMdRegisterAllowed(1);
//        permissionUser.setDeleteAllowed(0);
//        permissionUser.setAcAllowed(0);
//        permissionUser.setRacAllowed(1);
    }

    @Test
    public void getUserCollectionPermission() {
        InheritPermissionBean cPermissionBean = this.cPermissionService.getUserInheritPermission(collectionId, userId);
        System.out.println("=== The user permission for collecton: " + cPermissionBean.getCollectionId() + " user id : " + cPermissionBean.getPermUserId()

                + " view alllowed: " + cPermissionBean.getViewAllowed()
                + " update alllowed: " + cPermissionBean.getUpdateAllowed()
                + " import alllowed: " + cPermissionBean.getImportAllowed()
                + " export alllowed: " + cPermissionBean.getExportAllowed()
                + " delete alllowed: " + cPermissionBean.getDeleteAllowed()
                + " md register alllowed: " + cPermissionBean.getMdRegisterAllowed()
                + " access control alllowed: " + cPermissionBean.getAcAllowed()
                + " restricted access control alllowed: " + cPermissionBean.getRacAllowed());
        Assert.assertNotNull(cPermissionBean);
        Assert.assertEquals("exported allowed", cPermissionBean.getExportAllowed(), 1);
    }
}
