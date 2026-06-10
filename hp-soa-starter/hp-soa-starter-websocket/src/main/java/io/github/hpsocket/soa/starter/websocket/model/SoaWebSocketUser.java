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
    private Long userId;
    private String userName;
    private Integer userType;
    private Long groupId;
}
