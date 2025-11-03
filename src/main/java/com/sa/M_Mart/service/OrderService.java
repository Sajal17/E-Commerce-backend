package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CreateOrderRequest;
import com.sa.M_Mart.dto.OrderDTO;
import org.springframework.data.domain.Page;


public interface OrderService {

    OrderDTO createOrder(CreateOrderRequest request);
    OrderDTO getOrderById(Long id);

    Page<OrderDTO> getOrdersByCustomer(Long customerId, int page, int size);
    Page<OrderDTO> getAllOrders(int page, int size);
    Page<OrderDTO> getOrdersByStatus(String orderStatus, int page, int size);

    OrderDTO updateOrderStatus(Long orderId, String status);

    Page<OrderDTO> getOrdersBySellerId(Long sellerId,int page,int size);
}

