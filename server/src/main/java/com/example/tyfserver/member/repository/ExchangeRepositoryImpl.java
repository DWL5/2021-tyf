package com.example.tyfserver.member.repository;

import com.example.tyfserver.donation.domain.DonationStatus;
import com.example.tyfserver.member.domain.ExchangeStatus;
import com.example.tyfserver.member.dto.ExchangeAmountDto;
import com.example.tyfserver.member.dto.QExchangeAmountDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static com.example.tyfserver.donation.domain.QDonation.donation;
import static com.example.tyfserver.member.domain.QExchange.exchange;

public class ExchangeRepositoryImpl implements ExchangeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ExchangeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ExchangeAmountDto> calculateExchangeAmountFromDonation(YearMonth exchangeApproveOn) {
        return queryFactory
                .select(new QExchangeAmountDto(exchange.id, donation.point.sum()))
                .from(exchange)
                .leftJoin(exchange.member.receivedDonations, donation)
                .groupBy(exchange.id)
                .where(
                        waitingStatus(),
                        waitingForExchangeStatus(),
                        beforeStartOfThisMonth(exchangeApproveOn)
                )
                .fetch();
    }

    private BooleanExpression waitingForExchangeStatus() {
        return donation.status.eq(DonationStatus.WAITING_FOR_EXCHANGE);
    }

    private BooleanExpression waitingStatus() {
        return exchange.status.eq(ExchangeStatus.WAITING);
    }

    private BooleanExpression beforeStartOfThisMonth(YearMonth exchangeApproveOn) {
        // 정산승인월 1일 00:00 이전
        LocalDateTime startOfNextMonth = exchangeApproveOn.atDay(1).atStartOfDay();
        return donation.createdAt.before(startOfNextMonth);
    }
}
