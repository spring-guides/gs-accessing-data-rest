package com.example.datajpahateoas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member addMember(String name, Integer age, Grade grade) {

        Member findMember = memberRepository.findByName(name);

        if(findMember == null){

            Member member = Member.builder()
                    .name(name)
                    .age(age)
                    .grade(grade.GOLD)
                    .build();

            return memberRepository.save(member);
        }

        return findMember;
    }
}
