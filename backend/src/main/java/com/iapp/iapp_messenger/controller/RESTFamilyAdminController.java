package com.iapp.iapp_messenger.controller;

import com.iapp.iapp_messenger.dao.dto.ApiResponse;
import com.iapp.iapp_messenger.dao.hibernate.Family;
import com.iapp.iapp_messenger.dao.hibernate.FamilyRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// REST-контроллер административных операций с таблицей семей.
@RestController
@RequestMapping("/api/families-admin")
public class RESTFamilyAdminController {

    private final FamilyRepository familyRepository;
    private final JdbcTemplate jdbcTemplate;

    public RESTFamilyAdminController(FamilyRepository familyRepository, JdbcTemplate jdbcTemplate) {
        this.familyRepository = familyRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /// Получить всю таблицу семей.
    @GetMapping("/all")
    public ApiResponse<List<Family>> getAllFamilies() {
        return ApiResponse.ok(familyRepository.findAll());
    }

    /// Получить размер текущей PostgreSQL базы данных в байтах.
    @GetMapping("/database-size")
    public ApiResponse<Long> getDatabaseSize() {
        Long size = jdbcTemplate.queryForObject(
                "select pg_database_size(current_database())",
                Long.class);

        return ApiResponse.ok(size);
    }

    /// Получить одну семью по идентификатору.
    @GetMapping("/{id}")
    public ApiResponse<Family> getFamily(@PathVariable Long id) {

        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));

        return ApiResponse.ok(family);
    }

    /// Создать новую семью.
    @PostMapping("/create")
    public ApiResponse<Family> createFamily(@RequestBody Family family) {

        /// Идентификатор генерирует Hibernate.
        family.setId(null);

        return ApiResponse.ok(familyRepository.save(family));
    }

    /// Полностью обновить данные семьи.
    @PostMapping("/update")
    public ApiResponse<Family> updateFamily(@RequestBody Family family) {

        Long id = family.getId();

        if (id == null)
            throw new IllegalArgumentException("Family id is null");

        Family existing = familyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Family with id=" + id + " not found"));

        return ApiResponse.ok(familyRepository.save(family));
    }

    /// Полная перезапись таблицы. Удаление всех старых строк => добавление новых.
    @Transactional
    @PostMapping("/rewrite-all")
    public ApiResponse<String> rewriteAll(@RequestBody List<Family> families)
    {

        familyRepository.deleteAll();
        familyRepository.flush();

        for (Family family : families) {
            family.setId(null);
        }

        familyRepository.saveAll(families);

        return ApiResponse.ok("Table rewritten");
    }

    /// Удаление семьи по идентификатору.
    @PostMapping("/delete/{id}")
    public ApiResponse<String> deleteFamily(@PathVariable Long id) {

        familyRepository.deleteById(id);

        return ApiResponse.ok("Deleted");
    }

    /// Проверочная точка доступа для быстрой диагностики доступности контроллера.
    @GetMapping("/hi")
    public String test() {
        return "ACCESS SUCCESSFUL";
    }
}
