package vn.infogate.ispider.storage.model.entity;

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

    private String provinceCode;

    private String district;

    private String districtCode;

    private String ward;

    private String wardCode;

    public LocationModel(String provinceCode, String districtCode, String wardCode) {
        this.provinceCode = provinceCode;
        this.districtCode = districtCode;
        this.wardCode = wardCode;
    }
}
