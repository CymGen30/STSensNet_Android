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

package com.st.SensNet.net6LoWPAN.networkStatus;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.st.BlueSTSDK.Manager;
import com.st.BlueSTSDK.Node;
import com.st.SensNet.R;
import com.st.SensNet.net6LoWPAN.SensorNode;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.networkTopology.NetworkTopologyFragment;
import com.st.SensNet.net6LoWPAN.nodeStatus.NodeSensorsFragment;
import com.st.SensNet.net6LoWPAN.util.FragmentUtil;

import java.util.ArrayList;
import java.util.List;

public class NetworkStatusFragment extends Fragment implements NetworkStatusContract.View,
        SensorNodeRecyclerViewAdapter.AdapterInteractionListener,NetworkLedContract.View{

    private static final String NODE_TAG = NetworkStatusFragment.class.getCanonicalName()+".NODE_TAG";

    private static final String DISPLAYED_NODES = NetworkStatusFragment.class.getCanonicalName()+".DISPLAYED_NODES";

    public static NetworkStatusFragment getInstance(Node centralNode){
        Bundle args = new Bundle(1);
        args.putString(NODE_TAG,centralNode.getTag());
        NetworkStatusFragment fragment = new NetworkStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NetworkStatusFragment() {
    }


    private SensorNodeRecyclerViewAdapter mAdapter;
    private NetworkStatusContract.Presenter mPresenter;
    private NetworkLedContract.Presenter mLedPresenter;
    private SwipeRefreshLayout mRefreshLayout;
    private Node mNode;

    private MenuItem mSwitchLedOn;
    private MenuItem mSwitchLedOff;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ask to add our option to the menu
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_6lowpan_network_status, menu);
        mSwitchLedOff = menu.findItem(R.id.menu_network_switchOff);
        mSwitchLedOn = menu.findItem(R.id.menu_network_switchOn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_updateNetwork){
            if (mPresenter!=null) {
                mRefreshLayout.setRefreshing(true);
                mPresenter.getNodes();
            }
            return true;
        }
        if (id == R.id.menu_networkTopology){
            showTopology();
            return true;
        }
        if( id == R.id.menu_network_switchOn){
            mLedPresenter.switchAllOn();
            return true;
        }
        if( id == R.id.menu_network_switchOff){
            mLedPresenter.switchAllOff();
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<SensorNode> nodes = mAdapter.getDisplayedNodes();
        ArrayList<SensorNode> arrayList = new ArrayList<>(nodes);
        outState.putParcelableArrayList(DISPLAYED_NODES,arrayList);
    }

    protected void enableNeededNotification(@NonNull Node node) {
        Feature6LoWPANProtocol feature = node.getFeature(Feature6LoWPANProtocol.class);
        if(feature==null)
            return;

        mPresenter = new NetworkStatusPresenter(this,feature);
        mLedPresenter = new NetworkLedPresenter(this,feature);

        mPresenter.startNodeRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_6lowpan_netowrk_status, container, false);

        mRefreshLayout = view.findViewById(R.id.lowpan_netStatus_sensorNodeListRefresh);
        Resources res = getResources();
        mRefreshLayout.setColorSchemeColors(
                res.getColor(R.color.swipeColor_1),
                res.getColor(R.color.swipeColor_2),
                res.getColor(R.color.swipeColor_3),
                res.getColor(R.color.swipeColor_4));
        mRefreshLayout.setOnRefreshListener(() -> mPresenter.getNodes());

        RecyclerView recyclerView = view.findViewById(R.id.lowpan_netStatus_sensorNodeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new SensorNodeRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        if(savedInstanceState!=null && savedInstanceState.containsKey(DISPLAYED_NODES)){
            ArrayList<SensorNode> arrayList = savedInstanceState.getParcelableArrayList(DISPLAYED_NODES);
            showNodes(arrayList);
        }
        return view;
    }

    private @Nullable Node extractNodeArgs(){
        String tag = getArguments().getString(NODE_TAG);
        if(tag!=null)
            return Manager.getSharedInstance().getNodeWithTag(tag);
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNode = extractNodeArgs();
        if(mNode == null)
            return;

        if(mNode.isConnected())
            enableNeededNotification(mNode);
        else
            mNode.addNodeStateListener(new Node.NodeStateListener() {
                @Override
                public void onStateChange(Node node, Node.State newState, Node.State prevState) {
                    if(newState == Node.State.Connected) {
                        enableNeededNotification(node);
                        node.removeNodeStateListener(this);
                    }
                }
            });
    }

    @Override
    public void showNodes(final List<SensorNode> nodes) {
        FragmentUtil.runOnUiThread(this,() ->{
            mAdapter.displayNodeList(nodes);
            mRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void showNodeDetails(SensorNode node) {
        Fragment fragment = NodeSensorsFragment.instantiate(mNode,node.getAddress());
        //fragment.show(getFragmentManager(),"SensorDatails");
        getFragmentManager().beginTransaction()
                .replace(R.id.lowpan_netStatus_fragment,fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestTimeout() {
        FragmentUtil.runOnUiThread(this,() ->
                Toast.makeText(getActivity(),R.string.lowpan_netStatus_error,Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSensorNodeSelected(SensorNode node) {
        mPresenter.onNodeSelected(node);
    }

    private void showTopology(){
        Fragment fragment = NetworkTopologyFragment.instantiate(mNode);
        getFragmentManager().beginTransaction()
                .replace(R.id.lowpan_netStatus_fragment,fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPresenter!=null)
            mPresenter.stopNodeRefresh();
    }

    @Override
    public void showSwitchAllOn() {
        FragmentUtil.runOnUiThread(this,() -> {
            mSwitchLedOff.setVisible(false);
            mSwitchLedOn.setVisible(true);
        });
    }

    @Override
    public void showSwitchAllOff() {
        FragmentUtil.runOnUiThread(this,() -> {
            mSwitchLedOff.setVisible(true);
            mSwitchLedOn.setVisible(false);
        });
    }

    @Override
    public void showSwitchUpdateFail() {
        FragmentUtil.runOnUiThread(this,() ->
                Toast.makeText(getActivity(),R.string.lowpan_netStatus_ledError,Toast.LENGTH_SHORT).show());
    }
}

