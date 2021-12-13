package vn.infogate.ispider.storage.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;
import vn.infogate.ispider.common.utils.VNCharacterUtils;
import vn.infogate.ispider.storage.model.entity.LocationModel;

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
                var formattedText = replace(text);

                for (var location : locationMap.get(province.getKey())) {
                    if (text.contains(location.getWard()) || checkContain(formattedText, getShortWardText(location.getWard()))) {
                        return location;
                    }
                }
                for (var location : locationMap.get(province.getKey())) {
                    if (text.contains(location.getDistrict()) || checkContain(formattedText, getShortDistrict(location.getDistrict()))) {
                        return location;
                    }
                }
            }
        }
        return new LocationModel();
    }

    private boolean checkContain(String text, String givenText) {
        return text.contains(replace(givenText));
    }

    private String replace(String text) {
        var formatText = text;
        for (var i = 0; i < 10; i++) {
            formatText = formatText.replace("0" + i, String.valueOf(i));
        }
        return formatText;
    }

    private String getShortDistrict(String wardText) {
        return wardText
                .replaceFirst("^(thanh pho)", "")
                .replaceFirst("^(thi tran)", "")
                .replaceFirst("^(quan)", "")
                .replaceFirst("^(huyen)", "");
    }

    private String getShortWardText(String wardText) {
        return wardText
                .replaceFirst("^(phuong)", "")
                .replaceFirst("^(xa)", "");
    }

    private String getShortProvinceText(String provinceText) {
        return provinceText
                .replaceFirst("^(thanh pho)", "")
                .replaceFirst("^(tinh)", "");
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
