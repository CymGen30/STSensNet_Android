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

import java.util.Date;

/**
 * Remote node that contains the data exported by a GenericRemoteNode
 */
public class GenericRemoteNode extends EnvironmentalRemoteNode {

    /**
     * proximity value in the remote node, <0 if unknown
     */
    private int mProximity;
    private boolean mProximityEnabled;
    private boolean mLowRangeProximity;
    private short mMicLevel;
    private boolean mMicLevelEnabled;

    private @Nullable Date mDetectMovement;

    private byte[] mUnknownData;

    public GenericRemoteNode(int id){
        super(id);
        mProximity=-1;
        mMicLevel=-1;
        mDetectMovement=null;
        mUnknownData = null;
    }


    public boolean proximityIsEnabled(){return mProximityEnabled;}
    public boolean micIsEnabled(){return mMicLevelEnabled;}

    public void setProximityEnabled(boolean enabled){mProximityEnabled = enabled;}
    public void setMicEnabled(boolean enabled){mMicLevelEnabled = enabled;}

    public boolean hasProximity() {
        return  mProximity>=0;
    }

    public int getProximity() {
        return mProximity;
    }

    public boolean isLowRangeProximity(){ return  mLowRangeProximity;}

    public void setProximity(int proximity, boolean isLowRange) {
        if(proximity>=0)
            mProximity = proximity;
    }



    public boolean hasMicLevel() {
        return  mMicLevel>=0;
    }

    public short getMicLevel() {
        return mMicLevel;
    }

    public void setMicLevel(short micLevel) {
        if(micLevel>=0) {
            mMicLevel = micLevel;
        }
    }

    public byte[] getUnknownData() {
        return mUnknownData;
    }

    public void setUnknownData(byte[] unknownData) {
        if(unknownData!=null && unknownData.length!=0)
            this.mUnknownData = unknownData;
    }

    public boolean hasUnknownData() {
        return mUnknownData !=null && mUnknownData.length!=0;
    }

    public @Nullable
    Date getLastDetectMovement() {
        return mDetectMovement;
    }

    public void setDetectMovement(@Nullable Date detectMovement) {
        if(detectMovement==null)
            return;
        mDetectMovement = detectMovement;
    }

    //////////////////////PARCELABLE IMPLEMENTATION//////////////////////////////////

    public static final Creator<GenericRemoteNode> CREATOR = new Creator<GenericRemoteNode>() {
        @Override
        public GenericRemoteNode createFromParcel(Parcel in) {
            return new GenericRemoteNode(in);
        }

        @Override
        public GenericRemoteNode[] newArray(int size) {
            return new GenericRemoteNode[size];
        }
    };

    private GenericRemoteNode(Parcel in) {
        super(in);
        mProximity = in.readInt();
        mProximityEnabled = in.readByte() ==0;

        mMicLevel  =(short)in.readInt();
        mMicLevelEnabled = in.readByte() ==0;

        long timeValue = in.readLong();
        if(timeValue<0)
            mDetectMovement=null;
        else
            mDetectMovement = new Date(timeValue);

        int unknownDataLength = in.readInt();
        if(unknownDataLength!=0) {
            mUnknownData = new byte[in.readInt()];
            in.readByteArray(mUnknownData);
        }else{
            mUnknownData =null;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel,i);

        parcel.writeInt(mProximity);
        parcel.writeByte(mProximityEnabled?(byte)0:(byte)1);
        parcel.writeInt(mMicLevel);
        parcel.writeByte(mMicLevelEnabled?(byte)0:(byte)1);

        if(mDetectMovement==null)
            parcel.writeLong(-1);
        else
            parcel.writeLong(mDetectMovement.getTime());

        if(mUnknownData !=null) {
            parcel.writeInt(mUnknownData.length);
            parcel.writeByteArray(mUnknownData);
        }else{
            parcel.writeInt(0);
        }
    }
}
