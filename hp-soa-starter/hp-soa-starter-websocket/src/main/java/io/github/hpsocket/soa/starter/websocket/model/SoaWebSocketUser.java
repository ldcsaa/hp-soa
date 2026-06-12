package io.github.hpsocket.soa.starter.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * <b>Web Socket 用户</b>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class SoaWebSocketUser implements Serializable
{
    /** 用户编号 */
    private Long userId;
    /** 用户名称 */
    private String userName;
    /** 用户类型 */
    private Integer userType;
    /** 用户组编号 */
    private Long groupId;
}
