package soadev.ext.trinidad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.el.ELContext;

import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeyIndex;
import org.apache.myfaces.trinidad.model.SortCriterion;


public class SortableFilterableModel extends CollectionModel {
    private List wrappedData;
    private List<Integer> sortedFilteredIndexList;
    private Integer baseIndex;
    private SortCriterion sortCriterion = null;

    public SortableFilterableModel(List wrappedData) {
        super();
        this.wrappedData = wrappedData;
        sortedFilteredIndexList = new ArrayList<Integer>();
        for (int i = 0; i < wrappedData.size(); i++) {
            sortedFilteredIndexList.add(i);
        }
    }

    public Object getRowKey() {
        return isRowAvailable() ? baseIndex : null;
    }

    public void setRowKey(Object object) {
        baseIndex = object == null ? -1 : ((Integer)object);
    }

    public boolean isRowAvailable() {
        return sortedFilteredIndexList.indexOf(baseIndex) != -1;
    }

    public int getRowCount() {
        return -1;
//        return sortedFilteredIndexList.size();
    }

    public Object getRowData() {
        return wrappedData.get(baseIndex);
    }

    public int getRowIndex() {
        return sortedFilteredIndexList.indexOf(baseIndex);
    }

    public void setRowIndex(int i) {
        if(i < 0 || i >= sortedFilteredIndexList.size()){
            baseIndex = -1;
        }else{
            baseIndex =  sortedFilteredIndexList.get(i);
        }
        
    }

    public Object getWrappedData() {
        return wrappedData;
    }

    public void setWrappedData(Object object) {
        this.wrappedData = (List)object;
    }

    public List<Integer> getSortedFilteredIndexList() {
        return sortedFilteredIndexList;
    }

    @Override
    public boolean isSortable(String property) {
        try {
            Object data = wrappedData.get(0);
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
            Collections.sort(sortedFilteredIndexList); //returns original order but still same filter
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
        if (sortedFilteredIndexList!= null && !sortedFilteredIndexList.isEmpty()) {
            Comparator<Integer> comp = new Comp(property);
            if (!isAscending)
                comp = new Inverter<Integer>(comp);
            Collections.sort(sortedFilteredIndexList, comp);
        }
    }

    private final class Comp implements Comparator<Integer> {
        public Comp(String property) {
            _prop = property;
        }

        public int compare(Integer x, Integer y) {
            Object instance1 = wrappedData.get(x);
            Object value1 = evaluateProperty(instance1, _prop);

            Object instance2 = wrappedData.get(y);
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
