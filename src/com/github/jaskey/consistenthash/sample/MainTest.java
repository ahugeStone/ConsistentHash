package com.github.jaskey.consistenthash.sample;

import com.github.jaskey.consistenthash.ConsistentHashRouter;

import java.util.*;

/**
 * 测试类
 *
 * @author ahuang
 * @version V1.0
 * @Title: MainTest
 * @Program: ConsistentHash
 * @Package com.github.jaskey.consistenthash.sample
 * @create 2019-02-24 19:20
 */
public class MainTest {

    public static void main(String[] args) {
        // 5组dubbo服务
        DubboGroupNode g1 = new DubboGroupNode("group1");
        DubboGroupNode g2 = new DubboGroupNode("group2");
        DubboGroupNode g3 = new DubboGroupNode("group3");
        DubboGroupNode g4 = new DubboGroupNode("group4");
        DubboGroupNode g5 = new DubboGroupNode("group5");

        ConsistentHashRouter<DubboGroupNode> testRouter =
                new ConsistentHashRouter(Arrays.asList(g1, g2, g3, g4, g5), 160);

        List<String> argmentList = new ArrayList<>();
        for(int i=0; i < 10000; i++) {
            argmentList.add(String.valueOf(10000 + i));
        }
        // 5个dubbo节点
        testArgmentList(argmentList, testRouter);
        // 6个
        DubboGroupNode g6 = new DubboGroupNode("group6");
        testRouter.addNode(g6);
        testArgmentList(argmentList, testRouter);
        testRouter.removeNode(g6);
        testArgmentList(argmentList, testRouter);
    }

    public static void testArgmentList(List<String> argmentList, ConsistentHashRouter consistentHashRouter) {
        SortedMap<String, Integer> resultMap = new TreeMap<>();

        for(String argment : argmentList) {
            String routeNode = consistentHashRouter.routeNode(argment).toString();
//            System.out.println("argment " + argment + " ---> " + routeNode);

            Integer num = resultMap.get(routeNode);
            if(null == num) {
                num = 0;
            }
            num++;
            resultMap.put(routeNode, num);
        }
        System.out.println(resultMap);
    }
}
