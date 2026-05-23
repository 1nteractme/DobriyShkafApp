package com.interactme.data

import java.time.LocalDate

/// Описание полей карточки семьи для построения формы и преобразования ввода.
enum class FamilyField(val title: String, val kind: Kind)
{
    FamilyNumber("Номер семьи", Kind.Number),
    LastExchangeVisit("Последний обмен", Kind.Date),
    LastSocialDayVisit("Последний соц.день", Kind.Date),
    FirstVisitDate("Последний визит", Kind.Date),
    NewClient("Новый клиент", Kind.Boolean),
    ParentsName("ФИО родителей", Kind.Text),
    Phone("Телефон", Kind.Text),
    Address("Адрес", Kind.MultilineText),
    ChildrenInfo("Дети", Kind.MultilineText),
    HasDisabledChildren("Дети-инвалиды", Kind.Boolean),
    LargeFamily("Многодетная", Kind.Boolean),
    LivesInSputnik("Проживает в Спутнике", Kind.Boolean),
    SocialSupport("Соц. выдача", Kind.Boolean),
    SocialPackagesCount("Кол-во соц.пакетов", Kind.Number),

    Donated100("Дарение 100", Kind.Number),
    Donated200("Дарение 200", Kind.Number),
    Donated300("Дарение 300", Kind.Number),
    Donated400("Дарение 400", Kind.Number),
    Donated500("Дарение 500", Kind.Number),
    Donated1000("Дарение 1000", Kind.Number),

    DonatedPointsSum("Сумма принесённых", Kind.Number),

    Spent100("Обмен 100", Kind.Number),
    Spent200("Обмен 200", Kind.Number),
    Spent300("Обмен 300", Kind.Number),
    Spent400("Обмен 400", Kind.Number),
    Spent500("Обмен 500", Kind.Number),
    Spent1000("Обмен 1000", Kind.Number),

    Balance100("Остаток 100", Kind.Number),
    Balance200("Остаток 200", Kind.Number),
    Balance300("Остаток 300", Kind.Number),
    Balance400("Остаток 400", Kind.Number),
    Balance500("Остаток 500", Kind.Number),
    Balance1000("Остаток 1000", Kind.Number),

    BalancePointsSum("Сумма остатка", Kind.Number),
    DonationAmountRub("Пожертвование, руб", Kind.Number),
    ExtraPaymentRub("Доплата, руб", Kind.Number),
    PaymentType("Тип оплаты", Kind.Payment),
    WrittenOffPoints("Списано баллов", Kind.Number),
    TakenItemsCount("Взято вещей", Kind.Number),
    DonorsCount("Кол-во дарителей", Kind.Number);

    /// Тип UI-виджета для поля формы.
    enum class Kind { Text, MultilineText, Number, Date, Boolean, Payment }
}

/// Возвращает значение поля из DTO семьи.
fun Family.valueOf(field: FamilyField): Any? = when (field) {
    FamilyField.FamilyNumber -> familyNumber
    FamilyField.LastExchangeVisit -> lastExchangeVisit
    FamilyField.LastSocialDayVisit -> lastSocialDayVisit
    FamilyField.FirstVisitDate -> firstVisitDate
    FamilyField.NewClient -> newClient
    FamilyField.ParentsName -> parentsName
    FamilyField.Phone -> phone
    FamilyField.Address -> address
    FamilyField.ChildrenInfo -> childrenInfo
    FamilyField.HasDisabledChildren -> hasDisabledChildren
    FamilyField.LargeFamily -> largeFamily
    FamilyField.LivesInSputnik -> livesInSputnik
    FamilyField.SocialSupport -> socialSupport
    FamilyField.SocialPackagesCount -> socialPackagesCount

    FamilyField.Donated100 -> donated100
    FamilyField.Donated200 -> donated200
    FamilyField.Donated300 -> donated300
    FamilyField.Donated400 -> donated400
    FamilyField.Donated500 -> donated500
    FamilyField.Donated1000 -> donated1000

    FamilyField.DonatedPointsSum -> donatedPointsSum

    FamilyField.Spent100 -> spent100
    FamilyField.Spent200 -> spent200
    FamilyField.Spent300 -> spent300
    FamilyField.Spent400 -> spent400
    FamilyField.Spent500 -> spent500
    FamilyField.Spent1000 -> spent1000

    FamilyField.Balance100 -> balance100
    FamilyField.Balance200 -> balance200
    FamilyField.Balance300 -> balance300
    FamilyField.Balance400 -> balance400
    FamilyField.Balance500 -> balance500
    FamilyField.Balance1000 -> balance1000

    FamilyField.BalancePointsSum -> balancePointsSum
    FamilyField.DonationAmountRub -> donationAmountRub
    FamilyField.ExtraPaymentRub -> extraPaymentRub
    FamilyField.PaymentType -> paymentType
    FamilyField.WrittenOffPoints -> writtenOffPoints
    FamilyField.TakenItemsCount -> takenItemsCount
    FamilyField.DonorsCount -> donorsCount
}

