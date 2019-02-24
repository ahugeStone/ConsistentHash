package com.github.jaskey.consistenthash;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 一致性hash存储
 *
 * @author ahuang
 * @version V1.0
 * @Title: ConsistentHashRouterHolder
 * @Program: ConsistentHash
 * @Package com.github.jaskey.consistenthash
 * @create 2019-02-24 21:43
 */
public class ConsistentHashRouterHolder {
    private final ConcurrentMap<String, ConsistentHashRouter<?>> routers = new ConcurrentHashMap<String, ConsistentHashRouter<?>>();
    private final ConcurrentMap<String, Set<String>> groupsList = new ConcurrentHashMap<String, Set<String>>();


    public ConsistentHashRouter<?> getRouter (String shardName) {
        Set<String> groups = groupsList.get(shardName);
        int identityHashCode = System.identityHashCode(groups);
        ConsistentHashRouter<?> router = routers.get(shardName);
        if(null == router || router.getIdentityHashCode() != identityHashCode) {
            router = new ConsistentHashRouter(groups, 320, identityHashCode);
            routers.put(shardName, router);
        }

        return router;
    }

    /**
     *
     * @param shardName
     * @param group
     */
    public void addGroup(String shardName, String group) {
        if(null == group) return;
        Set<String> groups = groupsList.get(shardName);
        if(null == groups) {
            groups = new CopyOnWriteArraySet<String>();
            groups.add(group);
            groupsList.put(shardName, groups);
        } else {
            groups.add(group);
        }
    }



}
