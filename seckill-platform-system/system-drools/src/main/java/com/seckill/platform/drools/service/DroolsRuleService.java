package com.seckill.platform.drools.service;


import com.seckill.platform.drools.domain.DroolsRule;

import java.util.List;

/**
 * Todo
 *
 * @Author wys
 * @Date 2023/4/24 14:00
 */
public interface DroolsRuleService {

    List<DroolsRule> findAll();
    void addDroolsRule(DroolsRule droolsRule);

    void updateDroolsRule(DroolsRule droolsRule);

    void deleteDroolsRule(Long ruleId, String ruleName);
}
