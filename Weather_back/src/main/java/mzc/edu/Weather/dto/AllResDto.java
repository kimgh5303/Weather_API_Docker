package mzc.edu.Weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AllResDto {

    private boolean success;
    private String message;
    private Object data;

    public AllResDto(boolean success, String message){
        this.success = success;
        this.message = message;
    }
}

