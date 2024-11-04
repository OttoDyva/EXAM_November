package dat.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.daos.TripDAO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import dat.enums.Category;
import dat.populators.Populate;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Set;

public class TripController {
    private EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("trips");
    private TripDAO tripDAO = new TripDAO(emf);
    private final ObjectMapper objectMapper;


    public TripController() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void getPackingItemsForCategory(Context ctx) {
        try {
            String category = ctx.pathParam("category").toLowerCase();

            String apiUrl = "https://packingapi.cphbusinessapps.dk/packinglist/" + category;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ctx.status(200).result(response.body());
            } else {
                ctx.status(500).result("Failed to retrieve packing items from external API for category: " + category);
            }
        } catch (IOException | InterruptedException e) {
            ctx.status(500).result("Error retrieving packing items: " + e.getMessage());
        }
    }


    public void getPackingItemsWeightSum(Context ctx) {
        try {
            String apiUrl = "https://packingapi.cphbusinessapps.dk/packinglist/";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode items = objectMapper.readTree(response.body()).get("items");
                int weightSum = 0;
                for (JsonNode item : items) {
                    weightSum += item.get("weightInGrams").asInt();
                }
                ctx.status(200).result("Total weight: " + weightSum + " grams");
            } else {
                ctx.status(500).result("Failed to retrieve packing items from external API");
            }
        } catch (IOException | InterruptedException e) {
            ctx.status(500).result("Error calculating weight: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            TripDTO jsonRequest = ctx.bodyValidator(TripDTO.class)
                    .check(dto -> dto.getName() != null && !dto.getName().isEmpty(), "Trip name must be set")
                    .check(dto -> dto.getStartTime() != null, "Trip start time must be set")
                    .check(dto -> dto.getEndTime() != null, "Trip end time must be set")
                    .check(dto -> dto.getStartPosition() != null, "Trip start position must be set")
                    .check(dto -> dto.getPrice() > 0, "Trip price must be greater than 0")
                    .check(dto -> dto.getCategory() != null, "Trip category must be set")
                    .get();

            TripDTO tripDTO = tripDAO.create(jsonRequest);

            ctx.status(201).json(tripDTO);
        } catch (Exception e) {
            ctx.status(500).result("Error creating trip: " + e.getMessage());
            ctx.status(400).result("Bad request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getById(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class)
                    .check(idCheck -> idCheck > 0, "Trip id must be greater than 0")
                    .get();

            TripDTO tripDTO = tripDAO.getById(id);
            String category = tripDTO.getCategory().name().toLowerCase();

            String apiUrl = "https://packingapi.cphbusinessapps.dk/packinglist/" + category;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectNode responseJson = objectMapper.valueToTree(tripDTO);
            if (response.statusCode() == 200) {
                JsonNode packingItems = objectMapper.readTree(response.body()).get("items");
                responseJson.set("packingItems", packingItems);
            }

            ctx.status(200).json(responseJson);
        } catch (IOException | InterruptedException e) {
            ctx.status(500).result("Error retrieving trip details: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(404).result("Trip not found: " + e.getMessage());
        }
    }

    public void getAll(Context ctx) {
        try {
            ctx.status(200).json(tripDAO.getAll());
        } catch (Exception e) {
            ctx.status(500).result("Error reading all trips: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Context ctx) {
        try {
            TripDTO jsonRequest = ctx.bodyValidator(TripDTO.class)
                    .check(dto -> dto.getName() != null && !dto.getName().isEmpty(), "Trip name must be set")
                    .check(dto -> dto.getStartTime() != null, "Trip start time must be set")
                    .check(dto -> dto.getEndTime() != null, "Trip end time must be set")
                    .check(dto -> dto.getStartPosition() != null, "Trip start position must be set")
                    .check(dto -> dto.getPrice() > 0, "Trip price must be greater than 0")
                    .check(dto -> dto.getCategory() != null, "Trip category must be set")
                    .get();

            int id = ctx.pathParamAsClass("id", Integer.class).check(idCheck -> idCheck > 0, "Trip id must be greater than 0").get();
            TripDTO tripDTO = tripDAO.update(id, jsonRequest);

            ctx.status(200).json(tripDTO);
        } catch (Exception e) {
            ctx.status(500).result("Error updating trip: " + e.getMessage());
            ctx.status(400).result("Bad request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            tripDAO.delete(id);

            ctx.status(204);
        } catch (EntityNotFoundException e) {
            ctx.status(404).result("Trip not found: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Error deleting trip: " + e.getMessage());
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = ctx.pathParamAsClass("tripId", Integer.class).check(idCheck -> idCheck > 0, "Trip id must be greater than 0").get();
            int guideId = ctx.pathParamAsClass("guideId", Integer.class).check(idCheck -> idCheck > 0, "Guide id must be greater than 0").get();
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(201).result("Guide added to trip successfully");
        } catch (Exception e) {
            ctx.status(500).result("Error adding guide to trip: " + e.getMessage());
            ctx.status(404).result("Trip or guide not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getTripsByGuide(Context ctx) {
        try {
            int guideId = ctx.pathParamAsClass("guideId", Integer.class).check(idCheck -> idCheck > 0, "Guide id must be greater than 0").get();
            Set<Trip> tripList = tripDAO.getTripsByGuide(guideId);
            ctx.status(200).json(tripList);
        } catch (Exception e) {
            ctx.status(500).result("Error reading trips by guide: " + e.getMessage());
            ctx.status(404).result("Trips not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getTripsByCategory(Context ctx) {
        try {
            Category category = Category.valueOf(ctx.pathParam("category"));
            Set<TripDTO> tripList = tripDAO.getTripsByCategory(category);
            ctx.status(200).json(tripList);
        } catch (Exception e) {
            ctx.status(500).result("Error reading trips by category: " + e.getMessage());
            ctx.status(404).result("Trips not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getGuideWithTotalSumOfTheirTrips(Context ctx) {
        try {
            int guideId = ctx.pathParamAsClass("guideId", Integer.class).check(idCheck -> idCheck > 0, "Guide id must be greater than 0").get();
            Set<TripDTO> tripList = tripDAO.getGuideWithTotalSumOfTheirTrips();
            ctx.status(200).json(tripList);
        } catch (Exception e) {
            ctx.status(500).result("Error reading guides with total sum of their trips: " + e.getMessage());
            ctx.status(404).result("Guide not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void populate(Context ctx) {
        try {
            Populate.populate(emf);
            ctx.status(201).result("Trips populated successfully");
        } catch (Exception e) {
            ctx.status(500).result("Error populating trips: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
