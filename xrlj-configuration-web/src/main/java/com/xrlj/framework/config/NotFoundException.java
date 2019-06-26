package com.xrlj.framework.config;

import com.xrlj.framework.spring.mvc.api.ApiResult;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 报路径不存在错误，请求方式GET或者POST不对。都是进入这里。这里弃用。<br>
 *     在具体项目中继承并注入。
 */
//@Controller
public class NotFoundException implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = {"/error"})
    @ResponseBody
    public ApiResult error(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
        ApiResult apiResult = new ApiResult();

        int status = response.getStatus();
        apiResult.setCode(status);
        apiResult.setSuccess(false);
        apiResult.setData("");
        if (404 == status) {
            apiResult.setMsg("请求路径不存在");
        } else {
            apiResult.setMsg("系统内部未知异常");
        }

        return apiResult;
    }
}
