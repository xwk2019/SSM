package cn.last.dao;

import java.util.List;

import cn.last.pojo.AppVersion;
import org.apache.ibatis.annotations.Param;


public interface AppVersionMapper {

    public List<AppVersion> getAppVersionList(@Param("appId") Integer appId) throws Exception;

    public int add(AppVersion appVersion) throws Exception;

    public int modify(AppVersion appVersion) throws Exception;

    public AppVersion getAppVersionById(@Param("id") Integer id);

    int deleteApkFile(@Param("id") Integer id);

    public int getVersionCountByAppId(@Param("appId") Integer appId);

    public int deleteVersionByAppId(@Param("appId") Integer appId);
}
