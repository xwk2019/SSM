package cn.last.dao;

import cn.last.pojo.AppCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppCategoryMapper {
    List<AppCategory> getAppCategoryList(@Param("parentId") Integer parentId);
}
