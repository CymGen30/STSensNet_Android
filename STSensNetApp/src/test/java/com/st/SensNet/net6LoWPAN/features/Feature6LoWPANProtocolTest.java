/*
 * Copyright (c) 2017  STMicroelectronics – All rights reserved
 * The STMicroelectronics corporate logo is a trademark of STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name nor trademarks of STMicroelectronics International N.V. nor any other
 *   STMicroelectronics company nor the names of its contributors may be used to endorse or
 *   promote products derived from this software without specific prior written permission.
 *
 * - All of the icons, pictures, logos and other images that are provided with the source code
 *   in a directory whose title begins with st_images may only be used for internal purposes and
 *   shall not be redistributed to any third party or modified in any way.
 *
 * - Any redistributions in binary form shall not include the capability to display any of the
 *   icons, pictures, logos and other images that are provided with the source code in a directory
 *   whose title begins with st_images.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.st.SensNet.net6LoWPAN.features;

import android.support.annotation.NonNull;

import com.st.BlueSTSDK.Node;
import com.st.SensNet.net6LoWPAN.SensorNode;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol.CHANGE_ACUTATOR_STATUS_CMD_ID;
import static com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol.GET_NODE_LIST_CMD_ID;
import static com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol.GET_NODE_STATUS_CMD_ID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Feature6LoWPANProtocolTest {


    private static class TestableFeature6LoWPANProtocol extends Feature6LoWPANProtocol{

        public TestableFeature6LoWPANProtocol(Node n) {
            super(n);
        }

        public byte[] getLastWriteData() {
            return lastWriteData;
        }

        private byte[] lastWriteData;

        public void receiveData(byte[] data){
            extractData(0,data,0);
        }

        @Override
        protected boolean writeData(@NonNull byte[] data){
            lastWriteData = data;
            return true;
        }
    }

    @Mock
    private Node mNode;

    @Mock
    private Feature6LoWPANProtocol.NetworkNodeListCallback mNodeListCallback;

    @Mock
    private Feature6LoWPANProtocol.NodeStatusCallback mNodeStatusCallback;

    private TestableFeature6LoWPANProtocol mFeature;

    @Before
    public void init(){
        mFeature = new TestableFeature6LoWPANProtocol(mNode);
    }

    @Test
    public void getNetworkNodeListSendCorrectMessage(){

        mFeature.getNetworkNodeList(mNodeListCallback);

        NetworkRequest cmd  = new NetworkRequest(mFeature.getLastWriteData());

        Assert.assertEquals(GET_NODE_LIST_CMD_ID,cmd.getCommandId());
        Assert.assertEquals(6,cmd.getLength());
    }

    @Test
    public void getNetworkNodeListEnableNotification(){
        mFeature.getNetworkNodeList(mNodeListCallback);
        verify(mNode).enableNotification(mFeature);
    }

    private static final byte GET_NODE_LIST_RESPONSE[] = new byte[]{0x00,0x00, // timestamp
            0x30,0x00, //command
            0x0F,0x00, //length 6 + 1 +8
            0x01, //1 node
            0x01,0x02,0x03,0x04,0x5,0x06,0x07,0x08 //address
    };

    @Test
    public void whenTheNodeListReceiveAllTheDataTheCallbackIsCalled(){

        mFeature.getNetworkNodeList(mNodeListCallback);
        mFeature.receiveData(GET_NODE_LIST_RESPONSE);

        ArgumentCaptor<List> parseResponseCapt = ArgumentCaptor.forClass(List.class);

        verify(mNodeListCallback).onComplete(parseResponseCapt.capture());

        List<SensorNode> nodes = (List<SensorNode>) parseResponseCapt.getValue();
        Assert.assertEquals(1, nodes.size());
        Assert.assertTrue(
                Arrays.equals(Arrays.copyOfRange(GET_NODE_LIST_RESPONSE,
                        GET_NODE_LIST_RESPONSE.length-8, GET_NODE_LIST_RESPONSE.length),
                nodes.get(0).getAddress().getBytes()));
    }

    @Test
    public void whenTheNodeReceiveAllTheDataDisableTheNotificaiton(){
        mFeature.getNetworkNodeList(mNodeListCallback);
        mFeature.receiveData(GET_NODE_LIST_RESPONSE);

        verify(mNode).disableNotification(mFeature);
    }

    @Test
    public void getNodeStatusListSendCorrectMessage(){
        NetworkAddress address = new NetworkAddress(new byte[]{1,2,3,4,5,6,7,9});

        SensorNode node = new SensorNode(address);

        mFeature.getNodeStatus(node,mNodeStatusCallback);

        NetworkRequest cmd  = new NetworkRequest(mFeature.getLastWriteData());

        Assert.assertEquals(GET_NODE_STATUS_CMD_ID,cmd.getCommandId());
        Assert.assertEquals(6+8,cmd.getLength());
        Assert.assertTrue(Arrays.equals(address.getBytes(),cmd.getPayload()));
    }

    private static final byte GET_NODE_STATUS_REPONSE[] = new byte[]{0x00,0x00, // timestamp
            0x32,0x00, //command
            0x28,0x00, //length 6 + 2 + 32
            0x66,0x77, //id
            16, 17, 34, //temp
            32, 51, 68, //pressure
            48, 85, 102, //humidity
            112, 119, //led dimming
            64, -103, -86, -69, -52, -35, -18, //acc
            80, -1, 0, 17, 34, 51, 68, //gyro
            96, 85, 102, 119, -120, -103, -86 //mag
    };

    private static void checkAllTheNodePropriertiesAreUpdated(SensorNode node){
        verify(node).setHumidity(anyShort());
        verify(node).setTemperature(anyShort());
        verify(node).setPressure(anyShort());

        verify(node).setAccX(anyShort());
        verify(node).setAccY(anyShort());
        verify(node).setAccZ(anyShort());

        verify(node).setGyroX(anyShort());
        verify(node).setGyroY(anyShort());
        verify(node).setGyroZ(anyShort());

        verify(node).setMagX(anyShort());
        verify(node).setMagY(anyShort());
        verify(node).setMagZ(anyShort());

    }

    @Test
    public void whenTheNodeStatusReceiveAllTheDataTheCallbackIsCalled(){
        NetworkAddress address = new NetworkAddress(new byte[]{1,2,3,4,5,6,0x66,0x77});

        SensorNode node = mock(SensorNode.class);
        when(node.getAddress()).thenReturn(address);

        mFeature.getNodeStatus(node,mNodeStatusCallback);
        mFeature.receiveData(GET_NODE_STATUS_REPONSE);

        verify(mNodeStatusCallback).onNodeStatusUpdate(eq(node));

        checkAllTheNodePropriertiesAreUpdated(node);
    }

    @Test
    public void whenTheNodeStatusReceiveIfTheNodeIdIsDifferentTheErrorCallbackIsCalled(){
        NetworkAddress address = new NetworkAddress(new byte[]{1,2,3,4,5,6,0x78,0x66});

        SensorNode node = mock(SensorNode.class);
        when(node.getAddress()).thenReturn(address);

        mFeature.getNodeStatus(node,mNodeStatusCallback);
        mFeature.receiveData(GET_NODE_STATUS_REPONSE);

        verify(mNodeStatusCallback).onRequestFail(eq(node));
    }


    @Test
    public void updateLedDimmingSendCorrectCommand(){
        byte address[] = new byte[]{1,2,3,4,5,6,7,9};
        byte ledDimmingValue = 50;

        byte requestPayload[] = new byte[]{
                1,2,3,4,5,6,7,9,
                0x10, //led
                ledDimmingValue
        };

        SensorNode node = new SensorNode(new NetworkAddress(address));
        node.setLedDimming(ledDimmingValue);

        mFeature.updateLedDimming(node);

        NetworkRequest cmd  = new NetworkRequest(mFeature.getLastWriteData());

        Assert.assertEquals(CHANGE_ACUTATOR_STATUS_CMD_ID,cmd.getCommandId());
        Assert.assertEquals(6+8+2,cmd.getLength());
        Assert.assertTrue(Arrays.equals(requestPayload,cmd.getPayload()));
    }

    @Test
    public void ifNoCommandIsSendTheReceivedDataAreIgnored(){
        //send some data and then see that the command is correctly managed
        mFeature.receiveData(new byte[10]);
        whenTheNodeListReceiveAllTheDataTheCallbackIsCalled();
    }

}