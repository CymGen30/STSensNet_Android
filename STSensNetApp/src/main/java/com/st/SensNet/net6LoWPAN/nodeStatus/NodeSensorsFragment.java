/*
 * Copyright (c) 2018  STMicroelectronics – All rights reserved
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
package com.st.SensNet.net6LoWPAN.nodeStatus;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.BlueSTSDK.Manager;
import com.st.BlueSTSDK.Node;
import com.st.SensNet.R;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.features.NetworkAddress;
import com.st.SensNet.net6LoWPAN.util.AddressFormatter;
import com.st.SensNet.net6LoWPAN.util.FragmentUtil;


public class NodeSensorsFragment extends Fragment implements NodeStatusContract.View {

    private static final String BOARDER_ROUTER = NodeSensorsFragment.class.getCanonicalName()+".BOARDER_ROUTER";
    private static final String NODE_ADDRESS = NodeSensorsFragment.class.getCanonicalName()+".NODE_ADDRESS";

    public static NodeSensorsFragment instantiate(Node router, NetworkAddress address){
        NodeSensorsFragment fragment = new NodeSensorsFragment();

        Bundle args = new Bundle();
        args.putString(BOARDER_ROUTER,router.getTag());
        args.putByteArray(NODE_ADDRESS,address.getBytes());
        fragment.setArguments(args);
        return fragment;
    }

    private NodeStatusContract.Presenter mPresenter;

    private TextView mAddress;
    private SensorStatusView mTemperature;
    private SensorStatusView mPressure;
    private SensorStatusView mHumidity;
    private SensorStatusView mAccelerometer;
    private SensorStatusView mMagnetometer;
    private SensorStatusView mGyroscope;
    private LedStatusView mLedStatus;


    @Override
    public void showNodeAddress(NetworkAddress address) {
        FragmentUtil.runOnUiThread(this,()-> mAddress.setText(AddressFormatter.format(address)));

    }

    @Override
    public void showTemperature(float temperature) {
        FragmentUtil.runOnUiThread(this,()-> mTemperature.setSensorValue(getString(R.string.lowpan_details_temperature_format,temperature)));
    }

    @Override
    public void showPressure(float pressure) {
        FragmentUtil.runOnUiThread(this,()-> mPressure.setSensorValue(getString(R.string.lowpan_details_pressure_format,pressure)));
    }

    @Override
    public void showHumidity(float humidity) {
        FragmentUtil.runOnUiThread(this,()-> mHumidity.setSensorValue(getString(R.string.lowpan_details_humidity_format,humidity)));
    }

    @Override
    public void showAccelerometer(float x, float y, float z) {
        FragmentUtil.runOnUiThread(this,()-> mAccelerometer.setSensorValue(
                getString(R.string.lowpan_details_accelerometer_format, x,y,z)));
    }

    @Override
    public void showGyroscope(float x, float y, float z) {
        FragmentUtil.runOnUiThread(this,()-> mGyroscope.setSensorValue(
                getString(R.string.lowpan_details_gyroscope_format, x,y,z)));
    }

    @Override
    public void showMagnetometer(float x, float y, float z) {
        FragmentUtil.runOnUiThread(this,()-> mMagnetometer.setSensorValue(
                getString(R.string.lowpan_details_magnetometer_format, x,y,z)));
    }

    @Override
    public void showLedStatus(byte dimmingValue) {
        FragmentUtil.runOnUiThread(this,()->{
            mLedStatus.setLedStatus((byte) (dimmingValue/4));
            mLedStatus.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHasOptionsMenu(true);
        Bundle boundle = getArguments();
        Node n = Manager.getSharedInstance().getNodeWithTag(boundle.getString(BOARDER_ROUTER));
        Feature6LoWPANProtocol boarderRouter = n.getFeature(Feature6LoWPANProtocol.class);
        NetworkAddress nodeAddress = new NetworkAddress(boundle.getByteArray(NODE_ADDRESS));

        mPresenter = new NodeSensorPresenter(this,boarderRouter,nodeAddress);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.framgnet_6lowpan_sensor_details,container,false);

        mAddress = root.findViewById(R.id.lowpan_details_address);
        mTemperature = root.findViewById(R.id.lowpan_details_temperature);
        mPressure = root.findViewById(R.id.lowpan_details_pressure);
        mHumidity = root.findViewById(R.id.lowpan_details_humidity);
        mAccelerometer = root.findViewById(R.id.lowpan_details_accelerometer);
        mMagnetometer = root.findViewById(R.id.lowpan_details_magnetometer);
        mGyroscope = root.findViewById(R.id.lowpan_details_gyroscope);
        mLedStatus = root.findViewById(R.id.lowpan_details_led);
        mLedStatus.setOnLedStatusChangeListener(newValue -> {
            if(mPresenter!=null)
                mPresenter.setDimmingStatus((byte)( newValue*25));
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.startRetrievingSensorData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopRetrievingSensorData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
