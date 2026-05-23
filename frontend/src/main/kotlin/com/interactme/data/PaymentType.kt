package com.interactme.data

/// Варианты оплаты в интерфейсе; в DTO и JSON сохраняется строковое значение.
enum class PaymentType(val title: String)
{
    Cash("Наличные"),
    Transfer("Перевод"),
    QrCode("QR-код"),
    Debt("Долг");

    companion object {
        /// Находит enum по строке, пришедшей из backend или выбранной в интерфейсе.
        fun fromTitle(title: String?): PaymentType? = entries.firstOrNull { it.title == title }
    }
}