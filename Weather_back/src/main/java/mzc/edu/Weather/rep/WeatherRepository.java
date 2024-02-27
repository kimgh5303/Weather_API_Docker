package mzc.edu.Weather.rep;

import mzc.edu.Weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, String> {

    @Modifying
    @Query(value = "INSERT INTO location (loc_id, loc_name, loc_latitude, loc_longitude) VALUES (:locid, :locname, :loclatitude, :loclongitude)", nativeQuery = true)
    void addLocation(@Param("locid") String locid, @Param("locname") String locname, @Param("loclatitude") double latitude, @Param("loclongitude") double longitude);

    @Modifying
    @Query(value = "UPDATE location\n" +
            "SET loc_name = :locname, loc_latitude = :loclatitude, loc_longitude = :loclongitude\n" +
            "WHERE loc_id = :locid", nativeQuery = true)
    void updateLocation(@Param("locid") String locid, @Param("locname") String locname, @Param("loclatitude") double latitude, @Param("loclongitude") double longitude);

    void deleteById(String id);
}
