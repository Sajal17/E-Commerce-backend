package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CreatePaymentRequest;
import com.sa.M_Mart.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(CreatePaymentRequest request);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getPaymentsByCustomer(Long customerId);
    List<PaymentDTO> getPaymentsByStatus(String status);
    PaymentDTO updatePaymentStatus(Long paymentId, String status);
}
