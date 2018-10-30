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
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Features.Field;
import com.st.BlueSTSDK.Node;
import com.st.SensNet.net6LoWPAN.SensorNode;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * class to interact with the 6LoWPAN boarder router using the BLE
 * this feature doesn't have fields since the communication is synchronous an it use callbacks
 */
public class Feature6LoWPANProtocol extends Feature {

    private static final int REQUEST_TIMEOUT_MS=2000;

    @VisibleForTesting
    public static final short GET_NODE_LIST_CMD_ID = 0x0030;

    @VisibleForTesting
    public static final short GET_NODE_STATUS_CMD_ID = 0x0032;

    @VisibleForTesting
    public static final short CHANGE_ACUTATOR_STATUS_CMD_ID = 0x0051;

    @VisibleForTesting
    public static final short GET_NETWORK_TOPOLOGY_CMD_ID = 0x0070;

    private static final String FEATURE_NAME = "6LoWPAN Network Bridge";

    private NetworkNodeListCallback mNodeListCallback;
    private PrivateNodeStatusCallback mNodeStatusCallback;
    private NetworkTopologyCallback mNodeTopologyCallback;
    private PrivateLedStatusCallback mNodeLedStatusCallback;


    /** object containing the current buffer for the current network response */
    private NetworkResponse mNetworkResponse=null;

    public Feature6LoWPANProtocol(Node n) {
        super(FEATURE_NAME, n, new Field[]{});
    }

    /**
     * call each time a new package arrive from the border router
     * @param timestamp data timestamp
     * @param data       array where read the data
     * @param dataOffset offset where start to read the data
     * @return all the byte will be used by this methond, the feature doesn't have fiels, so the
     * returned sample will be empty
     */
    @Override
    protected ExtractResult extractData(long timestamp, byte[] data, int dataOffset) {
        if(mNetworkResponse!=null) {
            mNetworkResponse.append(data);
            Log.d("extractData", Arrays.toString(data));
            if (mNetworkResponse.isCompleted()) {
                byte payload[] = mNetworkResponse.getPayload();
                short commandId = mNetworkResponse.getCommandId();
                mRequestTimeout.cancel();
                mRequestTimeout = null;
                mNetworkResponse=null;
                getParentNode().disableNotification(this);
                switch (commandId) {
                    case GET_NODE_LIST_CMD_ID:
                        List<SensorNode> nodes = GetSensorNodeListParser.parse(payload);
                        if(nodes!=null)
                            mNodeListCallback.onComplete(nodes);
                        else
                            mNodeTopologyCallback.onRequestFail();
                        mNodeListCallback = null;
                        break;
                    case GET_NODE_STATUS_CMD_ID:
                        if(mNodeStatusCallback!=null)
                            mNodeStatusCallback.onResponseIsReady(GetSensorNodeStatusParser.parse(payload));
                        mNodeStatusCallback = null;
                        break;
                    case GET_NETWORK_TOPOLOGY_CMD_ID:
                        NetworkTopology topology =GetNetworkTopologyParser.parse(payload);
                        if(topology!=null)
                            mNodeTopologyCallback.onNetworkTopologyIsReady(topology);
                        else
                            mNodeTopologyCallback.onRequestFail();

                        mNodeTopologyCallback = null;
                        break;
                    case CHANGE_ACUTATOR_STATUS_CMD_ID:
                        GetActuatorResponseParser.ActuatorResponse resp = GetActuatorResponseParser.parse(payload);
                        if(mNodeLedStatusCallback!=null)
                            mNodeLedStatusCallback.onResponseIsReady(resp);
                        mNodeLedStatusCallback = null;
                        break;
                    }

            }//is is completed
        }//if networkResponse!=null

        //consume all value without producing data, since it will fire the command response
        return new ExtractResult(new Sample(timestamp,new Number[]{},new Field[]{}),data.length);
    }


    private ResponseManager mRequestTimeout;

