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

package com.st.SensNet.netBle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.gui.demos.DemoFragment;
import com.st.SensNet.R;
import com.st.SensNet.netBle.features.CommandFeature;

public class DemoFragmentWithCommand extends DemoFragment implements CommandFeature.CommandCallback {

    protected CommandFeature mCommandFeature;
    private boolean mStartScanningMenuItemVisible;
    private boolean mStopScanningMenuItemVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //when we eneable the notification the first one will tell the current status
        mStartScanningMenuItemVisible=false;
        mStopScanningMenuItemVisible=false;

        //ask to add our option to the menu
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_blestar_command, menu);
        menu.findItem(R.id.menu_command_start_scann).setVisible(mStartScanningMenuItemVisible);
        menu.findItem(R.id.menu_command_stop_scann).setVisible(mStopScanningMenuItemVisible);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_command_start_scann) {
            if(mCommandFeature!=null)
                mCommandFeature.startScanning();
            return true;
        }//else
        if (id == R.id.menu_command_stop_scann) {
            if(mCommandFeature!=null)
                mCommandFeature.stopScanning();
            return true;
        }//else

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void enableNeededNotification(@NonNull Node node) {
        mCommandFeature = node.getFeature(CommandFeature.class);
        if(mCommandFeature!=null){
            mCommandFeature.setCommandCallback(this);
            node.enableNotification(mCommandFeature);
        }

    }

    @Override
    protected void disableNeedNotification(@NonNull Node node) {
        if(mCommandFeature!=null){
            node.disableNotification(mCommandFeature);
            mCommandFeature.setCommandCallback(null);
        }
    }

    @Override
    public void onScanIsStarted(CommandFeature command) {
        mStartScanningMenuItemVisible=false;
        mStopScanningMenuItemVisible=true;
        updateGui(new Runnable() {
            @Override
            public void run() {
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onScanIsStopped(CommandFeature command) {
        mStartScanningMenuItemVisible=true;
        mStopScanningMenuItemVisible=false;
        updateGui(new Runnable() {
            @Override
            public void run() {
                getActivity().invalidateOptionsMenu();
            }
        });
    }
}
