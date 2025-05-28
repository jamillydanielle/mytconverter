package com.mytconvert.convertion.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mytconvert.convertion.entity.Convertion;


@Repository
public interface ConvertionRepository extends JpaRepository<Convertion, Long> {
    Page<Convertion> findByUserId(Long userId, Pageable pageable);

    List<Convertion> findByUserId(Long userId);

    @Transactional
    @Modifying
    void deleteById(Long id);
}
