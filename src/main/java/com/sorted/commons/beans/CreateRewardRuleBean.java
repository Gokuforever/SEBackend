package com.sorted.commons.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sorted.commons.entity.beans.Reward;
import com.sorted.commons.entity.beans.RewardConditions;
import com.sorted.commons.enums.RewardEvent;
import com.sorted.commons.enums.RewardScope;
import com.sorted.commons.enums.RewardValidityType;

import java.time.LocalDate;
import java.util.List;

public record CreateRewardRuleBean(
        @JsonProperty("reward_name")
        String rewardName,
        @JsonProperty("reward_description")
        String rewardDescription,
        @JsonProperty("triggering_event")
        RewardEvent triggeringEvent,
        boolean active,
        Reward reward,
        @JsonProperty("reward_conditions")
        RewardConditions rewardConditions,
        RewardScope scope,
        @JsonProperty("audience_user_ids")
        List<String> audienceUserIds,
        @JsonProperty("is_stackable")
        boolean isStackable,
        @JsonProperty("once_per_user")
        boolean oncePerUser,
        @JsonProperty("validity_type")
        RewardValidityType validityType,
        @JsonProperty("valid_from")
        LocalDate validFrom,
        @JsonProperty("valid_upto")
        LocalDate validUpto,
        @JsonProperty("validity_in_days")
        Integer validityInDays
) {
}
