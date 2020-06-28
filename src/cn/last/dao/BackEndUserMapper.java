package cn.last.dao;

import cn.last.pojo.BackEndUser;
import org.apache.ibatis.annotations.Param;

public interface BackEndUserMapper {
    BackEndUser backLogin(@Param("userCode") String userCode);
}
