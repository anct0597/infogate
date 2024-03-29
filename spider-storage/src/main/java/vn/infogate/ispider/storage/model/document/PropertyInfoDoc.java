package vn.infogate.ispider.storage.model.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

@Getter
@Setter
@ToString
public class PropertyInfoDoc {

    @Field(PropertyInfoConstants.ID)
    private String id;

    @Field(PropertyInfoConstants.TITLE)
    private String title;

    @Field(PropertyInfoConstants.SHORT_SUMMARY)
    private String shortSummary;

    @Field(PropertyInfoConstants.AREA)
    private Double area;

    @Field(PropertyInfoConstants.AREA_UNIT)
    private Integer unitArea;

    @Field(PropertyInfoConstants.TOTAL_PRICE)
    private Double totalPrice;

    @Field(PropertyInfoConstants.WIDTH)
    private Double width;

    @Field(PropertyInfoConstants.LENGTH)
    private Double length;

    @Field(PropertyInfoConstants.KITCHEN)
    private Integer floors;

    @Field(PropertyInfoConstants.TOTAL_PRICE_CAL_UNIT)
    private Integer totalPriceCalUnit;

    @Field(PropertyInfoConstants.UNIT_PRICE)
    private Double unitPrice;

    @Field(PropertyInfoConstants.UNIT_PRICE_CAL_UNIT)
    private Integer unitPriceCalUnit;

    @Field(PropertyInfoConstants.PROVINCE_CODE)
    private String provinceCode;

    @Field(PropertyInfoConstants.DISTRICT_CODE)
    private String districtCode;

    @Field(PropertyInfoConstants.WARD_CODE)
    private String wardCode;

    @Field(PropertyInfoConstants.LOCATION)
    private String location;

    @Field(PropertyInfoConstants.PROPERTY_TYPE)
    private Integer propertyType;

    @Field(PropertyInfoConstants.EQUIPMENT)
    private Integer equipment;

    @Field(PropertyInfoConstants.LEGAL_STATUS)
    private Integer legalStatus;

    // Url to property news.
    @Field(PropertyInfoConstants.URL)
    private String url;

    @Field(PropertyInfoConstants.BED_ROOMS)
    private Integer bedRooms;

    @Field(PropertyInfoConstants.BATH_ROOMS)
    private Integer bathRooms;

    @Field(PropertyInfoConstants.TOILET)
    private Integer toilets;

    @Field(PropertyInfoConstants.KITCHEN)
    private Integer kitchen;

    @Field(PropertyInfoConstants.LIVING_ROOMS)
    private Integer livingRooms;

    @Field(PropertyInfoConstants.DIRECTION)
    private Integer direction;

    @Field(PropertyInfoConstants.IMAGES)
    private List<String> images;

    @Field(PropertyInfoConstants.PHONES)
    private List<String> phones;

    // Name of property name.
    @Field(PropertyInfoConstants.INVESTOR_PROJECT)
    private String investorProject;

    @Field(PropertyInfoConstants.INVESTOR)
    private String investor;

    // Origin of property news
    @Field(PropertyInfoConstants.SOURCE_INFO)
    private String source;

    // Sell or rent
    @Field(PropertyInfoConstants.PUBLISH_TYPE)
    private Integer publishType;

    // Property owner or Broker
    @Field(PropertyInfoConstants.PUBLISHER_TYPE)
    private Integer publisherType;

    // Phone number of publisher.
    @Field(PropertyInfoConstants.PUBLISHER)
    private String publisher;

    @Field(PropertyInfoConstants.PUBLISH_DATE)
    private Long publishDate;

    @Field(PropertyInfoConstants.CRAWLED_TIME)
    private Long crawledTime;

    @Field(PropertyInfoConstants.REPORTERS)
    private List<String> reporters;

    @Field(PropertyInfoConstants.REPORT_REASONS)
    private List<String> reportReasons;
}
