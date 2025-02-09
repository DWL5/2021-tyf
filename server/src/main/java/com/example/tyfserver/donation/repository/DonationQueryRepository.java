package com.example.tyfserver.donation.repository;

import com.example.tyfserver.donation.domain.Donation;
import com.example.tyfserver.member.domain.Member;

import java.time.YearMonth;
import java.util.List;

public interface DonationQueryRepository {

    Long waitingTotalPoint(Long creatorId);

    Long exchangedTotalPoint(Long creatorId);

    List<Donation> findDonationsToExchange(Member creator, YearMonth exchangeOn);

    Long calculateExchangeAmountFromDonation(Member creator, YearMonth exchangeOn);
}
