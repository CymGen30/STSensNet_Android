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

package com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.SensNet.R;
import com.st.SensNet.netBle.RemoteNodeUtils.data.GenericRemoteNode;

/**
 * View Holder that contains the data for a GenericNode that has receved some unknown data
 * It will show this data as an hexadecimal string
 */
public class UnknownDataViewHolder extends RemoteNodeViewHolder {


    static public void onBindViewHolder(final UnknownDataViewHolder holder, GenericRemoteNode item) {
        RemoteNodeViewHolder.onBindViewHolder(holder,item);
        holder.setData(item.getUnknownData());
    }

    static public UnknownDataViewHolder build(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.unknown_data_node_item, parent, false);
        return new UnknownDataViewHolder(view);
    }

    private TextView mDataLabel;
    private TextView mTypeIdLabel;

    public UnknownDataViewHolder(View itemView) {
        super(itemView,
                itemView.findViewById(R.id.remoteIdLabel));
        mDataLabel = itemView.findViewById(R.id.unknownDataLablel);
        mTypeIdLabel = itemView.findViewById(R.id.unknownTypeIDLablel);
    }

    public void setData(@Nullable byte[] data){
        if(data==null || data.length==0)
            return;

        String typeId= mTypeIdLabel.getContext().getString(R.string.unknownNode_typeIDFormat,data[0]);


        StringBuilder dataString = new StringBuilder();
        for (int i = 1; i<data.length;i++) {
            dataString.append(String.format("%02X ",data[i]));
        }//for

        String dataValue = mDataLabel.getContext().getString(R.string.unknownNode_DataFormat,dataString.toString());

        mDataLabel.setText(dataValue);
        mTypeIdLabel.setText(typeId);
    }
}
