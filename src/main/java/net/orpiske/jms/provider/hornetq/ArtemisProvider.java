/**
 * Copyright 2014 Otavio Rodolfo Piske
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.orpiske.jms.provider.hornetq;

import net.orpiske.jms.provider.AbstractProvider;
import net.orpiske.jms.provider.exception.ProviderInitializationException;
import net.orpiske.jms.test.annotations.Consumer;
import net.orpiske.jms.test.annotations.EndPointType;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.jms.server.config.ConnectionFactoryConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;

import javax.jms.*;
import java.util.Arrays;

public class ArtemisProvider extends AbstractProvider {
    private EmbeddedJMS embeddedJMS = new EmbeddedJMS();
    private boolean started;

    public ArtemisProvider() {

    }

    @Override
    public void start() throws ProviderInitializationException {
        if (started) {
            return;
        }

        try {
            embeddedJMS.start();

            connection = newConnection();
            session = newSession();

            started = true;
        } catch (Exception e) {
            throw new ProviderInitializationException("Unable to start the " +
                    "embedded broker", e);
        }
    }

    @Override
    public void stop() {
        if (!started) {
            return;
        }

        try {
            embeddedJMS.stop();
            started = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Connection newConnection() throws ProviderInitializationException {
        ConnectionFactory cf = (ConnectionFactory) embeddedJMS.lookup("cf");

        try {
            return cf.createConnection();
        } catch (JMSException e) {
            throw new ProviderInitializationException("Unable to create a new" +
                    " connection", e);
        }
    }


    public EmbeddedJMS getJmsServer() {
        return embeddedJMS;
    }

    @Override
    protected Destination getDestination(EndPointType type, String address) throws JMSException {
        if (type == EndPointType.TOPIC) {
            if (address.length() == 0) {
                return session.createTopic(null);
            }
            else {
                return (Destination) embeddedJMS.lookup("topic/" + address);
            }
        }
        else {
            if (address.length() == 0) {
                return session.createQueue(null);
            }
        }

        return (Destination) embeddedJMS.lookup("queue/" + address);
    }

    @Override
    public MessageConsumer createConsumer(Consumer consumer) throws JMSException {
        MessageConsumer messageConsumer = super.createConsumer(consumer);

        super.connection.start();
        return messageConsumer;
    }
}
