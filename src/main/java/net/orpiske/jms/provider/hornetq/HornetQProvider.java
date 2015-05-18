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

public class HornetQProvider extends AbstractProvider {
    private final EmbeddedJMS jmsServer = new EmbeddedJMS();
    private TransportConfiguration transportConfiguration;

    public HornetQProvider() {

    }

    @Override
    public void start() throws ProviderInitializationException {
        try {
            jmsServer.start();
        } catch (Exception e) {
            throw new ProviderInitializationException("Unable to start the " +
                    "embedded broker", e);
        }
    }

    @Override
    public void stop() {
        try {
            jmsServer.stop();
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


        return null;
    }


    public EmbeddedJMS getJmsServer() {
        return jmsServer;
    }
}
