package cn.last.service.developer.impl;

import cn.last.dao.AppVersionMapper;
import cn.last.pojo.AppVersion;
import cn.last.service.developer.AppVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AppVerSionServiceImpl implements AppVersionService {
    @Resource
    AppVersionMapper appVersionMapper;

    @Override
    public List<AppVersion> getAppVersionList(Integer appId) throws Exception {
        return appVersionMapper.getAppVersionList(appId);
    }

    @Override
    public AppVersion getAppVersionById(Integer id) {
        return appVersionMapper.getAppVersionById(id);
    }

    @Override
    public boolean modify(AppVersion appVersion) throws Exception {
        boolean flag = false;
        if (appVersionMapper.modify(appVersion) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean deleteApkFile(Integer id) {
        boolean flag = false;
        if (appVersionMapper.deleteApkFile(id) > 0) {
            flag = true;
        }
        return flag;
    }
}
