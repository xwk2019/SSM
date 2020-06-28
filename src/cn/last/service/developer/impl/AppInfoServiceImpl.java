package cn.last.service.developer.impl;

import cn.last.dao.AppInfoMapper;
import cn.last.dao.AppVersionMapper;
import cn.last.pojo.AppInfo;
import cn.last.pojo.AppVersion;
import cn.last.service.developer.AppInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class AppInfoServiceImpl implements AppInfoService {
    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private AppVersionMapper appVersionMapper;

    @Override
    public boolean add(AppInfo appInfo) {
        boolean flag = false;
        if (appInfoMapper.add(appInfo) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public AppInfo getAppInfo(Integer id, String APKName) throws Exception {
        return appInfoMapper.getAppInfo(id, APKName);
    }

    @Override
    public boolean modify(AppInfo appInfo) {
        boolean flag = false;
        if (appInfoMapper.Appmodify(appInfo) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean deleteAppLogo(Integer id) throws Exception {
        boolean flag = false;
        if (appInfoMapper.deleteAppLogo(id) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean appsysdeleteAppById(Integer id) throws Exception {
        boolean flag = false;
        int versionCount = appVersionMapper.getVersionCountByAppId(id);
        List<AppVersion> appVersionList = null;
        if (versionCount > 0) {//1 先删版本信息
            //<1> 删除上传的apk文件
            appVersionList = appVersionMapper.getAppVersionList(id);
            for (AppVersion appVersion : appVersionList) {
                if (appVersion.getApkLocPath() != null && !appVersion.getApkLocPath().equals("")) {
                    File file = new File(appVersion.getApkLocPath());
                    if (file.exists()) {
                        if (!file.delete())
                            throw new Exception();
                    }
                }
            }
            //<2> 删除app_version表数据
            appVersionMapper.deleteVersionByAppId(id);
        }
        //2 再删app基础信息
        //<1> 删除上传的logo图片
        AppInfo appInfo = appInfoMapper.getAppInfo(id, null);
        if (appInfo.getLogoLocPath() != null && !appInfo.getLogoLocPath().equals("")) {
            File file = new File(appInfo.getLogoLocPath());
            if (file.exists()) {
                if (!file.delete())
                    throw new Exception();
            }
        }
        //<2> 删除app_info表数据
        if (appInfoMapper.deleteAppInfoById(id) > 0) {
            flag = true;
        }
        return flag;
    }


}
