package org.example.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders", schema = "kafka_orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            updatable = false)
    private Long id;
    @Column(name = "product_name",
            nullable = false)
    private String productName;
    @Column(name = "bar_code",
            nullable = false)
    private String barCode;
    @Column(name = "quantity",
            nullable = false)
    private int quantity;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "order_date",
            nullable = false, updatable = false)
    private LocalDateTime orderDate;
    @Column(name = "status",
            nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
