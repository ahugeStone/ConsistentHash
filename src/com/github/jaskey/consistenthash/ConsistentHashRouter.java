/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jaskey.consistenthash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author linjunjie1103@gmail.com
 *
 * To hash Node objects to a hash ring with a certain amount of virtual node.
 * Method routeNode will return a Node instance which the object key should be allocated to according to consistent hash algorithm
 *
 * @param <T>
 */
public class ConsistentHashRouter<T extends Node> {
    /**
     * 保存虚拟节点的hash环，使用有序map保存虚拟节点，默认使用key排序
     */
    private final SortedMap<Long, VirtualNode<T>> ring = new TreeMap<>();
    /**
     * 使用的hash算法，默认MD5
     */
    private final HashFunction hashFunction;
    /**
     * 默认虚拟节点数
     */
    private int defaultVnode = 160;

    public int getIdentityHashCode() {
        return identityHashCode;
    }

    /**
     * hash指纹
     */
    private final int identityHashCode;

    /**
     * 使用默认md5算法的构造器
     * @param pNodes 物理节点的集合
     * @param vNodeCount 虚拟节点的数量
     */
    public ConsistentHashRouter(Collection<T> pNodes, int vNodeCount, int identityHashCode) {
        this(pNodes,vNodeCount,identityHashCode, new MD5Hash());
    }

    /**
     * 构造器
     * @param pNodes 物理节点的集合 collections of physical nodes
     * @param vNodeCount 虚拟节点的数量 amounts of virtual nodes
     * @param hashFunction hash算法 hash Function to hash Node instances
     */
    public ConsistentHashRouter(Collection<T> pNodes, int vNodeCount, int identityHashCode, HashFunction hashFunction) {
        if (hashFunction == null) {
            throw new NullPointerException("Hash Function is null");
        }
        this.identityHashCode = identityHashCode;
        this.hashFunction = hashFunction;
        if (pNodes != null) {
            for (T pNode : pNodes) {
                // 添加物理节点
                addNode(pNode, vNodeCount);
            }
        }
    }

    /**
     * add physic node to the hash ring with some virtual nodes
     * 添加物理节点到hash环
     * @param pNode 要添加的物理节点 physical node needs added to hash ring
     * @param vNodeCount 虚拟节点数量，需要大于等于0 the number of virtual node of the physical node. Value should be greater than or equals to 0
     */
    public void addNode(T pNode, int vNodeCount) {
        // 虚拟节点数不能小于0
        if (vNodeCount < 0) throw new IllegalArgumentException("illegal virtual node counts :" + vNodeCount);
        // 获取该物理节点对应hash环中虚拟节点的数量
        int existingReplicas = getExistingReplicas(pNode);
        // 依次添加 vNodeCount 个虚拟节点到hash环中
        for (int i = 0; i < vNodeCount; i++) {
            // 虚拟节点编号，每添加一个该物理节点的虚拟节点，该编号顺序递增一次
            VirtualNode<T> vNode = new VirtualNode<>(pNode, i + existingReplicas);
            // 虚拟节点key格式为物理节点key_虚拟节点编号
            ring.put(hashFunction.hash(vNode.getKey()), vNode);
        }
    }

    public void addNode(T pNode) {
        this.addNode(pNode, defaultVnode);
    }

    /**
     * remove the physical node from the hash ring
     * 从hash环中移除某个物理节点对应的所有虚拟节点
     * @param pNode
     */
    public void removeNode(T pNode) {
        Iterator<Long> it = ring.keySet().iterator();
        while (it.hasNext()) {
            Long key = it.next();
            VirtualNode<T> virtualNode = ring.get(key);
            // 移除hash环中该物理节点对应的所有虚拟节点
            if (virtualNode.isVirtualNodeOf(pNode)) {
                it.remove();
            }
        }
    }

    /**
     * with a specified key, route the nearest Node instance in the current hash ring
     * 通过指定key值，使用hash环路由到最近的虚拟节点实例
     * @param objectKey the object key to find a nearest Node
     * @return 返回该key值对应到的物理节点
     */
    public T routeNode(String objectKey) {
        if (ring.isEmpty()) {
            return null;
        }
        Long hashVal = hashFunction.hash(objectKey);
        SortedMap<Long,VirtualNode<T>> tailMap = ring.tailMap(hashVal);
        // 如果路由到的hash环的结尾，则需要返回环的首项
        Long nodeHashVal = !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
        return ring.get(nodeHashVal).getPhysicalNode();
    }

    /**
     * 获取hash环中物理节点pNode对应的虚拟节点数量
     * @param pNode 物理节点
     * @return 虚拟节点编号，如果该节点存在于hash环中，则返回的就是该节点在环中的起始位置，否则就是hash环中现存虚拟节点的数量
     */
    public int getExistingReplicas(T pNode) {
        int replicas = 0;
        // 遍历hash环中所有虚拟节点，依次判断是否为传入物理节点的虚拟节点
        // 如果是，虚拟节点数量+1
        for (VirtualNode<T> vNode : ring.values()) {
            if (vNode.isVirtualNodeOf(pNode)) {
                replicas++;
            }
        }
        return replicas;
    }

    
    //default hash function

    /**
     * 默认的hash算法-md5实现
     */
    private static class MD5Hash implements HashFunction {
        MessageDigest instance;

        public MD5Hash() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
            }
        }

        @Override
        public long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();

            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }
    }

    public void setDefaultVnode(Integer defaultVnode) {
        this.defaultVnode = defaultVnode;
    }

}
