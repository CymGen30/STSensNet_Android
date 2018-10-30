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
package com.st.SensNet.net6LoWPAN.networkTopology;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.st.BlueSTSDK.Manager;
import com.st.BlueSTSDK.Node;
import com.st.SensNet.R;
import com.st.SensNet.net6LoWPAN.features.Feature6LoWPANProtocol;
import com.st.SensNet.net6LoWPAN.util.FragmentUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tellh.com.recyclertreeview_lib.LayoutItemType;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;
import tellh.com.recyclertreeview_lib.TreeViewBinder;


public class NetworkTopologyFragment extends Fragment implements NetworkTopologyContract.View {

    private static final String BOARDER_ROUTER = NetworkTopologyFragment.class.getCanonicalName()+".BOARDER_ROUTER";

    private NetworkTopologyContract.Presenter mPresenter;

    public static NetworkTopologyFragment instantiate(Node router){
        NetworkTopologyFragment fragment = new NetworkTopologyFragment();

        Bundle args = new Bundle();
        args.putString(BOARDER_ROUTER,router.getTag());
        fragment.setArguments(args);
        return fragment;
    }

    private TreeViewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle boundle = getArguments();
        Node n = Manager.getSharedInstance().getNodeWithTag(boundle.getString(BOARDER_ROUTER));
        Feature6LoWPANProtocol boarderRouter = n.getFeature(Feature6LoWPANProtocol.class);

        mPresenter = new NetworkTopologyPresenter(this,boarderRouter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_6lowpan_topology2, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.lowpan_topologyNodeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new TreeViewAdapter(Collections.singletonList(new NetworkNodeViewBinder()));
        mAdapter.setOnTreeNodeListener(new NetworkNodeClickLister());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.requestNodeTopology();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AddressValue implements LayoutItemType {
        public String address;

        AddressValue(String dirName) {
            this.address = dirName;
        }

        @Override
        public int getLayoutId() {
            return R.layout.fragment_6lowpan_topology_item2;
        }
    }

    private TreeNode createTree(List<NetworkTopologyContract.NetworkNodeData> nodes){
        Map<String,TreeNode<AddressValue>> flatTree= new HashMap<>(nodes.size());

        TreeNode root = null;
        for(NetworkTopologyContract.NetworkNodeData node : nodes){
            String address = node.address;
            TreeNode<AddressValue> treeNode = new TreeNode<>(new AddressValue(address));
            if(!node.routeToRoot.isEmpty()) { //has a parent
                String parentAddress = node.routeToRoot.get(node.routeToRoot.size()-1);
                TreeNode<AddressValue> parentNode = flatTree.get(parentAddress);
                parentNode.addChild(treeNode);
            }else
                root=treeNode;

            flatTree.put(address,treeNode);
        }
        return root;
    }

    @Override
    public void showNodes(List<NetworkTopologyContract.NetworkNodeData> nodes) {
        TreeNode root = createTree(nodes);
        FragmentUtil.runOnUiThread(this, () -> mAdapter.refresh(Collections.singletonList(root)));
    }

    @Override
    public void onRequestFail() {
        FragmentUtil.runOnUiThread(this, () ->
                Toast.makeText(getActivity(), R.string.lowpan_topology_error,
                        Toast.LENGTH_SHORT).show());
    }

    public static class NetworkNodeViewBinder extends TreeViewBinder<NetworkNodeViewBinder.ViewHolder> {
        @Override
        public ViewHolder provideViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        public void bindView(ViewHolder viewHolder, int i, TreeNode treeNode) {
            viewHolder.setData(treeNode);
        }

        @Override
        public int getLayoutId() {
            return R.layout.fragment_6lowpan_topology_item2;
        }


        public static class ViewHolder extends TreeViewBinder.ViewHolder{

            private final TextView address;
            private final ImageView expandImage;

            public ViewHolder(View rootView) {
                super(rootView);
                address = rootView.findViewById(R.id.topologyAdressLabel);
                expandImage = rootView.findViewById(R.id.lowpan_topology_expandIcon);
            }

            public void setData(TreeNode node){
                AddressValue addressValue = (AddressValue)node.getContent();
                address.setText(addressValue.address);
                setExpandImage(node.isExpand());
                if(node.isLeaf()){
                    expandImage.setVisibility(View.INVISIBLE);
                }else {
                    expandImage.setVisibility(View.VISIBLE);
                }
            }

            public void setExpandImage(boolean isExpanded) {
                @DrawableRes int image = isExpanded ?
                        R.drawable.ic_expand_less_24dp : R.drawable.ic_expand_more_24dp;
                expandImage.setImageResource(image);
            }
        }

    }

    //need to change the expand image when a item is clicked
    public static class NetworkNodeClickLister implements TreeViewAdapter.OnTreeNodeListener{

        @Override
        public boolean onClick(TreeNode treeNode, RecyclerView.ViewHolder viewHolder) {
            NetworkNodeViewBinder.ViewHolder holder = (NetworkNodeViewBinder.ViewHolder) viewHolder;
            holder.setExpandImage(!treeNode.isExpand());
            return false;
        }

        @Override
        public void onToggle(boolean b, RecyclerView.ViewHolder viewHolder) {
            //NetworkNodeViewBinder.ViewHolder holder = (NetworkNodeViewBinder.ViewHolder) viewHolder;
            //holder.setExpandImage(b);
        }
    }

}
