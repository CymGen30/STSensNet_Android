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


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.gui.demos.DemoDescriptionAnnotation;
import com.st.BlueSTSDK.gui.demos.DemoFragment;
import com.st.SensNet.R;
import com.st.SensNet.netBle.features.CommandFeature;
import com.st.SensNet.netBle.features.GenericRemoteFeature;

@DemoDescriptionAnnotation(name="Debug")
public class DebugCommandFragment extends DemoFragment {


    private Node mNode;
    private GenericRemoteFeature mDataFeature;
    private CommandFeature mCommand;

    private TextView mNotificationTest;
    private Button mStartButton;
    private Button mStopButton;
    private Feature.FeatureListener mFeatureListener = new Feature.FeatureListener() {
        @Override
        public void onUpdate(final Feature f, Feature.Sample sample) {
            updateGui(new Runnable() {
                @Override
                public void run() {
                    mNotificationTest.append(f.toString()+"\n");
                }
            });
        }
    };

    public DebugCommandFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug_command, container, false);
        mNotificationTest = (TextView) view.findViewById(R.id.notificationText);

        Switch enableFeature = (Switch) view.findViewById(R.id.switchData);
        enableFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                if(isEnabled)
                    mNode.enableNotification(mDataFeature);
                else
                    mNode.disableNotification(mDataFeature);
            }
        });
        enableFeature = (Switch) view.findViewById(R.id.switchCommand);
        enableFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                if(isEnabled)
                    mNode.enableNotification(mCommand);
                else
                    mNode.disableNotification(mCommand);
                mStartButton.setEnabled(isEnabled);
                mStopButton.setEnabled(isEnabled);
            }
        });

        mStartButton = (Button) view.findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommand.startScanning();
            }
        });
        mStopButton = (Button) view.findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommand.stopScanning();
            }
        });

        return view;
    }


    @Override
    protected void enableNeededNotification(@NonNull Node node) {
        mNode = node;
        mDataFeature = node.getFeature(GenericRemoteFeature.class);
        mDataFeature.addFeatureListener(mFeatureListener);
        mCommand = node.getFeature(CommandFeature.class);
        mCommand.addFeatureListener(mFeatureListener);
    }

    @Override
    protected void disableNeedNotification(@NonNull Node node) {
        if(mNode.isEnableNotification(mDataFeature)){
            node.disableNotification(mDataFeature);
        }
        if(mNode.isEnableNotification(mCommand)){
            node.disableNotification(mCommand);
        }
        mDataFeature.removeFeatureListener(mFeatureListener);
        mCommand.removeFeatureListener(mFeatureListener);
    }

}
