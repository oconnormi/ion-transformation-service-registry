/**
 * Copyright (c) Connexta
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package com.connexta.transformation.impl;

import com.connexta.transformation.api.ServiceRegistryConsumer;
import com.connexta.transformation.api.ServiceRegistryProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * Spring component implementation of an AMQP consumer. Takes in an AMQP template. Used to handle
 * consumption of messages from a queue.
 */
@Component
public class ServiceRegistryConsumerImpl implements ServiceRegistryConsumer {
  private final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistryConsumerImpl.class);
  private final ServiceRegistryProducer producer;

  public ServiceRegistryConsumerImpl(AmqpTemplate amqpTemplate) {
    this.producer = new ServiceRegistryProducerImpl(amqpTemplate);
  }

  @Override
  public void consumeFromQueue(byte[] transformRequest) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode requestObj = mapper.readTree(transformRequest);
    String mimeType = requestObj.get("mimeType").toString();

    LOGGER.info(String.format("Querying for mime type %s", mimeType));

    // @TODO - do work (query database)

    producer.publishToQueue(transformRequest);
  }
}
