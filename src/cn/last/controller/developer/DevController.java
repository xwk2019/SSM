package cn.last.controller.developer;

import cn.last.pojo.*;
import cn.last.service.developer.DevService;
import cn.last.utils.PageSupport;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/dev")
public class DevController {

    @Resource
    private DevService devService;

    /*
    开发者平台登入操作
    * */
    @RequestMapping(value = "/doDevLogin", method = RequestMethod.POST)
    public String doDevLogin(@Param("devCode") String devCode,
                             @Param("devPassword") String devPassword,
                             HttpSession session,
                             HttpServletRequest request) {
        DevUser devUser = devService.devLogin(devCode, devPassword);
        if (devUser != null) {
            session.setAttribute("devUser", devUser);
            return "developer/main";
        }
        return "devlogin";
    }

    //-------------------------------------------------------------------
    /*
     * 注销操作
     * */
    @RequestMapping(value = "/logout")
    public String logout(HttpSession httpSession, HttpServletRequest request) {
        httpSession.invalidate();
        request.setAttribute("msg", "注销成功!");
        return "devlogin";
    }

    //-------------------------------------------------------------------
    /*
     * 返回主页
     * */
    @RequestMapping("/main")
    public String main() {
        return "developer/main";
    }


    //-------------------------------------------------------------------
    /*
     * 打开APP维护页面,查询
     * */
    @RequestMapping(value = "appSafeguardList")
    public String appSafeguardList(@RequestParam(value = "querySoftwareName", required = false) String querysoftwareName,
                                   @RequestParam(value = "queryStatus", required = false) Integer querystatus,
                                   @RequestParam(value = "queryCategoryLevel1", required = false) Integer queryCategoryLevel1,
                                   @RequestParam(value = "queryCategoryLevel2", required = false) Integer queryCategoryLevel2,
                                   @RequestParam(value = "queryCategoryLevel3", required = false) Integer queryCategoryLevel3,
                                   @RequestParam(value = "queryFlatformId", required = false) Integer queryflatformId,
                                   @RequestParam(value = "devId", required = false) Integer devId,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                   Model model) {
        /*
         *   查询条件内容
         * */
        List<DataDictionary> statusList = null;
        List<DataDictionary> flatFormList = null;
        List<AppCategory> categoryLevel1List = null;
        List<AppCategory> categoryLevel2List = null;
        List<AppCategory> categoryLevel3List = null;
        /*
        App状态
        * */
        statusList = devService.getDataDictionaryList("APP_STATUS");
        /*
        所属平台
        * */
        flatFormList = devService.getDataDictionaryList("APP_FLATFORM");
        /*
        一级分类
        * */
        categoryLevel1List = devService.getAppCategoryList(null);
        //选择：一级分类
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
          /*
        二级分类
        * */
        if (queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
            categoryLevel2List = this.getCategoryList(queryCategoryLevel1.toString());
            model.addAttribute("categoryLevel2List", categoryLevel2List);
            model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
        }
          /*
         三级分类
        * */
        if (queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")) {
            categoryLevel3List = this.getCategoryList(queryCategoryLevel2.toString());
            model.addAttribute("categoryLevel3List", categoryLevel3List);
            model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
        }
        //select

        PageSupport<AppInfo> pageSupport = devService.getAppInfoList(querysoftwareName, querystatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryflatformId, devId, pageIndex, pageSize);
        model.addAttribute("appInfoList", pageSupport);
        model.addAttribute("pageSupport", pageSupport);
        //查询条件--名称
        model.addAttribute("querySoftwareName", querysoftwareName);
        //选择：App状态
        model.addAttribute("statusList", statusList);
        model.addAttribute("queryStatus", querystatus);
        //选择：所属平台
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("queryFlatformId", queryflatformId);


        return "developer/appinfolist";
    }

