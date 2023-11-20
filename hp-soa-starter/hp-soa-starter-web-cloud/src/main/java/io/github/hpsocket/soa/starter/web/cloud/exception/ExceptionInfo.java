package io.github.hpsocket.soa.starter.web.cloud.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionInfo
{
    private Long timestamp;
    private Integer status;
    private String exception;
    private String message;
    private String path;
    private String error;
}
