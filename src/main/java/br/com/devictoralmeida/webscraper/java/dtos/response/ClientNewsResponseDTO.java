package br.com.devictoralmeida.webscraper.java.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientNewsResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5312464152999037655L;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_permalink")
    private String link;
}
