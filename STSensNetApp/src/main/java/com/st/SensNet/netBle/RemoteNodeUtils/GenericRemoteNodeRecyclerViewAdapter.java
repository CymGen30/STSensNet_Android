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

package com.st.SensNet.netBle.RemoteNodeUtils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder.GenericRemoteNodeViewHolder;
import com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder.GenericRemoteNodeViewHolder.GenericRemoteNodeViewCallback;
import com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder.RemoteNodeViewHolder;
import com.st.SensNet.netBle.RemoteNodeUtils.ViewHolder.UnknownDataViewHolder;
import com.st.SensNet.netBle.RemoteNodeUtils.data.GenericRemoteNode;

/**
 * adapter class used for display the EnviromentalRemoteNode information in a RecyclerView
 */
public class GenericRemoteNodeRecyclerViewAdapter extends
        RecyclerView.Adapter<RemoteNodeViewHolder> {

    private static final int ENVIRONMENTAL_VIEW_TYPE =0;
    private static final int UNKNOWN_VIEW_TYPE =1;

    private GenericRemoteNodeViewCallback mCallback;
    private SparseArray<GenericRemoteNode> mItems;

    /**
     * Create an adapter
     * @param callback callback to use for handle the user interaction
     * @param items collection of item to display
     */
    public GenericRemoteNodeRecyclerViewAdapter(@NonNull GenericRemoteNodeViewCallback callback,
                                                SparseArray<GenericRemoteNode> items) {
        mItems=items;
        mCallback=callback;
    }

    @Override
    public int getItemViewType(int position) {
        GenericRemoteNode item = mItems.valueAt(position);
        if (item.hasUnknownData())
            return UNKNOWN_VIEW_TYPE;
        else
            return ENVIRONMENTAL_VIEW_TYPE;
    }

    @Override
    public RemoteNodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == UNKNOWN_VIEW_TYPE)
            return UnknownDataViewHolder.build(parent);
        else
            return GenericRemoteNodeViewHolder.build(parent,mCallback);
    }

    @Override
    public void onBindViewHolder(final RemoteNodeViewHolder holder, int position) {
        GenericRemoteNode node = mItems.valueAt(position);
        if(holder instanceof UnknownDataViewHolder)
            UnknownDataViewHolder.onBindViewHolder((UnknownDataViewHolder)holder,node);
        else
            GenericRemoteNodeViewHolder.onBindViewHolder((GenericRemoteNodeViewHolder) holder,node);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
