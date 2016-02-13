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
package si.nlb.client;

import jsinterop.Utils;
import jsinterop.html.Window;
import si.nlb.client.resources.AppResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Demo implements EntryPoint {
	private Button clickMeButton;
	
	public void onModuleLoad() {
		AppResources resources = GWT.create(AppResources.class);
		resources.css().ensureInjected();
		
		RootPanel rootPanel = RootPanel.get();
		Label download = new Label("Download");
		download.addStyleName(resources.css().labelBold());
		rootPanel.add(download);
		rootPanel.add(new HTMLPanel("<br>"));
		clickMeButton = new Button();
		rootPanel.add(clickMeButton);
		clickMeButton.setText("Download");
		clickMeButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) 
			{
				ReportHandler.createReport(null);
//				Window.alert("Hello, GWT World!");
			}
		});
		
		Button btnTimeout = new Button("Download - sleep");
		btnTimeout.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ReportHandler.createReport("timeout");
			}
		});
		rootPanel.add(btnTimeout);
		Button btnLogout = new Button("Download - logout");
		btnLogout.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ReportHandler.createReport("logout");
			}
		});
		rootPanel.add(btnLogout);
		Button btnError = new Button("Download - error");
		btnError.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ReportHandler.createReport("error");
			}
		});
		rootPanel.add(btnError);
		HTMLPanel hr = new HTMLPanel("<hr style='width='100%';'>");
		rootPanel.add(hr);
		Label upload = new Label("Upload");
		upload.addStyleName(resources.css().labelBold());
		rootPanel.add(upload);
		rootPanel.add(new HTMLPanel("<br>"));
		
		new UploadGwt().init(resources);
		rootPanel.add(new HTMLPanel("<br>"));
		new UploadGxt().init(resources);

		rootPanel.add(new HTMLPanel("<hr style='width='100%';'>"));
		Label title = new Label("Testing GWT-RPC");
		title.addStyleName(resources.css().labelItalic());
		rootPanel.add(title);
		Button gwt = new Button("Invoke RPC");
		gwt.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				GwtRpcTest.Util.getInstance().testingRPC("Someone", new AsyncCallback<String>() 
				{
					@Override
					public void onSuccess(String result) 
					{
						Window.alert(result);
					}
					
					@Override
					public void onFailure(Throwable caught) 
					{
						Window.getConsole().error("Error: " + caught.getMessage());
					}
				});
			}
		});
		rootPanel.add(gwt);

	}
}
