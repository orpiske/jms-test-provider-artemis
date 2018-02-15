/**
 Copyright 2014 Otavio Rodolfo Piske

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.orpiske.jms.provider.configuration;

import net.orpiske.jms.provider.ProviderConfiguration;
import net.orpiske.jms.provider.hornetq.ArtemisProvider;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.ConnectionFactoryConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;

/**
 * This class configures the provider once it has been initialized
 */
public class ArtemisConfiguration implements
        ProviderConfiguration<ArtemisProvider>
{
    private static final Logger logger = LoggerFactory.getLogger
            (ArtemisConfiguration.class);

    /**
     * The address used by the broker
     */
    public static final String CONNECTOR = "tcp://localhost:61616";


    /**
     * Configure the provider
     * @param provider the provider to configure
     */
    public void configure(ArtemisProvider provider) {
        logger.info("Configuring the provider");

        String path = null;
        URL url = this.getClass().getResource("/");

        /*
          Check if we are running it in within the jar, in which case we
          won't be able to use its location ...
         */

        if (url == null) {
            /*
             ... and, if that's the case, we use the OS temporary directory
             for the data directory
             */
            path = FileUtils.getTempDirectoryPath();
        }
        else {
            path = url.getPath();
        }

        Configuration configuration = null;
        try {
            configuration = new ConfigurationImpl()
                    .setPersistenceEnabled(false)
                    .setJournalDirectory(path)
                    .setSecurityEnabled(false)
                    .addAcceptorConfiguration("tcp", CONNECTOR)
                    .addConnectorConfiguration("connector", CONNECTOR);
        } catch (Exception e) {
            throw new RuntimeException("Unable to add a connector for the "
                    + "service: " + e.getMessage(), e);
        }

        JMSConfiguration jmsConfig = new JMSConfigurationImpl();

        ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl()
                .setName("cf")
                .setConnectorNames(Arrays.asList("connector"))
                .setBindings("cf");

        jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);

        JMSQueueConfiguration requestQueue = new JMSQueueConfigurationImpl()
                .setName("net.orpiske.queue.request")
                .setDurable(false)
                .setBindings("queue/net.orpiske.queue.request");
        jmsConfig.getQueueConfigurations().add(requestQueue);

        JMSQueueConfiguration responseQueue = new JMSQueueConfigurationImpl()
                .setName("net.orpiske.queue.reply")
                .setDurable(false)
                .setBindings("queue/net.orpiske.queue.reply");
        jmsConfig.getQueueConfigurations().add(responseQueue);

        EmbeddedJMS embeddedJMS = provider.getJmsServer();
        embeddedJMS.setConfiguration(configuration);
        embeddedJMS.setJmsConfiguration(jmsConfig);
    }
}
