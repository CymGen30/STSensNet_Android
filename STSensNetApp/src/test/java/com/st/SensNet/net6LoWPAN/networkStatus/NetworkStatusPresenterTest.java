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

package com.st.SensNet.net6LoWPAN.networkStatus;

import com.st.SensNet.net6LoWPAN.SensorNode;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NetworkStatusPresenterTest {

    private static List<SensorNode> SENSOR_NODES= Arrays.asList(
            new SensorNode(new byte[]{0,0,0,0,0,0,0,1}),
            new SensorNode(new byte[]{0,0,0,0,0,0,0,2})
    );

    @Mock
    private Feature6LoWPANProtocol mFeature;

    @Mock
    private NetworkStatusContract.View mView;

    private NetworkStatusPresenter mPresenter;


    @Before
    public void init(){
        mPresenter = new NetworkStatusPresenter(mView,mFeature);
    }


    @Test
    public void toGetTheNodeListTheFeatureIsCalled(){
        mPresenter.getNodes();
        verify(mFeature).getNetworkNodeList(any(Feature6LoWPANProtocol.NetworkNodeListCallback.class));
    }


    @Test
    public void whenTheNodeListCallbackIsCalledTheViewIsUpdated(){
        mPresenter.getNodes();
        ArgumentCaptor<Feature6LoWPANProtocol.NetworkNodeListCallback> callback =
                ArgumentCaptor.forClass(Feature6LoWPANProtocol.NetworkNodeListCallback.class);
        verify(mFeature).getNetworkNodeList(callback.capture());

        callback.getValue().onComplete(SENSOR_NODES);
        verify(mView).showNodes(SENSOR_NODES);
    }

}