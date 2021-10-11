package vn.infogate.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import vn.infogate.ResultItems;
import vn.infogate.Task;
import vn.infogate.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Slf4j
public class FilePipeline extends FilePersistentBase implements Pipeline {

    public FilePipeline(String path) {
        setPath(path);
    }

    @Override
    @SuppressWarnings("all")
    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPARATOR + task.getUUID() + PATH_SEPARATOR;
        var file = getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".html");
        try (var printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    var iterable = (Iterable)entry.getValue();
                    printWriter.println(entry.getKey() + ":");
                    for (Object o : iterable) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }
        } catch (IOException e) {
            log.warn("write file error", e);
        }
    }
}
