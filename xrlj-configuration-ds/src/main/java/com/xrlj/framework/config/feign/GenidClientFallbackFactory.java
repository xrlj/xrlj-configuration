package com.xrlj.framework.config.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class GenidClientFallbackFactory implements FallbackFactory<GenidClient> {

    @Override
    public GenidClient create(Throwable throwable) {
        log.error(">>>>服务降级", throwable);
        return new GenidClient() {

            @Override
            public long genId() {
                log.error(">>>>>genid 降级");
                return -1;
            }

            @Override
            public Serializable expId(long id) {
                return null;
            }
        };
    }
}
