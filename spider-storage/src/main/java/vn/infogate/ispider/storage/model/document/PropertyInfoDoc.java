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

    // Name of property name.
    @Field(PropertyInfoConstants.PROPERTY_NAME)
    private String propertyName;

    // Short summary of property.
    @Field(PropertyInfoConstants.SHORT_SUMMARY)
    private String shortSummary;

    @Field(PropertyInfoConstants.AREA)
    private Double area;

    @Field(PropertyInfoConstants.AREA_UNIT)
    private Integer unitArea;

    @Field(PropertyInfoConstants.TOTAL_PRICE)
    private Long totalPrice;

    @Field(PropertyInfoConstants.UNIT_PRICE)
    private Long unitPrice;

    @Field(PropertyInfoConstants.PROVINCE_CODE)
    private String provinceCode;

    @Field(PropertyInfoConstants.DISTRICT_CODE)
    private String districtCode;

    @Field(PropertyInfoConstants.WARD_CODE)
    private String wardCode;

    // Raw property location.
    @Field(PropertyInfoConstants.RAW_LOCATION)
    private String rawLocation;

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

    @Field(PropertyInfoConstants.DIRECTION)
    private Integer direction;

    @Field(PropertyInfoConstants.IMAGE_URLS)
    private List<String> imageUrls;

    @Field(PropertyInfoConstants.PHONES)
    private List<String> phones;

    @Field(PropertyInfoConstants.INVESTOR)
    private String investor;

    // Origin of property news
    @Field(PropertyInfoConstants.SOURCE_INFO)
    private String source;

    // From Broker or property owner.
    @Field(PropertyInfoConstants.PUBLISH_TYPE)
    private Integer publishType;

    // Phone number of publisher.
    @Field(PropertyInfoConstants.PUBLISHER)
    private String publisher;

    @Field(PropertyInfoConstants.PUBLISH_DATE)
    private String publishDate;

    @Field(PropertyInfoConstants.PUBLISH_END_DATE)
    private String publishEndDate;

    @Field(PropertyInfoConstants.REPORTERS)
    private List<String> reporters;

    @Field(PropertyInfoConstants.REPORT_REASONS)
    private List<String> reportReasons;
}
