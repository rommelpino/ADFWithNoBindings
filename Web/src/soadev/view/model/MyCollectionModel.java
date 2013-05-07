package soadev.view.model;

import java.util.List;

import org.apache.myfaces.trinidad.model.CollectionModel;

import soadev.domain.Job;


public class MyCollectionModel extends CollectionModel {
    private List wrappedData;
    private int index;

    public MyCollectionModel() {

    }

    private int indexOf(Object rowKey) {
        int result = -1;
        for (Object obj : wrappedData) {
            result++;
            Job job = (Job)obj;
            if (rowKey.equals(job.getJobId())) {
                return result;
            }
        }
        return result;
    }

    public MyCollectionModel(List wrappedData) {
        this.wrappedData = wrappedData;
    }


    public Object getRowKey() {

        if (index < 0 || index >= wrappedData.size()) {
            return null;
        }
        Job job = (Job)wrappedData.get(index);
        System.out.println("getRowKey() return: " + job.getJobId());
        return job.getJobId();
    }

    public void setRowKey(Object object) {
        System.out.println("setRowKey(Object object) " + object);
        if (object == null) {
            index = -1;
        } else {
            index = indexOf(object);
        }
    }

    public boolean isRowAvailable() {
        System.out.println("isRowAvailable()  return: " +
                           (index > -1 && index < wrappedData.size()));
        return index > -1 && index < wrappedData.size();
    }

    public int getRowCount() {
        System.out.println("getRowCount() return: " + wrappedData.size());
        return wrappedData.size();
    }

    public Object getRowData() {
        System.out.println("getRowData()");
        if (isRowAvailable()) {
            System.out.println("return: " + wrappedData.get(index));
            return wrappedData.get(index);
        }
        return null;
    }

    public int getRowIndex() {
        System.out.println("getRowIndex() return: " + index);
        return index;
    }

    public void setRowIndex(int i) {
        System.out.println("setRowIndex(int i) " + i);
        index = i;
    }

    public Object getWrappedData() {
        System.out.println("getWrappedData() return: " + wrappedData);
        return wrappedData;
    }

    public void setWrappedData(Object object) {
        System.out.println("setWrappedData(Object object)" + object);
        this.wrappedData = (List)object;
    }

}
