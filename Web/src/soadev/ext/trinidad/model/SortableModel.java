package soadev.ext.trinidad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.UUID;

import javax.el.ELContext;

import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeyIndex;
import org.apache.myfaces.trinidad.model.SortCriterion;


public class SortableModel extends CollectionModel {
    private List<SortableModel.Row> model;
    private List<String> sortedList;
    private String rowKey;
    private SortCriterion sortCriterion = null;

    public SortableModel(List wrappedData) {
        super();
        initModel(wrappedData);
        _initSortedList();
        
    }

    private void initModel(List wrappedData) {
        this.model = new ArrayList<SortableModel.Row>();
        
        for (Object obj : wrappedData) {
            String key = UUID.randomUUID().toString();
            this.model.add(new SortableModel.Row(key,obj));
        }
    }

    private void _initSortedList() {
        this.sortedList = new ArrayList<String>();
        for(SortableModel.Row row: model){
            this.sortedList.add(row.getKey());
        }
    }

    public Object getRowKey() {
        return rowKey;
    }

    public void setRowKey(Object object) {
        this.rowKey = (String)object;
    }

    public boolean isRowAvailable() {
        return sortedList.indexOf(rowKey) != -1;
    }

    public int getRowCount() {
        return -1;
        //        return sortedFilteredIndexList.size();
    }

    public Object getRowData() {
        return _getData(rowKey);
    }

    private Object _getData(String rowKey) {
        SortableModel.Row row = new SortableModel.Row(rowKey);
        int i =  model.indexOf(row);
        if( 1 != -1){
            return model.get(i).getDataProvider();
        }
        return null;
    }

    public int getRowIndex() {
        return sortedList.indexOf(rowKey);
    }

    public void setRowIndex(int i) {
        if (i < 0 || i >= sortedList.size()) {
            rowKey = "-1";
        } else {
            rowKey = sortedList.get(i);
        }

    }

    public Object getWrappedData() {
        return model;
    }

    public void setWrappedData(Object object) {
        //TODO this.wrappedData = (List)object;
    }

    public List<String> getSortedList() {
        return sortedList;
    }

    @Override
    public boolean isSortable(String property) {
        try {
            Object data = model.get(0).getDataProvider();
            Object propertyValue = evaluateProperty(data, property);

            // when the value is null, we don't know if we can sort it.
            // by default let's support sorting of null values, and let the user
            // turn off sorting if necessary:
            return (propertyValue instanceof Comparable) ||
                (propertyValue == null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Object evaluateProperty(Object base, String property) {

        ELContext elCtx = FacesContext.getCurrentInstance().getELContext();
        //simple property -> resolve value directly
        if (!property.contains(".")) {
            return elCtx.getELResolver().getValue(elCtx, base, property);
        }
        int index = property.indexOf('.');
        Object newBase =
            elCtx.getELResolver().getValue(elCtx, base, property.substring(0,
                                                                           index));

        return evaluateProperty(newBase, property.substring(index + 1));
    }

    @Override
    public List<SortCriterion> getSortCriteria() {
        if (sortCriterion == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(sortCriterion);
        }
    }

    @Override
    public void setSortCriteria(List<SortCriterion> criteria) {
        if ((criteria == null) || (criteria.isEmpty())) {
            sortCriterion = null;
            // restore unsorted order:
            Collections.sort(sortedList); //returns original order but still same filter
        } else {
            SortCriterion sc = criteria.get(0);
            sortCriterion = sc;
            _sort(sortCriterion.getProperty(), sortCriterion.isAscending());
        }
    }

    private void _sort(String property, boolean isAscending) {

        if (getRowCount() == 0) {
            return;
        }
        if (sortedList != null && !sortedList.isEmpty()) {
            Comparator<String> comp = new Comp(property);
            if (!isAscending)
                comp = new Inverter<String>(comp);
            Collections.sort(sortedList, comp);
        }
    }

    public final class Row {
        private Object _dataProvider;
        private String _key;

        Row(String key, Object data) {
            this._key = key;
            this._dataProvider = data;
        }
        
        Row(String key) {
            this(key, null);
        }
        

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof SortableModel.Row)) {
                return false;
            }
            final SortableModel.Row other = (SortableModel.Row)object;
            if (!(_key == null ? other._key == null :
                  _key.equals(other._key))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 37;
            int result = 1;
            result = PRIME * result + ((_key == null) ? 0 : _key.hashCode());
            return result;
        }

        public Object getDataProvider() {
            return _dataProvider;
        }

        public String getKey() {
            return _key;
        }
    }

    private final class Comp implements Comparator<String> {
        public Comp(String property) {
            _prop = property;
        }

        public int compare(String x, String y) {
            Object instance1 = _getData(x);
            Object value1 = evaluateProperty(instance1, _prop);

            Object instance2 =_getData(y);
            Object value2 = evaluateProperty(instance2, _prop);

            if (value1 == null)
                return (value2 == null) ? 0 : -1;

            if (value2 == null)
                return 1;

            // bug 4545164. Sometimes, isSortable returns true
            // even if the underlying object is not a Comparable.
            // This happens if the object at rowIndex zero is null.
            // So test before we cast:
            if (value1 instanceof Comparable) {
                return ((Comparable<Object>)value1).compareTo(value2);
            } else {
                // if the object is not a Comparable, then
                // the best we can do is string comparison:
                return value1.toString().compareTo(value2.toString());
            }
        }
        private final String _prop;

    }

    private static final class Inverter<T> implements Comparator<T> {
        public Inverter(Comparator<T> comp) {
            _comp = comp;
        }

        public int compare(T o1, T o2) {
            return _comp.compare(o2, o1);
        }

        private final Comparator<T> _comp;
    }
}
