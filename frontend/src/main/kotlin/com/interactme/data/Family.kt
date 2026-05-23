package com.interactme.data

import java.time.LocalDate

/// DTO семьи на стороне frontend.

data class Family(
    var id: Long? = null,
    var familyNumber: Int? = null,
    var lastExchangeVisit: LocalDate? = null,
    var lastSocialDayVisit: LocalDate? = null,
    var firstVisitDate: LocalDate? = null,
    var newClient: Boolean? = null,
    var parentsName: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var childrenInfo: String? = null,
    var hasDisabledChildren: Boolean? = null,
    var largeFamily: Boolean? = null,
    var livesInSputnik: Boolean? = null,
    var socialSupport: Boolean? = null,
    var socialPackagesCount: Int? = null,

    var donated100: Int? = null,
    var donated200: Int? = null,
    var donated300: Int? = null,
    var donated400: Int? = null,
    var donated500: Int? = null,
    var donated1000: Int? = null,

    var donatedPointsSum: Int? = null,

    var spent100: Int? = null,
    var spent200: Int? = null,
    var spent300: Int? = null,
    var spent400: Int? = null,
    var spent500: Int? = null,
    var spent1000: Int? = null,

    var balance100: Int? = null,
    var balance200: Int? = null,
    var balance300: Int? = null,
    var balance400: Int? = null,
    var balance500: Int? = null,
    var balance1000: Int? = null,

    var balancePointsSum: Int? = null,
    var donationAmountRub: Int? = null,
    var extraPaymentRub: Int? = null,
    var paymentType: String? = null,
    var writtenOffPoints: Int? = null,
    var takenItemsCount: Int? = null,
    var donorsCount: Int? = null)