/// Обновляет текстовое, числовое или датированное поле в копии DTO.
fun Family.withField(field: FamilyField, rawValue: String): Family {
    val normalized = rawValue.trim().takeIf { it.isNotEmpty() }
    val updated = copy()

    fun number(): Int? = normalized?.toIntOrNull()
    fun date(): LocalDate? = normalized?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    when (field) {
        FamilyField.FamilyNumber -> updated.familyNumber = number()
        FamilyField.LastExchangeVisit -> updated.lastExchangeVisit = date()
        FamilyField.LastSocialDayVisit -> updated.lastSocialDayVisit = date()
        FamilyField.FirstVisitDate -> updated.firstVisitDate = date()
        FamilyField.ParentsName -> updated.parentsName = rawValue.takeIf { it.isNotEmpty() }
        FamilyField.Phone -> updated.phone = rawValue.takeIf { it.isNotEmpty() }
        FamilyField.Address -> updated.address = rawValue.takeIf { it.isNotBlank() }
        FamilyField.ChildrenInfo -> updated.childrenInfo = rawValue.takeIf { it.isNotBlank() }
        FamilyField.SocialPackagesCount -> updated.socialPackagesCount = number()

        FamilyField.Donated100 -> updated.donated100 = number()
        FamilyField.Donated200 -> updated.donated200 = number()
        FamilyField.Donated300 -> updated.donated300 = number()
        FamilyField.Donated400 -> updated.donated400 = number()
        FamilyField.Donated500 -> updated.donated500 = number()
        FamilyField.Donated1000 -> updated.donated1000 = number()

        FamilyField.DonatedPointsSum -> updated.donatedPointsSum = number()

        FamilyField.Spent100 -> updated.spent100 = number()
        FamilyField.Spent200 -> updated.spent200 = number()
        FamilyField.Spent300 -> updated.spent300 = number()
        FamilyField.Spent400 -> updated.spent400 = number()
        FamilyField.Spent500 -> updated.spent500 = number()
        FamilyField.Spent1000 -> updated.spent1000 = number()

        FamilyField.Balance100 -> updated.balance100 = number()
        FamilyField.Balance200 -> updated.balance200 = number()
        FamilyField.Balance300 -> updated.balance300 = number()
        FamilyField.Balance400 -> updated.balance400 = number()
        FamilyField.Balance500 -> updated.balance500 = number()
        FamilyField.Balance1000 -> updated.balance1000 = number()

        FamilyField.BalancePointsSum -> updated.balancePointsSum = number()
        FamilyField.DonationAmountRub -> updated.donationAmountRub = number()
        FamilyField.ExtraPaymentRub -> updated.extraPaymentRub = number()
        FamilyField.PaymentType -> updated.paymentType = rawValue.takeIf { it.isNotEmpty() }
        FamilyField.WrittenOffPoints -> updated.writtenOffPoints = number()
        FamilyField.TakenItemsCount -> updated.takenItemsCount = number()
        FamilyField.DonorsCount -> updated.donorsCount = number()
        FamilyField.NewClient,
        FamilyField.HasDisabledChildren,
        FamilyField.LargeFamily,
        FamilyField.LivesInSputnik,
        FamilyField.SocialSupport -> return updated
    }

    return updated
}

/// Обновляет булево поле в копии DTO.
fun Family.withBooleanField(field: FamilyField, value: Boolean): Family {
    val updated = copy()

    when (field) {
        FamilyField.NewClient -> updated.newClient = value
        FamilyField.HasDisabledChildren -> updated.hasDisabledChildren = value
        FamilyField.LargeFamily -> updated.largeFamily = value
        FamilyField.LivesInSputnik -> updated.livesInSputnik = value
        FamilyField.SocialSupport -> updated.socialSupport = value
        else -> Unit
    }

    return updated
}
