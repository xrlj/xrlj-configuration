package com.xrlj.framework.config;

import com.alibaba.fastjson.JSON;
import com.xrlj.framework.spring.mvc.api.ApiOpenException;
import com.xrlj.framework.spring.mvc.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * 统一异常处理。对外服务。
 */
@Slf4j
public class JsonHandlerExceptionResolverOpen extends SimpleMappingExceptionResolver {

	/**
	 * 用于获取JSONP调用时的回调函数名的请求参数名
	 */
	private String jsonpCallbackParameterName = "jsonpcallback";

	public String getJsonpCallbackParameterName() {
		return jsonpCallbackParameterName;
	}

	public void setJsonpCallbackParameterName(String jsonpCallbackParameterName) {
		this.jsonpCallbackParameterName = jsonpCallbackParameterName;
	}

	@Override
	public int getOrder() {
		return -100;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
		if (!response.isCommitted()) {
			if (handler instanceof HandlerMethod) {
				final String callbackName = request.getParameter(jsonpCallbackParameterName);
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				if (handlerMethod.getMethodAnnotation(ResponseBody.class) != null
						|| handlerMethod.getBeanType().getAnnotation(RestController.class) != null
						|| handlerMethod.getBeanType().getAnnotation(ResponseBody.class) != null
						|| callbackName != null) {
					return handleExceptionMessage(request, response, ex, callbackName);
				}
			}
		} else {
			log.error("不能将异常信息处理为JSON格式，因为输出流已提交", ex);
		}
		return super.resolveException(request, response, handler, ex);
	}

	private ModelAndView handleExceptionMessage(HttpServletRequest request, HttpServletResponse response, Exception ex,
                                                final String callbackName) {
		response.resetBuffer();

		return new ModelAndView(new View() {
			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
				response.setContentType(getContentType());
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.addHeader("m-error-type","service-open");
				PrintWriter out = response.getWriter();
				try {
					handleExceptionJsonMessage(out, ex, callbackName);
				} finally {
					out.close();
				}
			}

			@Override
			public String getContentType() {
				return callbackName == null ? "application/json;charset=utf-8" : "application/javascript;charset=utf-8";
			}
		});
	}

	public void handleExceptionJsonMessage(PrintWriter out, Exception ex, String callbackName) {
		ApiResult apiResult = new ApiResult();
		Throwable throwable;
		if (ex != null && ex.getCause() != null && ex.getMessage() != null) {
			throwable = ex.getCause();
		} else {
			throwable = ex;
		}
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(stringWriter)){
			throwable.printStackTrace(printWriter);
		}
		log.error("The handleExceptionJsonMessage will handled this exception.", throwable);
		if (throwable instanceof ApiOpenException) {
			apiResult.failure(((ApiOpenException) throwable).getCode(),throwable.getMessage());
        } else {
			apiResult.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),"系统内部异常，请联系系统管理员!");
        }
		if (callbackName != null) {
            out.print(callbackName);
            out.print("(");
            out.print(JSON.toJSONString(apiResult));
            out.print(")");
        } else {
            out.print(JSON.toJSONString(apiResult));
        }
	}
}
