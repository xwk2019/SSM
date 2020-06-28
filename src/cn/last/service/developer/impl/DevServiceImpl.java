package cn.last.service.developer.impl;

import cn.last.dao.*;
import cn.last.exception.DevLoginFailException;
import cn.last.pojo.*;
import cn.last.service.developer.DevService;
import cn.last.utils.PageSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DevServiceImpl implements DevService {
    //每个Resource对应一张表
    @Resource
    private DevUserMapper devUserMapper;
    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private DataDictionaryMapper dataDictionaryMapper;
    @Resource
    private AppCategoryMapper appCategoryMapper;
    @Resource
    private AppVersionMapper appVersionMapper;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public DevUser devLogin(String devCode, String devPassword) {

        DevUser devUser = devUserMapper.doDevLogin(devCode);

        if (devUser != null && devUser.getDevPassword().equals(devPassword)) {
            return devUser;
        }
        throw new DevLoginFailException("用户名或密码不正确!!!");

    }

    @Override
    public PageSupport<AppInfo> getAppInfoList(String querysoftwareName, Integer querystatus, Integer querycategoryLevel1, Integer querycategoryLevel2, Integer querycategoryLevel3, Integer queryflatformId, Integer devId, Integer pageIndex, Integer pageSize) {
        PageSupport<AppInfo> pageSupport = new PageSupport<>();
        //总记录数
        int totalCount = appInfoMapper.getAppInfoCount(querystatus, queryflatformId, querysoftwareName, querycategoryLevel1, querycategoryLevel2, querycategoryLevel3);
        pageSupport.setTotalCount(totalCount);
        //页面容量
        pageSupport.setPageSize(pageSize);
        //页码
        pageSupport.setPageIndex(pageIndex);
        //数据
        if (totalCount > 0) {
            List<AppInfo> list = appInfoMapper.getAppInfoList(querysoftwareName, querystatus, querycategoryLevel1, querycategoryLevel2, querycategoryLevel3, queryflatformId, devId, pageSupport.getStartRow(), pageSupport.getPageSize());
            pageSupport.setList(list);
        }
        return pageSupport;
    }

    @Override
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        return dataDictionaryMapper.getDataDictionaryList(typeCode);
    }

    @Override
    public List<AppCategory> getAppCategoryList(Integer parentId) {
        return appCategoryMapper.getAppCategoryList(parentId);
    }

    @Override
    public List<AppVersion> getAppVersionList(Integer id) throws Exception {
        return appVersionMapper.getAppVersionList(id);
    }

    @Override
    public AppInfo getAppInfo(Integer id, String APKName) throws Exception {
        return appInfoMapper.getAppInfo(id, APKName);
    }

    @Override
    public int addAppVersion(AppVersion appVersion) throws Exception {
        return appVersionMapper.add(appVersion);
    }

    @Override
    public boolean updateSaleStatusByAppId(AppInfo appInfoObj) throws Exception {
        Integer operator = appInfoObj.getModifyBy();
        if (operator >= 0 && appInfoObj.getId() >= 0) {
            AppInfo appInfo = appInfoMapper.getAppInfo(appInfoObj.getId(), (String) null);
            if (appInfo == null) {
                return false;
            } else {
                switch (appInfo.getStatus()) {
                    case 2:
                        this.onSale(appInfo, operator, 4, 2);
                        break;
                    case 3:
                    default:
                        return false;
                    case 4:
                        this.offSale(appInfo, operator, 5);
                        break;
                    case 5:
                        this.onSale(appInfo, operator, 4, 2);
                }

                return true;
            }
        } else {
            throw new Exception();
        }
    }

    @Override
    public int modifyVersionId(Integer versionId, Integer id) {
        return appInfoMapper.modifyVersionId(versionId, id);
    }

    private void onSale(AppInfo appInfo, Integer operator, Integer appInfStatus, Integer versionStatus) throws Exception {
        this.offSale(appInfo, operator, appInfStatus);
        this.setSaleSwitchToAppVersion(appInfo, operator, versionStatus);
    }

    private boolean offSale(AppInfo appInfo, Integer operator, Integer appInfStatus) throws Exception {
        AppInfo af = new AppInfo();
        af.setId(appInfo.getId());
        af.setStatus(appInfStatus);
        af.setModifyBy(operator);
        af.setOffSaleDate(new Date(System.currentTimeMillis()));
        appInfoMapper.modify(af);
        return true;
    }

    private boolean setSaleSwitchToAppVersion(AppInfo appInfo, Integer operator, Integer saleStatus) throws Exception {
        AppVersion appVersion = new AppVersion();
        appVersion.setId(appInfo.getVersionId());
        appVersion.setPublishStatus(saleStatus);
        appVersion.setModifyBy(operator);
        appVersion.setModifyDate(new Date(System.currentTimeMillis()));
        System.out.println(appVersion + "aaaaaaa");
        appVersionMapper.modify(appVersion);
        return false;
    }
}
