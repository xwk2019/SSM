package cn.last.service.developer;

import cn.last.pojo.AppVersion;

import java.util.List;

public interface AppVersionService {
    /**
     * 根据appId查询相应的app版本列表
     */
    public List<AppVersion> getAppVersionList(Integer appId) throws Exception;

    public AppVersion getAppVersionById(Integer id);

    public boolean modify(AppVersion appVersion) throws Exception;

    public boolean deleteApkFile(Integer id);


}
