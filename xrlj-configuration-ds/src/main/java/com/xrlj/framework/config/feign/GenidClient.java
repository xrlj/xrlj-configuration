package com.xrlj.framework.config.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

@FeignClient(name = "${service-sys-genid.name}", fallbackFactory = GenidClientFallbackFactory.class)
public interface GenidClient {

    @RequestMapping(method = RequestMethod.GET, value = "${service-sys-genid.genid-api.genId}")
    long genId();

    @RequestMapping(method = RequestMethod.GET, value = "${service-sys-genid.genid-api.gexpId}")
    Serializable expId(@PathVariable(name = "id") long id);
}

