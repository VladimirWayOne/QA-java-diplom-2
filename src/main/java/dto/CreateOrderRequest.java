package dto;

import lombok.*;


import lombok.extern.jackson.Jacksonized;


import java.util.List;
@Setter
@Getter
@Builder
@Jacksonized
public class CreateOrderRequest {
    private List<String> ingredients;

}
