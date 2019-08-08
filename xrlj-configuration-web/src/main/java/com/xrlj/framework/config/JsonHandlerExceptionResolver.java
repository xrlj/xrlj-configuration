package com.xrlj.framework.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xrlj.framework.spring.mvc.api.ApiException;
import com.xrlj.utils.time.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 统一异常处理。内部服务。
 */
@Slf4j
public class JsonHandlerExceptionResolver extends SimpleMappingExceptionResolver {

	@Value("${spring.application.name}")
	private String appName;

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

	/**
	 * 改变异常捕捉链中的顺序。改成-1，排到最前面。
	 * @return
	 */
	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
		if (!response.isCommitted()) {
			if (handler instanceof HandlerMethod) {
				final String callbackName = request.getParameter(jsonpCallbackParameterName);
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				if (handlerMethod.getMethodAnnotation(ResponseBody.class) != null
						|| handlerMethod.getBeanType().getAnnotation(ResponseBody.class) != null
						|| handlerMethod.getBeanType().getAnnotation(RestController.class) != null
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
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setContentType(getContentType());
				//系统抛出异常的时候，添加头信息，标志是内部服务的异常
				response.addHeader("m-error-type","service-inner");
				PrintWriter out = response.getWriter();
				try {
					handleExceptionJsonMessage(out, ex, callbackName, response);
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

	public void handleExceptionJsonMessage(PrintWriter out, Exception ex, String callbackName, HttpServletResponse response) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("service",appName);
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
		String errorStr = stringWriter.toString();
		log.error(">>>>>>>>请求异常信息：{}", errorStr);//打印错误日志
		//添加上服务名称，在api网关就能轻易看到是哪个服务报错，容易排错。
		data.put("timestamp", DateUtil.getDateToString(new Date(),null));
		if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            data.put("message", apiException.getMessage());
            data.put("status", apiException.getCode());
        } else if (throwable instanceof HttpMessageNotReadableException) {
			data.put("message","请求body格式为application/json");
//			data.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
		} else if (throwable instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException e = (MethodArgumentNotValidException) throwable;
			BindingResult bindingResult = e.getBindingResult();
			if (bindingResult.hasErrors()) {
				ObjectError error = bindingResult.getAllErrors().get(0);
				if (error instanceof FieldError) {
					FieldError fieldError = (FieldError) error;
					String errorMsg = fieldError.getDefaultMessage();
					data.put("message", String.format("参数%s校验错误：%s",fieldError.getField(), errorMsg));
//					data.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
				}
			}
		} else if (throwable instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) throwable;
			Set<ConstraintViolation<?>> s = constraintViolationException.getConstraintViolations();
			String erorMsg = constraintViolationException.getMessage();
			data.put("message", erorMsg);
		}  else if (throwable instanceof MissingServletRequestParameterException) {
			MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) throwable;
			String parName = missingServletRequestParameterException.getParameterName();
			data.put("message", String.format("缺少必传参数%s", parName));
//			data.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
		} else {
			data.put("message","系统内部异常,请联系技术开发人员");
//			data.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
		data.put("status", response.getStatus()); //拿系统异常返回的状态码
		data.put("type", throwable.getClass().getCanonicalName());
		data.put("error",stringWriter.toString());
		String json = JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect);
		if (callbackName != null) {
            out.print(callbackName);
            out.print("(");
            out.print(json);
            out.print(")");
        } else {
            out.print(json);
        }
	}
}
