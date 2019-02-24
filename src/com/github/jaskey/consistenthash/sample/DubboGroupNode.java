package com.github.jaskey.consistenthash.sample;

import com.github.jaskey.consistenthash.Node;

/**
 * dubbo分组节点
 *
 * @author ahuang
 * @version V1.0
 * @Title: DubboGroupNode
 * @Program: ConsistentHash
 * @Package com.github.jaskey.consistenthash.sample
 * @create 2019-02-24 19:04
 */
public class DubboGroupNode implements Node {

    /**
     * dubbo服务的分组名
     */
    private String groupId;

    public DubboGroupNode(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public String getKey() {
        return groupId;
    }
}
