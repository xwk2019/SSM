package cn.last.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping(value = "/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/entryBackLogin")
    public String entryBackLogin() {
        return "backendlogin";
    }

    @RequestMapping(value = "/entryDevLogin")
    public String entryDevLogin() {
        return "devlogin";
    }
}
