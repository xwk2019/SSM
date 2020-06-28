package cn.last.service.developer;

import cn.last.pojo.AppInfo;

public interface AppInfoService {
    public boolean add(AppInfo appInfo);

    public AppInfo getAppInfo(Integer id, String APKName) throws Exception;

    public boolean modify(AppInfo appInfo);

    public boolean deleteAppLogo(Integer id) throws Exception;

    public boolean appsysdeleteAppById(Integer id) throws Exception;
}

