package com.iapp.iapp_messenger.dao.hibernate;

import jakarta.persistence.*;

import java.time.LocalDate;

/// Hibernate-сущность семьи из таблицы families.
@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Номер семьи.
    @Column(unique = true)
    private Integer familyNumber;

    /// Последняя дата посещения обмена.
    private LocalDate lastExchangeVisit;

    /// Последняя дата посещения социального дня.
    private LocalDate lastSocialDayVisit;

    /// Дата первого посещения.
    private LocalDate firstVisitDate;

    /// Новый клиент (да/нет)
    private Boolean newClient;

    /// ФИО родителей.
    private String parentsName;

    /// Телефон
    private String phone;

    /// Адрес
    @Column(length = 5000)
    private String address;

    /// Дети (кол-во, пол, год рождения)
    @Column(length = 2000)
    private String childrenInfo;

    /// Есть дети-инвалиды.
    private Boolean hasDisabledChildren;

    /// Многодетная семья (3 и более детей с 2024г до 18 лет)Многодетная семья (3 и более детей с 2024г до 18 лет)
    private Boolean largeFamily;

    /// Проживает в Спутнике (Да/нет)
    private Boolean livesInSputnik;

    /// Нужна социальная выдача.
    private Boolean socialSupport;

    /// Количество социальных пакетов.
    private Integer socialPackagesCount;

    /// Баллы дарения: принесённые штуки в каждом номинале.
    private Integer donated100;
    private Integer donated200;
    private Integer donated300;
    private Integer donated400;
    private Integer donated500;
    private Integer donated1000;

    /// Сумма принесённых баллов
    private Integer donatedPointsSum;

    /// Баллы обмена: взятые вещи в каждом номинале.
    private Integer spent100;
    private Integer spent200;
    private Integer spent300;
    private Integer spent400;
    private Integer spent500;
    private Integer spent1000;

    /// Остаток баллов: штуки в каждом номинале.
    private Integer balance100;
    private Integer balance200;
    private Integer balance300;
    private Integer balance400;
    private Integer balance500;
    private Integer balance1000;

    /// Сумма остатка баллов
    private Integer balancePointsSum;

    /// Сумма пожертвования, руб.
    private Integer donationAmountRub;

    /// Сумма доплаты, руб
    private Integer extraPaymentRub;

    /// Тип оплаты: наличные, перевод по QR-коду или долг.
    private String paymentType;

    /// Сумма списанных баллов.
    private Integer writtenOffPoints;

    /// Количество взятых вещей.
    private Integer takenItemsCount;

    /// Количество дарителей.
    private Integer donorsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFamilyNumber() {
        return familyNumber;
    }

    public void setFamilyNumber(Integer familyNumber) {
        this.familyNumber = familyNumber;
    }

    public LocalDate getLastExchangeVisit() {
        return lastExchangeVisit;
    }

    public void setLastExchangeVisit(LocalDate lastExchangeVisit) {
        this.lastExchangeVisit = lastExchangeVisit;
    }

    public LocalDate getLastSocialDayVisit() {
        return lastSocialDayVisit;
    }

    public void setLastSocialDayVisit(LocalDate lastSocialDayVisit) {
        this.lastSocialDayVisit = lastSocialDayVisit;
    }

    public LocalDate getFirstVisitDate() {
        return firstVisitDate;
    }

    public void setFirstVisitDate(LocalDate firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public Boolean getNewClient() {
        return newClient;
    }

    public void setNewClient(Boolean newClient) {
        this.newClient = newClient;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChildrenInfo() {
        return childrenInfo;
    }

    public void setChildrenInfo(String childrenInfo) {
        this.childrenInfo = childrenInfo;
    }

    public Boolean getHasDisabledChildren() {
        return hasDisabledChildren;
    }

    public void setHasDisabledChildren(Boolean hasDisabledChildren) {
        this.hasDisabledChildren = hasDisabledChildren;
    }

    public Boolean getLargeFamily() {
        return largeFamily;
    }

    public void setLargeFamily(Boolean largeFamily) {
        this.largeFamily = largeFamily;
    }

    public Boolean getLivesInSputnik() {
        return livesInSputnik;
    }

    public void setLivesInSputnik(Boolean livesInSputnik) {
        this.livesInSputnik = livesInSputnik;
    }

    public Boolean getSocialSupport() {
        return socialSupport;
    }

    public void setSocialSupport(Boolean socialSupport) {
        this.socialSupport = socialSupport;
    }

    public Integer getSocialPackagesCount() {
        return socialPackagesCount;
    }

    public void setSocialPackagesCount(Integer socialPackagesCount) {
        this.socialPackagesCount = socialPackagesCount;
    }

    public Integer getDonated100() {
        return donated100;
    }

    public void setDonated100(Integer donated100) {
        this.donated100 = donated100;
    }

    public Integer getDonated200() {
        return donated200;
    }

    public void setDonated200(Integer donated200) {
        this.donated200 = donated200;
    }

    public Integer getDonated300() {
        return donated300;
    }

    public void setDonated300(Integer donated300) {
        this.donated300 = donated300;
    }

    public Integer getDonated400() {
        return donated400;
    }

    public void setDonated400(Integer donated400) {
        this.donated400 = donated400;
    }

    public Integer getDonated500() {
        return donated500;
    }

    public void setDonated500(Integer donated500) {
        this.donated500 = donated500;
    }

    public Integer getDonated1000() {
        return donated1000;
    }

    public void setDonated1000(Integer donated1000) {
        this.donated1000 = donated1000;
    }

    public Integer getDonatedPointsSum() {
        return donatedPointsSum;
    }

    public void setDonatedPointsSum(Integer donatedPointsSum) {
        this.donatedPointsSum = donatedPointsSum;
    }

    public Integer getSpent100() {
        return spent100;
    }

    public void setSpent100(Integer spent100) {
        this.spent100 = spent100;
    }

    public Integer getSpent200() {
        return spent200;
    }

    public void setSpent200(Integer spent200) {
        this.spent200 = spent200;
    }

    public Integer getSpent300() {
        return spent300;
    }

    public void setSpent300(Integer spent300) {
        this.spent300 = spent300;
    }

    public Integer getSpent400() {
        return spent400;
    }

    public void setSpent400(Integer spent400) {
        this.spent400 = spent400;
    }

    public Integer getSpent500() {
        return spent500;
    }

    public void setSpent500(Integer spent500) {
        this.spent500 = spent500;
    }

    public Integer getSpent1000() {
        return spent1000;
    }

    public void setSpent1000(Integer spent1000) {
        this.spent1000 = spent1000;
    }

    public Integer getBalance100() {
        return balance100;
    }

    public void setBalance100(Integer balance100) {
        this.balance100 = balance100;
    }

    public Integer getBalance200() {
        return balance200;
    }

    public void setBalance200(Integer balance200) {
        this.balance200 = balance200;
    }

    public Integer getBalance300() {
        return balance300;
    }

    public void setBalance300(Integer balance300) {
        this.balance300 = balance300;
    }

    public Integer getBalance400() {
        return balance400;
    }

    public void setBalance400(Integer balance400) {
        this.balance400 = balance400;
    }

    public Integer getBalance500() {
        return balance500;
    }

    public void setBalance500(Integer balance500) {
        this.balance500 = balance500;
    }

    public Integer getBalance1000() {
        return balance1000;
    }

    public void setBalance1000(Integer balance1000) {
        this.balance1000 = balance1000;
    }

    public Integer getBalancePointsSum() {
        return balancePointsSum;
    }

    public void setBalancePointsSum(Integer balancePointsSum) {
        this.balancePointsSum = balancePointsSum;
    }

    public Integer getDonationAmountRub() {
        return donationAmountRub;
    }

    public void setDonationAmountRub(Integer donationAmountRub) {
        this.donationAmountRub = donationAmountRub;
    }

    public Integer getExtraPaymentRub() {
        return extraPaymentRub;
    }

    public void setExtraPaymentRub(Integer extraPaymentRub) {
        this.extraPaymentRub = extraPaymentRub;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getWrittenOffPoints() {
        return writtenOffPoints;
    }

    public void setWrittenOffPoints(Integer writtenOffPoints) {
        this.writtenOffPoints = writtenOffPoints;
    }

    public Integer getTakenItemsCount() {
        return takenItemsCount;
    }

    public void setTakenItemsCount(Integer takenItemsCount) {
        this.takenItemsCount = takenItemsCount;
    }

    public Integer getDonorsCount() {
        return donorsCount;
    }

    public void setDonorsCount(Integer donorsCount) {
        this.donorsCount = donorsCount;
    }
}