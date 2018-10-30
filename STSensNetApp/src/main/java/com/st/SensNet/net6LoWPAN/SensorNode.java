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

package com.st.SensNet.net6LoWPAN;

import android.os.Parcel;
import android.os.Parcelable;

import com.st.BlueSTSDK.Utils.NumberConversion;
import com.st.SensNet.net6LoWPAN.features.NetworkAddress;
import com.st.SensNet.net6LoWPAN.util.AddressFormatter;

public class SensorNode  implements Parcelable {

    private NetworkAddress address;
    private float temperature;
    private float pressure;
    private short humidity;
    private int accX,accY,accZ;
    private int magX,magY,magZ;
    private float gyroX,gyroY,gyroZ;
    private byte ledDimming;

    public SensorNode(NetworkAddress address){
        this.address = address;
    }

    public SensorNode(byte[] address){
        this.address = new NetworkAddress(address);
    }

    protected SensorNode(Parcel in) {
        address = new NetworkAddress(in.createByteArray());
        temperature = in.readFloat();
        pressure = in.readInt();
        humidity = (short)in.readInt();
        accX = in.readInt();
        accY = in.readInt();
        accZ = in.readInt();
        magX = in.readInt();
        magY = in.readInt();
        magZ = in.readInt();
        gyroX = in.readInt();
        gyroY = in.readInt();
        gyroZ = in.readInt();
        ledDimming = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(address.getBytes());
        dest.writeFloat(temperature);
        dest.writeFloat(pressure);
        dest.writeInt(humidity);
        dest.writeInt(accX);
        dest.writeInt(accY);
        dest.writeInt(accZ);
        dest.writeInt(magX);
        dest.writeInt(magY);
        dest.writeInt(magZ);
        dest.writeFloat(gyroX);
        dest.writeFloat(gyroY);
        dest.writeFloat(gyroZ);
        dest.writeByte(ledDimming);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorNode> CREATOR = new Creator<SensorNode>() {
        @Override
        public SensorNode createFromParcel(Parcel in) {
            return new SensorNode(in);
        }

        @Override
        public SensorNode[] newArray(int size) {
            return new SensorNode[size];
        }
    };

    public NetworkAddress getAddress() {
        return address;
    }

    public String getName(){
        return AddressFormatter.format(address.getBytes());

    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(short humidity) {
        this.humidity = humidity;
    }

    public int getAccX() {
        return accX;
    }

    public void setAccX(int accX) {
        this.accX = accX;
    }

    public int getAccY() {
        return accY;
    }

    public void setAccY(int accY) {
        this.accY = accY;
    }

    public int getAccZ() {
        return accZ;
    }

    public void setAccZ(int accZ) {
        this.accZ = accZ;
    }

    public int getMagX() {
        return magX;
    }

    public void setMagX(int magX) {
        this.magX = magX;
    }

    public int getMagY() {
        return magY;
    }

    public void setMagY(int magY) {
        this.magY = magY;
    }

    public int getMagZ() {
        return magZ;
    }

    public void setMagZ(int magZ) {
        this.magZ = magZ;
    }

    public float getGyroX() {
        return gyroX;
    }

    public void setGyroX(float gyroX) {
        this.gyroX = gyroX;
    }

    public float getGyroY() {
        return gyroY;
    }

    public void setGyroY(float gyroY) {
        this.gyroY = gyroY;
    }

    public float getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(float gyroZ) {
        this.gyroZ = gyroZ;
    }

    public byte getLedDimming() {
        return ledDimming;
    }

    public void setLedDimming(byte ledDimming) {
        this.ledDimming = ledDimming;
    }

}
