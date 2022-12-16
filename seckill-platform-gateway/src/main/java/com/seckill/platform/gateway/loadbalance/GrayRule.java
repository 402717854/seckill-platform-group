package com.seckill.platform.gateway.loadbalance;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.google.common.base.Optional;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.seckill.platform.common.constant.GrayConstant;
import com.seckill.platform.gateway.context.GrayRequestContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 灰度发布的规则
 */
public class GrayRule extends ZoneAvoidanceRule {
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
    }

    @Override
    public Server choose(Object key) {
        try{
            //从ThreadLocal中获取灰度标记
            boolean grayTag = GrayRequestContextHolder.getGrayTag();
            //获取所有可用服务
            List<Server> serverList = this.getLoadBalancer().getReachableServers();
            //灰度发布的服务
            List<Server> grayServerList = new ArrayList<>();
            //正常的服务
            List<Server> normalServerList = new ArrayList<>();
            for(Server server : serverList) {
                NacosServer nacosServer = (NacosServer) server;
                //从nacos中获取元素剧进行匹配
                if(nacosServer.getMetadata().containsKey(GrayConstant.GRAY_HEADER)
                        && nacosServer.getMetadata().get(GrayConstant.GRAY_HEADER).equals(GrayConstant.GRAY_VALUE)) {
                    grayServerList.add(server);
                } else {
                    normalServerList.add(server);
                }
            }
            //如果被标记为灰度发布，则调用灰度发布的服务
            if(grayTag) {
                return originChoose(grayServerList,key);
            } else {
                return originChoose(normalServerList,key);
            }
        }finally {
            //清除灰度标记
            GrayRequestContextHolder.remove();
        }
    }

    private Server originChoose(List<Server> noMetaServerList, Object key) {
        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(noMetaServerList, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }
}
