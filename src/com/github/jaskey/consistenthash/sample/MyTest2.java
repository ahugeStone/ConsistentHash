package com.github.jaskey.consistenthash.sample;

import com.github.jaskey.consistenthash.ConsistentHashRouterHolder;

/**
 * 2
 *
 * @author ahuang
 * @version V1.0
 * @Title: MyTest2
 * @Program: ConsistentHash
 * @Package com.github.jaskey.consistenthash.sample
 * @create 2019-02-24 22:41
 */
public class MyTest2 {

    public static void main(String[] args) {
        ConsistentHashRouterHolder holder = new ConsistentHashRouterHolder();
        holder.addGroup("test", "group1");
    }
}
