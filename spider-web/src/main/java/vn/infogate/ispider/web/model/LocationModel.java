package vn.infogate.ispider.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author anct.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationModel {

    private String province;

    private String provinceId;

    private String district;

    private String districtId;

    private String ward;

    private String wardId;

    public LocationModel(String provinceId, String districtId, String wardId) {
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
    }
}
