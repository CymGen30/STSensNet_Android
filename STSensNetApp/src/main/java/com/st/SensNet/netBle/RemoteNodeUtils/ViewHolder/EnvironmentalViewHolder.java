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

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.st.BlueSTSDK.Features.FeatureLuminosity;
import com.st.BlueSTSDK.Features.remote.RemoteFeatureHumidity;
import com.st.BlueSTSDK.Features.remote.RemoteFeaturePressure;
import com.st.BlueSTSDK.Features.remote.RemoteFeatureTemperature;
import com.st.SensNet.R;
import com.st.SensNet.netBle.RemoteNodeUtils.data.EnvironmentalRemoteNode;

import java.util.Locale;

public class EnvironmentalViewHolder extends RemoteNodeViewHolder {

    private static final String TEMPERATURE_FORMAT ="%1$.2f %2$s";
    private static final String PRESSURE_FORMAT ="%1$.2f %2$s";
    private static final String HUMIDITY_FORMAT ="%1$.2f %2$s";
    private static final String LUX_FORMAT ="%1$d %2$s";

    static public void onBindViewHolder(final EnvironmentalViewHolder holder, EnvironmentalRemoteNode item) {
        RemoteNodeViewHolder.onBindViewHolder(holder,item);

        holder.updateNodeItem(item);
        holder.mTemperature.setText(
                String.format(
                        Locale.getDefault(),
                        TEMPERATURE_FORMAT,
                        item.getTemperature(),
                        RemoteFeatureTemperature.FEATURE_UNIT));

        holder.mPressure.setText(
                String.format(
                        Locale.getDefault(),
                        PRESSURE_FORMAT,
                        item.getPressure(),
                        RemoteFeaturePressure.FEATURE_UNIT));

        holder.mHumidity.setText(
                String.format(
                        Locale.getDefault(),
                        HUMIDITY_FORMAT,
                        item.getHumidity(),
                        RemoteFeatureHumidity.FEATURE_UNIT));

        if(item.hasLux())
            holder.setLux(item.getLux());
        else
            holder.setLedStatus(item.getLed());
    }

    /**
     * interface to implement for receive notification when the led switch change its status
     */
    public interface EnvironmentalNodeViewCallback {

        /**
         * call when the user change the switch status
         * @param node node associate with the switch
         * @param newStatus new switch status
         */
        void onLedSwitchChange(EnvironmentalRemoteNode node, boolean newStatus);
    }

    /**
     * current led state unknown, the class will set all the state
     */
    private static final byte NEXT_STATE_UNKNOWN=0;
    /**
     * a blestar_command for set the state on is send, the class is waiting an on state,
     * the class will update the state only if is it "on"
     */
    private static final byte NEXT_STATE_ON=1;

    /**
     * a blestar_command for set the state off is send, the class is waiting an on state,
     * the class will update the state only if is it "on"
     */
    private static final byte NEXT_STATE_OFF=-1;

    /**
     * call the listener since the user has change the led status
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                    if(mViewCallback!=null) {
                        mViewCallback.onLedSwitchChange(mItem, state);
                        if (state) {
                            mNextLedStatus = NEXT_STATE_ON;
                            mSwitchText.setText(R.string.ledOnStatus);
                        } else {
                            mNextLedStatus = NEXT_STATE_OFF;
                            mSwitchText.setText(R.string.ledOffStatus);
                        }//if-else
                        Log.d("LED:","Change: Status:"+mNextLedStatus+" newStatus:"+mNextLedStatus);
                    }
                }//onCheckedChanged
            };

    private EnvironmentalNodeViewCallback mViewCallback;

    private final TextView mTemperature;

    private final TextView mPressure;

    private final TextView mHumidity;

    private EnvironmentalRemoteNode mItem;

    private final TextView mSwitchText;
    private final CompoundButton mLedSwitch;
    private final ImageView mLedImage;
    private byte mNextLedStatus;

    public static EnvironmentalViewHolder build(ViewGroup parent, EnvironmentalNodeViewCallback callback){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.generic_node_item, parent, false);
        return new EnvironmentalViewHolder(view,callback);
    }

    private EnvironmentalViewHolder(View view,EnvironmentalNodeViewCallback callback) {
        this(view,
                view.findViewById(R.id.remoteIdLabel),
                view.findViewById(R.id.temperatureValLabel),
                view.findViewById(R.id.pressureValLabel),
                view.findViewById(R.id.humidityValLabel),
                view.findViewById(R.id.ledIcon),
                view.findViewById(R.id.ledSwitch),
                view.findViewById(R.id.ledStatusLabel),
                callback
        );

    }

    protected EnvironmentalViewHolder(View view,
                                      TextView nodeName,
                                      TextView temperatureValue,
                                      TextView pressureValue,
                                      TextView humidityValue,
                                      ImageView ledImage,
                                      CompoundButton ledSwitch,
                                      TextView ledValue,
                                      EnvironmentalNodeViewCallback callback){
        super(view,nodeName);
        mTemperature=temperatureValue;
        mPressure = pressureValue;
        mHumidity = humidityValue;
        mLedImage = ledImage;
        mLedSwitch = ledSwitch;
        mLedSwitch.setOnCheckedChangeListener(mSwitchListener);
        mSwitchText = ledValue;
        mViewCallback = callback;

    }

    /**
     * change the node associate with this view
     * @param node new node
     */

    private void updateNodeItem(EnvironmentalRemoteNode node){
        if(mItem==node)
            return;
        mItem=node;
        mNextLedStatus=NEXT_STATE_UNKNOWN;
    }

    /**
     * remove temporally the switch listener for avoid that the change status is notify to
     * the user
     * @param newStatus new switch status
     */
    private void changeSwitchWithoutNotify(boolean newStatus){
        mLedSwitch.setOnCheckedChangeListener(null);
        mLedSwitch.setChecked(newStatus);
        mLedSwitch.setOnCheckedChangeListener(mSwitchListener);
    }

    /**
     * set the led image, switch and text with the new state, the update is done only if
     * it is compatible with next state that the class is waiting
     * @param status true if the led is on, false otherwise , null if the status in unknown
     */
    private void setLedStatus(@Nullable Boolean status){
        if(status==null)
            return;
        Log.d("LED:","Status:"+mNextLedStatus+" newStatus:"+status);
        mLedSwitch.setVisibility(View.VISIBLE);
        //do the update only if is the state that we are waiting
        if(mNextLedStatus==NEXT_STATE_UNKNOWN ||
                (mNextLedStatus==NEXT_STATE_ON && status) ||
                (mNextLedStatus==NEXT_STATE_OFF && !status)) {
            if (status) {
                mLedImage.setImageResource(R.drawable.led_on);
                mSwitchText.setText(R.string.ledOnStatus);
                changeSwitchWithoutNotify(true);
            } else {
                mLedImage.setImageResource(R.drawable.led_off);
                mSwitchText.setText(R.string.ledOffStatus);
                changeSwitchWithoutNotify(false);
            }//if-else
        }
    }//setLedStatus

    private void setLux(int lux) {
        mLedImage.setImageResource(R.drawable.led_on);
        mSwitchText.setText( String.format(
                Locale.getDefault(),
                LUX_FORMAT,
                lux,
                FeatureLuminosity.FEATURE_UNIT));
        mLedSwitch.setVisibility(View.GONE);
    }

}
