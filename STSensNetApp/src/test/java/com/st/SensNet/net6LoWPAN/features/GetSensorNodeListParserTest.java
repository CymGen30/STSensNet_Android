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

import com.st.SensNet.net6LoWPAN.SensorNode;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class GetSensorNodeListParserTest {


    private static final byte[] ADDRESS1 = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x7,0x08};
    private static final byte[] ADDRESS2 = new byte[]{0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0xF,0x10};

    @Test
    public void IfThePayloadHasWrongSizeNullIsReturned_payloadTooSmall(){
        Assert.assertNull(GetSensorNodeListParser.parse(new byte[]{1}));
    }

    @Test
    public void IfThePayloadHasWrongSizeNullIsReturned_payloadTooBig(){
        Assert.assertNull(GetSensorNodeListParser.parse(new byte[]{1,0,0,0,0,0,0,0,0,0}));
    }

    @Test
    public void parseASingleNodeNetwork(){
        byte payload[] = new byte[9];
        payload[0]=1;
        System.arraycopy(ADDRESS1,0,payload,1,ADDRESS1.length);

        List<SensorNode> nodes = GetSensorNodeListParser.parse(payload);
        Assert.assertEquals(1,nodes.size());
        Assert.assertTrue(Arrays.equals(ADDRESS1,nodes.get(0).getAddress().getBytes()));
    }

    @Test
    public void parseADoubleNodeNetwork(){
        byte payload[] = new byte[1+2*ADDRESS1.length];
        payload[0]=2;
        System.arraycopy(ADDRESS1,0,payload,1,ADDRESS1.length);
        System.arraycopy(ADDRESS2,0,payload,1+ADDRESS1.length,ADDRESS1.length);

        List<SensorNode> nodes = GetSensorNodeListParser.parse(payload);
        Assert.assertEquals(2,nodes.size());
        Assert.assertTrue(Arrays.equals(ADDRESS1,nodes.get(0).getAddress().getBytes()));
        Assert.assertTrue(Arrays.equals(ADDRESS2,nodes.get(1).getAddress().getBytes()));
    }

}