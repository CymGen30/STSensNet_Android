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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.st.BlueSTSDK.Manager;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.Utils.ConnectionOption;
import com.st.BlueSTSDK.gui.ConnectionStatusView.ConnectionStatusController;
import com.st.BlueSTSDK.gui.ConnectionStatusView.ConnectionStatusView;
import com.st.BlueSTSDK.gui.NodeConnectionService;
import com.st.SensNet.R;
import com.st.BlueSTSDK.gui.ConnectionStatusView.ConnectionStatusContract;
import com.st.SensNet.net6LoWPAN.networkStatus.NetworkStatusFragment;

public class Demo6LowPANActivity extends AppCompatActivity {

    private static final String NODE_TAG = Demo6LowPANActivity.class.getName()+".NODE_TAG";
    private static final String CONNECTION_PARAM = Demo6LowPANActivity.class.getName()+".CONNECTION_PARAM";

    /**
     * create an intent for start this activity
     *
     * @param c          context used for create the intent
     * @param node       node to use for the demo
     * @return intent for start a demo activity that use the node as data source
     */
    public static Intent getStartIntent(Context c, @NonNull Node node, ConnectionOption option) {
        Intent i = new Intent(c, Demo6LowPANActivity.class);

        i.putExtra(NODE_TAG,node.getTag());
        i.putExtra(CONNECTION_PARAM,option);

        return i;
    }//getStartIntent

    private ConnectionStatusContract.View mConnectionView;
    private Node mNode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo6_low_pan);

        mConnectionView =
                findViewById(R.id.demo_connectionStatus);

        String nodeTag = getIntent().getStringExtra(NODE_TAG);

        mNode = Manager.getSharedInstance().getNodeWithTag(nodeTag);

        if(savedInstanceState==null) {
            NetworkStatusFragment fragment = NetworkStatusFragment.getInstance(mNode);
            getFragmentManager().beginTransaction()
                    .add(R.id.lowpan_netStatus_fragment, fragment)
                    .commit();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        ConnectionOption option = getIntent().getParcelableExtra(CONNECTION_PARAM);
        ConnectionStatusController mConnectionStatusController = new ConnectionStatusController(mConnectionView, mNode);
        getLifecycle().addObserver(mConnectionStatusController);
        NodeConnectionService.connect(this,mNode, option);
    }

    private void disconnectOnBack(){
        if(getFragmentManager().getBackStackEntryCount() == 0)
            NodeConnectionService.disconnect(this,mNode);
    }

    @Override
    public void onBackPressed() {
        disconnectOnBack();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            disconnectOnBack();
        }
        return super.onOptionsItemSelected(item);
    }
}
