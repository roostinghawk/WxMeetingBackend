package com.leadingsoft.liuw.service.impl;

import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.repository.WxUserRepository;
import com.leadingsoft.liuw.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuw on 2017/6/20.
 */
@Service
public class WxUserServiceImpl implements WxUserService {

    @Autowired
    private WxUserRepository wxUserRepository;

    @Override
    public WxUser initForApp(String openId) {
        return initForApp(openId, null);
    }

    @Override
    public WxUser initForApp(String openId, String unionId) {
        WxUser wxUser = wxUserRepository.findOneByOpenIdFromApp(openId);
        if(wxUser != null) {
            return wxUser;
        }

        wxUser = new WxUser();
        wxUser.setOpenIdFromApp(openId);
        wxUser.setUnionId(unionId);
        wxUser.setAppUser(true);
        return wxUserRepository.save(wxUser);
    }
}
