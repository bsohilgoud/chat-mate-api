package com.sohil.chatmate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name= "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="url")
    private String url;
    @Column(name="name")
    private String name;
    @Column(name="size")
    private Long size;
    @Column(name="type")
    private String type;
}
