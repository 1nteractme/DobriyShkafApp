package com.iapp.iapp_messenger.dao.hibernate;

/// Допустимые варианты оплаты; в сущности Family значение хранится строкой.
public enum PaymentType {
    CASH("Наличные"),
    TRANSFER("Перевод"),
    QR_CODE("QR-код"),
    DEBT("Долг");

    private final String title;

    PaymentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}