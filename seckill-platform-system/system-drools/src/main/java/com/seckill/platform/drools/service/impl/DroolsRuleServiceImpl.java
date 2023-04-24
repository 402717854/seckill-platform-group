package com.seckill.platform.drools.service.impl;

import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.drools.manage.DroolsManager;
import com.seckill.platform.drools.domain.DroolsRule;
import com.seckill.platform.drools.service.DroolsRuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Todo
 *
 * @Author wys
 * @Date 2023/4/24 14:00
 */
@Service
public class DroolsRuleServiceImpl implements DroolsRuleService {

    @Resource
    private DroolsManager droolsManager;

    /**
     * 模拟数据库
     */
    private Map<Long, DroolsRule> droolsRuleMap = new HashMap<>(16);

    @Override
    public List<DroolsRule> findAll() {
        return new ArrayList<>(droolsRuleMap.values());
    }

    @Override
    public void addDroolsRule(DroolsRule droolsRule) {
        droolsRule.validate();
        droolsRule.setCreatedTime(new Date());
        RedissonUtils.getMap(droolsRule.getRuleId()+":"+droolsRule.getKieBaseName()).put(droolsRule.getRuleId(),droolsRule);
//        droolsRuleMap.put(droolsRule.getRuleId(), droolsRule);
        droolsManager.addOrUpdateRule(droolsRule);
    }

    @Override
    public void updateDroolsRule(DroolsRule droolsRule) {
        droolsRule.validate();
        droolsRule.setUpdateTime(new Date());
        RedissonUtils.getMap(droolsRule.getRuleId()+":"+droolsRule.getKieBaseName()).put(droolsRule.getRuleId(),droolsRule);
//        droolsRuleMap.put(droolsRule.getRuleId(), droolsRule);
        droolsManager.addOrUpdateRule(droolsRule);
    }

    @Override
    public void deleteDroolsRule(Long ruleId, String ruleName) {
        DroolsRule droolsRule = droolsRuleMap.get(ruleId);
        if (null != droolsRule) {
            RedissonUtils.getMap(droolsRule.getRuleId()+":"+droolsRule.getKieBaseName()).remove(ruleId);
//            droolsRuleMap.remove(ruleId);
            droolsManager.deleteDroolsRule(droolsRule.getKieBaseName(), droolsRule.getKiePackageName(), ruleName);
        }
    }
}
