package cn.last.service.backend;

import cn.last.pojo.*;
import cn.last.utils.PageSupport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BackEndService {
    BackEndUser backLogin(String userCode, String userPassword);

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

    //获得对应id的软件信息
    AppInfo getAppInfo(@Param("id") Integer id) throws Exception;

    //获得对应id的版本信息
    public AppVersion getAppVersionById(Integer id);

    //审核修改状态
    boolean updateSatus(@Param("status") Integer var1, @Param("id") Integer var2) throws Exception;

}
