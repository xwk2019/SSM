package cn.last.controller.developer;

import cn.last.pojo.AppInfo;
import cn.last.pojo.AppVersion;
import cn.last.pojo.DataDictionary;
import cn.last.pojo.DevUser;
import cn.last.service.developer.AppInfoService;
import cn.last.service.developer.AppVersionService;
import cn.last.service.developer.DevService;
import cn.last.utils.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/dev")
public class AppController {
    @Resource
    AppInfoService appInfoService;
    @Resource
    AppVersionService appVersionService;
    @Resource
    DevService devService;

    /**
     * 增加app信息（跳转到新增appinfo页面）
     */
    @RequestMapping(value = "/addAppInfo", method = RequestMethod.GET)
    public String add(@ModelAttribute("appInfo") AppInfo appInfo) {
        return "developer/appinfoadd";
    }

    /**
     * 保存新增appInfo（主表）的数据
     */

    @RequestMapping(value = "/appinfoaddsave", method = RequestMethod.POST)
    public String addSave(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                          @RequestParam(value = "a_logoPicPath", required = false) MultipartFile attach) {

        String logoPicPath = null;
        String logoLocPath = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            int filesize = 500000;
            if (attach.getSize() > filesize) {//上传大小不得超过 50k
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                return "developer/appinfoadd";
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = appInfo.getAPKName() + ".jpg";//上传LOGO图片命名:apk名称.apk
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appinfoadd";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
        appInfo.setCreatedBy(((DevUser) session.getAttribute("devUser")).getId());
        appInfo.setCreationDate(new Date());
        appInfo.setLogoPicPath(logoPicPath);
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setDevId(((DevUser) session.getAttribute("devUser")).getId());
        appInfo.setStatus(1);
        try {
            if (appInfoService.add(appInfo)) {
                return "redirect:/dev/appSafeguardList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfoadd";
    }

    @RequestMapping(value = "/appinfomodify", method = RequestMethod.GET)
    public String modifyAppInfo(@RequestParam("id") String id,
                                @RequestParam(value = "error", required = false) String fileUploadError,
                                Model model) {
        AppInfo appInfo = null;
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        } else if (null != fileUploadError && fileUploadError.equals("error4")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_4;
        }
        try {
            appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute(appInfo);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appinfomodify";
    }

    @RequestMapping(value = "/appinfomodifysave", method = RequestMethod.POST)
    public String modifySave(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                             @RequestParam(value = "attach", required = false) MultipartFile attach) {
        String logoPicPath = null;
        String logoLocPath = null;
        String APKName = appInfo.getAPKName();
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            int filesize = 500000;
            if (attach.getSize() > filesize) {//上传大小不得超过 50k
                return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                        + "&error=error4";
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = APKName + ".jpg";//上传LOGO图片命名:apk名称.apk
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                            + "&error=error2";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                        + "&error=error3";
            }
        }
        appInfo.setModifyBy(((DevUser) session.getAttribute("devUser")).getId());
        appInfo.setModifyDate(new Date());
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setLogoPicPath(logoPicPath);
        try {
            if (appInfoService.modify(appInfo)) {
                return "redirect:/dev/appSafeguardList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfomodify";
    }

    /**
     * 修改最新的appVersion信息（跳转到修改appVersion页面）
     */
    @RequestMapping(value = "/appversionmodify", method = RequestMethod.GET)
    public String modifyAppVersion(@RequestParam("vid") String versionId,
                                   @RequestParam("aid") String appId,
                                   @RequestParam(value = "error", required = false) String fileUploadError,
                                   Model model) {
        AppVersion appVersion = null;
        List<AppVersion> appVersionList = null;
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        }
        try {
            appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute(appVersion);
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appversionmodify";
    }

    /*
     *  加载列表平台的异步(appinfoapp.js中)
     *  datadictionarylist.json
     * */
    @RequestMapping(value = "/datadictionarylist.json", method = RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> getFlatFormList(String tcode) {
        List<DataDictionary> list = null;
        list = devService.getDataDictionaryList(tcode);
        return list;
    }

    /**
     * 判断APKName是否唯一
     */
    @RequestMapping(value = "/apkexist.json", method = RequestMethod.GET)
    @ResponseBody
    public Object apkNameIsExit(@RequestParam String APKName) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(APKName)) {
            resultMap.put("APKName", "empty");
        } else {
            AppInfo appInfo = null;
            try {
                appInfo = appInfoService.getAppInfo(null, APKName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != appInfo)
                resultMap.put("APKName", "exist");
            else
                resultMap.put("APKName", "noexist");
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 保存修改后的appVersion
     */
    @RequestMapping(value = "/appversionmodifysave", method = RequestMethod.POST)
    public String modifyAppVersionSave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                       @RequestParam(value = "attach", required = false) MultipartFile attach) {

        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            if (prefix.equalsIgnoreCase("apk")) {//apk文件命名：apk名称+版本号+.apk
                String apkName = null;
                try {
                    apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {

                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error2";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            } else {
                return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                        + "&aid=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }
        appVersion.setModifyBy(((DevUser) session.getAttribute("devUser")).getId());
        appVersion.setModifyDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        try {
            if (appVersionService.modify(appVersion)) {
                return "redirect:/dev/appSafeguardList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appversionmodify";
    }

    /**
     * 修改操作时，删除文件（logo图片/apk文件），并更新数据库（app_info/app_version）
     */
    @RequestMapping(value = "/delfile", method = RequestMethod.GET)
    @ResponseBody
    public Object delFile(@RequestParam(value = "flag", required = false) String flag,
                          @RequestParam(value = "id", required = false) String id) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        String fileLocPath = null;
        if (flag == null || flag.equals("") ||
                id == null || id.equals("")) {
            resultMap.put("result", "failed");
        } else if (flag.equals("logo")) {//删除logo图片（操作app_info）
            try {
                fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id), null)).getLogoLocPath();
                File file = new File(fileLocPath);
                if (file.exists())
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appInfoService.deleteAppLogo(Integer.parseInt(id))) {//更新表
                            resultMap.put("result", "success");
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (flag.equals("apk")) {//删除apk文件（操作app_version）
            try {
                fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
                File file = new File(fileLocPath);
                if (file.exists())
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appVersionService.deleteApkFile(Integer.parseInt(id))) {//更新表
                            resultMap.put("result", "success");
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(value = "/delapp.json")
    @ResponseBody
    public Object delApp(@RequestParam String id) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(id)) {
            resultMap.put("delResult", "notexist");
        } else {
            try {
                if (appInfoService.appsysdeleteAppById(Integer.parseInt(id)))
                    resultMap.put("delResult", "true");
                else
                    resultMap.put("delResult", "false");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 查看app信息，包括app基本信息和版本信息列表（跳转到查看页面）
     */
    @RequestMapping(value = "/appview/{id}", method = RequestMethod.GET)
    public String view(@PathVariable String id, Model model) {
        AppInfo appInfo = null;
        List<AppVersion> appVersionList = null;
        try {
            appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appInfo);
        return "developer/appinfoview";
    }
}
