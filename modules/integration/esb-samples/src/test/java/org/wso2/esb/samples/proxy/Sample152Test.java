/*
*  Copyright (c) WSO2 Inc. (http://wso2.com) All Rights Reserved.

  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

package org.wso2.esb.samples.proxy;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import org.wso2.esb.integration.ESBIntegrationTestCase;
import org.wso2.esb.integration.axis2.SampleAxis2Server;
import org.wso2.esb.integration.axis2.StockQuoteClient;
import static org.testng.Assert.assertTrue;

public class Sample152Test extends ESBIntegrationTestCase {

    protected Log log = LogFactory.getLog(this.getClass());

    private StockQuoteClient axis2Client;

    public void init() throws Exception {
        axis2Client = new StockQuoteClient();
        launchBackendAxis2Service(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
    }

    @Test(groups = {"wso2.esb"}, description = "Sample 152: Switching transports and message format from SOAP to REST/POX")
    public void testSOAPtoPOXProxyService() throws Exception {

        loadSampleESBConfiguration(152);

        OMElement response = axis2Client.sendSimpleStockQuoteRequest("https://localhost:8243/services/StockQuoteProxy",
                null, "WSO2");

        assertTrue(response.toString().contains("WSO2"));
        log.info("Response : " + response.toString());
    }

}
