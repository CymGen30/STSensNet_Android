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

import com.st.BlueSTSDK.Utils.NumberConversion;

import java.util.Arrays;

public class NetworkRequest {

    private short timestamp;
    private short commandId;
    private @NonNull byte[] payload;

    public NetworkRequest(byte[] rawCmd) {
        timestamp = NumberConversion.LittleEndian.bytesToInt16(rawCmd,0);
        commandId = NumberConversion.LittleEndian.bytesToInt16(rawCmd,2);
        int payloadLength = NumberConversion.LittleEndian.bytesToInt16(rawCmd,4)-6;
        payload = new byte[payloadLength];
        System.arraycopy(rawCmd,6,payload,0,payloadLength);
    }

    public NetworkRequest() {
        timestamp=0;
        commandId=0;
        payload = new byte[0];
    }

    public short getTimestamp() {
        return timestamp;
    }

    void setTimestamp(short timestamp) {
        this.timestamp = timestamp;
    }

    void setCommandId(short commandId) {
        this.commandId = commandId;
    }

    public short getCommandId() {
        return commandId;
    }

    public @NonNull byte[] getPayload() {
        return payload;
    }

    void setPayload(@NonNull byte[] payload) {
        this.payload = payload;
    }

    public short getLength(){
        // 6 is the header size
        return (short)(6 + payload.length);
    }

    public byte[] getByte() {
        short length = getLength();
        byte rawData[] = new byte[length];

        rawData[0] = (byte)(timestamp & 0x00FF);
        rawData[1] = (byte)((timestamp & 0xFF00)>>8);

        rawData[2] = (byte)(commandId & 0x00FF);
        rawData[3] = (byte)((commandId & 0xFF00)>>8);

        rawData[4] = (byte)(length & 0x00FF);
        rawData[5] = (byte)((length & 0xFF00)>>8);

        System.arraycopy(payload,0,rawData,6,payload.length);

        return rawData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkRequest command = (NetworkRequest) o;

        if (timestamp != command.timestamp) return false;
        if (commandId != command.commandId) return false;
        return Arrays.equals(payload, command.payload);

    }

    public static class Builder{

        private NetworkRequest cmd = new NetworkRequest();

        public Builder withTimestamp(short i) {
            cmd.setTimestamp(i);
            return this;
        }

        public Builder withCommand(short id) {
            cmd.setCommandId(id);
            return this;
        }

        public Builder withPayload(byte[] payload) {
            cmd.setPayload(payload);
            return this;
        }

        public NetworkRequest build() {
            return cmd;
        }
    }

}
