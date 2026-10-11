package com.equipo3.codigospostales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP encargado de consumir la API del Equipo 1.
 */
public class ApiClient {

    private static final String API_URL =
            "http://codigos-postales.atwebpages.com/apis/codigos_postales.php?codigo=";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ApiClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        mapper = new ObjectMapper();
    }

    /**
     * Busca los asentamientos correspondientes a un código postal.
     */
    public List<CodigoPostal> buscar(String codigo)
            throws IOException, InterruptedException {

        String codigoCodificado =
                URLEncoder.encode(codigo, StandardCharsets.UTF_8);

        URI uri = URI.create(API_URL + codigoCodificado);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "La API respondió con código HTTP "
                    + response.statusCode()
            );
        }

        return convertirRespuesta(codigo, response.body());
    }

    /**
     * Convierte la respuesta JSON de la API en una lista de objetos
     * CodigoPostal.
     *
     * La API devuelve un arreglo de textos, por ejemplo:
     *
     * [
     *   "15 \"A\"",
     *   "Apolo",
     *   "Atardeceres",
     *   "Cerrada del Carmen"
     * ]
     */
    private List<CodigoPostal> convertirRespuesta(
            String codigo,
            String json) throws IOException {

        List<CodigoPostal> resultados = new ArrayList<>();

        JsonNode root = mapper.readTree(json);

        if (root.isArray()) {

            for (JsonNode item : root) {

                if (item.isTextual()) {

                    String asentamiento = item.asText();

                    resultados.add(
                            new CodigoPostal(
                                    codigo,
                                    asentamiento
                            )
                    );
                }
            }
        }

        return resultados;
    }
}