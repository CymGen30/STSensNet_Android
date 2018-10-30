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

package com.st.SensNet.netBle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.gui.demos.DemoDescriptionAnnotation;
import com.st.SensNet.R;
import com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder.GenericRemoteNodeViewHolder;
import com.st.SensNet.netBle.RemoteNodeUtils.data.EnvironmentalRemoteNode;
import com.st.SensNet.netBle.RemoteNodeUtils.data.GenericRemoteNode;
import com.st.SensNet.netBle.RemoteNodeUtils.GenericRemoteNodeRecyclerViewAdapter;
import com.st.SensNet.netBle.RemoteNodeUtils.anim.FadeInLeftAnimator;
import com.st.SensNet.netBle.features.CommandFeature;
import com.st.SensNet.netBle.features.GenericRemoteFeature;
import com.st.SensNet.netBle.util.ParcelableSparseArrayGenericNode;


/**
 * Demo that display the environmental data from a remote node
 */
@DemoDescriptionAnnotation(name="Remote Nodes",
        iconRes=R.drawable.demo_enviromental_icon,
        requareOneOf = {GenericRemoteFeature.class})
public class GenericRemoteNodeFragment extends DemoFragmentWithCommand implements
        GenericRemoteNodeViewHolder.GenericRemoteNodeViewCallback {

    private static final String DISCOVERED_NODE = GenericRemoteNodeFragment.class.getName()+".DISCOVERED_NODE";

    /**
     * collection of remoteNodes
     */
    private ParcelableSparseArrayGenericNode mRemoteNodeDatas= new ParcelableSparseArrayGenericNode() ;
    /**
     * adapter used for display the nodes info
     */
    private GenericRemoteNodeRecyclerViewAdapter mAdapter;

    /**
     * remote feature where read the data
     */
    private GenericRemoteFeature mRemoteFeature;
    private Integer mMicLevelEnabledId;
    private Integer mProximityEnabledId;

    /**
     * update the data received by the remote node
     * @param node node to update
     * @param sample data received by the node
     */
    private void updateNode(GenericRemoteNode node, Feature.Sample sample){
            node.setTemperature(GenericRemoteFeature.getTemperature(sample));
            node.setHumidity(GenericRemoteFeature.getHumidity(sample));
            node.setPressure(GenericRemoteFeature.getPressure(sample));
            node.setLed(GenericRemoteFeature.getLedStatus(sample));
            node.setProximity(GenericRemoteFeature.getProximity(sample),GenericRemoteFeature.isLowRangeProximity(sample));
            node.setDetectMovement(GenericRemoteFeature.getLastMotionDetection(sample));
            node.setMicLevel(GenericRemoteFeature.getMicLevel(sample));
            node.setLux(GenericRemoteFeature.getLuminosity(sample));
            node.setUnknownData(GenericRemoteFeature.getUnknownData(sample));
    }//updateNode

    /**
     * Update the node data and trigger an gui update
     */
    private Feature.FeatureListener mGenericRemoteFeatureListener = new Feature.FeatureListener() {
        @Override
        public void onUpdate(Feature f, Feature.Sample sample) {
            final int id = GenericRemoteFeature.getNodeId(sample);

            final int index = mRemoteNodeDatas.indexOfKey(id);
            final GenericRemoteNode node = index>=0 ? mRemoteNodeDatas.valueAt(index) :
                    new GenericRemoteNode(id);

            updateNode(node,sample);

            //run
            GenericRemoteNodeFragment.this.updateGui(() -> {
                if (index >= 0) {
                    mAdapter.notifyItemChanged(index);
                } else {
                    //add an notify on the main thread otherwise there are an possible
                    // inconsistency, if a notify an inserted in an old position, because
                    // outside the main thread there were a new insertion
                    mRemoteNodeDatas.append(id,node);
                    mAdapter.notifyItemInserted(mRemoteNodeDatas.indexOfKey(id));
                }//if
            });//updateGui
        }//onUpdate
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GenericRemoteNodeFragment() {
    }

    @Override
    protected void enableNeededNotification(@NonNull Node node) {
        super.enableNeededNotification(node);
        mRemoteFeature = node.getFeature(GenericRemoteFeature.class);
        if(mRemoteFeature!=null){
            mRemoteFeature.addFeatureListener(mGenericRemoteFeatureListener);
            node.enableNotification(mRemoteFeature);
        }//if
    }

    @Override
    protected void disableNeedNotification(@NonNull Node node) {
        super.disableNeedNotification(node);
        if(mRemoteFeature!=null){
            node.disableNotification(mRemoteFeature);
            mRemoteFeature.removeFeatureListener(mGenericRemoteFeatureListener);
            disablePrevNotification();
        }//if
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mRemoteNodeDatas = savedInstanceState.getParcelable(DISCOVERED_NODE);
        }
        //keep the remote node status
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remotenode_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mAdapter = new GenericRemoteNodeRecyclerViewAdapter(this,mRemoteNodeDatas);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DISCOVERED_NODE,mRemoteNodeDatas);
    }

    /**
     * send the blestar_command for change the led status on the board
     * @param node node associate with the switch
     * @param newStatus new switch status
     */
    @Override
    public void onLedSwitchChange(EnvironmentalRemoteNode node, boolean newStatus) {
        if(mRemoteFeature!=null)
            mRemoteFeature.changeSwitchStatus(node.getId(),newStatus );
    }


    /** switch off all the switch for the notification */
    private void disablePrevNotification(){
        if(mProximityEnabledId!=null){
            GenericRemoteNode temp = mRemoteNodeDatas.get(mProximityEnabledId);
            changeProxNotification(temp,false);
        }
        if(mMicLevelEnabledId!=null){
            GenericRemoteNode temp = mRemoteNodeDatas.get(mMicLevelEnabledId);
            changeMicNotification(temp,false);
        }
    }

    /**change the notificaiton state, and update the gui as consequence */
    private void changeProxNotification(GenericRemoteNode node,boolean newState){
        final int nodeId = node.getId();
        mRemoteFeature.enableProximity(nodeId, newState);
        node.setProximityEnabled(newState);
        if(newState) {
            mProximityEnabledId = nodeId;
        }else
            mProximityEnabledId=null;

        updateGui(() -> mAdapter.notifyItemChanged(mRemoteNodeDatas.indexOfKey(nodeId)));
    }

    @Override
    public void onProximitySwitchChange(GenericRemoteNode node, boolean newStatus) {
        if(mRemoteFeature!=null) {
            if(newStatus) {
                if(mProximityEnabledId!=null && mProximityEnabledId==node.getId())
                    return;
                disablePrevNotification();
                changeProxNotification(node,true);
            }else {
                if(mProximityEnabledId==null || mProximityEnabledId!=node.getId())
                    return;
                changeProxNotification(node,false);
            }//if-else
        }//if feature!=null
    }

    /**change the notificaiton state, and update the gui as consequence */
    private void changeMicNotification(GenericRemoteNode node,boolean newState){
        final int nodeId  = node.getId();

        mRemoteFeature.enableMicLevel(nodeId, newState);
        node.setMicEnabled(newState);
        if(newState) {
            mMicLevelEnabledId = nodeId;
        }else
            mMicLevelEnabledId=null;

        updateGui(() -> mAdapter.notifyItemChanged(mRemoteNodeDatas.indexOfKey(nodeId)));
    }

    @Override
    public void onMicLevelSwitchChange(GenericRemoteNode node, boolean newStatus) {
        if(mRemoteFeature!=null) {
            if(newStatus) {
                if(mMicLevelEnabledId!=null && mMicLevelEnabledId==node.getId())
                    return;
                disablePrevNotification();
                changeMicNotification(node,true);
            }else {
                if(mMicLevelEnabledId==null || mMicLevelEnabledId!=node.getId())
                    return;
                changeMicNotification(node,false);
            }//if-else
        }//if feature!=null
    }

    /**
     * the notificaitons are stopped when the the scan is enabled
     * @param command feature updated
     */
    @Override
    public void onScanIsStarted(CommandFeature command) {
        super.onScanIsStarted(command);
        disablePrevNotification();
    }
}
