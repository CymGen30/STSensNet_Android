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
package com.st.SensNet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Manager;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.Utils.ConnectionOption;
import com.st.BlueSTSDK.Utils.InvalidFeatureBitMaskException;
import com.st.SensNet.net6LoWPAN.Demo6LowPANActivity;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.features.SixLoWPANBLEGatewayConfiguration;
import com.st.SensNet.netBle.DemosBleActivity;
import com.st.STM32WB.p2pDemo.DemoSTM32WBActivity;
import com.st.SensNet.netBle.features.CommandFeature;
import com.st.SensNet.netBle.features.GenericRemoteFeature;
import com.st.STM32WB.p2pDemo.Peer2PeerDemoConfiguration;

/**
 * Display only the nodes that are running with a Nucleo of type 0x81 (BleStar1 FW)
 */
public class NodeListActivity extends com.st.BlueSTSDK.gui.NodeListActivity {

    private static final byte BLE_STAR_NODE_ID =(byte)0x81;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SparseArray<Class<? extends Feature>> temp = new SparseArray<>();
        temp.append(0x00080000, GenericRemoteFeature.class);
        temp.append(0x00040000, CommandFeature.class);
        temp.append(0x00020000, Feature6LoWPANProtocol.class);
        //todo add stm32wb
        try {
            Manager.addFeatureToNode(BLE_STAR_NODE_ID,temp);
        } catch (InvalidFeatureBitMaskException e) {
            e.printStackTrace();
        }

        SixLoWPANBLEGatewayConfiguration.registerFeature();

    }

    private final static String DIALOG_SHOWN = NodeListActivity.class.getCanonicalName()+".DIALOG_SHOWN";

    private void setDialogShown(final SharedPreferences prefs,boolean showNextTime){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(DIALOG_SHOWN, showNextTime);
        editor.apply();
    }

    private View loadInfoDialogView(){
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        View infoLayout = LayoutInflater.from(this).inflate(R.layout.dialog_update_fw_warnings, null);
        ((CheckBox)infoLayout.findViewById(R.id.dialogInfo_showAgainCheckBox))
                .setOnCheckedChangeListener((compoundButton, checked) -> setDialogShown(prefs,checked));
        return infoLayout;
    }

    private void showInfoDialog(){
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs.contains(DIALOG_SHOWN))
            return;

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.infoDialog_title);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setView(loadInfoDialogView());

        //Other dialog code
        alertDialog.setPositiveButton(android.R.string.ok,null);

        alertDialog.setNeutralButton(R.string.infoDialog_siteButtonName, (dialogInterface, i) -> {
            String url = getResources().getString(R.string.infoDialog_updateUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        AlertDialog dialog = alertDialog.show();

        // Make the textview clickable. Must be called after show()
        ((TextView)dialog.findViewById(R.id.dialogInfo_message))
                .setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void onStart() {
        super.onStart();
        showInfoDialog();
    }

    private boolean isBleStarNode(Node n){
        return n.getType() == Node.Type.NUCLEO && n.getTypeId() == BLE_STAR_NODE_ID;
    }

    @Override
    public boolean displayNode(Node n) {
        return Peer2PeerDemoConfiguration.isValidNode(n) ||
                isBleStarNode(n) ||
                SixLoWPANBLEGatewayConfiguration.isValidNode(n);
    }

    /**
     * when a node is select start the demo activity for that node
     * @param n selected node
     */
    @Override
    public void onNodeSelected(Node n) {

        ConnectionOption option = ConnectionOption.builder()
                .enableAutoConnect(true)
                .resetCache(clearCacheIsSelected())
                .build();
        if(Peer2PeerDemoConfiguration.isValidNode(n)){
            n.addExternalCharacteristics(Peer2PeerDemoConfiguration.getCharacteristicMapping());
            startActivity(DemoSTM32WBActivity.getStartIntent(this,n,option));
        }else if (isBleStarNode(n)) {
            startActivity(DemosBleActivity.getStartIntent(this, n, option));
        }else if (SixLoWPANBLEGatewayConfiguration.isValidNode(n)){
            startActivity(Demo6LowPANActivity.getStartIntent(this,n,option));
        }

    }

}
