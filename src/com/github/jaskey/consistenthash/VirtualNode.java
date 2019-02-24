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

/**
 * @author linjunjie1103@gmail.com
 * @param <T>
 */
public class VirtualNode<T extends Node> implements Node {
    /**
     * 表示的物理节点
     */
    final T physicalNode;
    /**
     * 虚拟节点编号，用以区分同一真实节点的不同虚拟节点
     */
    final int replicaIndex;

    /**
     * 虚拟节点构造器
     * @param physicalNode 对应的物理节点
     * @param replicaIndex 虚拟节点编号，每添加一个该物理节点的虚拟节点，该编号顺序递增一次
     */
    public VirtualNode(T physicalNode, int replicaIndex) {
        this.replicaIndex = replicaIndex;
        this.physicalNode = physicalNode;
    }

    /**
     * 虚拟节点的key为物理节点key_虚拟节点编号
     * @return
     */
    @Override
    public String getKey() {
        return physicalNode.getKey() + "-" + replicaIndex;
    }

    /**
     * 判断是否为某一物理节点（通过key值）
     * @param pNode 要判断的物理节点
     * @return
     */
    public boolean isVirtualNodeOf(T pNode) {
        return physicalNode.getKey().equals(pNode.getKey());
    }

    /**
     * 获得虚拟节点代表的物理节点
     * @return
     */
    public T getPhysicalNode() {
        return physicalNode;
    }
}
