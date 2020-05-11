package com.wutos.base.web.advice;

import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.util.BaseResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 */
@ControllerAdvice
public class MyControllerAdvice {

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    }

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     *
     * @param model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        //model.addAttribute("author", "Magical Sam");
    }

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseResponse errorHandler(Exception ex) {
        ex.printStackTrace();
        return BaseResponse.getInstance(500, ex.getMessage());
    }

    /**
     * 自定义异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = WutosException.class)
    public BaseResponse errorHandler(WutosException ex) {
        ex.printStackTrace();
        return BaseResponse.getInstance(ex.getCode(), ex.getMsg());
    }
}