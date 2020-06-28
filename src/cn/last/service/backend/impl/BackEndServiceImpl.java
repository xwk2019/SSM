package cn.last.service.backend.impl;

import cn.last.dao.*;
import cn.last.exception.BackEndLoginFailException;
import cn.last.pojo.*;
import cn.last.service.backend.BackEndService;
import cn.last.utils.PageSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class BackEndServiceImpl implements BackEndService {
    @Resource
    private BackEndUserMapper backEndUserMapper;
    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private DataDictionaryMapper dataDictionaryMapper;
    @Resource
    private AppCategoryMapper appCategoryMapper;
    @Resource
    private AppVersionMapper appVersionMapper;

    /*
     *
     * */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public BackEndUser backLogin(String userCode, String userPassword) {

        BackEndUser backEndUser = backEndUserMapper.backLogin(userCode);

        if (backEndUser != null && backEndUser.getUserPassword().equals(userPassword)) {
            return backEndUser;
        }
        throw new BackEndLoginFailException("用户名或密码不正确!!!");
    }

    /*
     *
     * */
    @Override
    public PageSupport<AppInfo> getAppInfoList(String softwareName, Integer status, Integer categoryLevel1, Integer categoryLevel2, Integer categoryLevel3, Integer flatformId, Integer devId, Integer pageIndex, Integer pageSize) {
        PageSupport<AppInfo> pageSupport = new PageSupport<>();
        //总记录数
        int totalCount = appInfoMapper.getAppInfoCount(status, flatformId, softwareName, categoryLevel1, categoryLevel2, categoryLevel3);
        pageSupport.setTotalCount(totalCount);
        //页面容量
        pageSupport.setPageSize(pageSize);
        //页码
        pageSupport.setPageIndex(pageIndex);
        //数据
        if (totalCount > 0) {
            List<AppInfo> list = appInfoMapper.getAppInfoList(softwareName, status, categoryLevel1, categoryLevel2, categoryLevel3, flatformId, devId, pageSupport.getStartRow(), pageSupport.getPageSize());
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
    public AppInfo getAppInfo(Integer id) throws Exception {
        return appInfoMapper.getAppInfo(id, null);
    }

    @Override
    public AppVersion getAppVersionById(Integer id) {
        return appVersionMapper.getAppVersionById(id);
    }

    @Override
    public boolean updateSatus(Integer status, Integer id) throws Exception {
        boolean flag = false;
        if (appInfoMapper.updateSatus(status, id) > 0) {
            flag = true;
        }

        return flag;
    }


}
