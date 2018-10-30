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

package com.st.SensNet.netBle.features;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Features.Field;
import com.st.BlueSTSDK.Node;

/**
 * Feature used for send command/request to the Ble Star fw
 */
public class CommandFeature extends Feature {
    public static final String FEATURE_NAME = "NetworkRequest";

    /**
     * interface to use for receive the command results
     */
    public interface CommandCallback{
        /**
         * called when the node start the scanning of new periferal
         * @param command object used to send the command
         */
        void onScanIsStarted(CommandFeature command);

        /**
         * called when the node stop the scanning of new periferal
         * @param command object used to send the command
         */
        void onScanIsStopped(CommandFeature command);
    }

    private static final byte COMMAND_START_SCANNING = 1;
    private static final byte COMMAND_STOP_SCANNING = 2;

    public static final String COMMAND_DATA_NAME = "CommandId";
    public static final byte DATA_MAX = (byte) 0xFF;
    public static final byte DATA_MIN = 0;

    public static final String COMMAND_PAYLOAD_DATA_NAME = "NetworkRequest data";

    protected static final Field COMMAND_ID_FIELD =
            new Field(COMMAND_DATA_NAME, null, Field.Type.UInt8, DATA_MAX, DATA_MIN);
    protected static final Field COMMAND_PAYLOAD_FIELD =
            new Field(COMMAND_PAYLOAD_DATA_NAME, null, Field.Type.ByteArray, DATA_MAX, DATA_MIN);

    private CommandCallback mCallback;

    public static byte getCommandId(Sample sample){
        if(hasValidIndex(sample,0))
            return sample.data[0].byteValue();
        return -1;
    }

    public static byte[] getCommandData(Sample sample){
        if(hasValidIndex(sample,1)) {
            int length = sample.data.length-1;
            byte[] cmdData = new byte[length];
            for(int i=0;i<length;i++){
                cmdData[i]=sample.data[i+1].byteValue();
            }//for
            return cmdData;
        }//if
        return new byte[]{};
    }//getCommandData

    private FeatureListener mListener = new FeatureListener() {
        @Override
        public void onUpdate(Feature f, Sample sample) {
            if(mCallback==null)
                return;
            switch (getCommandId(sample)){
                case COMMAND_START_SCANNING:
                    mCallback.onScanIsStarted(CommandFeature.this);
                    break;
                case COMMAND_STOP_SCANNING:
                    mCallback.onScanIsStopped(CommandFeature.this);
                    break;
            }
        }
    };

    /**
     * build a new disabled feature, that doesn't need to be initialized in the node side
     *
     * @param n        node that will update this feature
     */
    public CommandFeature(Node n) {
        super(FEATURE_NAME, n, new Field[]{COMMAND_ID_FIELD, COMMAND_PAYLOAD_FIELD});
    }

    public void setCommandCallback(CommandCallback callback){
        mCallback=callback;
        if(mCallback!=null)
            addFeatureListener(mListener);
        else
            removeFeatureListener(mListener);
    }

    public boolean startScanning(){
        return sendCommand(COMMAND_START_SCANNING,new byte[]{});
    }

    public boolean stopScanning(){
        return sendCommand(COMMAND_STOP_SCANNING,new byte[]{});
    }

    @Override
    protected ExtractResult extractData(long timestamp, byte[] data, int dataOffset) {

        if(data.length-dataOffset<1){ //blestar_command Id + ..
            throw new IllegalArgumentException("There are no 1 byte available to read");
        }

        int readData = dataOffset;
        byte commandId = data[readData++];
        //read payload if needed

        return new ExtractResult(
                new Sample(timestamp,new Number[]{commandId},getFieldsDesc()),
                readData-dataOffset);
    }
}
