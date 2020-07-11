package com.kunyiduan.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.kunyiduan.bean.ResponseDto;
import com.netflix.client.ClientException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.crypto.BadPaddingException;
import javax.validation.UnexpectedTypeException;

import static com.kunyiduan.exception.ExceptionCode.ENCRYPTION_ERROR;

/**
 * 异常处理器
 *
 * @author achilles
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    /**
     * 没有明细异常时，500+
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseDto handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error();
    }

    /**
     * 数据库主键冲突
     * @param e
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseDto handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(ExceptionCode.DB_DUPLICATEKEY.getCode(),ExceptionCode.DB_DUPLICATEKEY.getMessage());
    }

    /**
     * 捕获Aes加密时，如果获取到的加密字符串错误时，无法解密的异常
     * @param e
     * @return
     */
    @ExceptionHandler(BadPaddingException.class)
    public ResponseDto handleBadPaddingException(BadPaddingException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(ENCRYPTION_ERROR.getCode(),ENCRYPTION_ERROR.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseDto handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        String msg = String.format("参数不对:%s", e.getMessage());
        return new ResponseDto().error(203, msg);
    }

    @ExceptionHandler(BindException.class)
    public ResponseDto bindException(BindException e) {
        log.error(e.getMessage(), e);
        String msg = String.format("参数不能为空:%s", e.getAllErrors().get(0).getDefaultMessage());
        return new ResponseDto().error(201, msg);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseDto handleMissingServletRequestParameterException(UnexpectedTypeException e) {
        log.error(e.getMessage(), e);
        String msg = String.format("参数不能为空:%s", e.getMessage());
        return new ResponseDto().error(203, msg);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseDto handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(400, "路径不存在，请检查路径是否正确");
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = GlobalException.class)
    public ResponseDto GlobalException(GlobalException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = RetryableException.class)
    public ResponseDto RetryableException(RetryableException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(577, e.getMessage());
    }

    @ExceptionHandler(value = ClientException.class)
    public ResponseDto ClientException(ClientException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(533, e.getMessage());
    }

    /**
     * Json数据项验证异常--数据通过Json传输
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        int end = e.getMessage().lastIndexOf("]]");
        int start = e.getMessage().lastIndexOf('[');
        String errStr = e.getMessage().substring(start + 1, end);
        String msg = String.format("Json验证异常:%s", errStr);
        return new ResponseDto().error(401, msg);
    }

    /**
     * Json格式解析异常
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseDto handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(402, "Json数据格式错误");
    }

    /**
     * JWT解码异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = JWTDecodeException.class)
    public ResponseDto handleJWTDecodeException(JWTDecodeException e) {
        log.error(e.getMessage(), e);
        return new ResponseDto().error(ExceptionCode.TOKEN_FORMAT_ERROR.getCode(),ExceptionCode.TOKEN_FORMAT_ERROR.getMessage());
    }

}
