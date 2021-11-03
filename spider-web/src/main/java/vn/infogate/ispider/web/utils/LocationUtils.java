package vn.infogate.ispider.web.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.utils.ObjectMapperFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anct.
 */
@Slf4j
public class LocationUtils {

    private Map<String, String> provinceMap;
    private Multimap<String, LocationModel> locationMap;


    public LocationUtils() {
        initLocation();
    }

    public static LocationUtils getInstance() {
        return LocationUtils.SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final LocationUtils INSTANCE = new LocationUtils();
    }

    public LocationModel detectLocation(String text) {
        for (var province : provinceMap.entrySet()) {
            if (text.contains(province.getValue()) || text.contains(getShortProvinceText(province.getValue()))) {
                for (var location : locationMap.get(province.getKey())) {
                    if (text.contains(location.getWard()) | text.contains(getShortWardText(location.getWard()))) {
                        return location;
                    }
                }
                for (var location : locationMap.get(province.getKey())) {
                    if (text.contains(location.getDistrict()) | text.contains(getShortWardText(location.getDistrict()))) {
                        return location;
                    }
                }
            }
        }
        return new LocationModel();
    }

    private String getShortDistrict(String wardText) {
        return wardText
                .replace("thanh pho", "")
                .replace("thi tran", "")
                .replace("quan", "")
                .replace("huyen", "");
    }

    private String getShortWardText(String wardText) {
        return wardText
                .replace("phuong", "")
                .replace("xa", "");
    }

    private String getShortProvinceText(String provinceText) {
        return provinceText
                .replace("thanh pho", "")
                .replace("tinh", "");
    }

    private void initLocation() {
        try {
            this.provinceMap = new HashMap<>(63);
            this.locationMap = LinkedListMultimap.create();
            var mapper = ObjectMapperFactory.getInstance();

            var content = Files.readString(Paths.get("spider-web/data/location.txt"));
            var locations = mapper.readValue(content.replaceAll("\uFEFF", ""),
                    new TypeReference<List<LocationModel>>() {
                    });
            for (var location : locations) {
                location.setProvince(VNCharacterUtils.removeAccent(location.getProvince()).toLowerCase());
                location.setDistrict(VNCharacterUtils.removeAccent(location.getDistrict()).toLowerCase());
                location.setWard(VNCharacterUtils.removeAccent(location.getWard()).toLowerCase());
                provinceMap.putIfAbsent(location.getProvinceCode(), location.getProvince());
                locationMap.put(location.getProvinceCode(), location);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
