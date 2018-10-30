/*
 * Copyright (c) 2018  STMicroelectronics – All rights reserved
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
package com.st.SensNet.net6LoWPAN.nodeStatus;

import android.support.annotation.NonNull;

import com.st.SensNet.net6LoWPAN.SensorNode;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.features.NetworkAddress;

import java.util.Timer;
import java.util.TimerTask;

public class NodeSensorPresenter implements NodeStatusContract.Presenter {

    private static final long REFRESH_TIME_MS = 2000;
    private static final byte DEFAULT_DIMMING = 50;
    private NodeStatusContract.View mView;
    private Feature6LoWPANProtocol mBorderRouter;
    private NetworkAddress mNodeAddress;
    private boolean mLedStatusRequested=false;

    private Timer mRefreshDataTimer = new Timer();
    private TimerTask mRefreshDataTask = new TimerTask() {
        @Override
        public void run() {
            mBorderRouter.getNodeStatus(new SensorNode(mNodeAddress), new Feature6LoWPANProtocol.NodeStatusCallback() {
                @Override
                public void onNodeStatusUpdate(@NonNull SensorNode node) {
                    showData(node);
                    if(!mLedStatusRequested){
                        mBorderRouter.updateLedDimming(mNodeAddress, DEFAULT_DIMMING, new Feature6LoWPANProtocol.LedStatusChangeCallback() {
                            @Override
                            public void onStatusUpdated(NetworkAddress address, byte newValue) {
                                mView.showLedStatus(newValue);
                                mLedStatusRequested=true;
                            }

                            @Override
                            public void onRequestFail(NetworkAddress address) {
                                mLedStatusRequested=true;
                            }
                        });
                    }
                }

                @Override
                public void onRequestFail(@NonNull SensorNode node) {

                }
            });
        }
    };

    NodeSensorPresenter(NodeStatusContract.View view,Feature6LoWPANProtocol boarderRouter, NetworkAddress nodeAddress){
        mView = view;
        mBorderRouter = boarderRouter;
        mNodeAddress = nodeAddress;
    }

    private void showData(SensorNode node) {
        mView.showNodeAddress(node.getAddress());
        mView.showTemperature(node.getTemperature());
        mView.showHumidity(node.getHumidity());
        mView.showPressure(node.getPressure());
        mView.showAccelerometer(node.getAccX(),node.getAccY(),node.getAccZ());
        mView.showMagnetometer(node.getMagX(),node.getMagY(),node.getMagZ());
        mView.showGyroscope(node.getGyroX(),node.getGyroY(),node.getGyroZ());
    }

    @Override
    public void startRetrievingSensorData() {
        mRefreshDataTimer.scheduleAtFixedRate(mRefreshDataTask,0, REFRESH_TIME_MS);
    }

    @Override
    public void stopRetrievingSensorData() {
        mRefreshDataTimer.cancel();
    }

    @Override
    public void setDimmingStatus(byte dimmingValue) {
        mBorderRouter.updateLedDimming(mNodeAddress, dimmingValue, new Feature6LoWPANProtocol.LedStatusChangeCallback() {
            @Override
            public void onStatusUpdated(NetworkAddress address, byte newValue) {

            }

            @Override
            public void onRequestFail(NetworkAddress address) {

            }
        });
    }
}
