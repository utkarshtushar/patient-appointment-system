package com.appointment.system.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

    public static final String APPOINTMENT_BOOKING_QUEUE = "appointment.booking.queue";
    public static final String APPOINTMENT_BOOKING_EXCHANGE = "appointment.booking.exchange";
    public static final String APPOINTMENT_BOOKING_ROUTING_KEY = "appointment.booking.key";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";

    @Bean
    public Queue appointmentBookingQueue() {
        return QueueBuilder.durable(APPOINTMENT_BOOKING_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public TopicExchange appointmentBookingExchange() {
        return new TopicExchange(APPOINTMENT_BOOKING_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding appointmentBookingBinding() {
        return BindingBuilder
                .bind(appointmentBookingQueue())
                .to(appointmentBookingExchange())
                .with(APPOINTMENT_BOOKING_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
