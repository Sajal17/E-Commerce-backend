package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CreatePaymentRequest;
import com.sa.M_Mart.dto.PaymentDTO;
import com.sa.M_Mart.model.Payment;

public interface PaymentGateway {
    PaymentDTO createPayment(CreatePaymentRequest request) throws Exception;

     // Handle payment success or callback
    Payment handlePaymentSuccess(String paymentId);

     // Handle payment failure
    void handlePaymentFailure(String paymentId);
}
