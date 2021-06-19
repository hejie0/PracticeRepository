package net;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;

import java.net.InetSocketAddress;

/**
 * @Author: hejie
 * @Date: 2021/6/18 0:05
 * @Version: 1.0
 */

/***
 * 子网前缀28==11111111.11111111.11111111.11110000==255.255.255.240
 *
 *
 * IP子网示例: https://blog.csdn.net/weixin_34293246/article/details/92955514
 * 计算100.0.0.16/28对应的网络地址、广播地址和可分配IP地址范围。
 *
 *     将100.0.0.16和28位掩码换算为2进制做与进算，再将结果换算为十进制，即可得到网络地址为：100.0.0.16
 *
 *         100.0.0.16=01100100.00000000.00000000.00010000
 *
 *         01100100.00000000.00000000.00010000 & 11111111.11111111.11111111.11110000
 *
 *         =01100100.00000000.00000000.00010000=100.0.0.16
 *
 *     因为掩码为28位，所以主机位为4位，主机地址范围为0000-1111，广播地址为主机位全为1的地址：100.0.0.31
 *
 *         01100100.00000000.00000000.00011111=100.0.0.31
 *
 *     可分配IP地址为主机址范围减去主机位为全0的网络地址和主机位为全1的广播地址，即可分配IP地址范围为：
 *
 *         100.0.0.17-100.0.0.30
 *
 *         01100100.00000000.00000000.00010001-01100100.00000000.00000000.00011110
 */
public class TestIp {

    public static void main(String[] args) {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("100.0.0.16", 28, IpFilterRuleType.ACCEPT);

        boolean result = rule.matches(new InetSocketAddress("100.0.0.15", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.16", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.31", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.32", 8080));
        System.out.println(result);
    }
}
