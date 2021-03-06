package org.test.falcon.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.test.falcon.constant.Constant;
import org.test.falcon.exception.ProAPIException;
import org.test.falcon.mongo.document.ChildDevice;
import org.test.falcon.mongo.document.Device;
import org.test.falcon.mongo.document.Lead;
import org.test.falcon.mongo.document.User;
import org.test.falcon.service.SurveyLeadService;
import org.test.falcon.service.UserService;

@Service
public class SurveyLeadServiceImpl implements SurveyLeadService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService   userService;

    @Override
    public Lead insertLead(Lead surveyLead) {

        // TODO need to add check on email validity
        // TODO need to add otp verification flow
        User user = null;
        if (surveyLead.getPhone() == null || surveyLead.getPhone().isEmpty()) {
            throw new ProAPIException("Phone no. is required.");
        }
        user = userService.findUserByPhone(surveyLead.getPhone());
        if (user != null) {
            Update update = new Update();
            update.push(Constant.SURVEY_LEADS, surveyLead);
            update.set(Constant.UPDATED_AT, new Date());
            mongoTemplate.updateFirst(new Query(Criteria.where(Constant.ID).is(user.getId())), update, User.class);
        }
        else {
            user = new User();
            if (surveyLead != null && surveyLead.getName() != null || !surveyLead.getName().isEmpty()) {
                user.setUsername(surveyLead.getName());
            }
            else if (surveyLead != null && surveyLead.getEmailId() != null || !surveyLead.getEmailId().isEmpty()) {
                user.setUsername(surveyLead.getEmailId());
                user.setEmail(surveyLead.getEmailId());
            }
            else {
                user.setUsername(surveyLead.getPhone());
            }
            user.setAddress(surveyLead.getAddress());
            user.setPhone(surveyLead.getPhone());
            user.setPassword("abc123");
            user.setLeads(Arrays.asList(surveyLead));
            mongoTemplate.insert(Arrays.asList(user), User.class);
            // XXX trigger sms and email to user for username and password

        }

        return surveyLead;
    }

    @Override
    public List<Lead> getLeadsOfUser(String userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new ProAPIException("User doesn't exist.");
        }
        List<Lead> leads = user.getLeads();
        return leads;
    }

    @Override
    public List<Device> getDevices(String userId, String leadId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new ProAPIException("User doesn't exist.");
        }
        if (CollectionUtils.isNotEmpty(user.getLeads())) {
            for (Lead l : user.getLeads()) {
                if (leadId.equals(l.getId())) {
                    return l.getDevices();
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<ChildDevice> getChildDevices(String userId, String leadId, String masterDevId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new ProAPIException("User doesn't exist.");
        }
        if (CollectionUtils.isNotEmpty(user.getLeads())) {
            for (Lead l : user.getLeads()) {
                if (leadId.equals(l.getId())) {
                    if (CollectionUtils.isNotEmpty(l.getDevices())) {
                        for (Device device : l.getDevices()) {
                            if (masterDevId.equals(device.getDeviceId())) {
                                return device.getChildDevices();
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

}
