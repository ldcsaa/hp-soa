package io.github.hpsocket.soa.starter.web.cloud.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CloudExceptionInfo
{
    private Long timestamp;
    private Integer status;
    private Integer statusCode;
    private Integer resultCode;
    private String exception;
    private String message;
    private String path;
    private String error;
}
