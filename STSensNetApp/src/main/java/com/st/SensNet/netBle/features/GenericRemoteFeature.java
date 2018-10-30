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

import android.support.annotation.Nullable;
import android.util.Log;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Features.Field;
import com.st.BlueSTSDK.Features.remote.RemoteFeatureUtil;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.Utils.NumberConversion;

import java.util.Arrays;
import java.util.Date;

public class GenericRemoteFeature extends Feature {

    private static final byte TYPE_ID_PRESSURE=0x01;
    private static final byte TYPE_ID_HUMIDITY=0x02;
    private static final byte TYPE_ID_TEMPERATURE=0x03;
    private static final byte TYPE_ID_LED_STATUS=0x04;
    private static final byte TYPE_ID_PROXIMITY =0x05;
    private static final byte TYPE_ID_MOTION_DETECTION=0x06;
    private static final byte TYPE_ID_MIC_LEVEL=0x07;
    private static final byte TYPE_ID_LUX=0x08;

    public static final String FEATURE_NAME = "Generic Remote";

    private static final int REMOTE_NODE_ID_INDEX=0;

    public static final String PRESSURE_UNIT = "mBar";
    public static final String PRESSURE_DATA_NAME = "Pressure";
    public static final float PRESSURE_DATA_MAX = 2000;
    public static final float PRESSURE_DATA_MIN = 0;

    private static final int PRESSURE_INDEX=1;
    private static final Field PRESSURE_FIELD = new Field(PRESSURE_DATA_NAME, PRESSURE_UNIT,
            Field.Type.Float, PRESSURE_DATA_MAX, PRESSURE_DATA_MIN);


    public static final String TEMPERATURE_UNIT = "\u2103"; // celsius degree
    public static final String TEMPERATURE_DATA_NAME = "Temperature";
    public static final float TEMPERATURE_DATA_MAX = 100;
    public static final float TEMPERATURE_DATA_MIN = 0;

    private static final int TEMPERATURE_INDEX=2;
    private static final Field TEMPERATURE_FILED =
            new Field(TEMPERATURE_DATA_NAME, TEMPERATURE_UNIT, Field.Type.Float,
                    TEMPERATURE_DATA_MAX, TEMPERATURE_DATA_MIN);

    private static final int HUMIDITY_INDEX=3;
    public static final String HUMIDITY_UNIT = "%";
    public static final String HUMIDITY_DATA_NAME = "Humidity";
    public static final float HUMIDITY_DATA_MAX = 100;
    public static final float HUMIDITY_DATA_MIN = 0;

    private static final Field HUMIDITY_FILED =
            new Field(HUMIDITY_DATA_NAME, HUMIDITY_UNIT, Field.Type.Float, HUMIDITY_DATA_MAX, HUMIDITY_DATA_MIN);


    private static final int LED_INDEX=4;
    public static final String LED_UNIT = null;
    public static final String LED_DATA_NAME = "Led Status";
    public static final short LED_DATA_MAX = 1;
    public static final short LED_DATA_MIN = 0;

    private static final Field LED_FILED =
            new Field(LED_DATA_NAME, LED_UNIT, Field.Type.UInt8, LED_DATA_MAX, LED_DATA_MIN);


    private static final int PROXIMITY_INDEX=5;
    public static final String PROXIMITY_DATA_NAME = "Proximity";
    public static final String PROXIMITY_UNIT = "mm";
    /**
     * the sensor return this value when the object is out of range
     */
    public static final int PROXIMITY_OUT_OF_RANGE_VALUE = 0xFFFF;
    /**
     * maximum object distance manage by the sensor
     */
    public static final int LOW_RANGE_PROXIMITY_DATA_MAX = 0xFE;
    public static final int HIGH_RANGE_PROXIMITY_DATA_MAX = 0x7FFE;
    /**
     * minimal object distance manage by the sensor
     */
    public static final float PROXIMITY_DATA_MIN = 0;

    private static final Field LOW_RANGE_PROXIMITY_FIELD = new Field(PROXIMITY_DATA_NAME,
            PROXIMITY_UNIT, Field.Type.UInt16, PROXIMITY_DATA_MIN, LOW_RANGE_PROXIMITY_DATA_MAX);

