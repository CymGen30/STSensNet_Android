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

package com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.st.BlueSTSDK.gui.util.RepeatAnimator;
import com.st.SensNet.R;
import com.st.SensNet.netBle.RemoteNodeUtils.data.GenericRemoteNode;
import com.st.SensNet.netBle.features.GenericRemoteFeature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GenericRemoteNodeViewHolder extends EnvironmentalViewHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
    private static final String PROXIMITY_FORMAT ="%1$4d %2$s";
    private static final String MICLEVEL_FORMAT ="%1$4d %2$s";


    static public void onBindViewHolder(final GenericRemoteNodeViewHolder holder, GenericRemoteNode item) {
        EnvironmentalViewHolder.onBindViewHolder(holder,item);
        holder.updateNode(item);
        holder.setMotionDetection(item.getLastDetectMovement());
        holder.setProximity(item.getProximity(),item.isLowRangeProximity());
        holder.setMicLevel(item.getMicLevel());
    }

    public interface GenericRemoteNodeViewCallback extends  EnvironmentalNodeViewCallback{

        void onProximitySwitchChange(GenericRemoteNode node, boolean newStatus);
        void onMicLevelSwitchChange(GenericRemoteNode node, boolean newStatus);

    }

    private final ViewGroup mMotionLayout;
    private final TextView mMotionDetection;
    private Date mLastDisplayDate;
    private final RepeatAnimator mShakeMotionDetection;

    private final ViewGroup mProximityLayout;
    private final TextView mProximity;
    private final ProgressBar mProximityBar;
    private final CompoundButton mProximitySwitch;
    private final CompoundButton.OnCheckedChangeListener mProximityChangeListener;

    private final ViewGroup mMicLevelLayout;
    private final TextView mMicLevel;
    private final ProgressBar mMicLevelBar;

    private final CompoundButton mMicSwitch;
    private final CompoundButton.OnCheckedChangeListener mMicSwitchChangeListener;

    private GenericRemoteNode mNode;
    private final GenericRemoteNodeViewCallback mCallback;

    public static GenericRemoteNodeViewHolder build(ViewGroup parent, GenericRemoteNodeViewCallback callback){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.generic_node_item, parent, false);
        return new GenericRemoteNodeViewHolder(view,callback);
    }

    private static RepeatAnimator initShakeAnimation(View objectToShake){
        AnimatorSet shakeImage = (AnimatorSet) AnimatorInflater.loadAnimator(objectToShake.getContext(),
                R.animator.shake);
        shakeImage.setTarget(objectToShake);

        return new RepeatAnimator(shakeImage, 5);
    }

    private GenericRemoteNodeViewHolder(View view,GenericRemoteNodeViewCallback callback) {
        super(view,
                view.findViewById(R.id.remoteIdLabel),
                view.findViewById(R.id.temperatureValLabel),
                view.findViewById(R.id.pressureValLabel),
                view.findViewById(R.id.humidityValLabel),
                view.findViewById(R.id.ledIcon),
                view.findViewById(R.id.ledSwitch),
                view.findViewById(R.id.ledStatusLabel),
                callback);

        mCallback=callback;
        mMotionDetection = view.findViewById(R.id.motionDetectionStatusLabel);
        mMotionLayout = view.findViewById(R.id.motionDetectionLayout);
        mShakeMotionDetection = initShakeAnimation(mMotionLayout);
        mLastDisplayDate=null;

        mProximityLayout = view.findViewById(R.id.proximityLayout);
        mProximity = view.findViewById(R.id.proximityValue);
        mProximityBar = view.findViewById(R.id.proximityBar);
        mProximityBar.setMax(GenericRemoteFeature.LOW_RANGE_PROXIMITY_DATA_MAX);

        mProximitySwitch= view.findViewById(R.id.switchEnableDistance);
        mProximityChangeListener = (compoundButton, newStatus) -> mCallback.onProximitySwitchChange(mNode, newStatus);
        mProximitySwitch.setOnCheckedChangeListener(mProximityChangeListener);

        mMicLevelLayout = view.findViewById(R.id.micLevelLayout);
        mMicLevel = view.findViewById(R.id.micLevelValue);
        mMicLevelBar = view.findViewById(R.id.micLevelBar);
        mMicLevelBar.setMax((int) GenericRemoteFeature.MIC_LEVEL_DATA_MAX);
        mMicSwitch = view.findViewById(R.id.switchEnableMic);
        mMicSwitchChangeListener = (compoundButton, newStatus) -> mCallback.onMicLevelSwitchChange(mNode,newStatus);
        mMicSwitch.setOnCheckedChangeListener(mMicSwitchChangeListener);
    }


    private void setMotionDetection(@Nullable Date lastMovement){
        if(lastMovement==null){
            mMotionLayout.setVisibility(View.GONE);
            return;
        }//else

        //if we have already a display a date and is different from the one displayed
        if(mMotionLayout.getVisibility()==View.VISIBLE &&
                mLastDisplayDate!=null &&
                lastMovement!=mLastDisplayDate){
            if(!mShakeMotionDetection.isRunning())
                mShakeMotionDetection.start();
        }else {
            mMotionLayout.setVisibility(View.VISIBLE);
        }
        mLastDisplayDate=lastMovement;
        mMotionDetection.setText(mMotionDetection.getContext().getString(R.string.lastMotion,
                DATE_FORMAT.format(lastMovement)));
    }

    private void setProximity(int proximity, boolean lowRange) {
        if(proximity<0) {
            mProximityLayout.setVisibility(View.GONE);
            return;
        }//else

        mProximityLayout.setVisibility(View.VISIBLE);

        mProximitySwitch.setOnCheckedChangeListener(null);
        mProximitySwitch.setChecked(mNode.proximityIsEnabled());
        mProximitySwitch.setOnCheckedChangeListener(mProximityChangeListener);

        if(proximity != GenericRemoteFeature.PROXIMITY_OUT_OF_RANGE_VALUE) {
            mProximity.setText(
                    String.format(
                            Locale.getDefault(),
                            PROXIMITY_FORMAT, proximity,
                            GenericRemoteFeature.PROXIMITY_UNIT));
            if(lowRange) {
                mProximityBar.setProgress(proximity);
                mProximityBar.setVisibility(View.VISIBLE);
            }
        }else {
            mProximity.setText(R.string.proximity_out_of_range);
            mProximityBar.setVisibility(View.INVISIBLE);
        }//if-else
    }

    private void setMicLevel(short micLevel) {
        if(micLevel<0) {
            mMicLevelLayout.setVisibility(View.GONE);
            return;
        }//else

        mMicSwitch.setOnCheckedChangeListener(null);
        mMicSwitch.setChecked(mNode.micIsEnabled());
        mMicSwitch.setOnCheckedChangeListener(mMicSwitchChangeListener);

        mMicLevelLayout.setVisibility(View.VISIBLE);
        mMicLevel.setText(
                String.format(
                        Locale.getDefault(),
                        MICLEVEL_FORMAT, micLevel,
                        GenericRemoteFeature.MIC_LEVEL_UNIT));
        mMicLevelBar.setProgress(micLevel);
    }

    private void updateNode(GenericRemoteNode item) {
        if(mNode==null || mNode.getId()!=item.getId()) {
            mNode = item;
            mMicSwitch.setChecked(false);
            mProximitySwitch.setChecked(false);
        }


    }

}
