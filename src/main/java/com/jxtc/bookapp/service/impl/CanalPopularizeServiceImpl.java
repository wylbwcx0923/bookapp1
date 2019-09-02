package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.CanalMapper;
import com.jxtc.bookapp.mapper.app.CanalPopularizeCountMapper;
import com.jxtc.bookapp.mapper.app.OrderMapper;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import com.jxtc.bookapp.service.CanalPopularizeService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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

    @Override
    public void addCanal(Canal canal) {
        canalMapper.insert(canal);
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
        DecimalFormat df = new DecimalFormat("#.00");
        List<CanalPopularizeCount> canalPopularizeCounts = canalPopularizeCountMapper.selectCanalPopularizeList(canalId, startTime, endTime, offset, pageSize);
        if (canalPopularizeCounts != null && canalPopularizeCounts.size() > 0) {
            for (CanalPopularizeCount count : canalPopularizeCounts) {
                count.setCanalName(findCanalNameById(count.getNounId()));
                OrderCount orderCount = orderMapper.countOrdersByCanalId(count.getNounId(), count.getOneDay());
                count.setPayCount(orderCount.getCountOrder());//支付订单数
                count.setPayMoney(orderCount.getAmount());//支付订单总金额
                int downCount = userInfoMapper.countDownloadByCanalIdAndCreateTime(count.getNounId(), count.getOneDay());
                count.setDownCount(downCount);//下载数
                //保留两位小数
                if (downCount != 0) {
                    double downOrderProportion = orderCount.getCountOrder() / downCount;
                    count.setDownOrderProportion(Double.valueOf(df.format(downOrderProportion)));//下单比例
                } else {
                    count.setDownOrderProportion(0);//下单比例
                }
                //回本
                Double cose = count.getCost();
                if (cose != null && cose > 0) {
                    //回本金额
                    double reecoverMoney = orderCount.getAmount() - cose;
                    count.setRecoverMoney(reecoverMoney);//回本金额
                    //回本比例
                    double recoverProportion = reecoverMoney / cose;
                    count.setRecoverProportion(Double.valueOf(df.format(recoverProportion)));
                } else {
                    count.setRecoverMoney(0);//回本金额
                    count.setRecoverProportion(0);
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
