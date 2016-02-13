/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package si.nlb.server;

import javax.servlet.annotation.WebServlet;

import si.nlb.client.GwtRpcTest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@WebServlet("/si.nlb.Demo/GwtRpcTest")
public class GwtRpcTestImpl extends RemoteServiceServlet implements GwtRpcTest 
{
	@Override
	public String testingRPC(String user) 
	{
		System.out.println("GWt_RPC method invoked: " + user);
		return "hello from testingRPC ";
	}
}
