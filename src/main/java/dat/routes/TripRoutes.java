package dat.routes;

import dat.controllers.TripController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {
    private final TripController tripController = new TripController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", tripController::create, Role.ADMIN, Role.USER);
            get("/", tripController::getAll, Role.ANYONE);
            get("/{id}", tripController::getById, Role.ANYONE);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, Role.ADMIN);
            get("/getTripsByGuide/{guideId}", tripController::getTripsByGuide, Role.ANYONE);
            put("/{id}", tripController::update, Role.ADMIN);
            delete("/{id}", tripController::delete, Role.USER, Role.ADMIN);
            post("/populate", tripController::populate, Role.ADMIN);
            get("/category/{category}", tripController::getTripsByCategory, Role.ANYONE);
            get("guides/totalprice", tripController::getGuideWithTotalSumOfTheirTrips, Role.ANYONE);
            get("/trips/{category}/packinglist", tripController::getPackingItemsForCategory);
            get("/trips/packinglist/weight", tripController::getPackingItemsWeightSum);

        };
    }
}
