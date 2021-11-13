package vn.infogate.ispider.web.w5;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.ResultItems;
import vn.infogate.ispider.Task;
import vn.infogate.ispider.pipeline.Pipeline;
import vn.infogate.ispider.storage.repo.PropertyInfoRepo;
import vn.infogate.ispider.storage.transfomer.PropertyInfoTransformer;

/**
 * @author anct.
 */
@Slf4j
public class W5StoragePipeline implements Pipeline {

    private final PropertyInfoRepo propertyInfoRepo;

    public W5StoragePipeline() {
        this.propertyInfoRepo = PropertyInfoRepo.getInstance();
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        var propertyInfoDoc = PropertyInfoTransformer.toPropertyInfoDoc(
                resultItems.getRequest().getUrl(),
                resultItems.getFields());
        log.debug("[dhomes.com.vn] {}", propertyInfoDoc);
        propertyInfoRepo.save(propertyInfoDoc);
    }
}
