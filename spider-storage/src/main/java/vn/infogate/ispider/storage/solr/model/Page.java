package vn.infogate.ispider.storage.solr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Page<E> {
    private int pageNumber;
    private int pageSize = 10;
    private int pagesAvailable;
    private long totalItems;
    private long time;
    private int startNumber = -1;
    private int endNumber = -1;
    private List<E> pageItems;

    public Page() {
        this.pageItems = new ArrayList<>();
    }

    public Page(int pageNumber, int totalPage) {
        this.pageNumber = pageNumber;
        this.pagesAvailable = totalPage;
        this.pageItems = new ArrayList<>();
    }

    public Page(List<E> all, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageItems = new ArrayList<>(pageSize);
        this.computePagesAvailable(all.size(), pageSize);
        int start = (pageNumber - 1) * pageSize;
        int end = (int) Math.min(this.totalItems, (long) pageNumber * pageSize);
        if (all.size() <= pageSize) {
            this.pageItems = all;
        } else {
            this.pageItems = new ArrayList<>();
            if (start > 0 && end > start) {
                this.pageItems.addAll(all.subList(start, end));
            }
        }
    }

    @JsonIgnore
    public int getPageStart() {
        return Math.max(1, this.pageNumber - 3);
    }

    @JsonIgnore
    public int getPageEnd() {
        return Math.min(this.pagesAvailable, this.pageNumber + 3);
    }

    public void computePagesAvailable(long numberOfResult, int size) {
        this.pageSize = size;
        this.totalItems = numberOfResult;
        this.pagesAvailable = 0;
        if (numberOfResult % this.pageSize == 0L) {
            this.pagesAvailable = (int) (numberOfResult / this.pageSize);
        } else {
            this.pagesAvailable = (int) (numberOfResult / this.pageSize) + 1;
        }
    }

    @JsonIgnore
    public int getStartNumber() {
        if (this.startNumber <= -1) {
            this.startNumber = (this.pageNumber - 1) * this.pageSize;
        }
        return this.startNumber;
    }

    @JsonIgnore
    public int getEndNumber() {
        if (this.endNumber <= -1) {
            this.endNumber = this.pageNumber * this.pageSize;
        }
        return this.endNumber;
    }
}