package com.example.tyfserver.payment.repository;

import com.example.tyfserver.payment.domain.Item;
import com.example.tyfserver.payment.domain.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import supports.RepositoryTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private UUID uuid = UUID.randomUUID();
    private Payment payment = new Payment(Item.ITEM_1.getItemPrice(), Item.ITEM_1.getItemName(), uuid);

    @BeforeEach
    void setUp() {
        paymentRepository.save(payment);
    }

    @Test
    @DisplayName("findByMerchantUid")
    public void findByMerchantUidTest() {
        //given&then
        Payment payment = paymentRepository.findByMerchantUid(uuid).get();

        //then
        assertThat(payment.getId()).isEqualTo(payment.getId());
    }
}
