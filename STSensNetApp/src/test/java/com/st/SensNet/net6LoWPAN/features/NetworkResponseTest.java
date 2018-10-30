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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class NetworkResponseTest {

    private NetworkResponse mResp;

    @Before
    public void init(){
        mResp = new NetworkResponse();
    }

    @Test
    public void theByteAreAppendedToTheResponse(){
        mResp.append(new byte[]{1,2,3});
    }

    @Test(expected = IllegalStateException.class)
    public void ifNoEnoughtDataAreProvidedTheTimestampDoesntExist(){
        mResp.append(new byte[]{1});
        mResp.getTimestamp();
    }

    @Test
    public void if2BytesAreProvidedTheTimestampIsExtract(){
        mResp.append(new byte[]{0x01,0x02});
        short timestamp = mResp.getTimestamp();
        Assert.assertEquals(0x0102,timestamp);
    }

    @Test(expected = IllegalStateException.class)
    public void ifNoEnoughtDataAreProvidedTheCommandIdDoesntExist(){
        mResp.append(new byte[]{1,2,3});
        mResp.getCommandId();
    }

    @Test
    public void if4BytesAreProvidedTheCommandIdExtract(){
        mResp.append(new byte[]{0x01,0x02,0x03,0x04});
        short commandId = mResp.getCommandId();
        Assert.assertEquals(0x0403,commandId);
    }

    @Test(expected = IllegalStateException.class)
    public void ifNoEnoughtDataAreProvidedTheLengthIdDoesntExist(){
        mResp.append(new byte[]{1,2,3,4,5});
        mResp.getLength();
    }

    @Test
    public void if6BytesAreProvidedTheLengthIsExtract(){
        mResp.append(new byte[]{0x01,0x02,0x03,0x04,0x05,0x06});
        short length = mResp.getLength();
        Assert.assertEquals(0x0605,length);
    }

    @Test
    public void whenMoreByteThanTheLenghtAreRecevedTheResponseIsCompleted(){
        //message with length 10
        mResp.append(new byte[]{0x01,0x02,0x03,0x04,10,0x00});

        short length = mResp.getLength();
        Assert.assertEquals(10,length);

        Assert.assertFalse(mResp.isCompleted());
        mResp.append(new byte[4]);
        Assert.assertTrue(mResp.isCompleted());

        mResp.append(new byte[1]);

        Assert.assertTrue(mResp.isCompleted());
    }

    @Test
    public void ifTheHeaderIsNotCompleteTheRecevedIsNotCompleted(){
        //empty response
        Assert.assertFalse(mResp.isCompleted());
        mResp.append(new byte[]{0,0,0,0,6}); // 1 byte missing from the header
        Assert.assertFalse(mResp.isCompleted());

        mResp.append(new byte[]{0});  //header is complete, no payload
        Assert.assertTrue(mResp.isCompleted());
    }

    @Test(expected = IllegalStateException.class)
    public void ifTheResponseIsNotCompletedThePayloadIsNotReturned(){
        //message with length 10
        mResp.append(new byte[]{0x01,0x02,0x03,0x04,0x00,10});

        Assert.assertFalse(mResp.isCompleted());
        mResp.getPayload();
    }

    @Test
    public void whenTheMessageIsCompletedThePaylodIsReturned(){
        byte payload[] = new byte[]{0x05,0x06,0x07,0x08};
        mResp.append(new byte[]{0x01,0x02,0x03,0x04,(byte)(6+ payload.length),0});
        mResp.append(payload);

        Assert.assertTrue(Arrays.equals(payload,mResp.getPayload()));

    }

    @Test
    public void aResponseWithoutPayloadReturnAnEmptyArray(){
        mResp.append(new byte[]{0x01,0x02,0x03,0x04,6,0x00});

        Assert.assertTrue(mResp.getPayload().length==0);

    }

}