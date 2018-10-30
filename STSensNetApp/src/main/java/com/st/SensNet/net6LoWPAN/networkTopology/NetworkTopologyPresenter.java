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
package com.st.SensNet.net6LoWPAN.networkTopology;

import android.support.annotation.NonNull;

import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.features.NetworkAddress;
import com.st.SensNet.net6LoWPAN.features.NetworkTopology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NetworkTopologyPresenter implements NetworkTopologyContract.Presenter {

    private final NetworkTopologyContract.View mView;
    private final Feature6LoWPANProtocol mBoarderRouter;

    NetworkTopologyPresenter(NetworkTopologyContract.View mView, Feature6LoWPANProtocol mBoarderRouter) {
        this.mView = mView;
        this.mBoarderRouter = mBoarderRouter;
    }

    @Override
    public void requestNodeTopology() {
        mBoarderRouter.getNetworkTopology(new Feature6LoWPANProtocol.NetworkTopologyCallback() {
            @Override
            public void onNetworkTopologyIsReady(@NonNull NetworkTopology topology) {
                mView.showNodes(extractNodeData(topology.getNodes()));
            }

            @Override
            public void onRequestFail() {
                mView.onRequestFail();
            }
        });
    }


    private List<NetworkTopologyContract.NetworkNodeData> extractNodeData(Collection<NetworkTopology.NetworkNode> nodes){
        ArrayList<NetworkTopologyContract.NetworkNodeData> data = new ArrayList<>(nodes.size());

        for (NetworkTopology.NetworkNode node : nodes){
            List<NetworkAddress> routeToRoot = node.getRouteToRoot();
            ArrayList<String> routeToRootStr = new ArrayList<>(routeToRoot.size());
            for( int i = 0 ; i<routeToRoot.size() ; i++){
                routeToRootStr.add(routeToRoot.get(i).toString());
            }

            data.add(new NetworkTopologyContract.NetworkNodeData(
                    node.address.toString(),
                    routeToRootStr));
        }
        Collections.sort(data, (a, b) -> a.routeToRoot.size()-b.routeToRoot.size());
        return data;
    }
}
