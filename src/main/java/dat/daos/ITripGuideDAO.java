package dat.daos;

import java.util.Set;

public interface ITripGuideDAO <TripDTO>{
    void addGuideToTrip(int tripId, int guideId);
    Set<TripDTO> getTripsByGuide(int guideId);
}
