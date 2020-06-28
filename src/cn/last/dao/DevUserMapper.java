package cn.last.dao;

import cn.last.pojo.DevUser;
import org.apache.ibatis.annotations.Param;

public interface DevUserMapper {
    DevUser doDevLogin(@Param("devCode") String devCode);
}
