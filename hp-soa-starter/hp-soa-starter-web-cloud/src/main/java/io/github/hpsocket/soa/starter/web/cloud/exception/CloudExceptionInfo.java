package io.github.hpsocket.soa.starter.web.cloud.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <b>Spring Cloud 异常信息</b> */
@Getter
@Setter
@NoArgsConstructor
public class CloudExceptionInfo
{
    private Integer statusCode;
    private Integer resultCode;
    private String exception;
    private String message;
    private LocalDateTime timestamp;
    private Integer status;
    private String path;
    private String error;
}