    private static final Field HIGH_RANGE_PROXIMITY_FIELD = new Field(PROXIMITY_DATA_NAME,
            PROXIMITY_UNIT, Field.Type.UInt16, PROXIMITY_DATA_MIN, LOW_RANGE_PROXIMITY_DATA_MAX);

    private static final int MOTION_DETECTION_INDEX=6;
    public static final String MOTION_DETECTION_UNIT = "ms";
    public static final String MOTION_DETECTION_DATA_NAME = "Last Moviment";
    public static final long MOTION_DETECTION_DATA_MAX = Long.MAX_VALUE;
    public static final long MOTION_DETECTION_DATA_MIN = -1;

    private static final Field MOTION_DETECTION_FILED =
            new Field(MOTION_DETECTION_DATA_NAME, MOTION_DETECTION_UNIT, Field.Type.Int64, MOTION_DETECTION_DATA_MAX,
                    MOTION_DETECTION_DATA_MIN);

    private static final int MIC_LEVEL_INDEX=7;
    public static final String MIC_LEVEL_UNIT = "db";
    public static final String MIC_LEVEL_DATA_NAME = "Mic Level";
    public static final short MIC_LEVEL_DATA_MAX = 255;
    public static final short MIC_LEVEL_DATA_MIN = -1;

    private static final Field MIC_LEVEL_FILED =
            new Field(MIC_LEVEL_DATA_NAME, MIC_LEVEL_UNIT, Field.Type.UInt8, MIC_LEVEL_DATA_MAX,
                    MIC_LEVEL_DATA_MIN);

    private static final int LUX_INDEX=8;
    public static final String LUX_UNIT = "Lux";
    public static final String LUX_DATA_NAME = "Luminosity";
    public static final int LUX_DATA_MAX = Short.MAX_VALUE;
    public static final short LUX_DATA_MIN = -1;

    private static final Field LUX_FILED =
            new Field(LUX_DATA_NAME, LUX_UNIT, Field.Type.UInt8, LUX_DATA_MAX,
                    LUX_DATA_MIN);

    private static final int UNKNOWN_STATUS_INDEX=9;
    public static final String UNKNOWN_STATUS_UNIT = null;
    public static final String UNKNOWN_STATUS_DATA_NAME = "Unknown";
    public static final byte UNKNOWN_STATUS_DATA_MAX = (byte)256;
    public static final byte UNKNOWN_STATUS_DATA_MIN = 0;

    private static final Field UNKNOWN_FILED =
            new Field(UNKNOWN_STATUS_DATA_NAME, UNKNOWN_STATUS_UNIT, Field.Type.ByteArray, UNKNOWN_STATUS_DATA_MAX,
                    UNKNOWN_STATUS_DATA_MIN);


    private static @Nullable Boolean byteToBoolean(byte value){
        if(value<0)
            return null;
        else
            return (value!=0);
    }

    /**
     * return the node id that send the sample
     * @param s sample of data
     * @return node that send the data or -1 if is not a valid sample
     */
    public static int getNodeId(Sample s){
        if(hasValidIndex(s,REMOTE_NODE_ID_INDEX))
            return s.data[REMOTE_NODE_ID_INDEX].intValue();
        return -1;
    }

    /**
     * return the pressure in the sample
     * @param s sample of data
     * @return pressure inside the sample or NaN if not present
     */
    public static float getPressure(Sample s){
        if(hasValidIndex(s,PRESSURE_INDEX))
            return s.data[PRESSURE_INDEX].floatValue();
        //else
        return Float.NaN;
    }

    /**
     * extract the temperature from the sample
     * @param s sample of data
     * @return temperature inside the sample or NaN if not present
     */
    public static float getTemperature(Sample s){
        if(hasValidIndex(s,TEMPERATURE_INDEX))
            return s.data[TEMPERATURE_INDEX].floatValue();
        //else
        return Float.NaN;
    }

    /**
     * extract the humidity data from the sample
     * @param s sample of data
     * @return humidity inside the sample or NaN if not present
     */
    public static float getHumidity(Sample s){
        if(hasValidIndex(s,HUMIDITY_INDEX))
            return s.data[HUMIDITY_INDEX].floatValue();
        //else
        return Float.NaN;
    }

