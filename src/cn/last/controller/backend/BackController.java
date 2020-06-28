package cn.last.controller.backend;

import cn.last.pojo.*;
import cn.last.service.backend.BackEndService;
import cn.last.utils.PageSupport;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/backend")
public class BackController {
    @Resource
    private BackEndService backEndService;

    /*
    后台登入操作
    * */
    @RequestMapping(value = "/doBackLogin", method = RequestMethod.POST)
    public String doBackLogin(@Param("userCode") String userCode,
                              @Param("userPassword") String userPassword,
                              HttpSession session,
                              HttpServletRequest request) {
        BackEndUser backEndUser = backEndService.backLogin(userCode, userPassword);
        if (backEndUser != null) {
            session.setAttribute("backEndUser", backEndUser);
            return "backend/main";
        } else {
            request.setAttribute("error", "用户名或密码不正确!");
            return "forward:../entryBackLogin.html";

        }
    }

    /*
     * 注销操作
     * */
    @RequestMapping(value = "/logout")
    public String logout(HttpSession httpSession, HttpServletRequest request) {
        httpSession.invalidate();
        request.setAttribute("msg", "注销成功!");
        return "backendlogin";
    }

    /*
     * 返回主页
     * */
    @RequestMapping("/main")
    public String main() {
        return "backend/main";
    }
    /*
     * 显示Appinfo 列表List
     * */

    @RequestMapping(value = "appAuditList")
    public String appAuditList(@RequestParam(value = "querySoftwareName", required = false) String querysoftwareName,
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
        statusList = backEndService.getDataDictionaryList("APP_STATUS");
        /*
        所属平台
        * */
        flatFormList = backEndService.getDataDictionaryList("APP_FLATFORM");
        /*
        一级分类
        * */
        categoryLevel1List = backEndService.getAppCategoryList(null);
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

        PageSupport<AppInfo> pageSupport = backEndService.getAppInfoList(querysoftwareName, 1, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryflatformId, devId, pageIndex, pageSize);
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


        return "backend/applist";
    }

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
        categoryLevelList = backEndService.getAppCategoryList(pid == null ? null : Integer.parseInt(pid));
        return categoryLevelList;
    }

    //进入审核页面
    @RequestMapping(
            value = {"/check"},
            method = {RequestMethod.GET}
    )
    public String check(@RequestParam(value = "aid", required = false) String appId, @RequestParam(value = "vid", required = false) String versionId, Model model) {
        AppInfo appInfo = null;
        AppVersion appVersion = null;

        try {
            appInfo = backEndService.getAppInfo(Integer.parseInt(appId));
            appVersion = backEndService.getAppVersionById(Integer.parseInt(versionId));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        model.addAttribute(appVersion);
        model.addAttribute(appInfo);
        return "backend/appcheck";
    }

    @RequestMapping(
            value = {"/checksave"},
            method = {RequestMethod.POST}
    )
    public String checkSave(AppInfo appInfo) {
        try {
            if (backEndService.updateSatus(appInfo.getStatus(), appInfo.getId())) {
                return "redirect:/backend/appAuditList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "backend/appcheck";
    }
}
