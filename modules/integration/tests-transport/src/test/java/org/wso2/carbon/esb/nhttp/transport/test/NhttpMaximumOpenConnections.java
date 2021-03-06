/**
 *  Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.nhttp.transport.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.annotations.SetEnvironment;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import org.wso2.carbon.esb.ESBIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.io.File.separator;
import static org.testng.Assert.assertTrue;

public class NhttpMaximumOpenConnections extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManagerProp;
    private ServerConfigurationManager serverConfigurationManagerAxis2;
    private final int CONCURRENT_CLIENTS = 20;
    private MaximumOpenConnectionsClient[] maxOpenConnectionClients;
    private Thread[] clients;
    private List list;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

	serverConfigurationManagerProp = new ServerConfigurationManager(esbServer.getBackEndUrl());
        String nhttpFile = ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME) +
                separator + "synapseconfig" + separator + "MaxOpenConnections" + separator
                + "nhttp.properties";
        File srcFile = new File(nhttpFile);


        serverConfigurationManagerProp.applyConfiguration(srcFile);

        serverConfigurationManagerAxis2 = new ServerConfigurationManager(esbServer.getBackEndUrl());
        String nhttpAxis2xml = ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME) +
                separator + "synapseconfig" + separator + "MaxOpenConnections" + separator + "nhttp"
                + separator + "axis2.xml";
        File axis2File = new File(nhttpAxis2xml);
        serverConfigurationManagerAxis2.applyConfiguration(axis2File);

        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/MaxOpenConnections/max_open_connections.xml");
        list = Collections.synchronizedList(new ArrayList());
        maxOpenConnectionClients = new MaximumOpenConnectionsClient[CONCURRENT_CLIENTS];
        clients = new Thread[CONCURRENT_CLIENTS];
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.all})
    @Test(groups = "wso2.esb", description = "NHTTP Test Maximum Open Connections")
    public void testMaximumConnections() throws InterruptedException {
	initClients();         //initialising Axis2Clients
	startClients();
	int aliveCount = 0;
	Calendar startTime = Calendar.getInstance();
	while (aliveCount < CONCURRENT_CLIENTS) {
	    if ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) > 120000) {
		break;
	    }
	    if (clients[aliveCount].isAlive()) {
		aliveCount = 0;
		continue;
	    }
	    aliveCount++;
	}

//    int refusedCount = 0;
//    for(MaximumOpenConnectionsClient client: maxOpenConnectionClients) {
//        if(client.getConnectionRefused()) {
//            refusedCount += 1;
//        }
//    }
//        System.out.println("Refused count is: " + refusedCount);
//	  System.out.println("COUNT: " + MaximumOpenConnectionsClient.getDeniedRequests());
    	assertTrue(MaximumOpenConnectionsClient.getDeniedRequests() >= 1, "(NHTTP) No Connections Rejected by max_open_connection limit - max_open_connections limit will not be exact.");
        //assertTrue(refusedCount >= 1, "(NHTTP) No Connections Rejected by max_open_connection limit - max_open_connections limit will not be exact.");

    }

    private void initClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            maxOpenConnectionClients[i] = new MaximumOpenConnectionsClient(getProxyServiceURL("MaxOpenConnectionsTest"));
        }
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i] = new Thread(maxOpenConnectionClients[i]);
        }
    }

    private void startClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i].start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
    }

    /**
     * Replace nhttp.properties file and remove test client threads.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void atEnd() throws Exception {
	maxOpenConnectionClients = null;
	clients = null;
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManagerProp.restoreToLastConfiguration();
            serverConfigurationManagerProp = null;
            serverConfigurationManagerAxis2.restoreToLastConfiguration();
            serverConfigurationManagerAxis2 = null;
        }

    }
}
