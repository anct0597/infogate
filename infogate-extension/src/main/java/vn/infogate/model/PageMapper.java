package vn.infogate.model;

import vn.infogate.Page;

import java.util.List;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.2
 */
@SuppressWarnings("unchecked")
public class PageMapper<T> {

    private Class<T> clazz;

    private final PageModelExtractor pageModelExtractor;

    public PageMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.pageModelExtractor = PageModelExtractor.create(clazz);
    }

    public T get(Page page) {
        return (T) pageModelExtractor.process(page);
    }

    public List<T> getAll(Page page) {
        return (List<T>) pageModelExtractor.process(page);
    }
}
