package gov.utah.dts.openid.service;

import gov.utah.dts.openid.mapper.UserMapper;
import gov.utah.dts.openid.model.DatabaseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

	@Override
	public DatabaseUser findByUid(String uid) {
		return userMapper.getUserByUid(uid);
	}
}
