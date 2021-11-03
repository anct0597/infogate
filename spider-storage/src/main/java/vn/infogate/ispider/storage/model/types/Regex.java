package vn.infogate.ispider.storage.model.types;

import java.util.regex.Pattern;

/**
 * @author anct.
 */
public class Regex {

    public static final Pattern AREA = Pattern.compile("(\\d+)");
    public static final Pattern AREA_UNIT = Pattern.compile("(m²)");
    public static final Pattern PHONE = Pattern.compile("((03[2-9]|05[689]|07[06789]|08[1-6]|09[1-9])(\\d{7}))+");
    public static final Pattern NUMBER = Pattern.compile("(\\d+)");
    public static final Pattern PRICE = Pattern.compile("([\\d,.]+)(k|tr|trieu|ty|triệu|tỷ)?");
    public static final Pattern RANGE_PRICE = Pattern.compile("([\\d,.]+)([-])?([\\d,.]+)(k|tr|trieu|ty|triệu|tỷ)?");
}
