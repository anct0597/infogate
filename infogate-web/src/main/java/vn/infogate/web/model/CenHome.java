package vn.infogate.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.infogate.model.HasKey;
import vn.infogate.model.annotation.ExtractBy;
import vn.infogate.model.annotation.HelpUrl;
import vn.infogate.model.annotation.TargetUrl;

import java.util.UUID;

/**
 * @author anct
 */
@Setter
@Getter
@ToString
@TargetUrl(value = {"https://cenhomes.vn/du-an/[a-zA-Z-0-9]+",
        "https://cenhomes.vn/mua-nha/[a-zA-Z-0-9]+",
        "https://cenhomes.vn/thue-nha/[a-zA-Z-0-9]+"})
@HelpUrl(value = "https://cenhomes.vn/du-an/")
public class CenHome extends BaseModel implements HasKey {

    @ExtractBy("//*[@class='page-title']/text()")
    private String title;

    @Override
    public String key() {
        return UUID.randomUUID().toString();
    }
}
