package com.st.SensNet.net6LoWPAN.features;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

import static com.st.SensNet.net6LoWPAN.features.NetworkTopology.*;
import static org.mockito.Mockito.mock;

public class NetworkNodeTest {

    private static NetworkAddress addressOne = new NetworkAddress(new byte[]{
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01});

    private static NetworkAddress addressTwo = new NetworkAddress(new byte[]{
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02});

    private static NetworkAddress addressThree = new NetworkAddress(new byte[]{
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02});

    @Test
    public void aNodeWithoutParentIsTheRoot(){
        Assert.assertTrue(new NetworkNode(addressOne).isRoot());
    }

    @Test
    public void theChildNodeHasTheRootAsParent(){
        NetworkNode root = new NetworkNode(addressOne);
        NetworkNode child = new NetworkNode(addressTwo);
        root.addChild(child);
        Assert.assertEquals(child.getParent(),root);
        Assert.assertFalse(child.isRoot());
    }

    @Test
    public void rootHasHope0(){
        NetworkNode root = new NetworkNode(addressOne);
        Assert.assertEquals(0,root.hopeToRoot());
    }

    @Test
    public void hopeIsTheDistanceFromRoot(){
        NetworkNode root = new NetworkNode(addressOne);
        NetworkNode fistLevelChild = new NetworkNode(addressTwo);
        NetworkNode secondLevelChild = new NetworkNode(addressThree);
        root.addChild(fistLevelChild);
        fistLevelChild.addChild(secondLevelChild);
        Assert.assertEquals(0,root.hopeToRoot());
        Assert.assertEquals(1,fistLevelChild.hopeToRoot());
        Assert.assertEquals(2,secondLevelChild.hopeToRoot());
    }

    @Test
    public void routeRoot(){
        NetworkNode root = new NetworkNode(addressOne);
        NetworkNode fistLevelChild = new NetworkNode(addressTwo);
        NetworkNode secondLevelChild = new NetworkNode(addressThree);
        root.addChild(fistLevelChild);
        fistLevelChild.addChild(secondLevelChild);
        List<NetworkAddress> routeRootToRoot = root.getRouteToRoot();
        List<NetworkAddress> routeFirstLevelToRoot = fistLevelChild.getRouteToRoot();
        List<NetworkAddress> routeSecondLevelToRoot = secondLevelChild.getRouteToRoot();
        Assert.assertEquals(0,routeRootToRoot.size());

        Assert.assertEquals(1,routeFirstLevelToRoot.size());
        Assert.assertTrue(routeFirstLevelToRoot.contains(root.address));

        Assert.assertEquals(2,routeSecondLevelToRoot.size());
        Assert.assertTrue(routeSecondLevelToRoot.contains(root.address));
        Assert.assertTrue(routeSecondLevelToRoot.contains(fistLevelChild.address));
    }

}