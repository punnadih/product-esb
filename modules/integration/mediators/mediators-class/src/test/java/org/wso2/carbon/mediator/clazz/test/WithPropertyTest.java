package org.wso2.carbon.mediator.clazz.test;

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

/*test case for class mediator test with property*/

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.wso2.carbon.integration.core.AuthenticateStub;
import org.wso2.carbon.integration.core.FrameworkSettings;
import org.wso2.carbon.integration.core.TestTemplate;
import org.wso2.carbon.integration.core.utils.ArtifactReader;
import org.wso2.carbon.integration.core.utils.StockQuoteClient;
import org.wso2.carbon.mediation.configadmin.stub.ConfigServiceAdminStub;


public class WithPropertyTest extends TestTemplate {

    private static final Log log = LogFactory.getLog(WithPropertyTest.class);

    @Override
    public void init() {
        log.info("Initializing class Mediator Test class ");
        log.debug("Class Mediators Test Initialized");
    }


    @Override
    public void runSuccessCase() {
        log.debug("Running SuccessCase ");
        StockQuoteClient stockQuoteClient = new StockQuoteClient();

        try {

            AuthenticateStub authenticateStub = new AuthenticateStub();
            ConfigServiceAdminStub configServiceAdminStub = new ConfigServiceAdminStub("https://" + FrameworkSettings.HOST_NAME + ":" + FrameworkSettings.HTTPS_PORT + "/services/ConfigServiceAdmin");
            authenticateStub.authenticateAdminStub(configServiceAdminStub, sessionCookie);
            ArtifactReader artifactReader = new ArtifactReader();

            OMElement omElement = artifactReader.getOMElement(WithPropertyTest.class.getResource("/WithPropertySource.xml").getPath());
            configServiceAdminStub.updateConfiguration(omElement);

            OMElement result = stockQuoteClient.stockQuoteClientforProxy("http://" + FrameworkSettings.HOST_NAME + ":" + FrameworkSettings.HTTP_PORT, null, "IBM");

            System.out.println(result);

            log.info(result);
            if (!result.toString().contains("MSFT")) {

                Assert.fail("Class mediator doesn't work");
                log.error("Class mediator doesn't work");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("class mediator doesn't work : " + e.getMessage());

        }

    }

    @Override
    public void runFailureCase() {


    }

    @Override
    public void cleanup() {}
}
