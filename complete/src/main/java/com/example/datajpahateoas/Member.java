package com.example.datajpahateoas;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Grade grade;

}
