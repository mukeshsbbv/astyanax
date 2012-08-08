package com.netflix.astyanax.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.dht.BigIntegerToken;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.dht.RandomPartitioner;
import org.apache.cassandra.dht.Token;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Suppliers;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.TokenRange;
import com.netflix.astyanax.test.TestKeyspace;
import com.netflix.astyanax.test.TestTokenRange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: mkoch
 * Date: 5/23/12
 */
public class RingDescribeHostSupplierTest {
    private static final String NODE1 = "127.0.0.1";
    private static final String NODE2 = "127.0.0.2";
    private static final String NODE3 = "127.0.0.3";
    private static final String RANGE_1_END_TOKEN = "0";
    private static final String RANGE_2_END_TOKEN = "2000";
    private static final String RANGE_3_END_TOKEN = "4000";

    private RingDescribeHostSupplier hostSupplier;

    @Before
    public void setUp() throws Exception {
        TestKeyspace keyspace = new TestKeyspace("ringDescribeTestKeyspace");
        keyspace.setTokenRange(createTokenRange());
        hostSupplier = new RingDescribeHostSupplier(keyspace, 1234,
                Suppliers.<IPartitioner>ofInstance(new RandomPartitioner()));
    }


    @Test
    public void testGet() throws Exception {
        Map<Token,List<Host>> hostMap = hostSupplier.get();
        assertNotNull(hostMap);
        assertEquals(3, hostMap.size());

        List<Host> endpoints = hostMap.get(new BigIntegerToken(RANGE_1_END_TOKEN));
        assertEquals(1,endpoints.size());
        assertEquals(NODE1, endpoints.get(0).getIpAddress());

        endpoints = hostMap.get(new BigIntegerToken(RANGE_2_END_TOKEN));
        assertEquals(1,endpoints.size());
        assertEquals(NODE2, endpoints.get(0).getIpAddress());

        endpoints = hostMap.get(new BigIntegerToken(RANGE_3_END_TOKEN));
        assertEquals(1,endpoints.size());
        assertEquals(NODE3,endpoints.get(0).getIpAddress());
    }

    private List<TokenRange> createTokenRange() {
           List<TokenRange> tokenRanges = new ArrayList<TokenRange>();
           TokenRange node1Range = new TestTokenRange(RANGE_3_END_TOKEN, RANGE_1_END_TOKEN, Arrays.asList(NODE1));
           TokenRange node2Range = new TestTokenRange(RANGE_1_END_TOKEN, RANGE_2_END_TOKEN, Arrays.asList(NODE2));
           TokenRange node3Range = new TestTokenRange(RANGE_2_END_TOKEN, RANGE_3_END_TOKEN, Arrays.asList(NODE3));
           tokenRanges.addAll(Arrays.asList(node1Range, node2Range, node3Range));
           return tokenRanges;
       }

}
