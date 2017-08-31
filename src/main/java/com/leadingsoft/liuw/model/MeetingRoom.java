package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class MeetingRoom {

    @Id
    private String id;

    private String name;
}
