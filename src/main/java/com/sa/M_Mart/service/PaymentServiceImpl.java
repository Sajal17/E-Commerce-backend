package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CreatePaymentRequest;
import com.sa.M_Mart.dto.PaymentDTO;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.Order;
import com.sa.M_Mart.model.Payment;
import com.sa.M_Mart.repository.OrderRepository;
import com.sa.M_Mart.repository.PaymentRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentDTO createPayment(CreatePaymentRequest request) {

        AppUser customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.method().toUpperCase());

        Payment payment = Payment.builder()
                .customer(customer)
                .order(order)
                .amount(request.amount())
                .method(method)
                .status(Payment.PaymentStatus.PENDING)
                .transactionId(generateTransactionId())
                .build();

        return mapToDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public List<PaymentDTO> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
        return paymentRepository.findByStatus(paymentStatus)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.valueOf(status.toUpperCase()));
        return mapToDTO(payment);
    }

    // Helper to map Payment to PaymentDTO
    private PaymentDTO mapToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getCustomer().getId(),
                payment.getAmount(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getTransactionId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    // Example transaction ID generator (can replace with real gateway ID)
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}