package io.github.hpsocket.demo.infra.mysql.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class UserBo implements Serializable
{
	private Long id;
	private String name;
	private Integer age;
}
