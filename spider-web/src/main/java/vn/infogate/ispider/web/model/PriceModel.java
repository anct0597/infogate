package vn.infogate.ispider.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.infogate.ispider.storage.model.types.PriceUnit;

/**
 * @author anct.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceModel {
    private double value;

    private PriceUnit unit;
}
