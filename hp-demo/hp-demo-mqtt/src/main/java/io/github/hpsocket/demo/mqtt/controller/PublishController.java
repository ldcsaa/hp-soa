package io.github.hpsocket.demo.mqtt.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.hpsocket.demo.mqtt.contract.req.PublishRequest;
import io.github.hpsocket.demo.mqtt.contract.resp.PublishResponse;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "消息发布接口")
@RequestMapping(value = "/mqtt", method = {RequestMethod.POST})
public interface PublishController
{
    @PostMapping(value = "/publish")
    @Operation(summary = "发布消息", description = "发布消息")
    public Response<PublishResponse> publish(@RequestBody @Valid PublishRequest req);
}
