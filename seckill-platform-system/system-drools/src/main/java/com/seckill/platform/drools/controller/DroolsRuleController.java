package com.seckill.platform.drools.controller;

import com.seckill.platform.drools.manage.DroolsManager;
import com.seckill.platform.drools.domain.DroolsRule;
import com.seckill.platform.drools.service.DroolsRuleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Todo
 *
 * @Author wys
 * @Date 2023/4/24 14:02
 */
@RestController
@RequestMapping("/drools/rule")
public class DroolsRuleController {

    @Resource
    private DroolsRuleService droolsRuleService;
    @Resource
    private DroolsManager droolsManager;

    @GetMapping("findAll")
    public List<DroolsRule> findAll() {
        return droolsRuleService.findAll();
    }

    @PostMapping("add")
    public String addRule(@RequestBody DroolsRule droolsRule) {
        droolsRuleService.addDroolsRule(droolsRule);
        return "添加成功";
    }

    @PostMapping("update")
    public String updateRule(@RequestBody DroolsRule droolsRule) {
        droolsRuleService.updateDroolsRule(droolsRule);
        return "修改成功";
    }

    @PostMapping("deleteRule")
    public String deleteRule(Long ruleId, String ruleName) {
        droolsRuleService.deleteDroolsRule(ruleId, ruleName);
        return "删除成功";
    }

    @GetMapping("fireRule")
    public String fireRule(String kieBaseName, Integer param) {
        return droolsManager.fireRule(kieBaseName, param);
    }
}
