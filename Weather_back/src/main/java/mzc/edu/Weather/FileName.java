package mzc.edu.Weather;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FileName {
    public String createfileName() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
