package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Paginated orders by customer
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Page<Order> findDistinctByItems_Product_Seller_Id(Long sellerId,Pageable pageable);

    // Paginated orders by order status
    Page<Order> findByOrderStatus(Order.OrderStatus status, Pageable pageable);

    // Paginated orders by payment status
    Page<Order> findByPaymentStatus(Order.PaymentStatus status, Pageable pageable);
}

