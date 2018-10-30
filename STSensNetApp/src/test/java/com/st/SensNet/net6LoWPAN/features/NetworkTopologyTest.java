package com.st.SensNet.net6LoWPAN.features;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkTopologyTest {

    private static NetworkAddress getAddress(int lastVaue){
        return new NetworkAddress( new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte) lastVaue});
    }

    @Test
    public void aNodeCanBeRetrivedFromTheAddress(){
        NetworkTopology topology = new NetworkTopology();
        NetworkAddress src = getAddress(0x00);
        NetworkAddress dest = getAddress(0x01);
        topology.addConnection(src,dest);

        NetworkTopology.NetworkNode node = topology.getNetworkNode(src);

        Assert.assertTrue(node.isRoot());
        Assert.assertEquals(1,node.getChildes().size());

    }

    @Test
    public void theRootIsReturnCorrectly(){
        NetworkTopology topology = new NetworkTopology();
        NetworkAddress src = getAddress(0x00);
        NetworkAddress dest = getAddress(0x01);
        NetworkAddress dest1 = getAddress(0x02);
        NetworkAddress dest2 = getAddress(0x03);

        topology.addConnection(src,dest1);
        topology.addConnection(dest1,dest2);
        topology.addConnection(src,dest);

        NetworkTopology.NetworkNode node = topology.getRoot();

        Assert.assertTrue(node.isRoot());
        Assert.assertEquals(src,node.address);

    }

}