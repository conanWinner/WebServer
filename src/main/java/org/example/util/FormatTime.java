package org.example.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatTime {

    public FormatTime() {}

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String formattedTime = LocalDateTime.now().format(formatter);

    public String getFormattedTime() {
        return formattedTime;
    }
}
