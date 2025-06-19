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
    /*SELECT pg_get_serial_sequence('media', 'id');
SELECT MAX(id) FROM media;
ALTER SEQUENCE media_id_seq RESTART WITH 6;*/
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
