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
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.jms.server.embedded.EmbeddedJMS;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.HashSet;

public class HornetQProvider extends AbstractProvider {
    private final EmbeddedJMS jmsServer;
    private TransportConfiguration transportConfiguration;
    private boolean started;

    public HornetQProvider() {
        jmsServer = new EmbeddedJMS();
    }

    @Override
    public void start() throws ProviderInitializationException {
        if (started) {
            return;
        }


        try {
            jmsServer.start();

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
            jmsServer.stop();
            started = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TransportConfiguration getTransportConfiguration() {
        return transportConfiguration;
    }

    public void setTransportConfiguration(TransportConfiguration transportConfiguration) {
        this.transportConfiguration = transportConfiguration;
    }

    @Override
    protected Connection newConnection() throws ProviderInitializationException {
        ConnectionFactory cf = (ConnectionFactory)jmsServer.lookup("/cf");


        try {
            return cf.createConnection();
        } catch (JMSException e) {
            throw new ProviderInitializationException("Unable to create a new" +
                    " connection", e);
        }
    }


    public EmbeddedJMS getJmsServer() {
        return jmsServer;
    }
}
