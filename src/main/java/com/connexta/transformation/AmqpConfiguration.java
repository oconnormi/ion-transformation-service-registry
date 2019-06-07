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
package com.connexta.transformation;

import com.connexta.transformation.api.ServiceRegistryConsumer;
import com.connexta.transformation.impl.ServiceRegistryConsumerImpl;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Spring configuration for AMQP. Provides @Bean methods to be used for bean definitions. */
@Configuration
@EnableRabbit
@PropertySource("classpath:application.properties")
public class AmqpConfiguration {
  @Value("${spring.rabbitmq.host}")
  private String messageBrokerHost;

  @Value("${spring.rabbitmq.port}")
  private int messageBrokerPort;

  @Value("${spring.rabbitmq.virtual-host}")
  private String virtualHost;

  @Value("${spring.rabbitmq.username}")
  private String username;

  @Value("${spring.rabbitmq.password}")
  private String password;

  @Value("${spring.rabbitmq.template.exchange}")
  private String exchangeName;

  @Value("${spring.rabbitmq.request-queue-name}")
  private String requestQueueName;

  @Value("${spring.rabbitmq.service-queue-name}")
  private String serviceQueueName;

  @Value("${spring.rabbitmq.request-routing-key}")
  private String requestRoutingKey;

  @Value("${spring.rabbitmq.service-routing-key}")
  private String serviceRoutingKey;

  private static final String CONSUME_METHOD = "consumeFromQueue";

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory(messageBrokerHost, messageBrokerPort);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setVirtualHost(virtualHost);
    connectionFactory.setPublisherReturns(true);
    connectionFactory.setPublisherConfirms(true);
    return connectionFactory;
  }

  @Bean
  public Queue requestQueue() {
    return new Queue(requestQueueName, true);
  }

  @Bean
  public Queue serviceQueue() {
    return new Queue(serviceQueueName, true);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchangeName);
  }

  @Bean
  public Binding requestBinding() {
    return BindingBuilder.bind(requestQueue()).to(exchange()).with(requestRoutingKey);
  }

  @Bean
  public Binding serviceBinding() {
    return BindingBuilder.bind(serviceQueue()).to(exchange()).with(serviceRoutingKey);
  }

  @Bean
  public AmqpTemplate amqpTemplate() {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    rabbitTemplate.setExchange(exchangeName);
    rabbitTemplate.setRoutingKey(serviceRoutingKey);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public SimpleMessageListenerContainer container(
      ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(requestQueueName);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  public MessageListenerAdapter listenerAdapter() {
    ServiceRegistryConsumer consumer = new ServiceRegistryConsumerImpl(amqpTemplate());
    return new MessageListenerAdapter(consumer, CONSUME_METHOD);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
