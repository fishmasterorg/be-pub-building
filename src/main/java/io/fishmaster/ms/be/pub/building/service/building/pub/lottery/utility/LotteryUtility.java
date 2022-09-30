package io.fishmaster.ms.be.pub.building.service.building.pub.lottery.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.AtomicDouble;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery.LotteryPackConfigurationDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LotteryUtility {

    private record CardChance(
            LotteryPackConfigurationDto.Card card,
            Double chance,
            Double chanceBound) {
        public CardChance(LotteryPackConfigurationDto.Card card, Double chanceBound) {
            this(card, card.getChance(), chanceBound);
        }
    }

    private final List<CardChance> cardChances;
    private final List<LotteryPackConfigurationDto.Card> imitateCards;
    private final Integer imitateCardsCount;

    public static LotteryUtility of(LotteryPackConfigurationDto.Result result) {
        var bound = new AtomicDouble(0D);
        var cardChances = result.getCards().stream()
                .map(card -> new CardChance(card, bound.addAndGet(card.getChance())))
                .filter(cardChance -> cardChance.chance() > 0)
                .collect(Collectors.toList());

        var imitateCards = result.getCards().stream()
                .filter(card -> Boolean.TRUE.equals(card.getImitate()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new LotteryUtility(cardChances, imitateCards, result.getImitateCardsCount());
    }

    public LotteryPackConfigurationDto.Card getWinnerCard() {
        return findWinnerCard()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE, "Card not determined"));
    }

    public Optional<LotteryPackConfigurationDto.Card> findWinnerCard() {
        var lastCardRarityChance = cardChances.get(cardChances.size() - 1);

        var randomNumber = ThreadLocalRandom.current().nextDouble(0, lastCardRarityChance.chanceBound());

        return cardChances.stream()
                .filter(cardChance -> cardChance.chanceBound() > randomNumber)
                .map(CardChance::card)
                .filter(card -> Objects.nonNull(card.getId()))
                .findFirst();
    }

    public List<LotteryPackConfigurationDto.Card> getImitateCards() {
        return IntStream.range(0, imitateCardsCount)
                .mapToObj(value -> getImitateCard())
                .toList();
    }

    private LotteryPackConfigurationDto.Card getImitateCard() {
        var randomNumber = ThreadLocalRandom.current().nextInt(0, imitateCards.size());

        Collections.shuffle(imitateCards);

        var imitateCard = imitateCards.get(randomNumber);
        imitateCards.remove(imitateCard);

        return imitateCard;
    }

}
