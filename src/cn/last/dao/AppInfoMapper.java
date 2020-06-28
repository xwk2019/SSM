package cn.last.dao;

import cn.last.pojo.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppInfoMapper {
    List<AppInfo> getAppInfoList(@Param("softwareName") String softwareName,
                                 @Param("status") Integer status,
                                 @Param("categoryLevel1") Integer categoryLevel1,
                                 @Param("categoryLevel2") Integer categoryLevel2,
                                 @Param("categoryLevel3") Integer categoryLevel3,
                                 @Param("flatformId") Integer flatformId,
                                 @Param("devId") Integer devId,
                                 @Param("startRow") Integer startRow,
                                 @Param("pageSize") Integer pageSize);

    int getAppInfoCount(@Param("status") Integer status,
                        @Param("flatformId") Integer flatformId,
                        @Param("softwareName") String softwareName,
                        @Param("categoryLevel1") Integer categoryLevel1,
                        @Param("categoryLevel2") Integer categoryLevel2,
                        @Param("categoryLevel3") Integer categoryLevel3);

    int add(@Param("appInfo") AppInfo appInfo);

    AppInfo getAppInfo(@Param("id") Integer id, @Param("APKName") String APKName) throws Exception;

    //上架下架版本修改内容
    int modify(AppInfo appInfo) throws Exception;

    //修改最新版本号
    int modifyVersionId(@Param("versionId") Integer versionId, @Param("id") Integer id);

    public int Appmodify(AppInfo appInfo);

    public int deleteAppLogo(@Param(value = "id") Integer id) throws Exception;

    public int deleteAppInfoById(@Param(value = "id") Integer delId);

    /*后台*/

    int updateSatus(@Param("status") Integer var1, @Param("id") Integer var2) throws Exception;

}
