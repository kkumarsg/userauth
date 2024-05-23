package com.auth.user.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonSerialize
public class EmailFormat implements Serializable {

    private String to;
    private String from;
    private String content;
    private String subject;
}
