package com.sever.portfolio.repository;

import com.sever.portfolio.entity.CompanyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyValueRepository extends JpaRepository<CompanyValue, String> {

}