    //-------------------------------------------------------------------
    /*
     * 二级三级分类的异步（applist.js中）
     * */
    @RequestMapping(
            value = {"/categorylevellist.json"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
        if (pid.equals("")) {
            pid = null;
        }
        return this.getCategoryList(pid);
    }

    public List<AppCategory> getCategoryList(String pid) {
        List categoryLevelList = null;
        categoryLevelList = devService.getAppCategoryList(pid == null ? null : Integer.parseInt(pid));
        return categoryLevelList;
    }

    //-------------------------------------------------------------------
    /*
     * 打开新增版本页面
     * */
    @RequestMapping(value = "/appversionadd")
    public String appversionadd(Integer id, Model model) throws Exception {
        List<AppVersion> versionList = null;
        versionList = devService.getAppVersionList(id);
        model.addAttribute("appVersionList", versionList);
        model.addAttribute("appId", id);
        return "developer/appversionadd";
    }

    //-------------------------------------------------------------------
    /*
     * 新增版本
     * */
    @RequestMapping("/addversionsave")
    public String addversionsave(AppVersion appVersion,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 @RequestParam(value = "a_downloadLink", required = false) MultipartFile file) throws Exception {
        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (file.isEmpty()) {
            String realPath = session.getServletContext().getRealPath("/statics/uploadFile/");
            //2.验证文件是否合法
            String filename = file.getOriginalFilename();
            String suffix = FilenameUtils.getExtension(filename);
            if (!suffix.equalsIgnoreCase("apk")) {
                request.setAttribute("fileUploadError", "上传的文件类型不合法!");
                return "developer/appversionadd";
            }
            String apkName = null;
            try {
                apkName = this.devService.getAppInfo(appVersion.getAppId(), (String) null).getAPKName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (apkName == null || "".equals(apkName)) {
                request.setAttribute("fileUploadError", "上传的文件出错!");
                return "forward:/developer/appversionadd";
            }
            //文件名
            apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
            File targetFile = new File(realPath, apkFileName);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            try {
                file.transferTo(targetFile);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("fileUploadError", "上传的文件出错!");
                return "developer/appversionadd";
            }
            //下载路径
            downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
            //本地路径
            apkLocPath = realPath + apkFileName;
        } else {
            request.setAttribute("fileUploadError", "上传的文件出错!");
            return "forward:/developer/appversionadd?id=" + appVersion.getAppId();
        }
        appVersion.setCreatedBy(devService.getAppInfo(appVersion.getAppId(), null).getCreatedBy());
        appVersion.setCreationDate(new Date());
        appVersion.setModifyDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        int result = devService.addAppVersion(appVersion);
        if (result == 1) {
            //更换appinfoz中的最新版本号versionId
            devService.modifyVersionId(appVersion.getId(), appVersion.getAppId());
            return "redirect:/dev/appSafeguardList";
        }

        return "developer/addversionadd?id=" + appVersion.getAppId();
    }

    //-------------------------------------------------------------------

    /*
     * app 下架   已上架（4）的软件
     *    上架   审核通过（2）  已下架（5）的软件
     * */
    @RequestMapping(
            value = {"/{appid}/sale"},
            method = {RequestMethod.PUT}
    )
    @ResponseBody
    public Object sale(@PathVariable String appid, HttpSession session) {
        HashMap<String, Object> resultMap = new HashMap();
        Integer appIdInteger = 0;

        try {
            appIdInteger = Integer.parseInt(appid);
        } catch (Exception var8) {
            appIdInteger = 0;
        }

        resultMap.put("errorCode", "0");
        resultMap.put("appId", appid);
        if (appIdInteger > 0) {
            try {
                DevUser devUser = (DevUser) session.getAttribute("devUser");
                AppInfo appInfo = new AppInfo();
                appInfo.setId(appIdInteger);
                appInfo.setModifyBy(devUser.getId());
                if (this.devService.updateSaleStatusByAppId(appInfo)) {
                    resultMap.put("resultMsg", "success");
                } else {
                    resultMap.put("resultMsg", "failed");
                }
            } catch (Exception var7) {
                resultMap.put("errorCode", "exception000001");
            }
        } else {
            resultMap.put("errorCode", "param000001");
        }

        return resultMap;
    }


}
