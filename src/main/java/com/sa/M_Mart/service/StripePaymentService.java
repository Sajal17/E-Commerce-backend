package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CreatePaymentRequest;
import com.sa.M_Mart.dto.PaymentDTO;
import com.sa.M_Mart.model.Order;
import com.sa.M_Mart.model.Payment;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.repository.OrderRepository;
import com.sa.M_Mart.repository.PaymentRepository;
import com.sa.M_Mart.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Profile("stripe") // optional, to use Stripe profile
public class StripePaymentService implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${stripe.api.key}")
    private String apiKey;

    @Override
    public PaymentDTO createPayment(CreatePaymentRequest request) throws Exception { //error

        Stripe.apiKey = apiKey;

        AppUser customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Stripe expects amount in smallest currency unit (cents)
        long amountInCents = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd") // or "inr" for India
                .setReceiptEmail(customer.getEmail())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        Payment payment = Payment.builder()
                .customer(customer)
                .order(order)
                .amount(order.getTotalPrice())
                .method(Payment.PaymentMethod.CARD) // default //error
                .status(Payment.PaymentStatus.PENDING)
                .transactionId(intent.getId())
                .build();

        paymentRepository.save(payment);

        return mapToDTO(payment);
    }

    //  Handle Payment Success
    @Override
    public Payment handlePaymentSuccess(String paymentId) {
        Payment payment = paymentRepository.findByTransactionId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Update payment status
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        // Also mark order as paid / processing
        Order order = payment.getOrder();
        order.setOrderStatus(Order.OrderStatus.PROCESSING);

        orderRepository.save(order);
        paymentRepository.save(payment);

        return payment;
    }

    // Handle Payment Failure

    @Override
    public void handlePaymentFailure(String paymentId) {
        Payment payment = paymentRepository.findByTransactionId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.FAILED);

        Order order = payment.getOrder();
        order.setOrderStatus(Order.OrderStatus.CANCELLED);

        orderRepository.save(order);
        paymentRepository.save(payment);
    }

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
}
