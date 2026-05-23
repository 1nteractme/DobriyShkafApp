package com.iapp.iapp_messenger.dao.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/// Репозиторий Hibernate для доступа к таблице семей.
public interface FamilyRepository extends JpaRepository<Family, Long> {

    /// Найти семью по её номеру.
    Optional<Family> findByFamilyNumber(Integer familyNumber);
}