    /**
     * reuqest the list of all the network nodes
     * @param callback object where notify the command result
     */
    public boolean getNetworkNodeList(NetworkNodeListCallback callback) {
        Log.d("6lowpan","getNetworkNodeList: "+mRequestTimeout);

        if(mRequestTimeout !=null)
            return false;
        mNodeListCallback = callback;
        getParentNode().enableNotification(this);
        mNetworkResponse = new NetworkResponse();
        NetworkRequest cmd = new NetworkRequest.Builder()
                .withCommand(GET_NODE_LIST_CMD_ID)
                .build();
        mRequestTimeout = new ResponseManager(() -> {
            if(mNodeListCallback!=null) {
                mNodeListCallback.onRequestTimeOut();
                mNodeListCallback = null;
            }
        });
        writeData(cmd.getByte());
        return true;
    }


    public void abortRequest(){
        mNetworkResponse=null;
        mNodeStatusCallback=null;
        mNodeListCallback=null;
        if(mRequestTimeout !=null) {
            mRequestTimeout.cancel();
            getParentNode().disableNotification(this);
        }
    }

    /**
     * request all the data exported by a node
     * @param node network node to query
     * @param callback object where notify the response
     */
    public boolean getNodeStatus(SensorNode node, NodeStatusCallback callback){
        Log.d("6lowpan","getNodeStatus: "+mRequestTimeout);

        if(mRequestTimeout !=null)
            return false;
        mNodeStatusCallback = new PrivateNodeStatusCallback(node,callback);
        getParentNode().enableNotification(this);
        mNetworkResponse = new NetworkResponse();
        NetworkRequest cmd = new NetworkRequest.Builder()
                .withCommand(GET_NODE_STATUS_CMD_ID)
                .withPayload(node.getAddress().getBytes())
                .build();
        mRequestTimeout = new ResponseManager(() -> {
            if(mNodeListCallback!=null) {
                callback.onRequestFail(node);
                mNodeListCallback = null;
            }
        });
        writeData(cmd.getByte());
        return true;
    }

    /**
     * request the network topological information
     * @param callback object where notify the response
     */
    public boolean getNetworkTopology(NetworkTopologyCallback callback){
        Log.d("6lowpan","getNetworkTopology: "+mRequestTimeout);
        if(mRequestTimeout !=null)
            return false;
        mNodeTopologyCallback = callback;
        getParentNode().enableNotification(this);
        mNetworkResponse = new NetworkResponse();
        NetworkRequest cmd = new NetworkRequest.Builder()
                .withCommand(GET_NETWORK_TOPOLOGY_CMD_ID)
                .build();
        mRequestTimeout = new ResponseManager(() -> {
            if(mNodeListCallback!=null) {
                mNodeTopologyCallback.onRequestFail();
                mNodeTopologyCallback=null;
            }
        });
        writeData(cmd.getByte());
        return true;
    }


    /**
     * change the light dimming for a specific node
     * @param address node to change
     * @param dimmingValue new dimming value, 0 = off, 100 = on
     */
    public boolean updateLedDimming(NetworkAddress address,byte dimmingValue,LedStatusChangeCallback callback) {
        Log.d("6lowpan","update dimming: "+mRequestTimeout);
        if(mRequestTimeout !=null)
            return false;
        byte payload[] = new byte[10];

        System.arraycopy(address.getBytes(),0,payload,0,8);
        payload[8]=0x10;
        payload[9]=dimmingValue;

        mNodeLedStatusCallback = new PrivateLedStatusCallback(address,callback);
        getParentNode().enableNotification(this);
        mNetworkResponse = new NetworkResponse();
        NetworkRequest cmd = new NetworkRequest.Builder()
                .withCommand(CHANGE_ACUTATOR_STATUS_CMD_ID)
                .withPayload(payload)
                .build();
        mRequestTimeout = new ResponseManager(() -> {
            if(mNodeLedStatusCallback!=null) {
                callback.onRequestFail(address);
                mNodeLedStatusCallback=null;
            }
        });
        writeData(cmd.getByte());
        return true;
    }

    /**
     * send the current  sensor node dimming value to the remote node
     * @param node node to syncronize
     */
    public void updateLedDimming(SensorNode node,LedStatusChangeCallback callback) {
        updateLedDimming(node.getAddress(),node.getLedDimming(),callback);
    }

    /**
     * interface to implement to receive the node list
     */
    public interface NetworkNodeListCallback{
        /**
         * call when all the network nodes are received
         * @param nodes list of nodes, or null if the response was corrupted
         */
        void onComplete(@NonNull List<SensorNode> nodes);

