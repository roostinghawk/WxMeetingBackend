package com.leadingsoft.liuw.repository;

import com.leadingsoft.liuw.model.WxUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.Repository;

import java.util.Date;
import java.util.List;

/**
 * WxUserRepository
 */
public interface WxUserRepository extends MongoRepository<WxUser, String> {


    WxUser findOne(final String id);

    WxUser save(final WxUser model);

    int countByOpenId(final String openId);

    WxUser findOneByOpenId(final String openId);

    WxUser findOneByOpenIdFromApp(final String openId);

    WxUser findByUnionId(final String unionId);

}
