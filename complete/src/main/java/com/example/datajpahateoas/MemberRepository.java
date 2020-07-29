package com.example.datajpahateoas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RepositoryRestResource
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByName(String name);
}
