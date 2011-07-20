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
package au.edu.monash.merc.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.dao.impl.ActivityDAO;
import au.edu.monash.merc.capture.domain.Activity;
import au.edu.monash.merc.capture.service.ActivityService;

@Scope("prototype")
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private ActivityDAO activityDao;

	public void setActivityDao(ActivityDAO activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public Activity getActivityByActKey(String activityKey) {
		return this.activityDao.getActivityByActKey(activityKey);
	}

	@Override
	public Activity getActivityById(long id) {
		return this.activityDao.get(id);
	}

	@Override
	public List<Activity> getAllActivities() {
		return this.activityDao.getAllActivities();
	}

	@Override
	public List<Activity> getActivitiesByCollectionId(long cid) {
		return this.activityDao.getActivitiesByCollectionId(cid);
	}

	@Override
	public void saveActivity(Activity activity) {
		this.activityDao.add(activity);
	}

	@Override
	public void deleteActivity(Activity activity) {
		this.activityDao.remove(activity);
	}

	@Override
	public void deleteActivityById(long id) {
		this.activityDao.deleteActivityById(id);
	}

	@Override
	public void deleteActivityByActKey(String actKey) {
		this.activityDao.deleteActivityByActKey(actKey);
	}

	@Override
	public void updateActivity(Activity activity) {
		this.activityDao.update(activity);
	}
}
