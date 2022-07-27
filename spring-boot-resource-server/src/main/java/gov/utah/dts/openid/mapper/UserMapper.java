package gov.utah.dts.openid.mapper;

import gov.utah.dts.openid.model.DatabaseUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

	@Select("select user_id as userId, utah_id as utahId, role, first_name as firstName, last_name as lastName, email, inactive from member where utah_id = #{uid}")
	DatabaseUser getUserByUid(@Param("uid") String uid);

}
