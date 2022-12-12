package com.ht.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.yygh.common.R;
import com.ht.yygh.config.MD5;
import com.ht.yygh.hosp.service.HospitalSetService;
import com.ht.yygh.model.hosp.HospitalSet;
import com.ht.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Random;
import java.util.List;

@RestController
@CrossOrigin //跨域
@Api(tags = "医院设置接口")
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

//查询所有医院设置
    @ApiOperation(value = "医院设置列表")
    @GetMapping("/findAll")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list",list);
    }

    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable Long id){
        hospitalSetService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "医院分页查询")
    @PostMapping("/findPageHospSet/{page}/{limit}")
    public R pageList(@PathVariable Integer page,@PathVariable Integer limit,@RequestBody HospitalSetQueryVo searchObj){
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (searchObj == null){
            queryWrapper = null;
        }else {
            String hosname = searchObj.getHosname();
            String hoscode = searchObj.getHoscode();
            if (!StringUtils.isEmpty(hosname)){
                queryWrapper.like("hosname",hosname);
            }
            if (!StringUtils.isEmpty(hoscode)){
                queryWrapper.like("hocode",hoscode);
            }
        }
        R r = new R();
        Page<HospitalSet> pageList = new Page<>(page,limit);
        hospitalSetService.page(pageList,queryWrapper);
        List<HospitalSet> records = pageList.getRecords();
        long total = pageList.getTotal();
        r.data("total",total);
        r.data("rows",records);
        return R.ok().data(r.getData());
    }

    public R save(@RequestBody HospitalSet hospitalSet){
        if (hospitalSet == null){
            return R.error();
        }
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setApiUrl(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean isSave = hospitalSetService.save(hospitalSet);
        return  isSave ? R.ok():R.error();
    }
}
