package vn.infogate.ispider.storage.transfomer;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.common.utils.UUIdUtils;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.document.PropertyInfoDoc;

import java.util.Map;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
@Slf4j
public class PropertyInfoTransformer {

    public static PropertyInfoDoc toPropertyInfoDoc(String url, Map<String, Object> fieldValues) {
        log.debug("url: {},field values: {}", url, fieldValues);
        var chInfo = new PropertyInfoDoc();
        chInfo.setId(UUIdUtils.generateSimpleUUID());
        chInfo.setUrl(url);
        chInfo.setArea((Double) fieldValues.get(PropertyInfoConstants.AREA));
        return chInfo;
    }
}
