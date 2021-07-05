package com.example.tyfserver.donation.controller;

import com.example.tyfserver.donation.dto.DonationMessageRequest;
import com.example.tyfserver.donation.dto.DonationRequest;
import com.example.tyfserver.donation.dto.DonationResponse;
import com.example.tyfserver.donation.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public ResponseEntity<DonationResponse> createDonation(@RequestBody DonationRequest donationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donationService.createDonation(donationRequest));
    }

    @PostMapping("{donationId}/messages")
    public ResponseEntity<Void> addDonationMessage(@PathVariable Long donationId,
                                                   @RequestBody DonationMessageRequest donationMessageRequest) {

        donationService.addMessageToDonation(donationId, donationMessageRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