    /**
     * extract the led status from the sample
     * @param s data sample
     * @return !=0 if the led is on, 0 if is off ,<0 if the status in unknown
     */
    public static @Nullable Boolean getLedStatus(Sample s){
        byte value=-1;
        if(hasValidIndex(s,LED_INDEX))
            value = s.data[LED_INDEX].byteValue();
        //else
        return byteToBoolean(value);
    }

    /**
     * extract the proximity from the sample
     * @param s data sample
     * @return <0 if the proximity is not know, otherwise the proximity distance
     */
    public static int getProximity(Sample s){
        if(hasValidIndex(s,PROXIMITY_INDEX))
            return s.data[PROXIMITY_INDEX].intValue();
        //else
        return -1;
    }

    public static boolean isLowRangeProximity(Sample s) {
        if(hasValidIndex(s,PROXIMITY_INDEX))
            return s.dataDesc[PROXIMITY_INDEX].getMax().intValue() == LOW_RANGE_DATA_MAX;
        //else
        return false;
    }

    /**
     * extract the luminosity from the sample
     * @param s data sample
     * @return <0 if the luminosity is not know, otherwise the luminosity
     */
    public static int getLuminosity(Sample s){
        if(hasValidIndex(s,LUX_INDEX))
            return s.data[LUX_INDEX].intValue();
        //else
        return -1;
    }

    /**
     * extract the proximity from the sample
     * @param s data sample
     * @return <0 if the proximity is not know, otherwise the proximity distance
     */
    public static short getMicLevel(Sample s){
        if(hasValidIndex(s,MIC_LEVEL_INDEX))
            return s.data[MIC_LEVEL_INDEX].shortValue();
        //else
        return -1;
    }

    /**
     * extract the door status form the sample
     * @param s data sample
     * @return <0 if the status is unknow, 0 if is close , 1 is it open
     */
    public static @Nullable Date getLastMotionDetection(Sample s){
        long value = -1;
        if(hasValidIndex(s,MOTION_DETECTION_INDEX))
            value = s.data[MOTION_DETECTION_INDEX].longValue();
        //else
        if(value>0)
            return new Date(value);
        return null;
    }

    public static @Nullable byte[] getUnknownData(Sample s){
        if(!hasValidIndex(s,UNKNOWN_STATUS_INDEX))
            return null;
        //else
        int length = s.data.length-UNKNOWN_STATUS_INDEX;
        byte data[] = new byte[length];
        for(int i=0;i<length;i++){
            data[i]=s.data[UNKNOWN_STATUS_INDEX+i].byteValue();
        }
        return data;
    }

    public GenericRemoteFeature(Node n) {
        super(FEATURE_NAME, n, new Field[]{RemoteFeatureUtil.REMOTE_DEVICE_ID,
                PRESSURE_FIELD,HUMIDITY_FILED,TEMPERATURE_FILED, LED_FILED,LOW_RANGE_PROXIMITY_FIELD,
                MOTION_DETECTION_FILED,MIC_LEVEL_FILED,LUX_FILED,UNKNOWN_FILED});
    }

