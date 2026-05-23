package com.iapp.iapp_messenger;

import com.iapp.iapp_messenger.dao.hibernate.Family;
import com.iapp.iapp_messenger.test_client.FamilyApiClient;

import java.util.List;

/// Ручной тестовый клиент для проверки REST API семей.
public class Main {

    /// Загружает семьи и создаёт тестовую запись через API-клиент.
    public static void main(String[] args) throws Exception {

        FamilyApiClient api = new FamilyApiClient();

        List<Family> families = api.getAllFamilies();

        System.out.println("COUNT = " + families.size());

        Family family = new Family();
        family.setFamilyNumber(777 + families.size() + 1);
        family.setParentsName("Y");
        family.setPhone("+799999999");

        Family created = api.createFamily(family);

        System.out.println("CREATED ID = " + created.getId());
    }
}