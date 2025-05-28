package com.mytconvert.convertion;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.mytconvert.convertion.Configuration.SecurityConfig;
import com.mytconvert.convertion.repository.ConvertionRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SecurityConfig.class)
class ConvertionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ConvertionRepository convertionRepository;

        @Autowired
        private EntityManager entityManager;

        @BeforeEach
        void setUp() {

                convertionRepository.deleteAll();
        }
}