    @Override
    protected ExtractResult extractData(long timestamp, byte[] data, int dataOffset) {
        if (data.length - dataOffset < 3) //2 (node Id)+1 (type id)
            throw new IllegalArgumentException("There are no 3 bytes available to read");

        float pressure=Float.NaN;
        float temperature=Float.NaN;
        float humidity=Float.NaN;
        byte ledStatus=-1;
        short micLevel=-1;
        int lux=-1;
        int proximity=-1;
        boolean isLowRange=true;
        long motionDetection=-1;
        byte unknownData[]=null;

        int readData = dataOffset;

        int nodeID = NumberConversion.LittleEndian.bytesToUInt16(data,readData);
        readData+=2;

        boolean validDataId =true;
        while (validDataId && readData<data.length){
            switch (data[readData]){
                case TYPE_ID_PRESSURE:
                    pressure= NumberConversion.LittleEndian.bytesToInt32(data, readData+1)/100.0f;
                    readData+=4;
                    break;
                case TYPE_ID_TEMPERATURE:
                    temperature = NumberConversion.LittleEndian.bytesToInt16(data, readData+1)/10.0f;
                    readData+=2;
                    break;
                case TYPE_ID_HUMIDITY:
                    humidity = NumberConversion.LittleEndian.bytesToInt16(data, readData+1)/10.0f;
                    readData+=2;
                    break;
                case TYPE_ID_LED_STATUS:
                    ledStatus= data[readData+1];
                    readData++;
                    break;
                case TYPE_ID_PROXIMITY:
                    int rawProximity = NumberConversion.LittleEndian.bytesToUInt16(data,readData+1);
                    proximity = extractLuxValue(rawProximity);
                    isLowRange = isLowRangeSensor(rawProximity);
                    readData+=2;
                    break;
                case TYPE_ID_MIC_LEVEL:
                    micLevel = NumberConversion.byteToUInt8(data,readData+1);
                    readData+=1;
                    break;
                case TYPE_ID_LUX:
                    lux = NumberConversion.LittleEndian.bytesToUInt16(data, readData+1);
                    readData+=2;
                    break;
                case TYPE_ID_MOTION_DETECTION:
                    motionDetection= System.currentTimeMillis();
                    break;
                default://else
                    unknownData = extractUnknownData(data,readData);
                    validDataId=false;
            }
            readData++; //+1 for the typeid
        }

        Field fields[] = getFieldsDesc();
        fields[PROXIMITY_INDEX] = isLowRange ? LOW_RANGE_PROXIMITY_FIELD : HIGH_RANGE_PROXIMITY_FIELD;

        Number extractData[] = new Number[]{nodeID,pressure,temperature,
                humidity,ledStatus,proximity,motionDetection,micLevel,lux};
        extractData = appendUnknownData(extractData,unknownData);

        return new ExtractResult(new Sample(timestamp,extractData,getFieldsDesc()),
            readData-dataOffset);
    }

    private static boolean isLowRangeSensor(int value){
        return (value & 0x8000)==0;
    }

    private static int getRangeValue(int value){
        return (value & ~0x8000);
    }

    private static final int LOW_RANGE_DATA_MAX = 0x00FE;
    private static final int HIGH_RANGE_DATA_MAX = 0X7FFE;

    private int extarctLowRagngeData(int value){
        int rangeValue = getRangeValue(value);
        if(rangeValue > LOW_RANGE_DATA_MAX){
            rangeValue = PROXIMITY_OUT_OF_RANGE_VALUE;
        }

        return rangeValue;
    }

    private int extarctHightRagngeData(int value){
        int rangeValue = getRangeValue(value);
        if(rangeValue > HIGH_RANGE_DATA_MAX){
            rangeValue = PROXIMITY_OUT_OF_RANGE_VALUE;
        }
        return rangeValue;
    }

    private int extractLuxValue(int rawValue){

        if(isLowRangeSensor(rawValue)){
            return extarctLowRagngeData(rawValue);
        }//else
        return extarctHightRagngeData(rawValue);
    }

    private Number[] appendUnknownData(Number[] prefix, byte[] suffix) {
        if(suffix==null || suffix.length==0)
            return prefix;
        //else
        Number temp[] = new Number[prefix.length+suffix.length];
        System.arraycopy(prefix,0,temp,0,prefix.length);
        for(int i=0;i<suffix.length;i++){
            temp[prefix.length+i]=suffix[i];
        }
        return temp;
    }

    private byte[] extractUnknownData(byte[] data, int offset) {
        int length = data.length-offset;
        byte temp[] = new byte[length];
        System.arraycopy(data,offset,temp,0,length);
        return temp;
    }

    /**
     * send a message for change the led status in a remote node
     * @param remoteId node where change the status
     * @param newStatus new status
     */
    public void changeSwitchStatus(int remoteId, boolean newStatus) {
        sendEnableCommand(TYPE_ID_LED_STATUS,remoteId,newStatus);
    }

    public void enableMicLevel(int remoteId, boolean newStatus) {
        sendEnableCommand(TYPE_ID_MIC_LEVEL,remoteId,newStatus);
    }

    public void enableProximity(int remoteId, boolean newStatus) {
        sendEnableCommand(TYPE_ID_PROXIMITY,remoteId,newStatus);
    }

    private void sendEnableCommand(byte commandId, int remoteId, boolean newStatus) {
        byte command[]=new byte[4];
        byte nodeId[] = NumberConversion.BigEndian.uint16ToBytes(remoteId);
        command[0]=nodeId[0];
        command[1]=nodeId[1];
        command[2]=commandId;
        command[3]= newStatus ? (byte) 0x1 : (byte) 0x0;
        writeData(command);
    }


}
