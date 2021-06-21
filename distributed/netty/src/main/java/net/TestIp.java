package net;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import org.jboss.netty.handler.ipfilter.IpSubnet;
import org.jboss.netty.handler.ipfilter.IpV4Subnet;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

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
 *            https://blog.csdn.net/dilijia/article/details/82896563
 * 计算100.0.0.18/28对应的网络地址、广播地址和可分配IP地址范围。
 *
 *     将100.0.0.18和28位掩码换算为2进制做与进算，再将结果换算为十进制，即可得到网络地址为：100.0.0.16
 *
 *         100.0.0.18=01100100.00000000.00000000.00010010
 *
 *         01100100.00000000.00000000.00010010 & 11111111.11111111.11111111.11110000
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

    public static void main(String[] args) throws UnknownHostException {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("100.0.0.18", 28, IpFilterRuleType.ACCEPT);

        boolean result = rule.matches(new InetSocketAddress("100.0.0.15", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.16", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.17", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.31", 8080));
        System.out.println(result);

        result = rule.matches(new InetSocketAddress("100.0.0.32", 8080));
        System.out.println(result);

        IpV4Subnet ipV4Subnet = new IpV4Subnet("100.0.0.16/28");
        result = ipV4Subnet.contains("100.0.0.17");
        System.out.println(result);

        IpSubnet ipSubnet = new IpSubnet("100.0.0.16/28");
        result = ipSubnet.contains("100.0.0.17");
        System.out.println(result);
    }
}
