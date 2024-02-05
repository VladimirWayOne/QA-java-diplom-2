package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


import lombok.extern.jackson.Jacksonized;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@Builder
@Jacksonized
@JsonInclude(NON_EMPTY)
public class CreateUserRequest {
    private String email;
    private String password;
    private String name;
}
