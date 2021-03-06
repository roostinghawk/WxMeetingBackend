package com.leadingsoft.liuw.service.impl;

import com.leadingsoft.liuw.exception.CustomRuntimeException;
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
    public WxUser initForApp(String openId, String nickName) {
        return initForApp(openId, null, nickName);
    }

    @Override
    public WxUser initForApp(String openId, String unionId, String nickName) {
        WxUser wxUser = wxUserRepository.findOneByOpenIdFromApp(openId);
        if(wxUser != null) {
            return wxUser;
        }

        wxUser = new WxUser();
        wxUser.setOpenIdFromApp(openId);
        wxUser.setUnionId(unionId);
        wxUser.setAppUser(true);
        wxUser.setNickName(nickName);
        return wxUserRepository.save(wxUser);
    }

    @Override
    public String getName(String openId) {
        final WxUser creator = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        return creator.getName() == null ? creator.getNickName() : creator.getName();
    }

    @Override
    public void updateName(String openId, String name) {
        final WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        if(wxUser == null) {
            throw new CustomRuntimeException("404", "微信用户不存在");
        }

        wxUser.setName(name);

        this.wxUserRepository.save(wxUser);
    }
}
