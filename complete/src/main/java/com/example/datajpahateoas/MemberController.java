package com.example.datajpahateoas;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/hateoas")
    public EntityModel<Member> insertMember(@RequestParam(value = "name") String name, @RequestParam(value = "age") Integer age, Grade grade){

        Member member = memberService.addMember(name, age, grade);

        EntityModel<Member> memberEntityModel = new EntityModel<>(member);
        memberEntityModel.add(linkTo(methodOn(MemberController.class).insertMember(name, age, grade)).withSelfRel());

        return memberEntityModel;
    }
}
