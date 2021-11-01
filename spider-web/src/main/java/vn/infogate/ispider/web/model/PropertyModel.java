package vn.infogate.ispider.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.infogate.ispider.web.types.PropertyDirection;
import vn.infogate.ispider.web.types.PropertyType;
import vn.infogate.ispider.web.types.AreaUnit;

import java.util.List;

/**
 * @author anct.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "url"})
public class PropertyModel {

    private String id;

    // Name of property name.
    private String propertyName;

    // Short summary of property.
    private String shortSummary;

    private double area;

    private AreaUnit unitArea;

    private Long price;

    private Long unitPrice;

    private String provinceCode;

    private String districtCode;

    private String wardCode;

    // Raw property location.
    private String rawLocation;

    private PropertyType propertyType;

    // Url to property news.
    private String url;

    private int bedRooms;

    private PropertyDirection direction;

    private List<String> imageUrls;

    private List<String> phones;

    private String investor;

    // Origin of property news
    private String source;

    // From Broker or property owner.
    private String publishType;

    // Phone number of publisher.
    private String publisher;

    private String publishDate;

    private String publishEndDate;

    private List<String> reporters;

    private List<String> reportReasons;
}
