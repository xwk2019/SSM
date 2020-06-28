package cn.last.service.developer;

import cn.last.pojo.*;
import cn.last.utils.PageSupport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DevService {
    //登入操作
    DevUser devLogin(String devCode, String devPassword);

    //查询列表，及打开
    PageSupport<AppInfo> getAppInfoList(String querysoftwareName,
                                        Integer querystatus,
                                        Integer querycategoryLevel1,
                                        Integer querycategoryLevel2,
                                        Integer querycategoryLevel3,
                                        Integer queryflatformId,
                                        Integer devId,
                                        Integer pageIndex,
                                        Integer pageSize);

    List<DataDictionary> getDataDictionaryList(@Param("typeCode") String typeCode);

    List<AppCategory> getAppCategoryList(@Param("parentId") Integer parentId);

    //获得id的对应的版本号
    List<AppVersion> getAppVersionList(@Param("id") Integer id) throws Exception;

    //增加版本
    int addAppVersion(@Param("appVersion") AppVersion appVersion) throws Exception;

    //根据id 和 APKName 获得AppInfo对象
    AppInfo getAppInfo(@Param("id") Integer id, @Param("APKName") String APKName) throws Exception;

    //修改上架下架状态
    boolean updateSaleStatusByAppId(@Param("appInfo") AppInfo appInfo) throws Exception;

    int modifyVersionId(@Param("versionId") Integer versionId, @Param("id") Integer id);
}
