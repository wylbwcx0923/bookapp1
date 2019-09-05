package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.CanalMapper;
import com.jxtc.bookapp.mapper.app.CanalPopularizeCountMapper;
import com.jxtc.bookapp.mapper.app.OrderMapper;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import com.jxtc.bookapp.service.CanalPopularizeService;
import com.jxtc.bookapp.service.EquipmentService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "canalPopularizeService")
public class CanalPopularizeServiceImpl implements CanalPopularizeService {

    @Autowired
    private CanalMapper canalMapper;
    @Autowired
    private CanalPopularizeCountMapper canalPopularizeCountMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private EquipmentService equipmentService;

    @Override
    public void addCanal(Canal canal) {
        canalMapper.insert(canal);
        Canal canalNew = canalMapper.selectNewCanal();
        CanalPopularizeCount count = new CanalPopularizeCount();
        count.setCanalName(canal.getNounName());
        count.setNounId(canalNew.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        count.setOneDay(sdf.format(new Date()));
        canalPopularizeCountMapper.insertSelective(count);
    }

    @Override
    public void updateCanal(Canal canal) {
        canalMapper.updateByPrimaryKeySelective(canal);
    }

    @Override
    public List<Canal> getCanalList() {
        CanalExample example = new CanalExample();
        example.createCriteria();
        return canalMapper.selectByExample(example);
    }

    @Override
    public void deleteCanal(int id) {
        canalMapper.deleteByPrimaryKey(id);
    }

    private String findCanalNameById(int id) {
        CanalExample example = new CanalExample();
        example.createCriteria().andIdEqualTo(id);
        List<Canal> canals = canalMapper.selectByExample(example);
        if (canals != null && canals.size() > 0) {
            return canals.get(0).getNounName();
        }
        return "其他";
    }


    @Override
    public PageResult<CanalPopularizeCount> getCanalPopularizeCountList(int pageIndex, int pageSize, Integer canalId, String startTime, String endTime) {
        PageResult<CanalPopularizeCount> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        Integer total = canalPopularizeCountMapper.countCanalPopularize(canalId, startTime, endTime);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        List<CanalPopularizeCount> canalPopularizeCounts = canalPopularizeCountMapper.selectCanalPopularizeList(canalId, startTime, endTime, offset, pageSize);
        if (canalPopularizeCounts != null && canalPopularizeCounts.size() > 0) {
            for (CanalPopularizeCount count : canalPopularizeCounts) {
                int downCount = equipmentService.getEquipmentCountByCanalIdAndCreateTime(count.getNounId(), count.getOneDay());
                count.setDownCount(downCount);//下载数
                count.setCanalName(findCanalNameById(count.getNounId()));
                OrderCount orderCount = orderMapper.countOrdersByCanalId(count.getNounId(), count.getOneDay());
                count.setPayCount(orderCount.getCountOrder());//支付订单数
                count.setPayMoney(orderCount.getAmount());//支付订单总金额
                int registerCount = userInfoMapper.countDownloadByCanalIdAndCreateTime(count.getNounId(), count.getOneDay());
                count.setRegisterCount(registerCount);//注册数
                //保留两位小数
                if (registerCount != 0) {
                    String downOrderProportion = numberFormat.format((double) orderCount.getCountOrder() / (double) registerCount * 100) + "%";
                    count.setDownOrderProportion(downOrderProportion);//下单比例
                } else {
                    count.setDownOrderProportion("0%");//下单比例
                }
                //回本
                Double cose = count.getCost();
                if (cose != null && cose > 0) {
                    //回本金额
                    double recoverMoney = orderCount.getAmount() - cose;
                    count.setRecoverMoney(recoverMoney);//回本金额
                    //回本比例
                    String recoverProportion = numberFormat.format(orderCount.getAmount() / cose * 100) + "%";
                    if (orderCount.getAmount() / cose >= 1) {
                        count.setRecoverProportion("100%");
                    }
                    count.setRecoverProportion(recoverProportion);
                } else {
                    count.setRecoverMoney((double) 0);//回本金额
                    count.setRecoverProportion("0%");
                }

            }
        }
        pageResult.setPageList(canalPopularizeCounts);
        return pageResult;
    }

    @Override
    public void setCanalPopularizeCountRemarkOrCose(String remark, int id, Double cose) {
        CanalPopularizeCount count = new CanalPopularizeCount();
        count.setId(id);
        count.setCost(cose);
        count.setRemark(remark);
        canalPopularizeCountMapper.updateByPrimaryKeySelective(count);
    }

    @Override
    public Map<String, Object> getDifferenceCanalUserCounts() {
        Map<String, Object> map = new HashMap<>();
        List<Canal> canals = getCanalList();
        if (canals != null && canals.size() > 0) {
            for (Canal canal : canals) {
                map.put(canal.getNounName(), userInfoMapper.countDownloadByCanalIdAndCreateTime(canal.getId(), null));
            }
        }
        return map;
    }
}
