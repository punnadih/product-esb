/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.nhttp.transport.test;

import static org.testng.Assert.assertEquals;
import org.testng.Assert;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.customservers.webserver.SimpleWebServer;
import org.wso2.carbon.automation.core.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.core.utils.httpserverutils.SimpleHttpClient;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.annotations.SetEnvironment;
import org.wso2.carbon.automation.core.utils.httpserverutils.SimpleHttpClient;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.esb.util.SimulatedService;
import org.wso2.carbon.esb.util.ESBTestConstant;
import org.wso2.carbon.esb.util.WireMonitorServer;

public class ContentTypeCharsetTestCase extends ESBIntegrationTest {

	private Log log = LogFactory.getLog(ContentTypeCharsetTestCase.class);

	// public WireMonitorServer wireServer;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		super.init();

		loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/nhttp_transport"
		                                  + "/content_type_charset_synapse.xml");

		// wireServer = new WireMonitorServer(8991);
	}

	@Test(groups = { "wso2.esb" }, description = "Test for charset value proprty in the header response")
	public void testReturnContentType() throws Exception {

		int port = 9005;

		String contentType = "application/xml;charset=UTF-8";

		String charset = "charset";

		SimpleWebServer simpleWebServer = new SimulatedService(port, 200);

		try {
			simpleWebServer.start();

			SimpleHttpClient httpClient = new SimpleHttpClient();

			Map<String, String> headers = new HashMap<String, String>();

			headers.put("content-type", contentType);

			HttpResponse response = httpClient.doGet(getProxyServiceURL("FooProxy"), headers);

			simpleWebServer.terminate();

			String contentTypeData = response.getEntity().getContentType().getValue();

			Assert.assertTrue(contentTypeData.contains(charset));

			if (contentTypeData.contains(charset)) {

				String[] pairs = contentTypeData.split(";");

				for (String pair : pairs) {

					if (pair.contains(charset)) {

						String[] charsetDetails = pair.split("=");

						Assert.assertTrue(!charsetDetails[1].equals(""));

					}
				}
			}
		} finally {

			simpleWebServer.terminate();

			waitForPortCloser(port);
		}
	}

	@AfterClass(alwaysRun = true)
	public void stop() throws Exception {
		cleanup();
	}

	public boolean waitForPortCloser(int port) throws UnknownHostException {

		long time = System.currentTimeMillis() + 5000;

		boolean isPortAvailable = true;

		while (System.currentTimeMillis() < time) {

			isPortAvailable =
			                  ClientConnectionUtil.isPortOpen(port, InetAddress.getLocalHost()
			                                                                   .getHostName());

			if (!isPortAvailable) {

				return isPortAvailable;
			}
		}
		return isPortAvailable;
	}
}
