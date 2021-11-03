package vn.infogate.ispider.storage.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.infogate.ispider.storage.model.types.CalculationUnit;

/**
 * @author anct.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceModel {
    private double value;

    private CalculationUnit unit;
}
