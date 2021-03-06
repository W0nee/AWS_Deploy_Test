package com.ducks.goodsduck.commons.model.entity;

import com.ducks.goodsduck.commons.model.dto.ItemUploadRequest;
import com.ducks.goodsduck.commons.model.enums.StatusGrade;
import com.ducks.goodsduck.commons.model.enums.TradeStatus;
import com.ducks.goodsduck.commons.model.enums.TradeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id @GeneratedValue
    private Long id;

    //TODO USER 엔티티와의 관계 정의

    //TODO IDOLMEMBER 엔티티와의 관계 정의

    //TODO CATEGORY_ITEM 엔티티와의 관계 정의

    private String name;
    private int price;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradetStatus;

    @Enumerated(EnumType.STRING)
    private StatusGrade statusGrade;

    private String imageUrl;
    private String description;
    private LocalDateTime itemCreatedAt;
    private LocalDateTime updatedAt;
    private int likesItemCount;

    public Item(ItemUploadRequest itemUploadRequest) {
        this.name = itemUploadRequest.getName();
        this.price = itemUploadRequest.getPrice();
        this.tradeType = itemUploadRequest.getTradeType();
        this.tradetStatus = itemUploadRequest.getTradetStatus();
        this.imageUrl = itemUploadRequest.getImageUrl();
        this.description = itemUploadRequest.getDescription();
        this.itemCreatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likesItemCount = 0;

    }
}
