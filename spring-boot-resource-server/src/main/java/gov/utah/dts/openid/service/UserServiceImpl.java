package gov.utah.dts.openid.service;

import gov.utah.dts.openid.mapper.UserMapper;
import gov.utah.dts.openid.model.DatabaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
    private UserMapper userDao;

	@Override
	public DatabaseUser findByUid(String uid) {
		return userDao.getUserByUid(uid);
	}
}
