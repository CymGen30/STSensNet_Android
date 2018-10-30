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

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.SensNet.R;
import com.st.SensNet.net6LoWPAN.SensorNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SensorNodeRecyclerViewAdapter extends RecyclerView.Adapter<SensorNodeRecyclerViewAdapter.ViewHolder> {

    public interface AdapterInteractionListener{
        void onSensorNodeSelected(SensorNode node);
    }

    private List<SensorNode> mValues = new ArrayList<>();
    private final AdapterInteractionListener mListener;

    SensorNodeRecyclerViewAdapter(AdapterInteractionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_6lowpan_node_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        SensorNode item = mValues.get(position);
        holder.mItem = item;
        holder.mIdView.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onSensorNodeSelected(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<SensorNode> getDisplayedNodes() {
        return mValues;
    }

    public void displayNodeList(List<SensorNode> nodes){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SensorNodeDiffCallback(mValues, nodes));
        diffResult.dispatchUpdatesTo(this);
        mValues=nodes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        SensorNode mItem;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.sensorNode_id);
        }
    }

    private static class SensorNodeDiffCallback extends DiffUtil.Callback{

        private final List<SensorNode> mOldList;
        private final List<SensorNode> mNewList;

        private SensorNodeDiffCallback(List<SensorNode> oldList, List<SensorNode> newList) {
            this.mOldList = oldList;
            this.mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).getAddress()
                    .equals(mNewList.get(newItemPosition).getAddress());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            //we show only the address, so it not important that the sensor are different
            return true;
        }
    }
}
