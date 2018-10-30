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

package com.st.SensNet.netBle.RemoteNodeUtils.data;

import android.os.Parcel;
import android.support.annotation.Nullable;

/**
 * RemoteNode that contains the environmental data
 */
public class EnvironmentalRemoteNode extends RemoteNode{

    /**
     * remote node temperature
     */
    private float mTemperature=Float.NaN;

    /**
     * remote node Humidity
     */
    private float mHumidity=Float.NaN;

    /**
     * remote node Pressure
     */
    private float mPressure=Float.NaN;

    private int mLux=-1;

    /**
     * remote node led
     */
    private @Nullable Boolean mLed;


    public EnvironmentalRemoteNode(int id){
        super(id);
        mTemperature=Float.NaN;
        mHumidity= Float.NaN;
        mPressure=Float.NaN;
        mLed=null;
    }

    public float getHumidity() {
        return mHumidity;
    }

    public void setHumidity(float humidity) {
        if(!Float.isNaN(humidity))
            this.mHumidity = humidity;
    }

    public @Nullable Boolean getLed() {
        return mLed;
    }

    public void setLed(Boolean led) {
        if(led!=null)
            mLed =led;
    }

    public float getPressure() {
        return mPressure;
    }

    public void setPressure(float mPressure) {
        if(!Float.isNaN(mPressure))
            this.mPressure = mPressure;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        if(!Float.isNaN(temperature))
            this.mTemperature = temperature;
    }


    public int getLux(){
        return mLux;
    }

    public boolean hasLux(){
        return mLux>=0;
    }

    public void setLux(int newLux){
        if(newLux>=0)
            mLux=newLux;
    }

    protected EnvironmentalRemoteNode(Parcel in) {
        super(in);
        mTemperature = in.readFloat();
        mHumidity = in.readFloat();
        mPressure = in.readFloat();
        mLux = in.readInt();
        byte ledValue = in.readByte();
        if(ledValue<0)
            mLed=null;
        else
            mLed = ledValue!=0;

    }

    public static final Creator<EnvironmentalRemoteNode> CREATOR = new Creator<EnvironmentalRemoteNode>() {
        @Override
        public EnvironmentalRemoteNode createFromParcel(Parcel in) {
            return new EnvironmentalRemoteNode(in);
        }

        @Override
        public EnvironmentalRemoteNode[] newArray(int size) {
            return new EnvironmentalRemoteNode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel,i);
        parcel.writeFloat(mTemperature);
        parcel.writeFloat(mHumidity);
        parcel.writeFloat(mPressure);
        parcel.writeInt(mLux);
        if(mLed==null)
            parcel.writeByte((byte)-1);
        else
            parcel.writeByte((byte)(mLed ? 0 : 1));


    }


}