        void onRequestTimeOut();
    }


    /**
     * interface to implement to update the new node sensor data
     */
    public interface NodeStatusCallback{
        /**
         * called when the data for the node are updated
         * @param node node with the new sensor values
         */
        void onNodeStatusUpdate(@NonNull SensorNode node);

        /**
         * call when the update request fail
         * @param node that fail to respond
         */
        void onRequestFail(@NonNull SensorNode node);
    }

    public interface NetworkTopologyCallback {
        /**
         * call when network topology is ready.
         * @param topology network topology
         */
        void onNetworkTopologyIsReady(@NonNull NetworkTopology topology);

        /**
         * call when a timeout occur or the data received are corrupted
         */
        void onRequestFail();
    }

    public interface LedStatusChangeCallback {
        void onStatusUpdated(NetworkAddress address,byte newValue);
        void onRequestFail(NetworkAddress address);
    }

    /**
     * internal call back used to check that the received data are from the request node
     */
    private static class PrivateNodeStatusCallback{

        private SensorNode mNode;
        private NodeStatusCallback mCallback;

        /**
         *
         * @param node node taht the user ask to update
         * @param callback user callback object
         */
        PrivateNodeStatusCallback(SensorNode node, NodeStatusCallback callback) {
            this.mNode = node;
            this.mCallback = callback;
        }

        /**
         * update the sensor node data with the value inside the SensorData object
         * @param data new data from the network, they will be copied inside the SensorNode
         */
        private void updateNodeValues(SensorData data){

            mNode.setTemperature(data.getTemperature());
            mNode.setPressure(data.getPressure());
            mNode.setHumidity(data.getHumidity());

            mNode.setAccX(data.getAccX());
            mNode.setAccY(data.getAccY());
            mNode.setAccZ(data.getAccZ());

            mNode.setMagX(data.getMagX());
            mNode.setMagY(data.getMagY());
            mNode.setMagZ(data.getMagZ());

            mNode.setGyroX(data.getGyroX());
            mNode.setGyroY(data.getGyroY());
            mNode.setGyroZ(data.getGyroZ());
        }

        /**
         * call when the response is ready
         * @param data object containing the sensor data received from the network
         */
        void onResponseIsReady(SensorData data){

            if(mNode.getAddress().getShortAddress() != data.getShortSensorNodeId()){
                //the answer is not the node of this callback.. something bad happen
                mCallback.onRequestFail(mNode);
                return;
            }

            updateNodeValues(data);

            mCallback.onNodeStatusUpdate(mNode);
        }
    }


    private static class PrivateLedStatusCallback {

        private NetworkAddress mAddress;
        private LedStatusChangeCallback mCallback;

        /**
         * @param node     node taht the user ask to update
         * @param callback user callback object
         */
        PrivateLedStatusCallback(NetworkAddress node, LedStatusChangeCallback callback) {
            this.mAddress = node;
            this.mCallback = callback;
        }

        /**
         * call when the response is ready
         *
         * @param data object containing the sensor data received from the network
         */
        void onResponseIsReady(@Nullable GetActuatorResponseParser.ActuatorResponse data) {

            if(data==null) {
                mCallback.onRequestFail(mAddress);
                return;
            }//else

            if (mAddress.getShortAddress() != data.shortNodeId &&
                    !mAddress.equals(NetworkAddress.BROADCAST_ADDRESS)) {
                //the answer is not the node of this callback.. something bad happen
                mCallback.onRequestFail(mAddress);
                return;
            }
            mCallback.onStatusUpdated(mAddress,data.actuatorStatus);
        }
    }

    private class ResponseManager{

        Timer mTimer = new Timer();
        TimerTask mOnTimeout;

        public ResponseManager(@NonNull Runnable onTimeout){
            mOnTimeout = new TimerTask() {
                @Override
                public void run() {
                    onTimeout.run();
                    mRequestTimeout=null;
                    mNetworkResponse=null;
                    getParentNode().disableNotification(Feature6LoWPANProtocol.this);
                }
            };
            mTimer.schedule(mOnTimeout, REQUEST_TIMEOUT_MS);
        }

        public void cancel(){
            mOnTimeout.cancel();
            mTimer.cancel();
            mTimer =null;
            mOnTimeout=null;
        }


    }

}
