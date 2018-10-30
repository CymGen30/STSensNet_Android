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
package com.st.SensNet.net6LoWPAN.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkTopology {

    /**
     *
     */
    public static class NetworkNode {

        public final NetworkAddress address;

        private NetworkNode parent;

        private ArrayList<NetworkNode> childes;

        public NetworkNode(NetworkAddress address, NetworkNode parent) {
            this.address = address;
            this.parent = parent;
            childes = new ArrayList<>();
        }

        public NetworkNode(NetworkAddress address) {
            this(address,null);
        }

        public boolean isRoot(){
            return parent == null;
        }

        public NetworkNode getParent() {
            return parent;
        }

        public List<NetworkAddress> getRouteToRoot(){
            if(isRoot()){
                return new ArrayList<>();
            }else{
                List<NetworkAddress> parentRoot = parent.getRouteToRoot();
                parentRoot.add(parent.address);
                return parentRoot;
            }
        }

        public int hopeToRoot(){
            if(isRoot())
                return 0;
            else return  1 + parent.hopeToRoot();
        }

        public void addChild(NetworkNode newChild){
            newChild.parent=this;
            childes.add(newChild);
        }

        public Collection<NetworkNode> getChildes(){
            return Collections.unmodifiableCollection(childes);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NetworkNode that = (NetworkNode) o;
            return address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return address.hashCode();
        }

    }

    private Map<NetworkAddress,NetworkNode> allNodes = new HashMap<>();

    public void addConnection(NetworkAddress src, NetworkAddress dest){
        NetworkNode srcNode = null;
        NetworkNode destNode = new NetworkNode(dest);
        if(allNodes.containsKey(src)){
            srcNode = allNodes.get(src);
        }else{
            srcNode = new NetworkNode(src);
            allNodes.put(srcNode.address,srcNode);
        }
        srcNode.addChild(destNode);
        allNodes.put(destNode.address,destNode);
    }

    public NetworkNode getNetworkNode(NetworkAddress address){
        return allNodes.get(address);
    }

    public  NetworkNode getRoot(){
        for (NetworkNode node: allNodes.values()){
            if(node.isRoot())
                return node;
        }
        return null;
    }

    public Collection<NetworkNode> getNodes(){
        return Collections.unmodifiableCollection(allNodes.values());
    }

}
