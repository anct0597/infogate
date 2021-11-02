package vn.infogate.ispider.storage.solr.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(of = "id")
public class UDoc {
    private final String id;

    private final String root;

    private final Set<UField> updateFields;

    private int updateFailCount;

    private UDoc(String id, String root, Set<UField> updateFields) {
        this.id = id;
        this.root = root;
        this.updateFields = updateFields;
    }

    public static UDoc createUpdateDoc(String id, Set<UField> updateFields) {
        return new UDoc(id, null, updateFields);
    }

    public static UDoc createUpdateChildDoc(String id, String root, Set<UField> updateFields) {
        return new UDoc(id, root, updateFields);
    }

    public void increaseFailCount() {
        this.updateFailCount++;
    }
}
