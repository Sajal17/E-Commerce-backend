package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.*;
import com.sa.M_Mart.model.*;
import com.sa.M_Mart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public OrderDTO createOrder(CreateOrderRequest request) {
        AppUser customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> items = request.items().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));

            return OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .price(price)
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customer(customer)
                .items(items)
                .totalPrice(total)
                .shippingAddress(request.shippingAddress())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .orderStatus(Order.OrderStatus.PENDING)
                .build();

        items.forEach(item -> item.setOrder(order));

        return mapToDTO(orderRepository.save(order));
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Page<OrderDTO> getOrdersByCustomer(Long customerId, int page, int size) {
        return orderRepository.findByCustomerId(customerId, PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    @Override
    public Page<OrderDTO> getAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    @Override
    public Page<OrderDTO> getOrdersByStatus(String status, int page, int size) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByOrderStatus(orderStatus, PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    @Override
    public Page<OrderDTO> getOrdersBySellerId(Long sellerId,int page,int size){
       return orderRepository.findDistinctByItems_Product_Seller_Id(sellerId,PageRequest.of(page,size))
               .map(this::mapToDTO);
    }
    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        return mapToDTO(order);
    }

    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemsDTO = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        return new OrderDTO(
                order.getId(),
                order.getCustomer().getId(),
                itemsDTO,
                order.getTotalPrice(),
                order.getShippingAddress(),
                order.getPaymentStatus().name(),
                order.getOrderStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}