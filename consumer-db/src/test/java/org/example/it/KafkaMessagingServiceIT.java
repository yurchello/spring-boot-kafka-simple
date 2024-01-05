package org.example.it;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.entity.Order;
import org.example.entity.Status;
import org.example.repository.OrdersRepository;
import org.example.service.messaging.OrderEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class KafkaMessagingServiceIT {

    private static final Long ORDER_ID = 1L;
    private static final String TOPIC_NAME_SEND_ORDER = "send-order-event";

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432)
            .withReuse(true);
    @Container
    static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.4"))
                    .withEmbeddedZookeeper()
                    .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9093 ,BROKER://0.0.0.0:9092")
                    .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:PLAINTEXT")
                    .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "BROKER")
                    .withEnv("KAFKA_BROKER_ID", "1")
                    .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
                    .withEnv("KAFKA_LOG_FLUSH_INTERVAL_MESSAGES", Long.MAX_VALUE + "")
                    .withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0");


    static {
        Startables.deepStart(Stream.of(postgreSQLContainer, kafkaContainer)).join();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Autowired
    private OrdersRepository ordersRepository;

    @Test
    void shouldSaveOrder() throws InterruptedException {
        //given
        KafkaTemplate<String, OrderEvent> kafkaTemplate = configKafkaTemplate();
        OrderEvent orderEvent = getOrderEvent();
        Order order = getOrder();

        //when
        SECONDS.sleep(5);
        kafkaTemplate.send(TOPIC_NAME_SEND_ORDER, orderEvent.getBarCode(), orderEvent);
        SECONDS.sleep(5);

        //then
        Order orderFromDB = ordersRepository.findById(ORDER_ID).get();
        assertEquals(orderFromDB.getId(), ORDER_ID);
        assertEquals(orderFromDB.getProductName(), order.getProductName());
        assertEquals(orderFromDB.getBarCode(), order.getBarCode());
        assertEquals(orderFromDB.getQuantity(), order.getQuantity());
        assertEquals(orderFromDB.getPrice(), order.getPrice().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(orderFromDB.getAmount(), order.getAmount().setScale(2));
        assertEquals(orderFromDB.getOrderDate().getYear(), order.getOrderDate().getYear());
        assertEquals(orderFromDB.getStatus(), order.getStatus());
    }

    private <K, V> KafkaTemplate<K, V> configKafkaTemplate() {
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<K, V> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(producerFactory);
    }

    private OrderEvent getOrderEvent() {
        return new OrderEvent(
                "pensil",
                "0000003",
                100,
                new BigDecimal("0.99")
        );
    }

    private Order getOrder() {
        return new Order(
                1L,
                "pensil",
                "0000003",
                100,
                new BigDecimal("0.99"),
                new BigDecimal("99.0"),
                LocalDateTime.now(),
                Status.APPROVED
        );
    }
}
