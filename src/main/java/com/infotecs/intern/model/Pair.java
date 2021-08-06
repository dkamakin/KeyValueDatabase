package com.infotecs.intern.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "pair")
@Table(name = "pair")
public class Pair {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    private String key;
    private String value;

    @Column(name = "timestamp")
    private String timeStamp;

    public Pair(String key, String value, String timeStamp) {
        this.key = key;
        this.value = value;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "key = " + key + "| value = " + value + "| timestamp = " + timeStamp;
    }

}
