package soadev.ext.trinidad.model;

import java.util.Collections;
import java.util.List;

import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;

public abstract class LazySortableModel<T> extends CollectionModel {


    private int rowIndex = -1;

    private int dataStartIndex = -1;
    private int dataEndIndex = -1;


    protected int rowCount;

    private List<T> data;
    private List<SortCriterion> criteria;

    public LazySortableModel() {
        super();
    }

    public boolean isRowAvailable() {
        if (rowIndex == -1) {
            return false;
        }
        if (dataStartIndex == -1) {
            load();
        }
        if (getRowCount() > rowIndex &&
            (rowIndex < dataStartIndex || rowIndex > dataEndIndex)) {
            load();
        }
        if (data == null || data.isEmpty()) {
            return false;
        }

        return rowIndex - dataStartIndex < data.size();
    }

    private void load() {
        int pageSize = getPageSize();
        int first = rowIndex - pageSize / 2;
        if (first < 0) {
            first = 0;
        }
        load(first, pageSize);

    }

    private void load(int first, int pageSize) {
        this.data =
                load(first, pageSize, getSortCriteria(), getFilterCriteria());
        this.dataStartIndex = first;
        this.dataEndIndex = first + pageSize - 1;
    }

    public int getRowCount(){
        return -1;
    }

    public abstract int getPageSize();

    public abstract List<AttributeCriterion> getFilterCriteria();

    public T getRowData() {
        return data.get(rowIndex - dataStartIndex);
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Object getWrappedData() {
        return data;
    }

    public void setWrappedData(Object list) {
        this.data = (List)list;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public abstract List<T> load(int first, int pageSize,
                                 List<SortCriterion> sortCriteria,
                                 List<AttributeCriterion> filterCriteria);

    public Object getRowKey() {
        return rowIndex;
    }

    public void setRowKey(Object object) {
        if (object != null)
            this.rowIndex = (Integer)object;
    }

    @Override
    public boolean isSortable(String property) {
        return true;
    }

    @Override
    public List<SortCriterion> getSortCriteria() {
        if (criteria == null) {
            return Collections.emptyList();
        }
        return criteria;
    }

    @Override
    public void setSortCriteria(List<SortCriterion> criteria) {
        if (!getSortCriteria().equals(criteria)) {
            this.criteria = criteria;
            reload();
        }
    }

    public void reload() {
        load(0, getPageSize());
        rowCount = -1;
    }
}
