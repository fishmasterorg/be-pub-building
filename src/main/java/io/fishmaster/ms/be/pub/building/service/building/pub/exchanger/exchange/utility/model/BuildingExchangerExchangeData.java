package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingExchangerExchangeData {
    List<AccountCardLockedForCraftReqDto> lockedCards;
    List<AccountCardDto> craftedCards;
    List<AccountCardDto> additionalCraftedCards;
    Map<Speciality, ResultExperience> experiences;
    Double characterEnergy;
    Double craftTax;

    public List<AccountCardLockedForCraftReqDto> getLockedCards() {
        if (Objects.isNull(this.lockedCards)) {
            this.lockedCards = new ArrayList<>();
        }
        return this.lockedCards;
    }

    public void upsertLockedCards(List<AccountCardLockedForCraftReqDto> lockedCards) {
        this.lockedCards = Stream.concat(
                lockedCards.stream().map(reqDto -> Map.entry(reqDto.getCardId(), reqDto)),
                getLockedCards().stream().map(reqDto -> Map.entry(reqDto.getCardId(), reqDto))).collect(
                        Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (first, second) -> {
                                    first.setLimit(first.getLimit() + second.getLimit());
                                    return first;
                                }))
                .values().stream().toList();
    }

    public List<AccountCardDto> getCraftedCards() {
        if (Objects.isNull(this.craftedCards)) {
            this.craftedCards = new ArrayList<>();
        }
        return this.craftedCards;
    }

    public void upsertCraftedCards(AccountCardDto accountCardDto) {
        upsertCraftedCards(List.of(accountCardDto));
    }

    public void upsertCraftedCards(List<AccountCardDto> accountCardDtos) {
        getCraftedCards()
                .addAll(accountCardDtos);
    }

    public Map<Speciality, ResultExperience> getExperiences() {
        if (Objects.isNull(this.experiences)) {
            this.experiences = new EnumMap<>(Speciality.class);
        }
        return this.experiences;
    }

    public List<ResultExperience> getExperiencesList() {
        if (Objects.isNull(this.experiences)) {
            this.experiences = new EnumMap<>(Speciality.class);
        }
        return getExperiences().values().stream().toList();
    }

    public void upsertExperiences(List<ResultExperience> experiences) {
        var experienceMap = experiences.stream()
                .collect(Collectors.toMap(ResultExperience::getSpeciality, Function.identity()));

        this.experiences = Stream.concat(
                experienceMap.entrySet().stream(),
                getExperiences().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (reqDto, reqDto2) -> {
                    reqDto.setQuantity(reqDto.getQuantity() + reqDto2.getQuantity());
                    return reqDto;
                }));
    }

    public List<AccountCardDto> getAdditionalCraftedCards() {
        if (Objects.isNull(this.additionalCraftedCards)) {
            this.additionalCraftedCards = new ArrayList<>();
        }
        return this.additionalCraftedCards;
    }

    public void upsertAdditionalCraftedCards(AccountCardDto accountCardDto) {
        getAdditionalCraftedCards()
                .add(accountCardDto);
    }

    public List<AccountCardDto> getAllCraftedCards() {
        return Stream.concat(getCraftedCards().stream(), getAdditionalCraftedCards().stream()).toList();
    }

    public Double getCharacterEnergy() {
        if (Objects.isNull(this.characterEnergy)) {
            return 0D;
        }
        return this.characterEnergy;
    }

    public void upsertCharacterEnergy(Double quantity) {
        this.characterEnergy = getCharacterEnergy() + quantity;
    }

    public Double getCraftTax() {
        if (Objects.isNull(this.craftTax)) {
            return 0D;
        }
        return this.craftTax;
    }

    public void upsertCraftTax(Double quantity) {
        this.craftTax = getCraftTax() + quantity;
    }
}
