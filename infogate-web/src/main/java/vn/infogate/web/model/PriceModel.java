package vn.infogate.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.infogate.web.types.PriceUnit;

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
