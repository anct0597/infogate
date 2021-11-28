package vn.infogate.ispider.core.pipeline;

import vn.infogate.ispider.core.ResultItems;
import vn.infogate.ispider.core.Task;
import vn.infogate.ispider.core.model.MultiPageModel;
import vn.infogate.ispider.core.utils.DoubleKeyMap;
import vn.infogate.ispider.core.utils.Experimental;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A pipeline combines the result in more than one page together.<br>
 * Used for news and articles containing more than one web page. <br>
 * MultiPagePipeline will store parts of object and output them when all parts are extracted.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Experimental
public class MultiPagePipeline implements Pipeline {

    private final DoubleKeyMap<String, String, Boolean> pageMap = new DoubleKeyMap<>(ConcurrentHashMap.class);

    private final DoubleKeyMap<String, String, MultiPageModel> objectMap = new DoubleKeyMap<>(ConcurrentHashMap.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        var iterator = resultItems.getFields().entrySet().iterator();
        while (iterator.hasNext()) {
            handleObject(iterator);
        }
    }

    private void handleObject(Iterator<Map.Entry<String, Object>> iterator) {
        Map.Entry<String, Object> objectEntry = iterator.next();
        Object o = objectEntry.getValue();
        if (o instanceof MultiPageModel) {
            MultiPageModel multiPageModel = (MultiPageModel) o;
            pageMap.put(multiPageModel.getPageKey(), multiPageModel.getPage(), Boolean.FALSE);
            synchronized (pageMap.get(multiPageModel.getPageKey())) {
                pageMap.put(multiPageModel.getPageKey(), multiPageModel.getPage(), Boolean.TRUE);
                if (multiPageModel.getOtherPages() != null) {
                    for (String otherPage : multiPageModel.getOtherPages()) {
                        Boolean aBoolean = pageMap.get(multiPageModel.getPageKey(), otherPage);
                        if (aBoolean == null) {
                            pageMap.put(multiPageModel.getPageKey(), otherPage, Boolean.FALSE);
                        }
                    }
                }
                //check if all pages are processed
                Map<String, Boolean> booleanMap = pageMap.get(multiPageModel.getPageKey());
                objectMap.put(multiPageModel.getPageKey(), multiPageModel.getPage(), multiPageModel);
                if (booleanMap == null) {
                    return;
                }
                for (Map.Entry<String, Boolean> stringBooleanEntry : booleanMap.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        iterator.remove();
                        return;
                    }
                }
                var entryList = new ArrayList<>(objectMap.get(multiPageModel.getPageKey()).entrySet());
                if (entryList.size() != 0) {
                    entryList.sort((o1, o2) -> {
                        try {
                            int i1 = Integer.parseInt(o1.getKey());
                            int i2 = Integer.parseInt(o2.getKey());
                            return i1 - i2;
                        } catch (NumberFormatException e) {
                            return o1.getKey().compareTo(o2.getKey());
                        }
                    });
                    MultiPageModel value = entryList.get(0).getValue();
                    for (int i = 1; i < entryList.size(); i++) {
                        value = value.combine(entryList.get(i).getValue());
                    }
                    objectEntry.setValue(value);
                }
            }
        }

    }

}
