package ua.kostenko.recollector.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.dto.UserDto;

import java.util.List;

/**
 * Generic Data Transfer Object for API responses.
 *
 * @param <T> The type of the data in the response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic Data Transfer Object for API responses.")
public class Response<T> {

    @Schema(description = "HTTP status code of the response.", example = "200")
    private int statusCode;

    @Schema(description = "Message providing additional information about the status code of the response.", example = "OK")
    private String statusMessage;

    @Schema(description = "The data payload of the response.", implementation = Object.class, anyOf = {UserDto.class,
                                                                                                       CategoryDto.class,
                                                                                                       ItemDto.class,
                                                                                                       StatisticDto.class,
                                                                                                       List.class,
                                                                                                       String.class})
    private T data;

    @Schema(description = "Meta-information about the response, such as pagination details.", implementation = MetaInfo.class)
    private MetaInfo meta;

    @Schema(description = "Error message if the response indicates an error.", example = "Invalid request")
    private String error;
}